package de.kittybot.kittybot.slashcommands;

import de.kittybot.kittybot.slashcommands.application.CommandOption;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.entities.InviteImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Options{

	private final Map<String, SlashCommandEvent.OptionData> options;
	private final Map<String, CommandOption<?>> definedOptions;

	public Options(List<SlashCommandEvent.OptionData> options, List<CommandOption<?>> definedOptions){
		this.options = options.stream().collect(Collectors.toMap(SlashCommandEvent.OptionData::getName, Function.identity()));
		this.definedOptions = definedOptions.stream().collect(Collectors.toMap(CommandOption::getName, Function.identity()));
	}

	public boolean has(String name){
		return this.options.containsKey(name);
	}

	public SlashCommandEvent.OptionData get(String name){
		return this.options.get(name);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String name, Class<T> clazz){
		return (T) this.definedOptions.get(name).parseValue(get(name));
	}

	public String getString(String name){
		return get(name, String.class);
	}

	public String getString(String name, String defaultString){
		return has(name) ? getString(name) : defaultString;
	}

	public int getInt(String name){
		return get(name, Integer.class);
	}

	public int getInt(String name, int defaultInt){
		return has(name) ? getInt(name) : defaultInt;
	}

	public float getFloat(String name){
		return get(name, Float.class);
	}

	public float getFloat(String name, float defaultFloat){
		return has(name) ? getFloat(name) : defaultFloat;
	}

	public boolean getBoolean(String name){
		return get(name, Boolean.class);
	}

	public boolean getBoolean(String name, boolean defaultBoolean){
		return has(name) ? getBoolean(name) : defaultBoolean;
	}

	public Member getMember(String name){
		return this.options.get(name).getAsMember();
	}

	public Long getLong(String name){
		return get(name, Long.class);
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

	public MessageChannel getMessageChannel(String name){
		return get(name, MessageChannel.class);
	}

	public GuildChannel getGuildChannel(String name){
		return get(name, GuildChannel.class);
	}

	public Role getRole(String name){
		return get(name, Role.class);
	}

	public LocalDateTime getTime(String name){
		return get(name, LocalDateTime.class);
	}

	public RestAction<ListedEmote> getEmote(Guild guild, String name){
		return guild.retrieveEmoteById(getEmoteId(name));
	}

	public long getEmoteId(String name){
		return get(name, Long.class);
	}

	public String getEmoteName(String name){
		return get(name, String.class);
	}

	public boolean getEmoteAnimated(String name){
		return get(name).getAsString().startsWith("<a:");
	}

	public Map<String, SlashCommandEvent.OptionData> getMap(){
		return this.options;
	}

	public Stream<SlashCommandEvent.OptionData> stream(){
		return this.options.values().stream();
	}

}
