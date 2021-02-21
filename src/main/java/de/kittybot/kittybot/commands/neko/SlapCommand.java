package de.kittybot.kittybot.commands.neko;

import de.kittybot.kittybot.objects.enums.Neko;

@SuppressWarnings("unused")
public class SlapCommand extends ReactionCommand{

	public SlapCommand(){
		super(Neko.SLAP, "Slaps a user", "slaps");
	}

}
