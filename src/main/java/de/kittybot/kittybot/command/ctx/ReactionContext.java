package de.kittybot.kittybot.command.ctx;

import de.kittybot.kittybot.command.ReactiveMessage;
import de.kittybot.kittybot.managers.CommandManager;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

public class ReactionContext{

	private final GuildMessageReactionAddEvent event;
	private final CommandManager commandManager;
	private final ReactiveMessage reactiveMessage;

	public ReactionContext(GuildMessageReactionAddEvent event, CommandManager commandManager, ReactiveMessage reactiveMessage){
		this.reactiveMessage = reactiveMessage;
		this.commandManager = commandManager;
		this.event = event;
	}

	public GuildMessageReactionAddEvent getEvent(){
		return this.event;
	}

	public CommandManager getCommandManager(){
		return this.commandManager;
	}

	public ReactiveMessage getReactiveMessage(){
		return this.reactiveMessage;
	}

}
