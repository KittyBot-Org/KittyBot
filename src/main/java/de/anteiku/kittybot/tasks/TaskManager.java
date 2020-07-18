package de.anteiku.kittybot.tasks;

import de.anteiku.kittybot.KittyBot;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class TaskManager{

	private final KittyBot main;
	private final ScheduledExecutorService ses;

	private final Map<String, Task> tasks;
	private final Map<String, ScheduledFuture<?>> runningTasks;

	public TaskManager(KittyBot main){
		this.main = main;
		tasks = new LinkedHashMap<>();
		runningTasks = new LinkedHashMap<>();
		ses = Executors.newScheduledThreadPool(1);
	}

	public void addTask(Task task){
		tasks.put(task.getName(), task);
	}

	public void startAll(){
		for(Map.Entry<String, Task> t : tasks.entrySet()){
			Task task = t.getValue();
			runningTasks.put(t.getKey(), ses.scheduleAtFixedRate(task, 0, task.getDelay(), task.getTimeUnit()));
		}
	}

	public void stop(String task, boolean interrupt){
		runningTasks.get(task).cancel(interrupt);
	}

	public void stopAll(boolean interrupt){
		for(Map.Entry<String, ScheduledFuture<?>> t : runningTasks.entrySet()){
			t.getValue().cancel(interrupt);
		}
	}

}
