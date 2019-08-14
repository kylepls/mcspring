package in.kyle.mcspring.command;

import java.lang.reflect.Method;

public interface CommandRegistration {
    void register(Command command, Method method, Object object);
}
