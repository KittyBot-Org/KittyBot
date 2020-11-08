package de.kittybot.kittybot.objects.data;

public class ReactiveMessage{

	public String channelId, messageId, userId, commandId, command, allowed;

	public ReactiveMessage(String channelId, String messageId, String userId, String commandId, String command, String allowed){
		this.channelId = channelId;
		this.messageId = messageId;
		this.userId = userId;
		this.commandId = commandId;
		this.command = command;
		this.allowed = allowed;
	}

}
