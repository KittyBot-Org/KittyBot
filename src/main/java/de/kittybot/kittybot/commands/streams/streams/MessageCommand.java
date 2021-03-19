package de.kittybot.kittybot.commands.streams.streams;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;

public class MessageCommand extends GuildSubCommand{

	public MessageCommand(){
		super("message", "Sets the stream announcement message template");
		addOptions(
			new CommandOptionString("message", "The message template").required()
		);
	}

	@Override
	public void run(Options options, GuildCommandContext ctx){
		var message = options.getString("message");
		ctx.get(SettingsModule.class).setStreamAnnouncementMessage(ctx.getGuildId(), message);
		ctx.reply("Set stream announcements template to:\n" + message);
	}

}
