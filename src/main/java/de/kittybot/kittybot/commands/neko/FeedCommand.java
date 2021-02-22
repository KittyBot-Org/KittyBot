package de.kittybot.kittybot.commands.neko;

import de.kittybot.kittybot.objects.enums.Neko;

@SuppressWarnings("unused")
public class FeedCommand extends ReactionCommand{

	public FeedCommand(){
		super(Neko.FEED, "Feeds a user", "feeds");
	}

}
