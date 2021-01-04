package de.kittybot.kittybot.events;

import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateIconEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdatePermissionsEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.awt.*;
import java.time.Instant;

public class OnGuildEvent extends ListenerAdapter{

	private final KittyBot main;

	public OnGuildEvent(KittyBot main){
		this.main = main;
	}

	@Override
	public void onGuildJoin(@Nonnull GuildJoinEvent event){
		var guild = event.getGuild();
		var guildCount = (int) event.getJDA().getGuildCache().size();
		var owner = guild.getOwner();
		sendToLogChannel(String.format("Hellowo I joined the guild: `%s`%s`%d` members!%nCurrently I'm in %d guilds!",
				guild.getName(),
				owner == null ? " " : " with owner: `" + owner.getUser().getAsTag() + "` and ",
				guild.getMemberCount(),
				guildCount
		));
		guild.retrieveAuditLogs().type(ActionType.BOT_ADD).limit(1).cache(false).queue(entries -> {
			var entry = entries.get(0);
			if(!entry.getTargetId().equals(event.getJDA().getSelfUser().getId())){
				return;
			}
			var user = entry.getUser();
			if(user == null){
				return;
			}
			var embed = new EmbedBuilder()
					.setTitle("Hellowo and thank your for adding me to your Discord Server!")
					.setDescription("To get started you maybe want to set up some self assignable roles. This can be done with `.roles add @role :emote:`. You will need a emote for each role and they should be from your server!\n\n"
							+ "If you want to know my other commands just type ``.commands``.\n"
							+ "To change my prefix use ``.options prefix <your wished prefix>``.\n" + "In case you forgot any command just type ``.cmds`` to get a full list of all my commands!\n"
							+ "You can also setup all this stuff via the webinterface at https://kittybot.de\n\n"
							+ "To report bugs/suggest features either join my [Support Server](https://discord.gg/sD3ABd5), add me on Discord ``toπ#3141`` or message me on [Twitter](https://twitter.com/TopiSenpai)")
					.setColor(Colors.KITTYBOT_BLUE)
					.setThumbnail(event.getJDA().getSelfUser().getEffectiveAvatarUrl())
					.setFooter(guild.getName(), guild.getIconUrl())
					.setTimestamp(Instant.now())
					.build();
			var messageRestAction = user.openPrivateChannel().flatMap(channel -> channel.sendMessage(embed));
			var defaultChannel = guild.getDefaultChannel();
			if(defaultChannel != null && defaultChannel.canTalk()){
				messageRestAction = messageRestAction.onErrorFlatMap(ignored -> defaultChannel.sendMessage(embed));
			}
			messageRestAction.queue();
		});
	}

	@Override
	public void onGuildUpdateIcon(@Nonnull GuildUpdateIconEvent event){

	}

	@Override
	public void onGuildUpdateName(@Nonnull GuildUpdateNameEvent event){

	}

	@Override
	public void onRoleUpdatePermissions(@Nonnull RoleUpdatePermissionsEvent event){

	}

	private void sendToLogChannel(String message){
		if(Config.LOG_GUILD_ID == -1 || Config.LOG_CHANNEL_ID == -1){
			return;
		}
		var jda = this.main.getJDA();
		var guild = jda.getGuildById(Config.LOG_GUILD_ID);
		if(guild == null){
			return;
		}
		var channel = guild.getTextChannelById(Config.LOG_CHANNEL_ID);
		if(channel == null || !channel.canTalk()){
			return;
		}
		channel.sendMessage(new EmbedBuilder().setColor(Colors.KITTYBOT_BLUE).setDescription(message).build()).queue();
	}

}
