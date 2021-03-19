package de.kittybot.kittybot.commands.streams.streams;

import de.kittybot.kittybot.modules.StreamModule;
import de.kittybot.kittybot.objects.streams.StreamType;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;

public class AddCommand extends GuildSubCommand{

	public AddCommand(){
		super("add", "Adds a new stream announcement for twitch");
		addOptions(
			new CommandOptionString("username", "The username of the streamer").required()
		);
	}

	@Override
	public void run(Options options, GuildCommandContext ctx){
		var type = StreamType.TWITCH;//StreamType.byId(options.getInt("service"));
		var username = options.getString("username");
		var user = ctx.get(StreamModule.class).add(username, ctx.getGuildId(), type);
		if(user == null){
			ctx.error("No user found with username " + username + "for " + type.getName());
			return;
		}
		ctx.reply("Stream announcement for " + type.getName() + " with username: " + user.getDisplayName() + " added");
	}

}
