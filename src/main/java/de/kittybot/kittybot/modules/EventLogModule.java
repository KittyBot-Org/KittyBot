package de.kittybot.kittybot.modules;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.external.JDAWebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import de.kittybot.kittybot.module.Module;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.Config;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.*;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.time.Instant;

@SuppressWarnings("unused")
public class EventLogModule extends Module{

	private JDAWebhookClient webhookClient;

	@Override
	public void onEnable(){
		if(Config.LOG_WEBHOOK_URL.isBlank()){
			this.webhookClient = null;
			return;
		}
		this.webhookClient = new WebhookClientBuilder(Config.LOG_WEBHOOK_URL).buildJDA();
	}

	@Override
	public void onReady(@NotNull ReadyEvent event){
		sendShard(event, "Ready", "started");
	}

	@Override
	public void onResumed(@Nonnull ResumedEvent event){
		sendShard(event, "Resumed", "resumed");
	}

	@Override
	public void onReconnected(@Nonnull ReconnectedEvent event){
		sendShard(event, "Reconnected", "reconnected");
	}

	@Override
	public void onDisconnect(@NotNull DisconnectEvent event){
		sendShard(event, "Disconnect", "disconnected");
	}

	@Override
	public void onShutdown(@Nonnull ShutdownEvent event){
		sendShard(event, "Shutdown", "shutdown");
	}

	@Override
	public void onException(@Nonnull ExceptionEvent event){
		send(event.getJDA(), "Exception", "Encountered exception:\n" + event.getCause().getMessage());
	}

	@Override
	public void onGuildJoin(@NotNull GuildJoinEvent event){
		var guildCount = (int) event.getJDA().getGuildCache().size();
		var guild = event.getGuild();
		guild.retrieveOwner().queue(owner -> send(event.getJDA(), "Guild Join", String.format(
			"Guild: `%s`(`%s`) with owner: `%s`(`%s`) and `%d` members%nGuilds: `%d`",
			guild.getName(),
			guild.getId(),
			owner.getUser().getAsTag(),
			owner.getUser().getId(),
			guild.getMemberCount(),
			guildCount
		)));
	}

	@Override
	public void onGuildLeave(@NotNull GuildLeaveEvent event){
		var guildCount = (int) event.getJDA().getGuildCache().size();
		var guild = event.getGuild();
		send(event.getJDA(), "Guild Leave", String.format(
			"Guild: `%s`(%s)%nGuilds: `%d`",
			guild.getName(),
			guild.getId(),
			guildCount
		));
	}

	public void sendShard(GenericEvent genericEvent, String event, String message){
		var jda = genericEvent.getJDA();
		send(jda, "Shard " + event, "Shard `" + jda.getShardInfo().getShardId() + "` " + message);
	}

	public void send(JDA jda, String event, String message){
		if(this.webhookClient == null){
			return;
		}
		var self = jda.getSelfUser();
		var avatarUrl = self.getEffectiveAvatarUrl();
		this.webhookClient.send(new WebhookMessageBuilder()
			.setAvatarUrl(avatarUrl)
			.setUsername(self.getName())
			.addEmbeds(new WebhookEmbedBuilder()
				.setColor(Colors.KITTYBOT_BLUE_INT)
				.setAuthor(new WebhookEmbed.EmbedAuthor(event, avatarUrl, Config.ORIGIN_URL))
				.setDescription(message)
				.setFooter(new WebhookEmbed.EmbedFooter("", avatarUrl))
				.setTimestamp(Instant.now())
				.build()
			).build()
		);
	}

}
