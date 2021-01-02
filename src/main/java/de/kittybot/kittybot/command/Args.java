package de.kittybot.kittybot.command;

import java.util.*;

public class Args{

	private final List<String> args;

	public Args(List<String> args){
		this.args = args;
	}

	public boolean is(int i, String arg){
		return this.args.get(i).equalsIgnoreCase(arg);
	}

	public int size(){
		return this.args.size();
	}

	public boolean isEmpty(){
		return this.args.isEmpty();
	}

	public boolean isEnable(int i){
		return is(i, "enable") || is(i, "true") || is(i, "on") || is(i, "an");
	}

	public boolean isDisable(int i){
		return is(i, "disable") || is(i, "false") || is(i, "off") || is(i, "aus");
	}

	public boolean isHelp(int i){
		return is(i, "?") || is(i, "help") || is(i, "hilfe");
	}

	public String get(int i){
		return this.args.get(i);
	}

	public List<String> getList(){
		return this.args;
	}

	public Args subArgs(){
		return new Args(this.args.subList(1, this.args.size()));
	}

	public List<String> subList(int from, int to){
		return this.args.subList(from, to);
	}

}
