package de.kittybot.kittybot.command.interaction;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ListedEmote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.requests.DeferredRestAction;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.requests.restaction.AuditableRestActionImpl;
import net.dv8tion.jda.internal.utils.Checks;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class InteractionDataOption implements InteractionOptionsHolder{

	private final String name;
	private final Object value;
	private final List<InteractionDataOption> options;

	public InteractionDataOption(String name, Object value, List<InteractionDataOption> options){
		this.name = name;
		this.value = value;
		this.options = options;
	}

	public String getName(){
		return this.name;
	}

	public Object getValue(){
		return this.value;
	}

	public long getLong(){
		try{
			return Long.parseLong(getString());
		}
		catch(NumberFormatException ignored){}
		return -1;
	}

	public int getInt(){
		return Integer.parseInt(getString());
	}

	public String getString(){
		return String.valueOf(this.value);
	}

	public boolean getBoolean(){
		return Boolean.parseBoolean(getString());
	}

	public RestAction<ListedEmote> getEmote(Guild guild){
		var rawEmote = getString();
		var matcher = Message.MentionType.EMOTE.getPattern().matcher(rawEmote);
		if(!matcher.matches()){
			return null;
		}
		long emoteId;
		try{
			emoteId = MiscUtil.parseSnowflake(matcher.group(2));
		}
		catch(NumberFormatException e){
			return null;
		}
		return guild.retrieveEmoteById(emoteId);
	}

	public long getEmoteId(){
		var rawEmote = getString();
		var matcher = Message.MentionType.EMOTE.getPattern().matcher(rawEmote);
		if(!matcher.matches()){
			return -1;
		}
		try{
			return MiscUtil.parseSnowflake(matcher.group(2));
		}
		catch(NumberFormatException ignored){}
		return -1;
	}

	public String getEmoteName(){
		var matcher = Message.MentionType.EMOTE.getPattern().matcher(getString());
		if(!matcher.matches()){
			return null;
		}
		return matcher.group(1);
	}

	public boolean getIsAnimatedEmote(){
		return getString().startsWith("<a:");
	}

	public List<InteractionDataOption> getOptions(){
		return this.options;
	}

	@Override
	public String toString(){
		return "InteractionDataOption{" +
				"name='" + this.name + '\'' +
				", value='" + this.value + '\'' +
				", options=" + this.options +
				'}';
	}

	public static InteractionDataOption fromJSON(DataObject json){
		return new InteractionDataOption(
				json.getString("name"),
				json.opt("value").orElse(null),
				InteractionDataOption.fromJSON(json.optArray("options").orElse(null))
		);
	}

	public static List<InteractionDataOption> fromJSON(DataArray json){
		if(json == null){
			return new ArrayList<>();
		}
		return json.stream((array, index) -> fromJSON(array.getObject(index))).collect(Collectors.toList());
	}

}
