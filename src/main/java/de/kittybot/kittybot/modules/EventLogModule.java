package de.kittybot.kittybot.modules;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.external.JDAWebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import de.kittybot.kittybot.module.Module;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.Config;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.*;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class EventLogModule extends Module{

	private final JDAWebhookClient webhookClient;

	public EventLogModule(){
		if(Config.LOG_WEBHOOK_URL.isBlank()){
			this.webhookClient = null;
			return;
		}
		this.webhookClient = new WebhookClientBuilder(Config.LOG_WEBHOOK_URL).buildJDA();
	}

	@Override
	public void onReady(@NotNull ReadyEvent event){
		send(event.getJDA(), "Ready", "KittyBot started successfully");
	}

	@Override
	public void onResumed(@Nonnull ResumedEvent event){
		send(event.getJDA(), "Resumed", "Resumed to remote server");
	}

	@Override
	public void onReconnected(@Nonnull ReconnectedEvent event){
		send(event.getJDA(), "Reconnected", "Reconnected to remote server");
	}

	@Override
	public void onDisconnect(@NotNull DisconnectEvent event){
		send(event.getJDA(), "Disconnect", "Disconnected from the remote server");
	}

	@Override
	public void onShutdown(@Nonnull ShutdownEvent event){
		send(event.getJDA(), "Shutdown", "KittyBot shutting down...");
	}

	@Override
	public void onException(@Nonnull ExceptionEvent event){
		send(event.getJDA(), "Exception", "Encountered exception:\n" + event.getCause().getMessage());
	}

	@Override
	public void onGuildJoin(@NotNull GuildJoinEvent event){
		var guildCount = (int) event.getJDA().getGuildCache().size();
		var guild = event.getGuild();
		guild.retrieveOwner().queue(owner -> send(event.getJDA(), "Join", String.format(
				"Guild: `%s(%s) with owner: `%s`(%s) and `%d` members\nGuilds: `%d`",
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
		send(event.getJDA(), "Leave", String.format(
				"Guild: `%s(%s)\nGuilds: `%d`",
				guild.getName(),
				guild.getId(),
				guildCount
		));
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
						.build()
				).build()
		);
	}

}
