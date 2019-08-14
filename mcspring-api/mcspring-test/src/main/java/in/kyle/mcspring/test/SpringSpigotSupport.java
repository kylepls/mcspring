package in.kyle.mcspring.test;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

import java.util.Properties;

import in.kyle.api.bukkit.TestServer;
import in.kyle.api.bukkit.entity.TestPlayer;
import in.kyle.api.generate.api.Generator;
import in.kyle.mcspring.SpringPlugin;
import in.kyle.mcspring.command.SimpleMethodInjection;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackageClasses = {SpringSpigotSupport.class, SpringPlugin.class})
@Import(SimpleMethodInjection.class)
class SpringSpigotSupport {
    
    @Bean
    Generator generator() {
        return Generator.create();
    }
    
    @Bean
    @Scope("prototype")
    TestPlayer player(Generator generator) {
        return generator.create(TestPlayer.class);
    }
    
    @Bean
    TestServer server(Generator generator) {
        return generator.create(TestServer.class);
    }
    
    @Bean
    BuildProperties buildProperties() {
        return new BuildProperties(new Properties());
    }
}
