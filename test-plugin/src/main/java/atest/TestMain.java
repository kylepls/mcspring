package atest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

import in.kyle.mcspring.SpringPlugin;
import in.kyle.mcspring.testplugin.PluginMain;

public class TestMain {
    public static void main(String[] args) throws Exception{
        System.out.println("TestMain.main");
        System.out.println("TEST");
        URLClassLoader loader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
        URLClassLoader classLoader =
                (URLClassLoader) PluginMain.INSTANCE.getClass().getClassLoader();
    
        Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        addURL.setAccessible(true);
        for (URL url : loader.getURLs()) {
            addURL.invoke(classLoader, url);
        }
        
        new SpringPlugin(null);
        System.out.println(PluginMain.INSTANCE.getName());
    }
}
