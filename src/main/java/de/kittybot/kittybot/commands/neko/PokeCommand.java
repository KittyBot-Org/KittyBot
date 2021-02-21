package de.kittybot.kittybot.commands.neko;

import de.kittybot.kittybot.objects.enums.Neko;

@SuppressWarnings("unused")
public class PokeCommand extends ReactionCommand{

	public PokeCommand(){
		super(Neko.POKE, "Pokes a user", "pokes");
	}

}
