package de.kittybot.kittybot.commands.neko;

import de.kittybot.kittybot.objects.enums.Neko;

@SuppressWarnings("unused")
public class KissCommand extends ReactionCommand{

	public KissCommand(){
		super(Neko.KISS, "Kisses a user", "kisses");
	}

}
