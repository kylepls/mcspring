package org.example.factions.commands;

import org.example.factions.api.FactionsApi;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import in.kyle.mcspring.test.MCSpringTest;
import in.kyle.mcspring.test.command.TestCommandExecutor;
import lombok.var;

import static org.assertj.core.api.Assertions.*;

@MCSpringTest(classes = TestFactionsCommand.Config.class)
class TestFactionsCommand {
    
    @Autowired
    TestCommandExecutor console;
    
    @Autowired
    FactionsApi api;
    
    @Test
    void testCreate() {
        var output = console.run("f create test");
        assertThat(output).containsExactly("Created faction: test");
    }
    
    @Configuration
    @ComponentScan(basePackages = {"org.example.factions"})
    static class Config {
    }
}
