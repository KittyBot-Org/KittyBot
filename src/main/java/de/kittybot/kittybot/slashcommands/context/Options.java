package de.kittybot.kittybot.slashcommands.context;

import de.kittybot.kittybot.slashcommands.application.CommandOption;
import de.kittybot.kittybot.slashcommands.interaction.InteractionDataOption;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ListedEmote;
import net.dv8tion.jda.api.requests.RestAction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Options{

	private final Map<String, CommandOption<?>> definedOptions;
	private final Map<String, InteractionDataOption> options;

	public Options(List<CommandOption<?>> definedOptions, List<InteractionDataOption> options){
		this.definedOptions = definedOptions.stream().collect(Collectors.toMap(CommandOption::getName, Function.identity()));
		this.options = options.stream().collect(Collectors.toMap(InteractionDataOption::getName, Function.identity()));
	}

	public int size(){
		return this.options.size();
	}

	public boolean isEmpty(){
		return this.options.isEmpty();
	}

	@SuppressWarnings("unchecked")
	private  <T> T getValue(String name, Class<T> clazz){
		return (T) this.definedOptions.get(name).parseValue(this.options.get(name).getValue());
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

	public String getString(String name){
		return getValue(name, String.class);
	}

	public LocalDateTime getTime(String name){
		return getValue(name, LocalDateTime.class);
	}

	/*
	public Emote getEmote(Guild guild, String name){
		return getValue(name, Emote.class);
	}*/

	public long getEmoteId(String name){
		return getValue(name, Long.class);
	}

	public RestAction<ListedEmote> getEmote(Guild guild, String name){
		return guild.retrieveEmoteById(getEmoteId(name));
	}

	/*
	public String getEmoteName(String name){
		return getValue(name, String.class);
	}*/

	/*
	public boolean getIsAnimatedEmote(String name){
		return getValue(name, Boolean.class);
	}*/

	public boolean has(String name){
		return this.options.containsKey(name);
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
