package de.kittybot.kittybot.slashcommands.interaction;

import de.kittybot.kittybot.slashcommands.application.CommandOptionType;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InteractionDataOption implements InteractionOptionsHolder{

	private final String name;
	private final CommandOptionType type;
	private final Object value;
	private final List<InteractionDataOption> options;

	public InteractionDataOption(String name, CommandOptionType type, Object value, List<InteractionDataOption> options){
		this.name = name.toLowerCase();
		this.type = type;
		this.value = value;
		this.options = options;
	}

	public static InteractionDataOption fromJSON(DataObject json){
		return new InteractionDataOption(
			json.getString("name"),
			CommandOptionType.get(json.getInt("type")),
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

	public String getName(){
		return this.name;
	}

	public CommandOptionType getType(){
		return this.type;
	}

	public Object getValue(){
		return this.value;
	}

	/*
	public String getEmoteName(){
		var matcher = Message.MentionType.EMOTE.getPattern().matcher(getString());
		if(!matcher.matches()){
			return null;
		}
		return matcher.group(1);
	}*/

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

}
