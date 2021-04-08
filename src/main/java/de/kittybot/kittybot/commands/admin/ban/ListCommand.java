package de.kittybot.kittybot.commands.admin.ban;

import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;

import java.util.stream.Collectors;

public class ListCommand extends GuildSubCommand{

	public ListCommand(){
		super("list", "Lists all bans");
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		ia.getGuild().retrieveBanList().queue(bans -> {
				if(bans.isEmpty()){
					ia.reply("There are no banned users yet");
					return;
				}
				ia.reply("**Banned Users:**\n" + bans.stream().map(ban -> MarkdownSanitizer.escape(ban.getUser().getAsTag()) + "(`" + ban.getUser().getId() + "`)" + " - " + ban.getReason()).collect(Collectors.joining("\n")));
			}, error -> ia.error("I was not able to retrieve the bans. Please give me the `ban members` permission")
		);
	}

}
