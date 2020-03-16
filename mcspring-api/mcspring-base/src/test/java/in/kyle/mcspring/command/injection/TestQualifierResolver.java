package in.kyle.mcspring.command.injection;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(classes = TestQualifierResolver.Config.class)
@Import(QualifierResolver.class)
class TestQualifierResolver {
    
    @Autowired
    QualifierResolver resolver;
    
    @Test
    void testSimpleResolve() throws NoSuchMethodException {
        abstract class Test {
            abstract void test(@Qualifier("noodle") String string);
        }
        Method method = Test.class.getDeclaredMethod("test", String.class);
        Optional<Object> resolve = resolver.resolve(method.getParameters()[0]);
        assertThat(resolve).hasValue("I like noodles");
    }
    
    @Test
    void testSimpleResolveDifferentType() throws NoSuchMethodException {
        abstract class Test {
            abstract void test(@Qualifier("noodle") int value);
        }
        Method method = Test.class.getDeclaredMethod("test", int.class);
        assertThatExceptionOfType(BeanNotOfRequiredTypeException.class).isThrownBy(() -> {
            resolver.resolve(method.getParameters()[0]);
        });
    }
    
    @Test
    void testNoQualifierIgnore() throws NoSuchMethodException {
        abstract class Test {
            abstract void test(int value);
        }
        Method method = Test.class.getDeclaredMethod("test", int.class);
        Optional<Object> resolve = resolver.resolve(method.getParameters()[0]);
        assertThat(resolve).isNotPresent();
    }
    
    @SpringBootConfiguration
    static class Config {
        @Bean(name = "noodle")
        String noodle() {
            return "I like noodles";
        }
    }
}
