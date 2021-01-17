package de.kittybot.kittybot.command.response;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FollowupMessage{

	private final String username, avatarUrl, content;
	private final List<MessageEmbed> embeds;
	private final boolean isTTS;
	private final EnumSet<Message.MentionType> allowedMentions;

	private FollowupMessage(String username, String avatarUrl, String content, List<MessageEmbed> embeds, boolean isTTS, EnumSet<Message.MentionType> allowedMentions){
		this.username = username;
		this.avatarUrl = avatarUrl;
		this.content = content;
		this.embeds = embeds;
		this.isTTS = isTTS;
		this.allowedMentions = allowedMentions;
	}

	@Nullable
	public String getUsername(){
		return username;
	}

	@Nullable
	public String getAvatarUrl(){
		return avatarUrl;
	}

	@Nullable
	public String getContent(){
		return content;
	}

	@NotNull
	public List<MessageEmbed> getEmbeds(){
		return embeds == null ? Collections.emptyList() : embeds;
	}

	public boolean isTTS(){
		return isTTS;
	}

	@NotNull
	public DataObject toJSON(){
		final DataObject payload = DataObject.empty();
		payload.put("content", this.content);
		if(this.embeds != null && !this.embeds.isEmpty()){
			payload.put("embeds", DataArray.fromCollection(this.embeds.stream().map(MessageEmbed::toData).collect(Collectors.toSet())));
		}
		if(this.avatarUrl != null){
			payload.put("avatar_url", this.avatarUrl);
		}
		if(this.username != null){
			payload.put("username", this.username);
		}
		payload.put("tts", isTTS);
		payload.put("allowed_mentions", getAllowedMentionsObj());
		return payload;
	}

	protected DataObject getAllowedMentionsObj(){
		DataObject allowedMentionsObj = DataObject.empty();
		DataArray parsable = DataArray.empty();
		if(allowedMentions != null){
			// Add parsing options
			allowedMentions.stream()
					.map(Message.MentionType::getParseKey)
					.filter(Objects::nonNull)
					.distinct()
					.forEach(parsable::add);
		}
		return allowedMentionsObj.put("parse", parsable);
	}

	public static class Builder{

		private String username, avatarUrl, content;
		private List<MessageEmbed> embeds;
		private boolean isTTS;
		private EnumSet<Message.MentionType> allowedMentions;

		public Builder(){
			this.username = null;
			this.avatarUrl = null;
			this.content = "";
			this.embeds = null;
			this.isTTS = false;
			this.allowedMentions = null;
		}

		public Builder setUsername(String username){
			this.username = username;
			return this;
		}

		public Builder setAvatarUrl(String avatarUrl){
			this.avatarUrl = avatarUrl;
			return this;
		}

		public Builder setContent(String content){
			this.content = content;
			return this;
		}

		public Builder setEmbeds(MessageEmbed... embeds){
			this.embeds = List.of(embeds);
			return this;
		}

		public Builder setTTS(boolean TTS){
			isTTS = TTS;
			return this;
		}

		public Builder setAllowedMentions(EnumSet<Message.MentionType> allowedMentions){
			this.allowedMentions = allowedMentions;
			return this;
		}

		public FollowupMessage build(){
			return new FollowupMessage(this.username, this.avatarUrl, this.content, this.embeds, this.isTTS, this.allowedMentions);
		}

	}

}
