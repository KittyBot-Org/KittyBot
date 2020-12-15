package de.kittybot.kittybot.exceptions;

import java.util.Set;

public class MissingConfigValuesException extends Exception{

	public MissingConfigValuesException(Set<String> missingKeys){
		super("Following config keys are missing: \"" + String.join(", ", missingKeys) + "\"");
	}

}
