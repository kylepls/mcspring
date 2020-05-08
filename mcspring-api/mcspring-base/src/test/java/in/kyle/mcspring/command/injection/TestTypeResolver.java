package in.kyle.mcspring.command.injection;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.lang.reflect.Method;
import java.util.Optional;

import lombok.var;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(classes = TestTypeResolver.Config.class)
@Import(TypeResolver.class)
class TestTypeResolver {
    
    @Autowired
    TypeResolver resolver;
    
    @Test
    void testSimpleResolve() throws NoSuchMethodException {
        abstract class Test {
            abstract void test(String string);
        }
        Method method = Test.class.getDeclaredMethod("test", String.class);
        Optional<Object> resolve = resolver.resolve(method.getParameters()[0]);
        assertThat(resolve).hasValue("A string");
    }
    
    @Test
    void testSimpleResolveDifferentType() throws NoSuchMethodException {
        abstract class Test {
            abstract void test(int value);
        }
        Method method = Test.class.getDeclaredMethod("test", int.class);
        var result = resolver.resolve(method.getParameters()[0]);
        assertThat(result).isNotPresent();
    }
    
    @SpringBootConfiguration
    static class Config {
        @Bean
        String string() {
            return "A string";
        }
    }
}
