package de.kittybot.kittybot.commands.neko;

import de.kittybot.kittybot.objects.Neko;
import de.kittybot.kittybot.slashcommands.application.CommandOptionChoice;

public class NekoCommandOptionChoice extends CommandOptionChoice<Integer>{

	public NekoCommandOptionChoice(Neko neko){
		super(neko.name(), neko.getId());
	}

}
