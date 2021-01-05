package de.kittybot.kittybot.managers;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.external.JDAWebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.Config;
import net.dv8tion.jda.api.events.*;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class EventLogManager extends ListenerAdapter{

	private final JDAWebhookClient webhookClient;
	private final KittyBot main;

	public EventLogManager(KittyBot main){
		this.main = main;
		if(Config.LOG_WEBHOOK_URL.isBlank()){
			this.webhookClient = null;
			return;
		}
		this.webhookClient = new WebhookClientBuilder(Config.LOG_WEBHOOK_URL).buildJDA();
	}

	@Override
	public void onReady(@NotNull ReadyEvent event){
		send("Ready", "KittyBot started successfully");
	}

	@Override
	public void onDisconnect(@NotNull DisconnectEvent event){
		send("Disconnect", "Disconnected from the remote server");
	}

	@Override
	public void onShutdown(@Nonnull ShutdownEvent event){
		send("Shutdown", "KittyBot shutting down...");
	}

	@Override
	public void onResumed(@Nonnull ResumedEvent event){
		send("Resumed", "Resumed to remote server");
	}

	@Override
	public void onReconnected(@Nonnull ReconnectedEvent event){
		send("Reconnected", "Reconnected to remote server");
	}

	@Override
	public void onException(@Nonnull ExceptionEvent event){
		send("Exception", "Encountered exception:\n" + event.getCause().getMessage());
	}

	@Override
	public void onGuildJoin(@NotNull GuildJoinEvent event){
		var guildCount = (int) event.getJDA().getGuildCache().size();
		event.getGuild().retrieveOwner().queue(owner -> {
			var guild = event.getGuild();
			send("Join", String.format("Guild: `%s(%s) with owner: `%s`(%s) and `%d` members\nGuilds: `%d`",
				guild.getName(),
				guild.getId(),
				owner.getUser().getAsTag(),
				owner.getUser().getId(),
				guild.getMemberCount(),
				guildCount
			));
			}
		);
	}

	@Override
	public void onGuildLeave(@NotNull GuildLeaveEvent event){
		var guildCount = (int) event.getJDA().getGuildCache().size();
		var guild = event.getGuild();
		send("Leave", String.format("Guild: `%s(%s)\nGuilds: `%d`",
			guild.getName(),
			guild.getId(),
			guildCount
		));
	}

	public void send(String event, String message){
		if(this.webhookClient == null){
			return;
		}
		var self = this.main.getJDA().getSelfUser();
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
