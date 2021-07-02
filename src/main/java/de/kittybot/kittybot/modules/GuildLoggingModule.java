package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.objects.enums.Emoji;
import de.kittybot.kittybot.objects.module.Module;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.Set;

@SuppressWarnings("unused")
public class GuildLoggingModule extends Module{

	@Override
	public Set<Class<? extends Module>> getDependencies(){
		return Set.of(InviteModule.class, MessageModule.class);
	}

	/*@Override
	public void onGuildMessageUpdate(@Nonnull GuildMessageUpdateEvent event){
		var settings = this.modules.get(SettingsModule.class).getSettings(event.getGuild().getIdLong());
		if(!settings.areLogMessagesEnabled()){
			return;
		}
		logEvent(event, Color.RED, event.getAuthor(), "Message edit", "");
	}

	@Override
	public void onGuildMessageDelete(@Nonnull GuildMessageDeleteEvent event){
		var settings = this.modules.get(SettingsModule.class).getSettings(event.getGuild().getIdLong());
		if(!settings.areLogMessagesEnabled()){
			return;
		}
		logEvent(event, Color.RED, null, "Message delete", "");
	}*/

	@Override
	public void onGuildMemberRemove(@Nonnull GuildMemberRemoveEvent event){
		var settings = this.modules.get(SettingsModule.class).getSettings(event.getGuild().getIdLong());
		if(!settings.areLogMessagesEnabled()){
			return;
		}
		var user = event.getUser();
		var userId = user.getIdLong();
		if(user.isBot()){
			event.getGuild().retrieveAuditLogs().type(ActionType.BOT_ADD).limit(5).cache(false).queue(entries -> {
				var audit = entries.stream().filter(entry -> entry.getTargetIdLong() == Config.BOT_ID).findFirst();
				var name = "unknown";
				if(audit.isPresent()){
					var auditUser = audit.get().getUser();
					if(auditUser != null){
						name = auditUser.getAsTag();
					}
				}
				logEvent(event, Color.BLUE, user, "Bot remove", "%s `%s` has `removed` bot %s(`%d`) from this server",
					Emoji.ROBOT.get(),
					name,
					user.getAsTag(),
					userId
				);
			});
			return;
		}
		logEvent(event, Color.GREEN, user, "User leave", "%s `%s`(`%d`) has `left` the server",
			Emoji.OUTBOX_TRAY.get(),
			user.getAsTag(),
			userId
		);
	}

	@Override
	public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event){
		var settings = this.modules.get(SettingsModule.class).getSettings(event.getGuild().getIdLong());
		if(!settings.areLogMessagesEnabled()){
			return;
		}
		var user = event.getUser();
		var userId = user.getIdLong();
		if(user.isBot()){
			event.getGuild().retrieveAuditLogs().type(ActionType.BOT_ADD).limit(5).cache(false).queue(entries -> {
				var audit = entries.stream().filter(entry -> entry.getTargetIdLong() == Config.BOT_ID).findFirst();
				var name = "unknown";
				if(audit.isPresent()){
					var auditUser = audit.get().getUser();
					if(auditUser != null){
						name = auditUser.getAsTag();
					}
				}
				logEvent(event, Color.BLUE, user, "Bot add", "%s **%s** has `added` bot %s(`%d`) to this server",
					Emoji.ROBOT.get(),
					name,
					user.getAsTag(),
					userId
				);
			});
			return;
		}
		var invite = this.modules.get(InviteModule.class).getUsedInvite(event.getGuild().getIdLong(), userId);
		logEvent(event, Color.GREEN, user, "User join", "%s **%s**(`%d`) has `joined` the server with invite `%s`(%s)",
			Emoji.INBOX_TRAY.get(),
			user.getAsTag(),
			userId,
			invite == null ? "unknown" : "https://discord.gg/" + invite.getCode(),
			invite == null ? "unknown" : MessageUtils.getUserMention(invite.getUserId())
		);
	}

	private void logEvent(GenericGuildEvent event, Color color, User user, String eventName, String message, Object... args){
		var settings = this.modules.get(SettingsModule.class).getSettings(event.getGuild().getIdLong());
		var guild = event.getGuild();
		var channel = guild.getTextChannelById(settings.getLogChannelId());
		if(channel == null){
			return;
		}
		channel.sendMessage(new EmbedBuilder()
			.setColor(color)
			.setDescription(String.format(message, args))
			.setFooter(eventName, user == null ? null : user.getEffectiveAvatarUrl())
			.setTimestamp(Instant.now())
			.build()
		).queue();
	}

}
