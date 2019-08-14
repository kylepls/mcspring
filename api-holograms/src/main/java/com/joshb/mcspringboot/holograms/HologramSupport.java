package com.joshb.mcspringboot.holograms;

import org.bukkit.plugin.Plugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import in.kyle.bukkit.holograms.HologramManager;

@Component
@Configuration
public class HologramSupport {
    
    @Bean
    HologramManager hologramManager(Plugin plugin) {
        return new HologramManager(plugin);
    }
}
