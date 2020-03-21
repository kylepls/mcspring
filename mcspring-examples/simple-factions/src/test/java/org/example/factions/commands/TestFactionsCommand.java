package org.example.factions.commands;

import org.example.factions.api.Faction;
import org.example.factions.api.FactionsApi;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

import in.kyle.mcspring.test.MCSpringTest;
import in.kyle.mcspring.test.command.TestCommandExecutor;
import lombok.val;

import static org.assertj.core.api.Assertions.*;

@MCSpringTest(classes = TestFactionsCommand.Config.class)
class TestFactionsCommand {
    
    @Autowired
    TestCommandExecutor console;
    
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
    public void testList() {
        val output = console.run("f info");
        assertThat(output).containsExactly("You are not in a faction");
    }
    
    @Configuration
    @ComponentScan(basePackages = {"org.example.factions"})
    static class Config {
    }
}
