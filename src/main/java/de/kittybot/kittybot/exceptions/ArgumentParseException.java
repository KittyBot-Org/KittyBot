package de.kittybot.kittybot.exceptions;

public class ArgumentParseException extends Exception{

	public ArgumentParseException(String arg, Class<?> tClass){
		super("Failed to parse '" + arg + "' to type '" + tClass.getName() + "'");
	}

}
