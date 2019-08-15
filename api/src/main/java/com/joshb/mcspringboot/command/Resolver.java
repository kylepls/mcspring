package com.joshb.mcspringboot.command;

import java.lang.reflect.Parameter;
import java.util.Optional;

public interface Resolver {
    Optional<Object> resolve(Parameter parameter);
}
