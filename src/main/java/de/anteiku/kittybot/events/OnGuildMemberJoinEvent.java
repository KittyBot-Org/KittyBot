package de.anteiku.kittybot.events;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.messages.Messages;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class OnGuildMemberJoinEvent extends ListenerAdapter{
	
	private KittyBot main;
	
	public OnGuildMemberJoinEvent(KittyBot main){
		this.main = main;
	}
	
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event){
		String id = main.database.getWelcomeChannelId(event.getGuild().getId());
		if(!id.equals("-1") || main.database.getWelcomeMessageEnabled(event.getGuild().getId())){
			TextChannel channel = event.getGuild().getTextChannelById(id);
			if(channel != null){
				channel.sendMessage(Messages.generateJoinMessage(main.database.getWelcomeMessage(event.getGuild().getId()), event.getUser())).queue();
			}
		}
	}
	
}
