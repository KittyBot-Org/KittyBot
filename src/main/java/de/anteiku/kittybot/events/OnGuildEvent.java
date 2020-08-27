package de.anteiku.kittybot.events;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.database.Database;
import de.anteiku.kittybot.objects.BotLists;
import de.anteiku.kittybot.objects.cache.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.Instant;

public class OnGuildEvent extends ListenerAdapter{

	@Override
	public void onGuildJoin(GuildJoinEvent event){
		var guildCount = (int) event.getJDA().getGuildCache().size();
		var guild = event.getGuild();
		BotLists.update(event.getJDA(), guildCount);
		Database.registerGuild(guild);
		InviteCache.initCaching(guild);
		for(AuditLogEntry entry : guild.retrieveAuditLogs().cache(false)){
			if(entry.getType() == ActionType.BOT_ADD && entry.getTargetId().equals(event.getJDA().getSelfUser().getId())){
				MessageEmbed embed = new EmbedBuilder().setTitle("Hellowo and thank your for adding me to your Discord Server!")
						.setDescription("To get started you maybe want to set up some self assignable roles. This can be done with `.roles add @role :emote:`. You will need a emote for each role and they should be from your server!\n\n" + "If you want to know my other commands just type ``.commands``.\n" + "To change my prefix use ``.options prefix <your wished prefix>``.\n" + "In case you forgot any command just type ``.cmds`` to get a full list off all my commands!\n\n" + "To report bugs/suggest features either join my [Support Server](https://discord.gg/sD3ABd5), add me on Discord ``Toπ#3141`` or message me on [Twitter](https://twitter.com/TopiSenpai)")
						.setColor(new Color(76, 80, 193))
						.setThumbnail(event.getJDA().getSelfUser().getEffectiveAvatarUrl())
						.setFooter(guild.getName(), guild.getIconUrl())
						.setTimestamp(Instant.now())
						.build();
				var user = entry.getUser();
				if(user != null){
					user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(embed).queue(null,//this should work if the first message got sent,
							failure -> {
								var defaultChannel = guild.getDefaultChannel();
								if(defaultChannel != null){
									defaultChannel.sendMessage(embed).queue();
								}
							}));
				}
				return;//just want to catch the last add loool
			}
		}
		var owner = guild.getOwner();
		KittyBot.sendToPublicLogChannel(String.format("Hellowo I joined the guild: ``%s``%s``%d`` members!%nCurrently I'm in %d guilds!", guild.getName(), owner == null ? " " : " with owner: ``" + owner
				.getUser()
				.getAsTag() + "`` and ", guild.getMemberCount(), guildCount));
	}

	@Override
	public void onGuildLeave(GuildLeaveEvent event){
		var guildCount = (int) event.getJDA().getGuildCache().size();
		var guild = event.getGuild();
		BotLists.update(event.getJDA(), guildCount);
		InviteCache.pruneCache(guild);
		PrefixCache.pruneCache(guild);
		MusicPlayerCache.pruneCache(guild);
		ReactiveMessageCache.pruneCache(guild);
		CommandResponseCache.pruneCache(guild);
		SelfAssignableRoleCache.pruneCache(guild);
		KittyBot.sendToPublicLogChannel(String.format("Helluwu I got kicked from the guild: ``%s``%nCurrently I'm in %d guilds!", guild.getName(), guildCount));
	}

}
