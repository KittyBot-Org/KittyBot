package de.kittybot.kittybot.objects.settings.guild;

import de.kittybot.kittybot.jooq.tables.records.GuildsRecord;
import de.kittybot.kittybot.objects.settings.IGuildSettings;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SnipesGuildSettings implements IGuildSettings{

	private boolean snipesEnabled;
	private final Set<Long> snipeDisabledChannels;

	public SnipesGuildSettings(GuildsRecord record, List<Long> snipeDisabledChannels){
		this.snipesEnabled = record.getSnipesEnabled();
		this.snipeDisabledChannels = new HashSet<>(snipeDisabledChannels);
	}

	public boolean areSnipesEnabled(){
		return this.snipesEnabled;
	}

	public void setSnipesEnabled(boolean snipesEnabled){
		this.snipesEnabled = snipesEnabled;
	}

}
