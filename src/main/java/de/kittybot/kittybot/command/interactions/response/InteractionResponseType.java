package de.kittybot.kittybot.command.interactions.response;

public enum InteractionResponseType{

	PONG(1),
	ACKNOWLEDGE(2),
	CHANNEL_MESSAGE(3),
	CHANNEL_MESSAGE_WITH_SOURCE(4),
	ACKNOWLEDGE_WITH_SOURCE(5);

	private final int type;

	InteractionResponseType(int type){
		this.type = type;
	}

	public int getType(){
		return this.type;
	}

}
