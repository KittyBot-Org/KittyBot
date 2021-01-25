package de.kittybot.kittybot.commands.neko;

import de.kittybot.kittybot.objects.enums.Neko;
import de.kittybot.kittybot.slashcommands.application.CommandOptionChoice;

public class NekoCommandOptionChoice extends CommandOptionChoice<Integer>{

	public NekoCommandOptionChoice(Neko neko){
		super(neko.getName(), neko.getId());
	}

}
