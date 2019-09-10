package in.kyle.mcspring.command;

import java.lang.reflect.Parameter;
import java.util.Optional;

public interface Resolver {
    Optional<Object> resolve(Parameter parameter);
}
