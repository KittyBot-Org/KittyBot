package de.kittybot.kittybot.commands.neko;

import de.kittybot.kittybot.objects.enums.Neko;

@SuppressWarnings("unused")
public class CuddleCommand extends ReactionCommand{

	public CuddleCommand(){
		super(Neko.CUDDLE, "Cuddles a user", "cuddles");
	}

}
