package de.kittybot.kittybot.slashcommands.interaction.response;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.EnumSet;
import java.util.List;

public class InteractionResponseData{

	private final boolean tts;
	private final String content;
	private final List<MessageEmbed> embeds;
	private final int flags;
	private final EnumSet<Message.MentionType> allowedMentions;

	protected InteractionResponseData(boolean tts, String content, List<MessageEmbed> embeds, int flags, EnumSet<Message.MentionType> allowedMentions){
		this.tts = tts;
		this.content = content;
		this.embeds = embeds;
		this.flags = flags;
		this.allowedMentions = allowedMentions;
	}

	public boolean isTts(){
		return this.tts;
	}

	public String getContent(){
		return this.content;
	}

	public List<MessageEmbed> getEmbeds(){
		return this.embeds;
	}

	public int getFlags(){
		return this.flags;
	}

	public EnumSet<Message.MentionType> getAllowedMentions(){
		return this.allowedMentions;
	}

}
