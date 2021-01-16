package de.kittybot.kittybot.command.interaction;

import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InteractionDataOption implements InteractionOptionsHolder{

	private final String name;
	private final Object value;
	private final List<InteractionDataOption> options;

	public InteractionDataOption(String name, Object value, List<InteractionDataOption> options){
		this.name = name;
		this.value = value;
		this.options = options;
	}

	public String getName(){
		return this.name;
	}

	public Object getValue(){
		return this.value;
	}

	public long getLong(){
		return Long.parseLong(getString());
	}

	public String getString(){
		return String.valueOf(this.value);
	}

	public boolean getBoolean(){
		return Boolean.parseBoolean(getString());
	}

	public List<InteractionDataOption> getOptions(){
		return this.options;
	}

	@Override
	public String toString(){
		return "InteractionDataOption{" +
				"name='" + this.name + '\'' +
				", value='" + this.value + '\'' +
				", options=" + this.options +
				'}';
	}

	public static InteractionDataOption fromJSON(DataObject json){
		return new InteractionDataOption(
				json.getString("name"),
				json.opt("value").orElse(null),
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
