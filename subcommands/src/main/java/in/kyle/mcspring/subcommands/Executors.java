package in.kyle.mcspring.subcommands;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.stream.Stream;

public interface Executors {
    
    default Method getMethod(int argCount) {
        try {
            Method writeReplace = this.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(true);
            SerializedLambda sl = (SerializedLambda) writeReplace.invoke(this);
            String methodName = sl.getImplMethodName();
            Class<?> clazz = Class.forName(sl.getImplClass().replace("/", "."));
            return Stream.of(clazz.getMethods(), clazz.getDeclaredMethods())
                    .flatMap(Stream::of)
                    .filter(m -> m.getName().equals(methodName))
                    .filter(m -> m.getParameters().length == argCount)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Method not found"));
        } catch (Exception e) {
            return null;
        }
    }
    
    interface E1<A> extends Executors, Serializable {
        void handle(A a1);
    }
    
    interface E2<A, B> extends Executors, Serializable {
        void handle(A a, B b);
    }
    
    interface E3<A, B, C> extends Executors, Serializable {
        void handle(A a, B b, C c);
    }
    
    interface E4<A, B, C, D> extends Executors, Serializable {
        void handle(A a, B b, C c, D d);
    }
    
    interface E5<A, B, C, D, E> extends Executors, Serializable {
        void handle(A a, B b, C c, D d, E e);
    }
    
    interface E6<A, B, C, D, E, F> extends Executors, Serializable {
        void handle(A a, B b, C c, D d, E e, F f);
    }
}
