package de.kittybot.kittybot.slashcommands.application;

import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.util.Collection;
import java.util.stream.Collectors;

public class CommandOptionChoice<T>{

	private final String name;
	private final T value;

	public CommandOptionChoice(String name, T value){
		this.name = name;
		this.value = value;
	}

	public static DataArray toJSON(Collection<CommandOptionChoice<?>> choices){
		return DataArray.fromCollection(
				choices.stream().map(CommandOptionChoice::toJSON).collect(Collectors.toList())
		);
	}

	public DataObject toJSON(){
		return DataObject.empty().put("name", this.name).put("value", this.value);
	}

}
