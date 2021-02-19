package de.kittybot.kittybot.commands.neko;

import de.kittybot.kittybot.objects.enums.Neko;

@SuppressWarnings("unused")
public class HugCommand extends ReactionCommand{

	public HugCommand(){
		super(Neko.HUG, "Hugs a user", "hugs");
	}

}
