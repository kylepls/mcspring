package in.kyle.mcspring.command;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;
import java.util.Optional;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
class SimpleSpringResolver implements Resolver {
    
    private final ApplicationContext context;
    
    @Override
    public Optional<Object> resolve(Parameter parameter) {
        try {
            return Optional.of(context.getBean(parameter.getType()));
        } catch (NoSuchBeanDefinitionException e) {
            return Optional.empty();
        }
    }
}
