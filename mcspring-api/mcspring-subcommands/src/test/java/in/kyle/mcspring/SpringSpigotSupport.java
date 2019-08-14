package in.kyle.mcspring;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;

import java.util.Collections;

import in.kyle.api.bukkit.entity.TestPlayer;
import in.kyle.api.generate.api.Generator;
import in.kyle.mcspring.command.SimpleMethodInjection;
import in.kyle.mcspring.subcommands.TestConsole;

@TestConfiguration
@EnableAutoConfiguration(exclude = {in.kyle.mcspring.SpringSpigotSupport.class})
@ComponentScan(basePackageClasses = TestConsole.class)
public class SpringSpigotSupport {
    
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
    SimpleMethodInjection injection() {
        return new SimpleMethodInjection(Collections.emptyList());
    }
}
