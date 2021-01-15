package de.kittybot.kittybot.command.interactions.response;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InteractionResponseData{

	private final boolean tts;
	private final String content;
	private final List<MessageEmbed> embeds;
	private final int flags;
	protected InteractionResponseData(boolean tts, String content, List<MessageEmbed> embeds, int flags){
		this.tts = tts;
		this.content = content;
		this.embeds = embeds;
		this.flags = flags;
	}

	public DataObject toJSON(){
		var json = DataObject.empty().put("content", this.content);
		if(this.tts){
			json.put("tts", true);
		}
		if(!this.embeds.isEmpty()){
			json.put("embeds", DataArray.fromCollection(this.embeds.stream().map(MessageEmbed::toData).collect(Collectors.toList())));
		}
		if(flags > 0){
			json.put("flags", this.flags);
		}
		return json;
	}

	public static class Builder{

		private boolean tts;
		private String content;
		private final List<MessageEmbed> embeds;
		private int flags;

		public Builder(){
			this.tts = false;
			this.content = "";
			this.embeds = new ArrayList<>();
			this.flags = 0;
		}

		public Builder setTTS(boolean tts){
			this.tts = tts;
			return this;
		}

		public Builder setContent(String content){
			this.content = content;
			return this;
		}

		public Builder setEphemeral(){
			this.flags = 1 << 6;
			return this;
		}

		public Builder addEmbeds(MessageEmbed... embeds){
			var newEmbeds = List.of(embeds);
			if(this.embeds.size() + newEmbeds.size() > 10){
				throw new IllegalArgumentException("Too many embeds provided. Max is 10");
			}
			this.embeds.addAll(newEmbeds);
			return this;
		}

		public InteractionResponseData build(){
			return new InteractionResponseData(this.tts, this.content, this.embeds, this.flags);
		}

	}

}
