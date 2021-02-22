package de.kittybot.kittybot.commands.neko;

import de.kittybot.kittybot.objects.enums.Neko;

@SuppressWarnings("unused")
public class PatCommand extends ReactionCommand{

	public PatCommand(){
		super(Neko.PAT, "Pats a user", "pats");
	}

}
