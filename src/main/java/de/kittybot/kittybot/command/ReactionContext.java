package de.kittybot.kittybot.command;

import de.kittybot.kittybot.modules.CommandModule;
import de.kittybot.kittybot.modules.ReactiveMessageModule;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

public class ReactionContext{

	private final GuildMessageReactionAddEvent event;
	private final CommandModule commandModule;
	private final ReactiveMessageModule reactiveMessageModule;
	private final ReactiveMessage reactiveMessage;

	public ReactionContext(GuildMessageReactionAddEvent event, CommandModule commandModule, ReactiveMessageModule reactiveMessageModule, ReactiveMessage reactiveMessage){
		this.reactiveMessage = reactiveMessage;
		this.commandModule = commandModule;
		this.reactiveMessageModule = reactiveMessageModule;
		this.event = event;
	}

	public GuildMessageReactionAddEvent getEvent(){
		return this.event;
	}

	public CommandModule getCommandModule(){
		return this.commandModule;
	}

	public ReactiveMessageModule getReactiveMessageModule(){
		return this.reactiveMessageModule;
	}

	public ReactiveMessage getReactiveMessage(){
		return this.reactiveMessage;
	}

}
