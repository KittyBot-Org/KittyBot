package de.kittybot.kittybot.objects.exceptions;

public class OptionParseException extends RuntimeException{

	public OptionParseException(String message){
		super(message, null, false, false);
	}

}
