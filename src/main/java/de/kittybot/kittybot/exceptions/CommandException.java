package de.kittybot.kittybot.exceptions;

public class CommandException extends Exception{

	public CommandException(String message){
		super(message);
	}

	public CommandException(Exception e){
		super(e);
	}

}
