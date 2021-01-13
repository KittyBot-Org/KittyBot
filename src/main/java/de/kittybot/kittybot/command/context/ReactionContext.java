package de.kittybot.kittybot.command.context;

import de.kittybot.kittybot.command.ReactiveMessage;
import de.kittybot.kittybot.module.Modules;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

public class ReactionContext extends Context{

	private final GuildMessageReactionAddEvent event;
	private final ReactiveMessage reactiveMessage;

	public ReactionContext(GuildMessageReactionAddEvent event, Modules modules, ReactiveMessage reactiveMessage){
		super(modules, event.getGuild());
		this.reactiveMessage = reactiveMessage;
		this.event = event;
	}

	public GuildMessageReactionAddEvent getEvent(){
		return this.event;
	}

	public ReactiveMessage getReactiveMessage(){
		return this.reactiveMessage;
	}

}
