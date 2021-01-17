package de.kittybot.kittybot.slashcommands.interaction;

import net.dv8tion.jda.api.utils.data.DataObject;

import java.util.List;

public class InteractionData implements InteractionOptionsHolder{

	private final long id;
	private final String name;
	private final List<InteractionDataOption> options;

	public InteractionData(long id, String name, List<InteractionDataOption> options){
		this.id = id;
		this.name = name;
		this.options = options;
	}

	public static InteractionData fromJSON(DataObject json){
		return new InteractionData(
				json.getLong("id"),
				json.getString("name"),
				InteractionDataOption.fromJSON(json.optArray("options").orElse(null))
		);
	}

	public long getId(){
		return this.id;
	}

	public String getName(){
		return this.name;
	}

	public List<InteractionDataOption> getOptions(){
		return this.options;
	}

}
