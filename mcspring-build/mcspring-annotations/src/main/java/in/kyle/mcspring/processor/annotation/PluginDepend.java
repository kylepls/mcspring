package in.kyle.mcspring.processor.annotation;

public @interface PluginDepend {
    String[] plugins();
    boolean soft() default false;
}
