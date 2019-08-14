package in.kyle.mcspring.processor.annotation;

public @interface SpringPlugin {
    String name();
    String version() default "0.0.1";
    String description() default "A Spring plugin";
}
