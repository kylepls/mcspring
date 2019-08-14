package com.joshb.mcspringboot.util;

import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class SpringScanner {
    
    private final ApplicationContext context;
    
    public Map<Method, Object> scanMethods(Class<? extends Annotation> annotation) {
        HashMap<Method, Object> methods = new HashMap<>();
        for (String beanName : context.getBeanDefinitionNames()) {
            Object object = context.getBean(beanName);
            
            Class<?> clazz = object.getClass();
            if (AopUtils.isAopProxy(clazz)) {
                clazz = AopUtils.getTargetClass(object);
            }
            
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(annotation)) {
                    methods.put(method, object);
                }
            }
        }
        return methods;
    }
}
