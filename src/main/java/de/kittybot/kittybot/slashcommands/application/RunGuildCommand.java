package de.kittybot.kittybot.slashcommands.application;

public abstract class RunGuildCommand extends Command implements RunnableGuildCommand{

	public RunGuildCommand(String name, String description, Category category){
		super(name, description, category);
	}
}
