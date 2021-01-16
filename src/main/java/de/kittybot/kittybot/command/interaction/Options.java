package de.kittybot.kittybot.command.interaction;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Options{

	private final Map<String, InteractionDataOption> options;

	public Options(List<InteractionDataOption> options){
		this.options = options.stream().collect(Collectors.toMap(InteractionDataOption::getName, Function.identity()));
	}

	public int size(){
		return this.options.size();
	}

	public boolean isEmpty(){
		return this.options.isEmpty();
	}

	public <T> boolean is(String name, T value){
		var option = this.options.get(name);
		return option.getValue().equals(value);
	}

	public boolean has(String name){
		return this.options.containsKey(name);
	}

	public long getLong(String name){
		return this.options.get(name).getLong();
	}

	public boolean getBoolean(String name){
		return this.options.get(name).getBoolean();
	}

	public String getString(String name){
		return this.options.get(name).getString();
	}

	public InteractionDataOption get(String name){
		return this.options.get(name);
	}

	public Map<String, InteractionDataOption> getMap(){
		return this.options;
	}

	public Stream<InteractionDataOption> stream(){
		return this.options.values().stream();
	}

}
