package de.kittybot.kittybot.command.interactions.interaction;

import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InteractionDataOption{

	private final String name;
	private final String value;
	private final List<InteractionDataOption> options;

	public InteractionDataOption(String name, String value, List<InteractionDataOption> options){
		this.name = name;
		this.value = value;
		this.options = options;
	}

	public String getName(){
		return this.name;
	}

	public String getValue(){
		return this.value;
	}

	public List<InteractionDataOption> getOptions(){
		return this.options;
	}

	public static InteractionDataOption fromJSON(DataObject json){
		return new InteractionDataOption(
				json.getString("name"),
				json.getString("value", null),
				InteractionDataOption.fromJSON(json.optArray("options").orElse(null))
		);
	}

	public static List<InteractionDataOption> fromJSON(DataArray json){
		if(json == null){
			return new ArrayList<>();
		}
		return json.stream((array, index) -> fromJSON(array.getObject(index))).collect(Collectors.toList());
	}

}
