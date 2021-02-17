package de.kittybot.kittybot.slashcommands.interaction;

public enum InteractionType{

	PING(1),
	APPLICATION_COMMAND(2);

	private final int type;

	InteractionType(int type){
		this.type = type;
	}

	public static InteractionType get(int type){
		if(type == 1){
			return PING;
		}
		else if(type == 2){
			return APPLICATION_COMMAND;
		}
		throw new IllegalArgumentException("Unknown InteractionType: " + type + " provided");
	}

	public int getType(){
		return this.type;
	}

}
