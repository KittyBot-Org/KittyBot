package de.kittybot.kittybot.objects.version;

import de.kittybot.kittybot.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class GitInfo{

	private static final Logger LOG = LoggerFactory.getLogger(GitInfo.class);
	private static final String BRANCH;
	private static final String COMMIT_ID;
	private static final String COMMIT_ID_ABBREV;
	private static final String COMMIT_USERNAME;
	private static final String COMMIT_EMAIL;
	private static final String COMMIT_MESSAGE;
	private static final String COMMIT_MESSAGE_SHORT;
	private static final String COMMIT_TIME;

	static{
		Properties prop = new Properties();
		try{
			prop.load(GitInfo.class.getClassLoader().getResourceAsStream("git.properties"));
		}
		catch(NullPointerException e){
			LOG.trace("Failed to load git repo information. Did you build with the git gradle plugin? Is the git.properties file present?");
		}
		catch(IOException e){
			LOG.info("Failed to load git repo information due to suspicious IOException", e);
		}
		BRANCH = String.valueOf(prop.getOrDefault("git.branch", ""));
		COMMIT_ID = String.valueOf(prop.getOrDefault("git.commit.id", ""));
		COMMIT_ID_ABBREV = String.valueOf(prop.getOrDefault("git.commit.id.abbrev", ""));
		COMMIT_USERNAME = String.valueOf(prop.getOrDefault("git.commit.user.name", ""));
		COMMIT_EMAIL = String.valueOf(prop.getOrDefault("git.commit.user.email", ""));
		COMMIT_MESSAGE = String.valueOf(prop.getOrDefault("git.commit.message.full", ""));
		COMMIT_MESSAGE_SHORT = String.valueOf(prop.getOrDefault("git.commit.message.short", ""));

		final String time = String.valueOf(prop.get("git.commit.time"));
		if(time == null || time.equals("null")){
			COMMIT_TIME = "Unofficial";
		}
		else{
			COMMIT_TIME = TimeUtils.parseTime(time);
		}
	}

	public static String getBranch(){
		return BRANCH;
	}

	public static String getShortCommitId(){
		return COMMIT_ID.substring(0, 7);
	}

	public static String getCommitId(){
		return COMMIT_ID;
	}

	public static String getCommitIdAbbrev(){
		return COMMIT_ID_ABBREV;
	}

	public static String getCommitUserName(){
		return COMMIT_USERNAME;
	}

	public static String getCommitUserEmail(){
		return COMMIT_EMAIL;
	}

	public static String getCommitMessageFull(){
		return COMMIT_MESSAGE;
	}

	public static String getCommitMessageShort(){
		return COMMIT_MESSAGE_SHORT;
	}

	public static String getCommitTime(){
		return COMMIT_TIME;
	}

}

