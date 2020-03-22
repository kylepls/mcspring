package in.kyle.mcspring.command.injection;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;
import java.util.Optional;

import in.kyle.mcspring.command.registration.Resolver;
import lombok.RequiredArgsConstructor;

@Order(1)
@Component
@RequiredArgsConstructor
class QualifierResolver implements Resolver {
    
    private final ApplicationContext context;
    
    @Override
    public Optional<Object> resolve(Parameter parameter) {
        Qualifier qualifier = parameter.getAnnotation(Qualifier.class);
        if (qualifier != null) {
            Object bean = context.getBean(qualifier.value(), parameter.getType());
            // spring will error out if mismatched type
            return Optional.of(bean);
        } else {
            return Optional.empty();
        }
    }
}
