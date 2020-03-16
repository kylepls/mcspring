package in.kyle.mcspring.command;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import in.kyle.mcspring.command.registration.Resolver;
import lombok.var;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.*;

@SuppressWarnings("unused")
class TestSimpleMethodInjection {
    
    @Test
    void testGetParametersEmpty() throws NoSuchMethodException {
        abstract class Test {
            abstract void test();
        }
        
        var injection = new SimpleMethodInjection(emptyList());
        var parameters = injection.getParameters(Test.class.getDeclaredMethod("test"), emptyList());
        assertThat(parameters).isEmpty();
    }
    
    @Test
    void testGetContextObjectParameters() throws NoSuchMethodException {
        abstract class Test {
            abstract void test(String s1, String s2, String s3);
        }
        
        var injection = new SimpleMethodInjection(emptyList());
        var testMethod =
                Test.class.getDeclaredMethod("test", String.class, String.class, String.class);
        var parameters = injection.getParameters(testMethod, emptyList(), "s1", "s2", "s3");
        assertThat(parameters).containsExactly("s1", "s2", "s3");
    }
    
    @Test
    void testGetContextObjectParametersNotFound() throws NoSuchMethodException {
        abstract class Test {
            abstract void test(int s1);
        }
        
        var injection = new SimpleMethodInjection(emptyList());
        var testMethod = Test.class.getDeclaredMethod("test", int.class);
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> {
            injection.getParameters(testMethod, emptyList(), "s1", "s2", "s3");
        });
    }
    
    @Test
    void testGetContextObjectFromResolver() throws NoSuchMethodException {
        abstract class Test {
            abstract void test(int s1, String s2, int s3);
        }
        
        var injection = new SimpleMethodInjection(emptyList());
        var testMethod = Test.class.getDeclaredMethod("test", int.class, String.class, int.class);
        List<Resolver> resolvers = new ArrayList<>();
        resolvers.add(parameter -> {
            if (int.class.isAssignableFrom(parameter.getType())) {
                return Optional.of(1);
            } else {
                return Optional.empty();
            }
        });
        resolvers.add(parameter -> {
            if (String.class.isAssignableFrom(parameter.getType())) {
                return Optional.of("string");
            } else {
                return Optional.empty();
            }
        });
        var parameters = injection.getParameters(testMethod, resolvers);
        assertThat(parameters).containsExactly(1, "string", 1);
    }
    
    @Test
    void testMakeResolvers() throws NoSuchMethodException {
        // given 3 parameters, make resolvers, and then resolve them back using an int param
        abstract class Test {
            abstract void param(int i);
        }
        var injection = new SimpleMethodInjection(emptyList());
        Method method = Test.class.getDeclaredMethod("param", int.class);
        Parameter parameter = method.getParameters()[0];
        var output = injection.makeResolvers(1, 2, 3)
                .stream()
                .map(r -> r.resolve(parameter))
                .map(Optional::get)
                .collect(Collectors.toList());
        assertThat(output).containsExactly(1, 2, 3);
    }
    
    @Test
    void testMakeResolversEmpty() throws NoSuchMethodException {
        // a resolver should return empty if it is not assignable to a given type
        abstract class Test {
            abstract void param(int i);
        }
        var injection = new SimpleMethodInjection(emptyList());
        Method method = Test.class.getDeclaredMethod("param", int.class);
        Parameter parameter = method.getParameters()[0];
        var output = injection.makeResolvers("test")
                .stream()
                .map(r -> r.resolve(parameter))
                .anyMatch(Optional::isPresent);
        assertThat(output).isFalse();
    }
}
