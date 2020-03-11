package de.anteiku.kittybot.tasks;

import de.anteiku.kittybot.KittyBot;

import java.util.HashMap;
import java.util.Map;

public class TaskManager{
	
	private KittyBot main;
	
	private Map<String, Task> tasks;
	
	public TaskManager(KittyBot main){
		this.main = main;
		init();
	}
	
	private void init(){
		tasks = new HashMap<>();
	}
	
	public void startAll(){
		for(Map.Entry<String, Task> t : tasks.entrySet()){
			t.getValue().start();
		}
	}
	
	public void stopAll(){
		for(Map.Entry<String, Task> t : tasks.entrySet()){
			t.getValue().start();
		}
	}
	
	public void registerTask(Task task){
		tasks.put(task.getTaskName(), task);
	}
	
	public void unregisterTask(Task task){
		tasks.remove(task);
	}
	
	public void unregisterTask(String task){
		tasks.remove(task);
	}
	
	
}
