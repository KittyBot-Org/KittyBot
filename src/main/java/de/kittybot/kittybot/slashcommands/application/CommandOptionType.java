package de.kittybot.kittybot.slashcommands.application;

import java.util.Arrays;

public enum CommandOptionType{

	SUB_COMMAND(1),
	SUB_COMMAND_GROUP(2),
	STRING(3),
	INTEGER(4),
	BOOLEAN(5),
	USER(6),
	CHANNEL(7),
	ROLE(8),
	UNKNOWN(0);

	private final int type;

	CommandOptionType(int type){
		this.type = type;
	}

	public static CommandOptionType get(int type){
		return Arrays.stream(values()).filter(t -> t.getType() == type).findFirst().orElse(null);
	}

	public int getType(){
		return this.type;
	}

}
