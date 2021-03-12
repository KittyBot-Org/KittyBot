package de.kittybot.kittybot.slashcommands;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.RestAction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Options{

	private final Map<String, SlashCommandEvent.OptionData> options;

	public Options(List<SlashCommandEvent.OptionData> options){
		this.options = options.stream().collect(Collectors.toMap(SlashCommandEvent.OptionData::getName, Function.identity()));
	}

	public int size(){
		return this.options.size();
	}

	public boolean isEmpty(){
		return this.options.isEmpty();
	}

	public SlashCommandEvent.OptionData get(String name){
		return this.options.get(name);
	}

	public String getString(String name){
		return get(name).getAsString();
	}

	public String getString(String name, String defaultString){
		return has(name) ? getString(name) : defaultString;
	}

	public int getInt(String name){
		return (int) get(name).getAsLong();
	}

	public int getInt(String name, int defaultInt){
		return has(name) ? getInt(name) : defaultInt;
	}

	public float getFloat(String name){
		return 0f;// TODO
	}

	public boolean getBoolean(String name){
		return get(name).getAsBoolean();
	}

	public boolean getBoolean(String name, boolean defaultBoolean){
		return has(name) ? getBoolean(name) : defaultBoolean;
	}

	public Member getMember(String name){
		return this.options.get(name).getAsMember();
	}

	public Long getLong(String name){
		return get(name).getAsLong();
	}

	public long getLong(String name, long defaultLong){
		return has(name) ? getLong(name) : defaultLong;
	}

	public User getUser(String name){
		return get(name).getAsUser();
	}

	public User getUser(String name, User user){
		return has(name) ? getUser(name) : user;
	}

	public GuildChannel getChannel(String name){
		return null;// TODO get(name).getAsLong();
	}

	public TextChannel getTextChannel(String name){
		return null;//TODO
		//throw new OptionParseException("Please provide a valid text channel");
	}

	public Role getRole(String name){
		return get(name).getAsRole();
	}

	public LocalDateTime getTime(String name){
		return null;//TODO
	}

	public RestAction<ListedEmote> getEmote(Guild guild, String name){
		return guild.retrieveEmoteById(getEmoteId(name));
	}

	public long getEmoteId(String name){
		/*var emote = getValue(name, String.class);
		var matcher = Message.MentionType.EMOTE.getPattern().matcher(emote);
		if(matcher.matches()){
			return MiscUtil.parseSnowflake(matcher.group(2));
		}
		throw new OptionParseException("Failed to parse emote id");*/
		return get(name).getAsLong();
	}

	public String getEmoteName(String name){
		/*var emote = getValue(name, String.class);
		var matcher = Message.MentionType.EMOTE.getPattern().matcher(emote);
		if(matcher.matches()){
			return matcher.group(1);
		}
		throw new OptionParseException("Failed to parse emote name");*/
		return get(name).getAsString();
	}

	public boolean getEmoteAnimated(String name){
		return get(name).getAsString().startsWith("<a:");
	}

	public boolean has(String name){
		return this.options.containsKey(name);
	}

	public Map<String, SlashCommandEvent.OptionData> getMap(){
		return this.options;
	}

	public Stream<SlashCommandEvent.OptionData> stream(){
		return this.options.values().stream();
	}

}
