package de.kittybot.kittybot.command.interactions.application;

import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ApplicationCommandOptionChoice{

	private final String name, value;

	public ApplicationCommandOptionChoice(String name, String value){
		this.name = name;
		this.value = value;
	}

	public ApplicationCommandOptionChoice(String name, int value){
		this.name = name;
		this.value = Integer.toString(value);
	}

	public DataObject toJSON(){
		return DataObject.empty()
				.put("name", this.name)
				.put("value", this.value);
	}

	public static DataArray toJSON(Collection<ApplicationCommandOptionChoice> choices){
		return DataArray.fromCollection(
				choices.stream().map(ApplicationCommandOptionChoice::toJSON).collect(Collectors.toList())
		);
	}

	public static ApplicationCommandOptionChoice fromJSON(DataObject json){
		return new ApplicationCommandOptionChoice(
				json.getString("name"),
				json.getString("value")
		);
	}

	public static List<ApplicationCommandOptionChoice> fromJSON(DataArray json){
		return json.stream((array, index) -> fromJSON(array.getObject(index))).collect(Collectors.toList());
	}

}
