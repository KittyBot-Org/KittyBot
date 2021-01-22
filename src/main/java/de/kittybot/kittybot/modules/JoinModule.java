package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.objects.data.Placeholder;
import de.kittybot.kittybot.objects.enums.Emoji;
import de.kittybot.kittybot.objects.module.Module;
import de.kittybot.kittybot.utils.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("unused")
public class JoinModule extends Module{

	private static final Logger LOG = LoggerFactory.getLogger(MessageUtils.class);
	private static final String INVITE_CODE_PREFIX = "https://discord.gg/";
	private static final Set<Class<? extends Module>> DEPENDENCIES = Set.of(InviteModule.class);
	private static final List<String> oldCommands = List.of(".play", ".guildbanner", ".kiss", ".translate", ".blush", ".lick", ".hastebin", ".commands", ".dashboard", ".info", ".settings", ".pat", ".test", ".history", ".poke", ".pause", ".unassign", ".volume", ".feed", ".guildicon", ".eval", ".stop", ".restrictemote", ".tickle", ".fluff", ".downloademotes", ".senko", ".editsnipe", ".steal", ".ping", ".roles", ".privacy", ".skip", ".seek", ".bite", ".cuddle", ".forward", ".neko", ".avatar", ".snipe", ".uptime", ".kitsune", ".help", ".rewind", ".hug", ".shuffle", ".slap", ".queue", ".assign");

	private List<String> randomJoinMessages;
	private List<String> randomLeaveMessages;

	public static void sendAnnouncementMessage(Guild guild, long channelId, String message){
		if(channelId == -1){
			return;
		}
		var channel = guild.getTextChannelById(channelId);
		if(channel != null && channel.canTalk()){
			channel.sendMessage(message).queue();
			return;
		}
		guild.getJDA().openPrivateChannelById(guild.getOwnerId()).flatMap(privateChannel ->
			privateChannel.sendMessage(channel == null ?
				"Your selected announcement channel is deleted. Please set a new one." :
				"I lack the permission to send %s messages to " + channel.getAsMention())
		).queue();
	}

	@Override
	public Set<Class<? extends Module>> getDependencies(){
		return DEPENDENCIES;
	}

	@Override
	protected void onEnable(){
		this.randomJoinMessages = FileUtils.loadMessageFile("join");
		this.randomLeaveMessages = FileUtils.loadMessageFile("leave");
	}

	@Override
	public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event){
		var msg = event.getMessage().getContentRaw();
		if(event.getAuthor().isBot()){
			return;
		}
		if(oldCommands.stream().anyMatch(msg::startsWith) || msg.contains("<@" + Config.BOT_ID + ">") || msg.contains("<@!" + Config.BOT_ID + ">")){
			event.getChannel().sendMessage(new EmbedBuilder()
				.setColor(Colors.KITTYBOT_BLUE)
				.setAuthor("KittyBot Slash Commands Update", event.getJDA().getSelfUser().getEffectiveAvatarUrl(), Config.ORIGIN_URL)
				.setDescription("KittyBot now uses the new slash commands.\n" +
					"To use them invite me again **(you don't need to kick me)** over this specific " + MessageUtils.maskLink("link", "https://discord.com/oauth2/authorize?client_id=587697058602025011&permissions=1345841383&scope=bot%20applications.commands") + " and type `/` in the chatbox.\n"
				)
				.setFooter(event.getMember().getEffectiveName(), event.getAuthor().getEffectiveAvatarUrl())
				.setTimestamp(Instant.now())
				.build()
			).queue();
		}
	}

	@Override
	public void onGuildJoin(@Nonnull GuildJoinEvent event){
		var guild = event.getGuild();
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
				.setDescription(
					"KittyBot uses Discords new " + Emoji.SLASH.get() + " Slash Commands system! Therefore there is no need for any custom prefix or else. Also you see all commands directly by typing `/` into the message box. Go and try it out!\n" +
						"The key features Kitty provides are self assignable roles with a nice selector or separate assign/unassign commands. Simply map each role with your custom emote to create a simple role selector!\n" +
						"Also Kitty provides playing music from various places like youtube soundcloud & more(Spotify coming soon).\n" +
						"Kitty can also manage stream announcements for you. Simply add them with `/settings streamannouncements <source> <username>`." +
						"I can also log several stuff like message deletions/edits member leaves/joins etc. Set a log channel with `/settings logmessages <enabled> <channel>`" +
						"Do you want to welcome new users and point them to your rules channel? Set the announcement channel with `/settings announcementchannel <channel>` and set a cute custom join message with `/settings joinmessage <enabled> <message>`" +
						"Most stuff can be easily set up via the webinterface here " + MessageUtils.maskLink("here", Config.ORIGIN_URL) + ".\n\n" +
						"To report bugs/suggest features reach out to me on " + MessageUtils.maskLink("Discord", Config.SUPPORT_GUILD_INVITE_URL) +
						"(Username: `toπ#3141`) or on " + MessageUtils.maskLink("Twitter", "https://twitter.com/TopiSenpai")
				)
				.setColor(Colors.KITTYBOT_BLUE)
				.setThumbnail(event.getJDA().getSelfUser().getEffectiveAvatarUrl())
				.setFooter(guild.getName(), guild.getIconUrl())
				.setTimestamp(Instant.now())
				.build();
			user.openPrivateChannel().flatMap(channel -> channel.sendMessage(embed)).queue(
				null,
				error -> {
					var channel = guild.getDefaultChannel();
					if(channel == null || !channel.canTalk()){
						return;
					}
					channel.sendMessage(embed).queue();
				}
			);
		});
	}

	@Override
	public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event){
		var settings = this.modules.get(SettingsModule.class).getSettings(event.getGuild().getIdLong());
		if(!settings.areLeaveMessagesEnabled()){
			return;
		}
		var user = event.getUser();
		var message = PlaceholderUtils.replacePlaceholders(settings.getLeaveMessage(),
			new Placeholder("random_leave_message", getRandomMessage(this.randomLeaveMessages)),
			new Placeholder("user", user.getAsMention()),
			new Placeholder("user_tag", user.getAsTag()),
			new Placeholder("user_name", user.getName())
		);
	}

	@Override
	public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event){
		var settings = this.modules.get(SettingsModule.class).getSettings(event.getGuild().getIdLong());
		if(!settings.areJoinMessagesEnabled()){
			return;
		}
		var user = event.getUser();
		var invite = this.modules.get(InviteModule.class).getUsedInvite(event.getGuild().getIdLong(), user.getIdLong());
		var message = PlaceholderUtils.replacePlaceholders(settings.getJoinMessage(),
			new Placeholder("random_join_message", getRandomMessage(this.randomJoinMessages)),
			new Placeholder("user", user.getAsMention()),
			new Placeholder("user_tag", user.getAsTag()),
			new Placeholder("user_name", user.getName()),
			new Placeholder("invite_code", invite == null ? "unknown" : invite.getCode()),
			new Placeholder("invite_link", invite == null ? "unknown" : INVITE_CODE_PREFIX + invite.getCode())
		);
	}

	public String getRandomMessage(List<String> messages){
		if(messages.size() > 1){
			return messages.get(ThreadLocalRandom.current().nextInt(messages.size() - 1));
		}
		return messages.iterator().next();
	}

}
