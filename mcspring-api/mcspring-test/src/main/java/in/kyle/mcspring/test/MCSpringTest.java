package in.kyle.mcspring.test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@SpringBootTest(classes = TestSpringSpigotSupport.class)
@Configuration
@ComponentScan(basePackageClasses = TestSpringSpigotSupport.class)
public @interface MCSpringTest {
    
    @AliasFor(annotation = SpringBootTest.class,
              attribute = "classes") Class<?>[] classes() default {TestSpringSpigotSupport.class};
}
