package de.kittybot.kittybot.utils;

import de.kittybot.kittybot.objects.Placeholder;

public class PlaceholderUtils{

	public static String replacePlaceholders(String message, Placeholder... placeholders){
		for(var placeholder : placeholders){
			message = message.replace(placeholder.getName(), placeholder.getValue());
		}
		return message;
	}

}
