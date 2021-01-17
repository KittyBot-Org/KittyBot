package de.kittybot.kittybot.slashcommands.interaction.response;

public enum InteractionResponseType{

	PONG(1),
	ACKNOWLEDGE(2),
	ACKNOWLEDGE_WITH_SOURCE(5),
	CHANNEL_MESSAGE(3),
	CHANNEL_MESSAGE_WITH_SOURCE(4);

	private final int type;

	InteractionResponseType(int type){
		this.type = type;
	}

	public int getType(){
		return this.type;
	}

}
