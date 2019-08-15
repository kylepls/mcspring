package com.joshb.mcspringboot;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.springframework.context.ConfigurableApplicationContext;

import lombok.AllArgsConstructor;

@AllArgsConstructor
class StaticSpring {
    
    private static ConfigurableApplicationContext parent;
    
    static ConfigurableApplicationContext getParentContainer() {
        return parent;
    }
    
    static boolean hasParent() {
        return parent != null;
    }
    
    static void setParent(ConfigurableApplicationContext context) {
        parent = context;
    }
    
    static void setupLogger() {
        Logger rootLogger = (Logger) LogManager.getRootLogger();
        rootLogger.setLevel(Level.WARN);
    }
}
