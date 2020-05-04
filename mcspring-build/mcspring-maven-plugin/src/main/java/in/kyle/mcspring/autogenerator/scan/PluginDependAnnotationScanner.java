package in.kyle.mcspring.autogenerator.scan;

import in.kyle.mcspring.annotation.PluginDepend;

import java.util.List;

public interface PluginDependAnnotationScanner {

    List<PluginDepend> getScannedAnnotations();

}
