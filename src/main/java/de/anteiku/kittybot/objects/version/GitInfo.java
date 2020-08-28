package de.anteiku.kittybot.objects.version;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Properties;

public class GitInfo{

	private static final Logger LOG = LoggerFactory.getLogger(GitInfo.class);
	private static final String branch;
	private static final String commitId;
	private static final String commitIdAbbrev;
	private static final String commitUserName;
	private static final String commitUserEmail;
	private static final String commitMessageFull;
	private static final String commitMessageShort;
	private static final String commitTime;
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

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
		branch = String.valueOf(prop.getOrDefault("git.branch", ""));
		commitId = String.valueOf(prop.getOrDefault("git.commit.id", ""));
		commitIdAbbrev = String.valueOf(prop.getOrDefault("git.commit.id.abbrev", ""));
		commitUserName = String.valueOf(prop.getOrDefault("git.commit.user.name", ""));
		commitUserEmail = String.valueOf(prop.getOrDefault("git.commit.user.email", ""));
		commitMessageFull = String.valueOf(prop.getOrDefault("git.commit.message.full", ""));
		commitMessageShort = String.valueOf(prop.getOrDefault("git.commit.message.short", ""));

		final String time = String.valueOf(prop.get("git.commit.time"));
		if(time == null || time.equals("null")) {
			commitTime = "Unofficial";
		}
		else {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
			commitTime = DATE_FORMAT.format(new Date(Instant.from(dtf.parse(time)).toEpochMilli()));
		}
	}

	public static String getBranch(){
		return branch;
	}

	public static String getShortCommitId(){
		return commitId.substring(0, 7);
	}

	public static String getCommitId(){
		return commitId;
	}

	public static String getCommitIdAbbrev(){
		return commitIdAbbrev;
	}

	public static String getCommitUserName(){
		return commitUserName;
	}

	public static String getCommitUserEmail(){
		return commitUserEmail;
	}

	public static String getCommitMessageFull(){
		return commitMessageFull;
	}

	public static String getCommitMessageShort(){
		return commitMessageShort;
	}

	public static String getCommitTime(){
		return commitTime;
	}
	
}

