package de.kittybot.kittybot.commands.admin.settings.streams;

import de.kittybot.kittybot.modules.StreamModule;
import de.kittybot.kittybot.objects.streams.StreamType;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;

public class RemoveCommand extends GuildSubCommand{

	public RemoveCommand(){
		super("remove", "Removes a stream announcement");
		addOptions(
			/*new CommandOptionInteger("service", "Which service the stream is from").required()
			.addChoices(
			new CommandOptionChoice<>("twitch", 1)/*,
			new CommandOptionChoice<>("youtube", 0)
			),*/
			new CommandOptionString("username", "The username of the streamer").required()
		);
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		var type = StreamType.TWITCH;//StreamType.byId(options.getInt("service"));
		var username = options.getString("username");
		var success = ia.get(StreamModule.class).remove(username, ia.getGuildId(), type);
		if(!success){
			ia.error("Could not find stream announcement for " + type.getName() + " with username: " + username + ". Check your spelling");
			return;
		}
		ia.reply("Stream announcement for " + type.getName() + " with username: " + username + " removed");
	}

}
