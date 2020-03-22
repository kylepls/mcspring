package in.kyle.mcspring.command.registration;

import java.lang.reflect.Method;

import in.kyle.mcspring.command.Command;

public interface CommandRegistration {
    void register(Command command, Method method, Object object);
}
