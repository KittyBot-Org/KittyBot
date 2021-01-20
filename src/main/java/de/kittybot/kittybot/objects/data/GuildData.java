package de.kittybot.kittybot.objects.data;

import com.jagrosh.jdautilities.oauth2.entities.OAuth2Guild;
import net.dv8tion.jda.api.entities.Guild;

public class GuildData{

	private final long id;
	private final String name, iconUrl;

	public GuildData(Guild guild){
		this(guild.getIdLong(), guild.getName(), guild.getIconUrl());
	}

	public GuildData(long id, String name, String iconUrl){
		this.id = id;
		this.name = name;
		this.iconUrl = iconUrl;
	}

	public GuildData(OAuth2Guild guild){
		this(guild.getIdLong(), guild.getName(), guild.getIconUrl());
	}

	public long getId(){
		return this.id;
	}

	public String getIdString(){
		return String.valueOf(this.id);
	}

	public String getName(){
		return this.name;
	}

	public String getIconUrl(){
		return this.iconUrl;
	}

}
