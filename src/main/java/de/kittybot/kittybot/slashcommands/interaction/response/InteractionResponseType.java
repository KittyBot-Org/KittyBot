package de.kittybot.kittybot.slashcommands.interaction.response;

public enum InteractionResponseType{

	PONG(1),
	CHANNEL_MESSAGE_WITH_SOURCE(4),
	DEFERRED_CHANNEL_MESSAGE_WITH_SOURCE(5);

	private final int type;

	InteractionResponseType(int type){
		this.type = type;
	}

	public int getType(){
		return this.type;
	}

}
