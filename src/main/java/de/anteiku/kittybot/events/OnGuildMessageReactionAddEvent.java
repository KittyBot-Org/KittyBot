package de.anteiku.kittybot.events;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.objects.ReactiveMessage;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class OnGuildMessageReactionAddEvent extends ListenerAdapter{
	
	private final KittyBot main;
	
	public OnGuildMessageReactionAddEvent(KittyBot main){
		this.main = main;
	}
	
	@Override
	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event){
		if(event.getMember().getUser().isBot() || event.getMember().getUser().isFake()){
			return;
		}
		
		ReactiveMessage reactiveMessage = main.commandManager.getReactiveMessage(event.getGuild(), event.getMessageId());
		if(reactiveMessage != null){
			if(reactiveMessage.allowed.equals("-1") || reactiveMessage.allowed.equals(event.getUserId())){
				main.commandManager.commands.get(reactiveMessage.command).reactionAdd(reactiveMessage, event);
			}
			else{
				event.getReaction().removeReaction(event.getUser()).queue();
			}
		}
	}
	
}
