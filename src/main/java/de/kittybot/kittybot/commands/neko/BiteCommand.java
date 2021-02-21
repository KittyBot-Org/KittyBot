package de.kittybot.kittybot.commands.neko;

import de.kittybot.kittybot.objects.enums.Neko;

@SuppressWarnings("unused")
public class BiteCommand extends ReactionCommand{

	public BiteCommand(){
		super(Neko.BITE, "Bites a user", "bites");
	}

}
