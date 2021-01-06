package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.module.Module;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;

import javax.annotation.Nonnull;
import java.time.Instant;

public class JoinModule extends Module{


	public JoinModule(){}

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
							"To get started you maybe want to set up some self assignable roles. This can be done with `.roles add @role :emote:`. You will need a emote for each role and they should be from your server!\n\n"
									+ "If you want to know my other commands just type ``.commands``.c"
									+ "To change my prefix use ``.options prefix <your wished prefix>``.\n"
									+ "In case you forgot any command just type ``.cmds`` to get a full list of all my commands!\n"
									+ "You can also setup all this stuff via the webinterface at " + Config.ORIGIN_URL + "\n\n"
									+ "To report bugs/suggest features either join my " + MessageUtils.maskLink(
									"Support Server",
									Config.SUPPORT_GUILD_INVITE_URL
							) + ", add me on Discord ``toπ#3141`` or message me on [Twitter](https://twitter.com/TopiSenpai)")
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

}
