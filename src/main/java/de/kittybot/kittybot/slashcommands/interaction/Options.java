package de.kittybot.kittybot.slashcommands.interaction;

import de.kittybot.kittybot.objects.exceptions.MissingOptionException;
import de.kittybot.kittybot.objects.exceptions.OptionParseException;
import de.kittybot.kittybot.slashcommands.application.CommandOption;
import de.kittybot.kittybot.slashcommands.interaction.InteractionDataOption;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.MiscUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Options{

	private final Map<String, CommandOption<?>> definedOptions;
	private final Map<String, InteractionDataOption> options;
	private final ResolvedMentions resolvedMentions;

	public Options(List<CommandOption<?>> definedOptions, List<InteractionDataOption> options, ResolvedMentions resolvedMentions){
		this.definedOptions = definedOptions.stream().collect(Collectors.toMap(CommandOption::getName, Function.identity()));
		this.options = options.stream().collect(Collectors.toMap(InteractionDataOption::getName, Function.identity()));
		this.resolvedMentions = resolvedMentions;
	}

	public int size(){
		return this.options.size();
	}

	public boolean isEmpty(){
		return this.options.isEmpty();
	}

	@SuppressWarnings("unchecked")
	private <T> T getValue(String name, Class<T> clazz){
		var value = this.options.get(name).getValue();
		if(value == null){
			throw new MissingOptionException(name, clazz);
		}
		return (T) this.definedOptions.get(name).parseValue(value);
	}

	public String getString(String name){
		return getValue(name, String.class);
	}

	public long getLong(String name){
		return getValue(name, Long.class);
	}

	public int getInt(String name){
		return getValue(name, Integer.class);
	}

	public float getFloat(String name){
		return getValue(name, Float.class);
	}

	public boolean getBoolean(String name){
		return getValue(name, Boolean.class);
	}

	public Member getMember(String name){
		return this.resolvedMentions.getMentionedMembers().get(getLong(name));
	}

	public User getUser(String name){
		return this.resolvedMentions.getMentionedUsers().get(getLong(name));
	}

	public GuildChannel getChannel(String name){
		return this.resolvedMentions.getMentionedChannels().get(getLong(name));
	}

	public TextChannel getTextChannel(String name){
		var channel = this.resolvedMentions.getMentionedChannels().get(getLong(name));
		if(channel instanceof TextChannel){
			return (TextChannel) channel;
		}
		throw new OptionParseException("Please provide a valid text channel");
	}

	public Role getRole(String name){
		return this.resolvedMentions.getMentionedRoles().get(getLong(name));
	}

	public LocalDateTime getTime(String name){
		return getValue(name, LocalDateTime.class);
	}

	public RestAction<ListedEmote> getEmote(Guild guild, String name){
		return guild.retrieveEmoteById(getEmoteId(name));
	}

	public long getEmoteId(String name){
		var emote = getValue(name, String.class);
		var matcher = Message.MentionType.EMOTE.getPattern().matcher(emote);
		if(matcher.matches()){
			return MiscUtil.parseSnowflake(matcher.group(2));
		}
		throw new OptionParseException("Failed to parse emote id");
	}

	public String getEmoteName(String name){
		var emote = getValue(name, String.class);
		var matcher = Message.MentionType.EMOTE.getPattern().matcher(emote);
		if(matcher.matches()){
			return matcher.group(1);
		}
		throw new OptionParseException("Failed to parse emote name");
	}

	public boolean getEmoteAnimated(String name){
		return getValue(name, String.class).startsWith("<a:");
	}

	public boolean has(String name){
		return this.options.containsKey(name);
	}

	@SuppressWarnings("unchecked")
	public <T> T getOrDefault(String name, T defaultValue){
		try{
			return (T) getValue(name, defaultValue.getClass());
		}
		catch(NullPointerException e){
			return defaultValue;
		}
	}

	public InteractionDataOption get(String name){
		return this.options.get(name);
	}

	public Map<String, InteractionDataOption> getMap(){
		return this.options;
	}

	public Stream<InteractionDataOption> stream(){
		return this.options.values().stream();
	}

}
