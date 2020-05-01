package de.anteiku.kittybot.objects;

public class ReactiveMessage{
	
	public String messageId, userId, commandId, command, allowed;
	
	public ReactiveMessage(String messageId, String userId, String commandId, String command, String allowed){
		this.messageId = messageId;
		this.userId = userId;
		this.commandId = commandId;
		this.command = command;
		this.allowed = allowed;
	}
	
}
