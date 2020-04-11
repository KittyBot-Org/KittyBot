package de.anteiku.kittybot.tasks;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.utils.Logger;

import java.util.concurrent.TimeUnit;

public abstract class Task implements Runnable{
	
	protected KittyBot main;
	protected String name;
	protected long delay;
	protected TimeUnit timeUnit;
	
	protected Task(KittyBot main, String name, long sleep, TimeUnit timeUnit){
		this.main = main;
		this.name = name;
		this.delay = sleep;
		this.timeUnit = timeUnit;
	}
	
	@Override
	public void run(){
		long start = System.currentTimeMillis();
		Logger.debug("Running task '" + name + "'!");
		task();
		Logger.debug("Task '" + name + "' finished! took '" + (System.currentTimeMillis() - start) / 1000 + "'ms");
	}
	
	abstract void task();
	
	public void stop(){
		main.taskManager.stop(name, false);
	}
	
	public String getName(){
		return name;
	}
	
	public long getDelay(){
		return delay;
	}
	
	public TimeUnit getTimeUnit(){
		return timeUnit;
	}
	
}
