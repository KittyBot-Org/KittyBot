package de.kittybot.kittybot.objects.settings;

public enum LogEvent{

	MESSAGE_EDIT(0),
	MESSAGE_DELETE(1),

	CHANNEL_CREATE(2),
	CHANNEL_EDIT(3),
	CHANNEL_DELETE(4),

	INVITE_CREATE(5),
	INVITE_POST(6),
	INVITE_DELETE(7),

	EMOTE_CREATE(1),
	EMOTE_EDIT(1),
	EMOTE_DELETE(2);

	private final int id;

	LogEvent(int id){
		this.id = id;
	}

	public static LogEvent byId(int id){
		for(var value : values()){
			if(value.id == id){
				return value;
			}
		}
		throw new IllegalArgumentException("LogEvent with id not found");
	}
}
