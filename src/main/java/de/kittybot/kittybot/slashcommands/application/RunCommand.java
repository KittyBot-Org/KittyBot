package de.kittybot.kittybot.slashcommands.application;

public abstract class RunCommand extends Command implements RunnableCommand{

	protected RunCommand(String name, String description, Category category){
		super(name, description, category);
	}

}
