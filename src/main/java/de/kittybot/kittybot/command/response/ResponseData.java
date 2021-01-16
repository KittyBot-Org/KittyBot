package de.kittybot.kittybot.command.response;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ResponseData{

	private final boolean tts;
	private final String content;
	private final List<MessageEmbed> embeds;
	private final int flags;
	private EnumSet<Message.MentionType> allowedMentions;

	protected ResponseData(boolean tts, String content, List<MessageEmbed> embeds, int flags, EnumSet<Message.MentionType> allowedMentions){
		this.tts = tts;
		this.content = content;
		this.embeds = embeds;
		this.flags = flags;
		this.allowedMentions = allowedMentions;
	}

	protected DataObject getAllowedMentionsObj()
	{
		DataObject allowedMentionsObj = DataObject.empty();
		DataArray parsable = DataArray.empty();
		if (allowedMentions != null)
		{
			// Add parsing options
			allowedMentions.stream()
					.map(Message.MentionType::getParseKey)
					.filter(Objects::nonNull)
					.distinct()
					.forEach(parsable::add);
		}
		return allowedMentionsObj.put("parse", parsable);
	}

	public DataObject toJSON(){
		var json = DataObject.empty().put("content", this.content);
		if(this.tts){
			json.put("tts", true);
		}
		if(!this.embeds.isEmpty()){
			json.put("embeds", DataArray.fromCollection(this.embeds.stream().map(MessageEmbed::toData).collect(Collectors.toList())));
		}
		if(this.flags > 0){
			json.put("flags", this.flags);
		}
		if(!this.allowedMentions.isEmpty()){
			json.put("allowed_mentions", getAllowedMentionsObj());
		}
		return json;
	}

}
