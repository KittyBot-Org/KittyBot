package de.kittybot.kittybot.commands.neko;

import de.kittybot.kittybot.objects.enums.Neko;

@SuppressWarnings("unused")
public class TickleCommand extends ReactionCommand{

	public TickleCommand(){
		super(Neko.TICKLE, "Tickles a user", "tickles");
	}

}
