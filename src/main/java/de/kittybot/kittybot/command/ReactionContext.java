package de.kittybot.kittybot.command;

import de.kittybot.kittybot.command.ReactiveMessage;
import de.kittybot.kittybot.managers.CommandManager;
import de.kittybot.kittybot.managers.ReactiveMessageManager;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

public class ReactionContext{

	private final GuildMessageReactionAddEvent event;
	private final CommandManager commandManager;
	private final ReactiveMessageManager reactiveMessageManager;
	private final ReactiveMessage reactiveMessage;

	public ReactionContext(GuildMessageReactionAddEvent event, CommandManager commandManager, ReactiveMessageManager reactiveMessageManager, ReactiveMessage reactiveMessage){
		this.reactiveMessage = reactiveMessage;
		this.commandManager = commandManager;
		this.reactiveMessageManager = reactiveMessageManager;
		this.event = event;
	}

	public GuildMessageReactionAddEvent getEvent(){
		return this.event;
	}

	public CommandManager getCommandManager(){
		return this.commandManager;
	}

	public ReactiveMessageManager getReactiveMessageManager(){
		return this.reactiveMessageManager;
	}

	public ReactiveMessage getReactiveMessage(){
		return this.reactiveMessage;
	}

}
