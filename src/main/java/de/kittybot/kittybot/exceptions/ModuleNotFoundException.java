package de.kittybot.kittybot.exceptions;

import de.kittybot.kittybot.module.Module;

public class ModuleNotFoundException extends RuntimeException{

	public <T extends Module> ModuleNotFoundException(Class<T> clazz){
		super("Module '" + clazz.getName() + "' not found");
	}

}
