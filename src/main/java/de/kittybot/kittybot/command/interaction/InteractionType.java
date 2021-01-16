package de.kittybot.kittybot.command.interaction;

import java.util.Arrays;

public enum InteractionType{

	PING(1),
	APPLICATION_COMMAND(2);

	private final int type;

	InteractionType(int type){
		this.type = type;
	}

	public static InteractionType get(int type){
		return Arrays.stream(values()).filter(t -> t.getType() == type).findFirst().orElse(null);
	}

	public int getType(){
		return this.type;
	}

}
