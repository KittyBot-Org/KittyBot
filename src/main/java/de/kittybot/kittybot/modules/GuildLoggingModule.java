package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.module.Module;
import de.kittybot.kittybot.objects.Emoji;
import de.kittybot.kittybot.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
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

	private static final Set<Class<? extends Module>> DEPENDENCIES = Set.of(InviteModule.class);

	@Override
	public Set<Class<? extends Module>> getDependencies(){
		return DEPENDENCIES;
	}

	@Override
	public void onGuildMemberRemove(@Nonnull GuildMemberRemoveEvent event){

	}

	@Override
	public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event){
		if(event.getUser().isBot()){
			logEvent(event, Color.GREEN, "Bot add", "{0} {1} ({2}) has",
				Emoji.INBOX_TRAY.get(),
				event.getUser().getAsTag(),
				event.getUser().getIdLong());
			return;
		}
		var user = event.getUser();
		var userId = user.getIdLong();
		var invite = this.modules.get(InviteModule.class).getUsedInvite(event.getGuild().getIdLong(), userId);
		logEvent(event, Color.GREEN, "User join", "{0} **{1}** ({2}) has `joined` the server with invite **https://discord.gg/{3}**(**{4}**)",
			Emoji.INBOX_TRAY.get(),
			user.getAsTag(),
			userId,
			invite.getCode(),
			MessageUtils.getUserMention(invite.getUserId())
		);
	}

	private void logEvent(GenericGuildEvent event, Color color, String eventName, String message, Object... args){
		var guild = event.getGuild();
		var settings = this.modules.get(SettingsModule.class).getSettings(guild.getIdLong());
		if(!settings.areLogMessagesEnabled()){
			return;
		}
		var channel = guild.getTextChannelById(settings.getLogChannelId());
		if(channel == null){
			return;
		}
		channel.sendMessage(new EmbedBuilder()
			.setColor(color)
			.setDescription(MessageFormat.format(message, args))
			.setFooter(eventName)
			.setTimestamp(Instant.now())
			.build()
		).queue();
	}

}
