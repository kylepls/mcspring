package in.kyle.mcspring.command;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.var;

/**
 * Used to inject method parameters
 * Does not support annotated parameters
 */
@Lazy
@Component
@RequiredArgsConstructor
public class SimpleMethodInjection {
    
    private final List<Resolver> resolvers;
    
    @SneakyThrows
    public Object invoke(Method method, Object object, List<Resolver> resolvers, Object... contextObjects) {
        Object[] params = getParameters(method, resolvers, contextObjects);
        method.setAccessible(true);
        if (params.length != 0) {
            return method.invoke(object, params);
        } else {
            return method.invoke(object);
        }
    }
    
    private List<Resolver> makeResolvers(Object... contextObjects) {
        return Stream.of(contextObjects)
                .filter(Objects::nonNull)
                .map(o -> (Resolver) parameter -> ClassUtils.isAssignable(parameter.getType(),
                                                                          o.getClass())
                        ? Optional.of(o)
                        : Optional.empty())
                .collect(Collectors.toList());
    }
    
    public Object[] getParameters(Method method, List<Resolver> contextResolvers, Object... contextObjects) {
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
                    var removed = methodResolvers.remove(j);
                    methodResolvers.add(removed);
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
}
