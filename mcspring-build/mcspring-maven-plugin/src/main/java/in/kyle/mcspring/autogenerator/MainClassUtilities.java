package in.kyle.mcspring.autogenerator;

import org.apache.maven.project.MavenProject;

public class MainClassUtilities {

    public static final String ROOT_PACKAGE = "org.springframework.boot.loader";

    private MainClassUtilities() {
    }

    public static String getMainClassName(MavenProject project) {
        return project.getArtifactId().replace("-", "");
    }

    public static String getMainClassLocation(MavenProject project) {
        return getMainClassPackage(project) + "." + getMainClassName(project);
    }

    public static String getMainClassPackage(MavenProject project) {
        String groupId = project.getGroupId().replace("-", "");
        return ROOT_PACKAGE + "." + groupId;
    }
}
