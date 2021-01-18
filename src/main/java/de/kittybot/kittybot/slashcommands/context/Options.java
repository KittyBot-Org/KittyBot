package de.kittybot.kittybot.slashcommands.context;

import de.kittybot.kittybot.slashcommands.interaction.InteractionDataOption;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ListedEmote;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Options{

	private final Map<String, InteractionDataOption> options;

	public Options(List<InteractionDataOption> options){
		this.options = options.stream().collect(Collectors.toMap(InteractionDataOption::getName, Function.identity()));
	}

	public int size(){
		return this.options.size();
	}

	public boolean isEmpty(){
		return this.options.isEmpty();
	}

	public <T> boolean is(String name, T value){
		var option = this.options.get(name);
		return option.getValue().equals(value);
	}

	public long getLong(String name){
		return this.options.get(name).getLong();
	}

	public int getInt(String name){
		return this.options.get(name).getInt();
	}

	public boolean getBoolean(String name){
		return this.options.get(name).getBoolean();
	}

	public String getString(String name){
		return this.options.get(name).getString();
	}

	public <T> List<T> collect(Function<? super String, T> predicate, String... names){
		return Arrays.stream(names).filter(this::has).map(predicate).collect(Collectors.toList());
	}

	public boolean has(String name){
		return this.options.containsKey(name);
	}

	public RestAction<ListedEmote> getEmote(Guild guild, String name){
		return this.options.get(name).getEmote(guild);
	}

	public long getEmoteId(String name){
		return this.options.get(name).getEmoteId();
	}

	public String getEmoteName(String name){
		return this.options.get(name).getEmoteName();
	}

	public boolean getIsAnimatedEmote(String name){
		return this.options.get(name).getIsAnimatedEmote();
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
