package de.anteiku.kittybot.events;

import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

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
		Long messageId = event.getMessageIdLong();
		
		if((main.commandManager.controllableMsgs.containsKey(messageId)) && ((main.commandManager.msgCtrl.get(messageId) == - 1L) || (main.commandManager.msgCtrl.get(messageId) == event.getUser().getIdLong()))){
			main.commandManager.controllableMsgs.get(messageId).reactionAdd(event.getChannel().getMessageById(main.commandManager.commandMessages.get(messageId)).complete(), event);
		}
	}
	
}
