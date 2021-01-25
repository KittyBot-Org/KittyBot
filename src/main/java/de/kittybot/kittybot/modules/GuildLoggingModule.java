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
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.Set;

@SuppressWarnings("unused")
public class GuildLoggingModule extends Module{

	private static final Set<Class<? extends Module>> DEPENDENCIES = Set.of(InviteModule.class, MessageModule.class);

	@Override
	public Set<Class<? extends Module>> getDependencies(){
		return DEPENDENCIES;
	}

	@Override
	public void onGuildMessageUpdate(@Nonnull GuildMessageUpdateEvent event){
		var settings = this.modules.get(SettingsModule.class).getSettings(event.getGuild().getIdLong());
		if(!settings.areLogMessagesEnabled()){
			return;
		}
		logEvent(event, Color.RED, null, "Message edit", "");
	}

	@Override
	public void onGuildMessageDelete(@Nonnull GuildMessageDeleteEvent event){
		var settings = this.modules.get(SettingsModule.class).getSettings(event.getGuild().getIdLong());
		if(!settings.areLogMessagesEnabled()){
			return;
		}
		logEvent(event, Color.RED, null, "Message delete", "");
	}

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
				logEvent(event, Color.BLUE, user, "Bot remove", "{0} {1} has `removed` bot {2}({3}) from this server",
					Emoji.ROBOT.get(),
					name,
					user.getAsTag(),
					userId
				);
			});
			return;
		}
		logEvent(event, Color.GREEN, user, "User leave", "{0} **{1}** ({2}) has `left` the server",
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
				logEvent(event, Color.BLUE, user, "Bot add", "{0} {1} has `added` bot {2}({3}) to this server",
					Emoji.ROBOT.get(),
					name,
					user.getAsTag(),
					userId
				);
			});
			return;
		}
		var invite = this.modules.get(InviteModule.class).getUsedInvite(event.getGuild().getIdLong(), userId);
		logEvent(event, Color.GREEN, user, "User join", "{0} **{1}** ({2}) has `joined` the server with invite **https://discord.gg/{3}**(**{4}**)",
			Emoji.INBOX_TRAY.get(),
			user.getAsTag(),
			userId,
			invite.getCode(),
			MessageUtils.getUserMention(invite.getUserId())
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
			.setDescription(MessageFormat.format(message, args))
			.setFooter(eventName, user == null ? "" : user.getEffectiveAvatarUrl())
			.setTimestamp(Instant.now())
			.build()
		).queue();
	}

}
