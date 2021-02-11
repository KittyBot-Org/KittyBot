package de.kittybot.kittybot.objects.exceptions;

public class MissingOptionException extends RuntimeException{

	public MissingOptionException(String name, Class<?> clazz){
		super("Option `" + name + "` of type `" + clazz.getSimpleName() + "` is missing");
	}

}
