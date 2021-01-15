package de.kittybot.kittybot.command.interactions.response;

import net.dv8tion.jda.api.utils.data.DataObject;

public class InteractionResponse{

	private final InteractionResponseType type;
	private final InteractionResponseData data;

	public InteractionResponse(InteractionResponseType type, InteractionResponseData data){
		this.type = type;
		this.data = data;
	}

	public DataObject toJSON(){
		var json = DataObject.empty().put("type", this.type.getType());
		return this.data == null ? json : json.put("data", this.data.toJSON());
	}

}
