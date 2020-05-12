package in.kyle.mcspring.test;

import org.bukkit.entity.Player;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

import java.util.Properties;

import in.kyle.mcspring.SpringPlugin;
import in.kyle.mcspring.command.SimpleMethodInjection;

import static org.mockito.Mockito.*;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackageClasses = {SpringSpigotSupport.class, SpringPlugin.class})
@Import(SimpleMethodInjection.class)
class SpringSpigotSupport {
    
    @Bean
    @Scope("prototype")
    Player player() {
        return mock(Player.class);
    }
    
    @Bean
    BuildProperties buildProperties() {
        return new BuildProperties(new Properties());
    }
}
