package de.kittybot.kittybot.slashcommands.interaction.response;

import de.kittybot.kittybot.modules.InteractionsModule;
import de.kittybot.kittybot.slashcommands.interaction.Interaction;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.requests.Requester;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;
import okhttp3.RequestBody;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

public class InteractionRespondAction extends RestActionImpl<Interaction>{

	private static final String CONTENT_TOO_BIG = String.format("A message may not exceed %d characters. Please limit your input!", Message.MAX_CONTENT_LENGTH);
	private static EnumSet<Message.MentionType> defaultMentions = EnumSet.allOf(Message.MentionType.class);
	private final Interaction interaction;
	protected EnumSet<Message.MentionType> allowedMentions;
	protected Set<String> mentionableUsers = new HashSet<>();
	protected Set<String> mentionableRoles = new HashSet<>();
	private InteractionResponseType type;
	private StringBuilder content = new StringBuilder();
	private boolean tts = false;
	private int flags = 0;
	private List<MessageEmbed> embeds = new ArrayList<>();

	public InteractionRespondAction(JDA api, Interaction interaction, InteractionResponseType type){
		super(api, InteractionsModule.INTERACTION_RESPONSE.compile(String.valueOf(interaction.getId()), interaction.getToken()));
		this.interaction = interaction;
		this.type = type;
	}

	@Nonnull
	public static EnumSet<Message.MentionType> getDefaultMentions(){
		return defaultMentions.clone();
	}

	public static void setDefaultMentions(@Nullable Collection<Message.MentionType> allowedMentions){
		InteractionRespondAction.defaultMentions = allowedMentions == null
			? EnumSet.allOf(Message.MentionType.class) // Default to all mentions enabled
			: Helpers.copyEnumSet(Message.MentionType.class, allowedMentions);
	}

	@Nonnull
	@Override
	public InteractionRespondAction setCheck(BooleanSupplier checks){
		return (InteractionRespondAction) super.setCheck(checks);
	}

	@Nonnull
	@Override
	public InteractionRespondAction deadline(long timestamp){
		return (InteractionRespondAction) super.deadline(timestamp);
	}

	@Override
	public RequestBody finalizeData(){
		return asJSON();
	}

	protected RequestBody asJSON(){
		return RequestBody.create(Requester.MEDIA_TYPE_JSON, getJSON().toJson());
	}

	protected DataObject getJSON(){
		var json = DataObject.empty();

		json.put("type", this.type.getType());

		if(!embeds.isEmpty() || content.length() > 0){
			var obj = DataObject.empty();
			if(!embeds.isEmpty()){
				obj.put("embeds", DataArray.fromCollection(this.embeds.stream().map(MessageEmbed::toData).collect(Collectors.toSet())));
			}
			if(content.length() > 0){
				obj.put("content", content.toString());
			}
			if(flags != 0){
				obj.put("flags", flags);
			}

			obj.put("tts", tts);
			if(allowedMentions != null || !mentionableUsers.isEmpty() || !mentionableRoles.isEmpty()){
				obj.put("allowed_mentions", getAllowedMentionsObj());
			}
			json.put("data", obj);
		}
		return json;
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
		if(!mentionableUsers.isEmpty()){
			// Whitelist certain users
			parsable.remove(Message.MentionType.USER.getParseKey());
			allowedMentionsObj.put("users", DataArray.fromCollection(mentionableUsers));
		}
		if(!mentionableRoles.isEmpty()){
			// Whitelist certain roles
			parsable.remove(Message.MentionType.ROLE.getParseKey());
			allowedMentionsObj.put("roles", DataArray.fromCollection(mentionableRoles));
		}
		return allowedMentionsObj.put("parse", parsable);
	}

	@Override
	protected void handleSuccess(Response response, Request<Interaction> request){
		request.onSuccess(this.interaction);
	}

	public boolean hasPermission(Permission perm){
		var channel = getChannel();
		if(channel.getType() != ChannelType.TEXT){
			return true;
		}
		TextChannel text = (TextChannel) channel;
		Member self = text.getGuild().getSelfMember();
		return self.hasPermission(text, perm);
	}

	@Nonnull
	public MessageChannel getChannel(){
		return this.interaction.getChannel();
	}

	@Nonnull
	@Override
	public InteractionRespondAction timeout(long timeout, @Nonnull TimeUnit unit){
		return (InteractionRespondAction) super.timeout(timeout, unit);
	}

	public InteractionRespondAction type(InteractionResponseType type){
		this.type = type;
		return this;
	}

	public InteractionRespondAction ephemeral(){
		this.flags = 1 << 6;
		return this;
	}

	public InteractionRespondAction embeds(MessageEmbed... embeds){
		if(embeds.length > 0){
			Checks.check(Arrays.stream(embeds).anyMatch(MessageEmbed::isSendable),
				"Provided Message contains an empty embed or an embed with a length greater than %d characters, which is the max for bot accounts!",
				MessageEmbed.EMBED_MAX_LENGTH_BOT);
		}
		this.embeds = List.of(embeds);
		return this;
	}

	@Nonnull
	public InteractionRespondAction allowedMentions(@Nullable Collection<Message.MentionType> allowedMentions){
		this.allowedMentions = allowedMentions == null
			? EnumSet.allOf(Message.MentionType.class)
			: Helpers.copyEnumSet(Message.MentionType.class, allowedMentions);
		return this;
	}

	@Nonnull
	public InteractionRespondAction mention(@Nonnull IMentionable... mentions){
		Checks.noneNull(mentions, "Mentionables");
		for(IMentionable mentionable : mentions){
			if(mentionable instanceof User || mentionable instanceof Member){
				mentionableUsers.add(mentionable.getId());
			}
			else if(mentionable instanceof Role){
				mentionableRoles.add(mentionable.getId());
			}
		}
		return this;
	}

	@Nonnull
	public InteractionRespondAction mentionUsers(@Nonnull String... userIds){
		Checks.noneNull(userIds, "User Id");
		Collections.addAll(mentionableUsers, userIds);
		return this;
	}

	@Nonnull
	public InteractionRespondAction mentionRoles(@Nonnull String... roleIds){
		Checks.noneNull(roleIds, "Role Id");
		Collections.addAll(mentionableRoles, roleIds);
		return this;
	}

	@Nonnull
	@CheckReturnValue
	public InteractionRespondAction tts(boolean isTTS){
		this.tts = isTTS;
		return this;
	}

	public InteractionRespondAction fromData(InteractionResponse response){
		this.type = response.getType();
		var data = response.getData();
		this.content = new StringBuilder(data.getContent());
		this.tts = data.isTts();
		this.allowedMentions = data.getAllowedMentions();
		this.embeds = data.getEmbeds();
		this.flags = data.getFlags();
		return this;
	}

	@Nonnull
	@CheckReturnValue
	public InteractionRespondAction content(final String content){
		if(content == null || content.isEmpty()){
			this.content.setLength(0);
		}
		else if(content.length() <= Message.MAX_CONTENT_LENGTH){
			this.content.replace(0, this.content.length(), content);
		}
		else{
			throw new IllegalArgumentException(CONTENT_TOO_BIG);
		}
		return this;
	}

	public Interaction getInteraction(){
		return this.interaction;
	}

}
