package de.kittybot.kittybot.command.interactions.application;

import java.util.Arrays;

public enum ApplicationCommandOptionType{

	SUB_COMMAND(1),
	SUB_COMMAND_GROUP(2),
	STRING(3),
	INTEGER(4),
	BOOLEAN(5),
	USER(6),
	CHANNEL(7),
	ROLE(8);

	private final int type;

	ApplicationCommandOptionType(int type){
		this.type = type;
	}

	public static ApplicationCommandOptionType get(int type){
		return Arrays.stream(values()).filter(t -> t.getType() == type).findFirst().orElse(null);
	}

	public int getType(){
		return this.type;
	}

}
