package de.anteiku.kittybot.tasks;

import de.anteiku.kittybot.KittyBot;

import java.util.concurrent.TimeUnit;

public class GuildUpdateTask extends Task{
	
	public static String NAME = "SubscribeTask";
	public static long SLEEP = 1;
	public static TimeUnit TIMEUNIT = TimeUnit.DAYS;
	
	public GuildUpdateTask(KittyBot main){
		super(main, NAME, SLEEP, TIMEUNIT);
	}
	
	@Override
	void task(){
	
	}
	
}
