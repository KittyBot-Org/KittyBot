package de.kittybot.kittybot.commands.admin.settings;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionBoolean;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;

public class NsfwCommand extends GuildSubCommand{

	public NsfwCommand(){
		super("nsfw", "Enables/Disables nsfw commands");
		addOptions(
			new CommandOptionBoolean("enabled", "Whether nsfw commands are enabled").required()
		);
	}

	@Override
	public void run(Options options, GuildCommandContext ctx){
		var enabled = options.getBoolean("enabled");
		ctx.get(SettingsModule.class).setNsfwEnabled(ctx.getGuildId(), enabled);
		ctx.reply((enabled ? "Enabled" : "Disabled") + "nsfw commands");
	}

}
