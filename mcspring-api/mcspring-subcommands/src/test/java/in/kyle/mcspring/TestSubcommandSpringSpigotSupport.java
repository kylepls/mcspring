package in.kyle.mcspring;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.Collections;

import in.kyle.mcspring.command.SimpleMethodInjection;
import in.kyle.mcspring.subcommands.TestConsole;

@TestConfiguration
//@EnableAutoConfiguration(exclude = {in.kyle.mcspring.SpringSpigotSupport.class})
@ComponentScan(basePackageClasses = TestConsole.class)
public class TestSubcommandSpringSpigotSupport {
    
    @Bean
    SimpleMethodInjection injection() {
        return new SimpleMethodInjection(Collections.emptyList());
    }
}
