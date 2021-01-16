package de.kittybot.kittybot.command.response;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.utils.Helpers;

import javax.annotation.Nullable;
import java.util.*;

public class Response{

	private static final int EPHEMERAL_FLAG = 1 << 6;

	private final ResponseType type;
	private final ResponseData data;

	public Response(ResponseType type, ResponseData data){
		this.type = type;
		this.data = data;
	}

	public DataObject toJSON(){
		var json = DataObject.empty().put("type", this.type.getType());
		if(this.data != null){
			json.put("data", this.data.toJSON());
		}
		return json;
	}

	public static class Builder{

		private ResponseType type;
		private boolean tts;
		private String content;
		private final List<MessageEmbed> embeds;
		private int flags;
		private EnumSet<Message.MentionType> allowedMentions;

		public Builder(){
			this.type = ResponseType.CHANNEL_MESSAGE_WITH_SOURCE;
			this.tts = false;
			this.content = "";
			this.embeds = new ArrayList<>();
			this.flags = 0;
			this.allowedMentions = EnumSet.allOf(Message.MentionType.class);
		}

		public Builder setType(ResponseType type){
			this.type = type;
			return this;
		}

		public Builder setTTS(boolean tts){
			this.tts = tts;
			return this;
		}

		public Builder setContent(String content){
			this.content = content;
			return this;
		}

		public Builder ephemeral(){
			this.flags = EPHEMERAL_FLAG;
			return this;
		}

		public boolean isEphemeral(){
			return this.flags == EPHEMERAL_FLAG;
		}

		public Builder setEphemeral(boolean ephemeral){
			if(ephemeral){
				this.flags = EPHEMERAL_FLAG;
			}
			else{
				this.flags = 0;
			}
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

		public Response build(){
			if(this.flags == EPHEMERAL_FLAG && !this.embeds.isEmpty()){
				throw new IllegalArgumentException("Ephemeral messages do not support embeds");
			}
			return new Response(this.type, new ResponseData(this.tts, this.content, this.embeds, this.flags, this.allowedMentions));
		}

	}

}
