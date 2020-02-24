package in.kyle.mcspring.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import in.kyle.api.bukkit.TestServer;
import in.kyle.api.generate.api.Generator;
import in.kyle.mcspring.SpringPlugin;

@TestConfiguration
@ComponentScan(basePackageClasses = {TestConfig.class, SpringPlugin.class})
public class TestConfig {
    
    @Bean
    Generator generator() {
        return Generator.create();
    }
}
