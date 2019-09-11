package de.anteiku.kittybot.tasks;

import de.anteiku.kittybot.KittyBot;

public class SubscribeTask extends Task{
	
	public static String NAME = "SubscribeTask";
	public static long SLEEP = 30000;
	
	public SubscribeTask(KittyBot main){
		super(main, NAME, SLEEP);
	}
	
	@Override
	void task(){
	
	}
	
}
