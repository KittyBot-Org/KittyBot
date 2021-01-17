package de.kittybot.kittybot.command.response;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.utils.Helpers;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class ResponseData{

	private final boolean tts;
	private final String content;
	private final List<MessageEmbed> embeds;
	private final int flags;
	private final EnumSet<Message.MentionType> allowedMentions;

	protected ResponseData(boolean tts, String content, List<MessageEmbed> embeds, int flags, EnumSet<Message.MentionType> allowedMentions){
		this.tts = tts;
		this.content = content;
		this.embeds = embeds;
		this.flags = flags;
		this.allowedMentions = allowedMentions;
	}

	private DataObject getAllowedMentionsObj(){
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

	public static class Builder{

		private boolean tts;
		private String content;
		private final List<MessageEmbed> embeds;
		private EnumSet<Message.MentionType> allowedMentions;

		public Builder(){
			this.tts = false;
			this.content = "";
			this.embeds = new ArrayList<>();
			this.allowedMentions = EnumSet.allOf(Message.MentionType.class);
		}

		public Builder setTTS(boolean tts){
			this.tts = tts;
			return this;
		}

		public Builder setContent(String content){
			this.content = content;
			return this;
		}

		public Builder addEmbeds(MessageEmbed... embeds){
			var newEmbeds = List.of(embeds);
			if(this.embeds.size() + newEmbeds.size() > 10){
				throw new IllegalArgumentException("Max of 10 embeds are supported");
			}
			this.embeds.addAll(newEmbeds);
			return this;
		}

		public Builder setAllowedMentions(@Nullable Collection<Message.MentionType> allowedMentions){
			this.allowedMentions = allowedMentions == null ? EnumSet.allOf(Message.MentionType.class) : Helpers.copyEnumSet(Message.MentionType.class, allowedMentions);
			return this;
		}

		public Builder setAllowedMentions(@Nullable Message.MentionType... allowedMentions){
			return setAllowedMentions(List.of(allowedMentions));
		}

		public ResponseData build(){
			return new ResponseData(this.tts, this.content, this.embeds, 0, this.allowedMentions);
		}

	}

}
