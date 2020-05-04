package in.kyle.mcspring.autogenerator.util;

import lombok.SneakyThrows;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClassLoadingUtility {

    public static List<URL> getJarFileURLs(List<File> jarFiles) {
        return jarFiles.stream().map(ClassLoadingUtility::createJarFileURL).collect(Collectors.toList());
    }

    @SneakyThrows
    private static URL createJarFileURL(File jarFile) {
        return new URL("jar:file:" + jarFile.getPath() + "!/");
    }

    //Creates a class loader with all jar file classes and classes in the sourceFolder parameter.
    @SneakyThrows
    public static URLClassLoader createClassLoader(ClassLoader parent, List<File> jarFiles, File sourceFolder) {
        List<URL> urls = new ArrayList<>(getJarFileURLs(jarFiles));
        urls.add(sourceFolder.toURI().toURL());
        return new URLClassLoader(urls.toArray(new URL[0]), parent);
    }
}
