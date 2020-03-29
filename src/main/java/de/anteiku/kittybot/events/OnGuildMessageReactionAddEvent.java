package de.anteiku.kittybot.events;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.utils.ReactiveMessage;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class OnGuildMessageReactionAddEvent extends ListenerAdapter{
	
	private KittyBot main;
	
	public OnGuildMessageReactionAddEvent(KittyBot main){
		this.main = main;
	}
	
	@Override
	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event){
		if(event.getMember().getUser().isBot() || event.getMember().getUser().isFake()){
			return;
		}
		
		ReactiveMessage reactiveMessage = main.commandManager.getReactiveMessage(event.getGuild(), event.getMessageId());
		if(reactiveMessage != null && (reactiveMessage.userId.equals("-1") || reactiveMessage.userId.equals(event.getUserId()))){
			main.commandManager.commands.get(reactiveMessage.command).reactionAdd(event.getChannel().retrieveMessageById(reactiveMessage.commandId).complete(), event);
		}
	}
	
}
