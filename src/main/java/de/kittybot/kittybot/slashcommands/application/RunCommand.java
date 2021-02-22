package de.kittybot.kittybot.slashcommands.application;

public abstract class RunCommand extends Command implements RunnableCommand{

	public RunCommand(String name, String description, Category category){
		super(name, description, category);
	}

}
