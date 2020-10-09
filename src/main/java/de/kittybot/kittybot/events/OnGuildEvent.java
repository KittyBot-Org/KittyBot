package de.kittybot.kittybot.events;

import de.kittybot.kittybot.KittyBot;
import de.kittybot.kittybot.cache.*;
import de.kittybot.kittybot.database.Database;
import de.kittybot.kittybot.objects.BotLists;
import de.kittybot.kittybot.objects.guilds.GuildData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateIconEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdatePermissionsEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.Instant;

public class OnGuildEvent extends ListenerAdapter{

	@Override
	public void onGuildJoin(GuildJoinEvent event){
		var guildCount = (int) event.getJDA().getGuildCache().size();
		var guild = event.getGuild();
		BotLists.update(guildCount);
		Database.registerGuild(guild);
		InviteCache.initCaching(guild);
		var owner = guild.getOwner();
		KittyBot.sendToPublicLogChannel(String.format("Hellowo I joined the guild: ``%s``%s``%d`` members!%nCurrently I'm in %d guilds!", guild.getName(), owner == null ? " " : " with owner: ``" + owner
				.getUser()
				.getAsTag() + "`` and ", guild.getMemberCount(), guildCount));
		if(!event.getGuild().getSelfMember().hasPermission(Permission.VIEW_AUDIT_LOGS)){
			return;
		}
		guild.retrieveAuditLogs().type(ActionType.BOT_ADD).limit(1).cache(false).queue(entries -> {
			var entry = entries.get(0);
			if(!entry.getTargetId().equals(event.getJDA().getSelfUser().getId())){
				return;
			}
			var user = entry.getUser();
			if(user == null){
				return;
			}
			var embed = new EmbedBuilder().setTitle("Hellowo and thank your for adding me to your Discord Server!")
					.setDescription("To get started you maybe want to set up some self assignable roles. This can be done with `.roles add @role :emote:`. You will need a emote for each role and they should be from your server!\n\n"
							+ "If you want to know my other commands just type ``.commands``.\n"
							+ "To change my prefix use ``.options prefix <your wished prefix>``.\n" + "In case you forgot any command just type ``.cmds`` to get a full list of all my commands!\n"
							+ "You can also setup all this stuff via the webinterface at https://kittybot.de\n\n"
							+ "To report bugs/suggest features either join my [Support Server](https://discord.gg/sD3ABd5), add me on Discord ``toπ#3141`` or message me on [Twitter](https://twitter.com/TopiSenpai)")
					.setColor(new Color(76, 80, 193))
					.setThumbnail(event.getJDA().getSelfUser().getEffectiveAvatarUrl())
					.setFooter(guild.getName(), guild.getIconUrl())
					.setTimestamp(Instant.now())
					.build();
			var defaultChannel = guild.getDefaultChannel();
			var messageRestAction = user.openPrivateChannel().flatMap(channel -> channel.sendMessage(embed));
			if(defaultChannel != null && defaultChannel.canTalk()){
				messageRestAction = messageRestAction.onErrorFlatMap(ignored -> defaultChannel.sendMessage(embed));
			}
			messageRestAction.queue();
		});

		var guildId = guild.getId();
		GuildCache.cacheGuild(guildId, new GuildData(guildId, guild.getName(), guild.getIconUrl()));
		guild.findMembers(member -> member.hasPermission(Permission.ADMINISTRATOR))
				.onSuccess(members -> members.forEach(member -> GuildCache.cacheGuildForUser(member.getId(), guildId)));
	}

	@Override
	public void onGuildLeave(GuildLeaveEvent event){
		var guildCount = (int) event.getJDA().getGuildCache().size();
		var guild = event.getGuild();
		BotLists.update(guildCount);
		InviteCache.pruneCache(guild);
		PrefixCache.pruneCache(guild);
		MusicPlayerCache.pruneCache(guild);
		ReactiveMessageCache.pruneCache(guild);
		CommandResponseCache.pruneCache(guild);
		SelfAssignableRoleCache.pruneCache(guild);
		MessageCache.pruneCache(guild);
		GuildCache.uncacheGuild(guild);
		KittyBot.sendToPublicLogChannel(String.format("Helluwu I got kicked from the guild: ``%s``%nCurrently I'm in %d guilds!", guild.getName(), guildCount));
	}

	@Override
	public void onGuildUpdateIcon(final GuildUpdateIconEvent event){
		var guild = event.getGuild();
		var guildId = guild.getId();
		GuildCache.cacheGuild(guildId, new GuildData(guildId, guild.getName(), event.getNewIconUrl()), false);
	}

	@Override
	public void onGuildUpdateName(final GuildUpdateNameEvent event){
		var guild = event.getGuild();
		var guildId = guild.getId();
		GuildCache.cacheGuild(guildId, new GuildData(guildId, event.getNewName(), guild.getIconUrl()), false);
	}

	@Override
	public void onRoleUpdatePermissions(final RoleUpdatePermissionsEvent event){
		var add = event.getNewPermissions().contains(Permission.ADMINISTRATOR) && !event.getOldPermissions().contains(Permission.ADMINISTRATOR);
		var remove = !event.getNewPermissions().contains(Permission.ADMINISTRATOR) && event.getOldPermissions().contains(Permission.ADMINISTRATOR);
		if(!add && !remove){
			return;
		}
		event.getGuild().findMembers(member -> member.getRoles().contains(event.getRole()) && DashboardSessionCache.hasSession(member.getId()))
				.onSuccess(members -> members.forEach(member -> {
					var userId = member.getId();
					var guildId = event.getGuild().getId();
					if(add){
						GuildCache.cacheGuildForUser(userId, guildId);
					}
					else{
						GuildCache.uncacheGuildForUser(userId, guildId);
					}
				}));
	}

}
