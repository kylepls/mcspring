package com.joshb.mcspringboot;

import org.springframework.context.ConfigurableApplicationContext;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StaticSpring {
    
    private static ConfigurableApplicationContext parent;
    
    public static ConfigurableApplicationContext getParentContainer() {
        return parent;
    }
    
    public static boolean hasParent() {
        return parent != null;
    }
    
    public static void setParent(ConfigurableApplicationContext context) {
        parent = context;
    }
}
