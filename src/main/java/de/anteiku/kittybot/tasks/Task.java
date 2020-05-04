package de.anteiku.kittybot.tasks;

import de.anteiku.kittybot.KittyBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public abstract class Task implements Runnable{
	
	protected static final Logger LOG = LoggerFactory.getLogger(Task.class);
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
		LOG.info("Running task '{}'!", name);
		task();
		LOG.info("Task '{}' finished! took {}ms", name, (System.currentTimeMillis() - start) / 1000);
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
