package in.kyle.mcspring.manager.commands;

import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import in.kyle.mcspring.manager.controller.PluginController;
import in.kyle.mcspring.test.MCSpringTest;
import in.kyle.mcspring.test.command.TestCommandExecutor;
import lombok.val;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MCSpringTest
class TestCommandPlugin {
    
    @Autowired
    TestCommandExecutor executor;
    
    @Autowired
    PluginController controller;
    
    @Test
    void testLoad() {
        Map<String, Path> loadablePlugins = new HashMap<>();
        loadablePlugins.put("test-plugin", Paths.get("test-plugin.jar"));
        doReturn(loadablePlugins).when(controller).getLoadablePlugins();
        doReturn(Optional.empty()).when(controller).load(any());
        val output = executor.run("pl load test-plugin");
        assertThat(output).containsExactly("Could not load test-plugin.jar see log for details");
    }
    
    @Test
    void testUnload() {
        Plugin plugin = mock(Plugin.class);
        doReturn("test-plugin").when(plugin).getName();
        doReturn(true).when(controller).unload(any());
        val plugins = new HashSet<>(Collections.singletonList(plugin));
        doReturn(plugins).when(controller).getPlugins();
        val output = executor.run("pl unload test-plugin");
        assertThat(output).containsExactly("Plugin test-plugin disabled");
    }
    
    @Test
    void testList() {
        Plugin plugin = mock(Plugin.class);
        doReturn("test-plugin").when(plugin).getName();
        val plugins = Stream.of(plugin).collect(Collectors.toMap(Plugin::getName, p -> true));
        doReturn(plugins).when(controller).getAllPlugins();
        
        val output = executor.run("pl list");
        assertThat(output).containsExactly("test-plugin");
    }
    
    @Configuration
    static class Config {
        @Bean
        PluginController pluginController() {
            return mock(PluginController.class);
        }
    }
}
