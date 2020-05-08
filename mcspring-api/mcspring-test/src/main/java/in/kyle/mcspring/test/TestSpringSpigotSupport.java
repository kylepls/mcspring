package in.kyle.mcspring.test;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.util.Properties;

import in.kyle.mcspring.SpringPlugin;
import in.kyle.mcspring.command.SimpleMethodInjection;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackageClasses = {TestSpringSpigotSupport.class, SpringPlugin.class})
@Import(SimpleMethodInjection.class)
class TestSpringSpigotSupport {
    
    @Bean
    BuildProperties buildProperties() {
        return new BuildProperties(new Properties());
    }
}
