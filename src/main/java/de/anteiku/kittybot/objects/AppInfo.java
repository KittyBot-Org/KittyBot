package de.anteiku.kittybot.objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppInfo {

    private static final Logger LOG = LoggerFactory.getLogger(AppInfo.class);

    private static final String VERSION;
    private static final String GROUP_ID;
    private static final String ARTIFACT_ID;
    private static final String BUILD_NUMBER;

    static {
        InputStream resourceAsStream = AppInfo.class.getResourceAsStream("/app.properties");
        Properties prop = new Properties();
        try {
            prop.load(resourceAsStream);
        } catch (IOException e) {
            LOG.error("Failed to load app.properties", e);
        }
        VERSION = prop.getProperty("version");
        GROUP_ID = prop.getProperty("groupId");
        ARTIFACT_ID = prop.getProperty("artifactId");
        BUILD_NUMBER = prop.getProperty("buildNumber");
    }

    public static String getVersionBuild() {
        return VERSION + "_" + BUILD_NUMBER;
    }

    public static String getGroupId() {
        return GROUP_ID;
    }

    public static String getArtifactId() {
        return ARTIFACT_ID;
    }

}
