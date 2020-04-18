package de.anteiku.kittybot.events;

import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.Instant;

public class OnGuildJoinEvent extends ListenerAdapter{
	
	
	private final KittyBot main;
	
	public OnGuildJoinEvent(KittyBot main){
		this.main = main;
	}
	
	@Override
	public void onGuildJoin(GuildJoinEvent event){
		for (AuditLogEntry entry : event.getGuild().retrieveAuditLogs().cache(false)){
			if (entry.getType() == ActionType.BOT_ADD && entry.getTargetId().equals(event.getJDA().getSelfUser().getId())){
				String introductionMessage = "To get started you maybe want to set up some self assignable roles. This can be done with `.roles add @role :emote:`. You will need a emote for each role and they should be from your server!\n\n" +
                    "If you want to know my other commands just type ``.commands``.\n" +
					"To change my prefix use ``.options prefix <your wished prefix>``.\n" +
					"In case you forgot any command just type ``.cmds`` to get a full list off all my commands!\n\n" +
					"To report bugs/suggest features either join my [Support Server](https://discord.gg/sD3ABd5), add me on Discord ``Toπ#3141`` or message me on [Twitter](https://twitter.com/TopiSenpai)";
				MessageEmbed embed = new EmbedBuilder()
					.setTitle("Hellowo and thank your for adding me to your Discord Server!")
					.setDescription(introductionMessage)
					.setTimestamp(Instant.now())
					.setColor(new Color(76, 80, 193))
					.setThumbnail(event.getJDA().getSelfUser().getEffectiveAvatarUrl())
					.setFooter(event.getGuild().getName(), event.getGuild().getIconUrl())
					.build();
				entry.getUser().openPrivateChannel().queue(
					privateChannel -> privateChannel.sendMessage(embed).queue(
						null,//this should work if the first message got sent,
						failure -> event.getGuild().getDefaultChannel().sendMessage(embed).queue()//if this fails fuck it :)
					)
				);
				return;//just want to catch the last add loool
			}
		}
	}
	
}
