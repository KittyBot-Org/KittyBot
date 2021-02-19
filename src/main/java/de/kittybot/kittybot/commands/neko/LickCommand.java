package de.kittybot.kittybot.commands.neko;

import de.kittybot.kittybot.objects.enums.Neko;

@SuppressWarnings("unused")
public class LickCommand extends ReactionCommand{

	public LickCommand(){
		super(Neko.LICK, "Licks a user", "licks");
	}

}
