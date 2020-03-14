package de.anteiku.kittybot.tasks;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.utils.API;
import de.anteiku.kittybot.utils.Logger;

public abstract class Task extends Thread{
	
	protected KittyBot main;
	protected String name;
	protected long sleep;
	protected boolean running = true;
	
	protected Task(KittyBot main, String name, long sleep){
		this.main = main;
		this.name = name;
		this.sleep = sleep;
		start();
	}
	
	@Override
	public void run(){
		while(running){
			long start = System.nanoTime();
			Logger.debug("Running task '" + name + "'!");
			task();
			Logger.debug("Task '" + name + "' finished! took '" + API.getMs(start) + "'ms");
			try{
				sleep(sleep);
			}
			catch(InterruptedException e){
				Logger.error(e);
			}
		}
	}
	
	abstract void task();
	
	public String getTaskName(){
		return name;
	}
	
}
