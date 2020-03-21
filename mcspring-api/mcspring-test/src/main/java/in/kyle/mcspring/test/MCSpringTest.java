package in.kyle.mcspring.test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AliasFor;
import org.springframework.test.annotation.DirtiesContext;

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
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public @interface MCSpringTest {
    
    @AliasFor(annotation = SpringBootTest.class,
              attribute = "classes") Class<?>[] classes() default {TestSpringSpigotSupport.class};
}
