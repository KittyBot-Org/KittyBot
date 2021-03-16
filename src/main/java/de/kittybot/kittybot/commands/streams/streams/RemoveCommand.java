package de.kittybot.kittybot.commands.streams.streams;

import de.kittybot.kittybot.modules.StreamModule;
import de.kittybot.kittybot.objects.streams.StreamType;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;

public class RemoveCommand extends GuildSubCommand{

	public RemoveCommand(){
		super("remove", "Removes a stream announcement");
		addOptions(
			new CommandOptionString("username", "The username of the streamer").required()
		);
	}

	@Override
	public void run(Options options, GuildCommandContext ctx){
		var type = StreamType.TWITCH;//StreamType.byId(options.getInt("service"));
		var username = options.getString("username");
		var success = ctx.get(StreamModule.class).remove(username, ctx.getGuildId(), type);
		if(!success){
			ctx.error("Could not find stream announcement for " + type.getName() + " with username: " + username + ". Check your spelling");
			return;
		}
		ctx.reply("Stream announcement for " + type.getName() + " with username: " + username + " removed");
	}

}
