package de.kittybot.kittybot.objects.guilds;

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
		return id;
	}

	public String getName(){
		return name;
	}

	public String getIconUrl(){
		return iconUrl;
	}

}
