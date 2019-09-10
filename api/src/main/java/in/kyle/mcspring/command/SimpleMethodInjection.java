package in.kyle.mcspring.command;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * Used to inject method parameters
 * Does not support annotated parameters
 */
@Component
@RequiredArgsConstructor
class SimpleMethodInjection {
    
    private final List<Resolver> resolvers;
    
    Object invoke(Method method, Object object, Object... contextObjects) {
        Set<Resolver> resolvers = Stream.of(contextObjects)
                .map(o -> (Resolver) parameter -> parameter.getType().isAssignableFrom(o.getClass())
                        ? Optional.of(o)
                        : Optional.empty())
                .collect(Collectors.toSet());
        return invoke(method, object, resolvers);
    }
    
    @SneakyThrows
    private Object invoke(Method method, Object object, Collection<Resolver> contextResolvers) {
        Parameter[] parameters = method.getParameters();
        Object[] params = new Object[parameters.length];
        
        List<Resolver> methodResolvers = new ArrayList<>();
        methodResolvers.addAll(contextResolvers);
        methodResolvers.addAll(resolvers);
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            params[i] = methodResolvers.stream()
                    .map(r -> r.resolve(parameter))
                    .flatMap(Stream::of)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(
                            "No such parameter " + parameter.getType() + " for " +
                            method.getName()));
        }
        
        return method.invoke(object, params);
    }
    
    @Component
    @AllArgsConstructor
    static class SimpleSpringResolver implements Resolver {
        
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
}
