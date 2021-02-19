package de.kittybot.kittybot.commands.neko;

import de.kittybot.kittybot.objects.enums.Neko;
import de.kittybot.kittybot.slashcommands.application.CommandOptionChoice;

public class NekoCommandOptionChoice extends CommandOptionChoice<String>{

	public NekoCommandOptionChoice(Neko neko){
		super(neko.name(), neko.name());
	}

}
