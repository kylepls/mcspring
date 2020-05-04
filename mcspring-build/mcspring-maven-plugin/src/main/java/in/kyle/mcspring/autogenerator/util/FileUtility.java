package in.kyle.mcspring.autogenerator.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtility {

    public static List<File> getFilesInDirectory(File directory) {
        List<File> files = new ArrayList<>();
        File[] filesInDirectory = directory.listFiles();
        if (filesInDirectory != null) {
            for (File file : filesInDirectory) {
                if (file.isDirectory()) {
                    files.addAll(getFilesInDirectory(file));
                } else {
                    files.add(file);
                }
            }
        }
        return files;
    }
}
