package de.kittybot.kittybot.commands.admin.settings;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionBoolean;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;

public class NsfwCommand extends GuildSubCommand{

	public NsfwCommand(){
		super("nsfw", "Enables/Disables nsfw commands");
		addOptions(
			new CommandOptionBoolean("enabled", "Whether nsfw commands are enabled").required()
		);
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		var enabled = options.getBoolean("enabled");
		ia.get(SettingsModule.class).setNsfwEnabled(ia.getGuildId(), enabled);
		ia.reply((enabled ? "Enabled" : "Disabled") + "nsfw commands");
	}

}
