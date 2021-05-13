package de.kittybot.kittybot.objects.settings.guild;

import de.kittybot.kittybot.jooq.tables.records.GuildsRecord;

public class LogGuildSettings{

	private long logChannelId;
	private boolean logMessagesEnabled;
	private final Set<>

	public LogGuildSettings(GuildsRecord record){
		this.logChannelId = record.getLogChannelId();
		this.logMessagesEnabled = record.getLogMessagesEnabled();
	}
}
