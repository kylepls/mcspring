package in.kyle.mcspring.command;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
public class SimpleMethodInjection {
    
    private final List<Resolver> resolvers;
    
    @SneakyThrows
    public Object invoke(Method method, Object object, Set<Resolver> resolvers, Object... contextObjects) {
        Object[] params = getParameters(method, resolvers, contextObjects);
        method.setAccessible(true);
        if (params.length != 0) {
            return method.invoke(object, params);
        } else {
            return method.invoke(object);
        }
    }
    
    private Set<Resolver> makeResolvers(Object... contextObjects) {
        return Stream.of(contextObjects)
                .filter(Objects::nonNull)
                .map(o -> (Resolver) parameter -> ClassUtils.isAssignable(parameter.getType(),
                                                                          o.getClass())
                        ? Optional.of(o)
                        : Optional.empty())
                .collect(Collectors.toSet());
    }
    
    public Object[] getParameters(Method method, Set<Resolver> contextResolvers, Object... contextObjects) {
        Parameter[] parameters = method.getParameters();
        Object[] params = new Object[parameters.length];
        
        List<Resolver> methodResolvers = new ArrayList<>();
        methodResolvers.addAll(contextResolvers);
        methodResolvers.addAll(makeResolvers(contextObjects));
        methodResolvers.addAll(resolvers);
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
    
            for (int j = 0; j < methodResolvers.size(); j++) {
                Resolver methodResolver = methodResolvers.get(j);
                Optional<Object> resolved = methodResolver.resolve(parameter);
                if (resolved.isPresent()) {
                    params[i] = resolved.get();
                    methodResolvers.remove(j);
                    break;
                }
            }
    
            if (params[i] == null) {
                throw new RuntimeException(
                        "Unable to resolve parameter " + parameter.getType() + " for " +
                        method.getName());
            }
        }
        return params;
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
