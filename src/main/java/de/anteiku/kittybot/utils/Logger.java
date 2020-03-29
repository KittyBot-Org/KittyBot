package de.anteiku.kittybot.utils;

import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.api.entities.PrivateChannel;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class Logger{
	
	private static PrintStream errorStream;
	private static boolean DEBUG = false;
	private static SimpleDateFormat sdf;
	private static KittyBot main;
	
	public Logger(KittyBot main){
		this.main = main;
		sdf = new SimpleDateFormat("HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+2"));
		LocalDateTime date = LocalDateTime.now();
		String errorLogName = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss"));
		File logDir = new File("logs");
		if(logDir.mkdir()){
			System.out.println("Created Error Log Dir: '" + logDir.getAbsolutePath() + "'");
		}
		File errorFile = new File(logDir, "bot-log-" + errorLogName + ".log");
		if(!errorFile.exists()){
			try{
				if(!errorFile.createNewFile()){
					System.out.println("Error creating Log File: '" + errorFile.getAbsolutePath() + "'");
				}
			}
			catch(IOException e){
				e.printStackTrace();
				System.out.println("Error creating Log File: '" + errorFile.getAbsolutePath() + "'");
				System.exit(0);
			}
		}
		try{
			errorStream = new PrintStream(new FileOutputStream(errorFile));
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
			System.out.println("Error accessing Log File: '" + errorFile.getAbsolutePath() + "'");
			System.exit(0);
		}
	}
	
	public static void print(String string){
		String time = "[" + sdf.format(new Date()) + "] ";
		System.out.println(time + string);
		errorStream.println(time + string);
		errorStream.flush();
	}
	
	public static void error(Exception e){
		e.printStackTrace(errorStream);
		e.printStackTrace();
		errorStream.flush();
		
		if(main.jda != null) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String message = "New Error received:\n" +
                 "```css\n" +
                 sw.toString();
			if(message.length() > 1993) {
				message = message.substring(0, 1993) + "...";
			}
			message += "\n```";
			PrivateChannel channel = main.jda.getUserById(main.config.get("admin_discord_id")).openPrivateChannel().complete();
			channel.sendMessage(message).queue();
		}
	}
	
	public static void debug(String string){
		String time = "[" + sdf.format(new Date()) + "]";
		if(DEBUG){
			System.out.println(time + string);
		}
		errorStream.println(time + string);
		errorStream.flush();
	}
	
	public static boolean getDebug(){
		return DEBUG;
	}
	
	public static void setDebug(boolean debug){
		DEBUG = debug;
	}
	
	public void close(){
		errorStream.flush();
		errorStream.close();
	}
	
}
