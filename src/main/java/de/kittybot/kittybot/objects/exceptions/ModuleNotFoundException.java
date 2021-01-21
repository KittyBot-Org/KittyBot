package de.kittybot.kittybot.objects.exceptions;

import de.kittybot.kittybot.objects.module.Module;

public class ModuleNotFoundException extends RuntimeException{

	public <T extends Module> ModuleNotFoundException(Class<T> clazz){
		super("Module '" + clazz.getName() + "' not found");
	}

}
