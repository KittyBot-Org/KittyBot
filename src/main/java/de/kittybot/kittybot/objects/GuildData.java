package de.kittybot.kittybot.objects;

public class GuildData{

	private final String id;
	private final String name;
	private final String iconUrl;

	public GuildData(final String id, final String name, final String iconUrl){
		this.id = id;
		this.name = name;
		this.iconUrl = iconUrl;
	}

	public String getId(){
		return this.id;
	}

	public String getName(){
		return this.name;
	}

	public String getIconUrl(){
		return this.iconUrl;
	}

}
