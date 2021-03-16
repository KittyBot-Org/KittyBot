package de.kittybot.kittybot.slashcommands.application;

import net.dv8tion.jda.api.utils.data.DataObject;

public class OptionChoice{

	private final String name;
	private final Object value;

	public OptionChoice(String name, String value){
		this.name = name;
		this.value = value;
	}

	public OptionChoice(String name, int value){
		this.name = name;
		this.value = value;
	}

	public OptionChoice(Enum<?> tEnum){
		this.name = tEnum.toString();
		this.value = tEnum;
	}

	public String getName(){
		return this.name;
	}

	public Object getValue(){
		return this.value;
	}

	public DataObject toJSON(){
		return DataObject.empty().put("name", this.name).put("value", this.value);
	}

}
