package de.anteiku.kittybot.objects.version;

import de.anteiku.kittybot.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class AppInfo{

	private static final Logger LOG = LoggerFactory.getLogger(AppInfo.class);
	private static final String VERSION;
	private static final String GROUP_ID;
	private static final String ARTIFACT_ID;
	private static final String BUILD_NUMBER;
	private static final String BUILD_TIME;

	static{
		Properties prop = new Properties();
		try{
			prop.load(AppInfo.class.getClassLoader().getResourceAsStream("app.properties"));
		}
		catch(IOException e){
			LOG.error("Failed to load app.properties", e);
		}
		VERSION = prop.getProperty("version");
		GROUP_ID = prop.getProperty("groupId");
		ARTIFACT_ID = prop.getProperty("artifactId");
		BUILD_NUMBER = prop.getProperty("buildNumber");
		var buildTime = prop.getProperty("buildTime");
		if(buildTime.equals("@env.BUILD_TIME@")){
			BUILD_TIME = "Unknown";
		}
		else{
			BUILD_TIME = TimeUtils.parseTimeMillis(buildTime);
		}
	}

	public static String getVersion(){
		return VERSION.substring(0, 7);
	}

	public static String getGroupId(){
		return GROUP_ID;
	}

	public static String getArtifactId(){
		return ARTIFACT_ID;
	}

	public static String getBuildNumber(){
		return BUILD_NUMBER;
	}

	public static String getBuildTime(){
		return BUILD_TIME;
	}

	public static String getVersionBuild(){
		return VERSION + "_" + BUILD_NUMBER;
	}

}
