package de.kittybot.kittybot.command;

import java.util.List;
import java.util.stream.Stream;

public class Args{

	private final List<String> arguments;

	public Args(String[] args){
		this.arguments = List.of(args);
	}

	public Args(List<String> args){
		this.arguments = args;
	}

	public int size(){
		return this.arguments.size();
	}

	public boolean isEmpty(){
		return this.arguments.isEmpty();
	}

	public boolean isEnable(int i){
		return is(i, "enable") || is(i, "true") || is(i, "on") || is(i, "an");
	}

	public boolean is(int i, String arg){
		return this.arguments.get(i).equalsIgnoreCase(arg);
	}

	public boolean isDisable(int i){
		return is(i, "disable") || is(i, "false") || is(i, "off") || is(i, "aus");
	}

	public boolean isHelp(int i){
		return is(i, "?") || is(i, "help") || is(i, "hilfe");
	}

	public String get(int i){
		return this.arguments.get(i);
	}

	public List<String> getList(){
		return this.arguments;
	}

	public Args subArgs(){
		return new Args(this.arguments.subList(1, this.arguments.size()));
	}

	public List<String> subList(int from, int to){
		return this.arguments.subList(from, to);
	}

	public Stream<String> stream(){
		return this.arguments.stream();
	}

}
