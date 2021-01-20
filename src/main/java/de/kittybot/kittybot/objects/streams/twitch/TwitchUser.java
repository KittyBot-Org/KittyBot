package de.kittybot.kittybot.objects.streams.twitch;

import net.dv8tion.jda.api.utils.data.DataObject;

public class TwitchUser{

	private final long id;
	private final String displayName;

	public TwitchUser(DataObject json){
		this.id = json.getLong("id");
		this.displayName = json.getString("display_name");
	}

	public long getId(){
		return this.id;
	}

	public String getDisplayName(){
		return this.displayName;
	}

}
