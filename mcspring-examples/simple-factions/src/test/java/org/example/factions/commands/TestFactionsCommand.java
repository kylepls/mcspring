package org.example.factions.commands;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.example.factions.api.Faction;
import org.example.factions.api.FactionsApi;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;
import java.util.UUID;

import in.kyle.mcspring.test.MCSpringTest;
import in.kyle.mcspring.test.command.TestCommandExecutor;
import in.kyle.mcspring.test.command.TestSender;
import lombok.val;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MCSpringTest(classes = TestFactionsCommand.Config.class)
class TestFactionsCommand {
    
    @Autowired
    TestCommandExecutor console;
    
    @Autowired
    Server server;
    
    @Autowired
    FactionsApi api;
    
    @Test
    void testCreate() {
        val output = console.run("f create test");
        assertThat(output).containsExactly("Created faction: test");
        assertThat(api.getFactions()).first().matches(f -> f.getName().equals("test"));
    }
    
    @Test
    void testDelete() {
        api.addFaction(new Faction("test-faction", UUID.randomUUID()));
        val output = console.run("f delete test-faction");
        assertThat(output).containsExactly("Deleted faction: test-faction");
        assertThat(api.getFactions()).isEmpty();
    }
    
    @Test
    void testJoin() {
        api.addFaction(new Faction("test", UUID.randomUUID()));
        val output = console.run("f join test");
        assertThat(output).containsExactly("You joined test");
        assertThat(api.getFactions()).first().matches(f -> f.getMembers().size() == 2);
    }
    
    @Test
    void testInfo() {
        val output = console.run("f info");
        assertThat(output).containsExactly("You are not in a faction");
    }
    
    @Test
    void testList() {
        TestSender sender = spy(TestSender.class);
        doReturn("test-sender").when(sender).getName();
        UUID toBeReturned = UUID.randomUUID();
        doReturn(toBeReturned).when(sender).getUniqueId();
        doReturn(sender).when(server).getPlayer(toBeReturned);
    
        Faction faction = new Faction("test-faction", sender.getUniqueId());
        api.addFaction(faction);
    
        val output = console.run("f list");
        assertThat(output).containsExactly("Factions: ", "test-faction: test-sender(owner)");
    }
    
    @Configuration
    @ComponentScan(basePackages = {"org.example.factions"})
    static class Config {
        @Bean
        Server server() throws NoSuchFieldException, IllegalAccessException {
            Server server = mock(Server.class);
            Field field = Bukkit.class.getDeclaredField("server");
            field.setAccessible(true);
            field.set(null, server);
            return server;
        }
    }
}
