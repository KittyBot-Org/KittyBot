package de.kittybot.kittybot.slashcommands.interaction;

import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.entities.GuildImpl;

import java.util.List;

public class InteractionData implements InteractionOptionsHolder{

	private final long id;
	private final String name;
	private final List<InteractionDataOption> options;
	private final ResolvedMentions resolvedMentions;

	public InteractionData(long id, String name, List<InteractionDataOption> options, ResolvedMentions resolvedMentions){
		this.id = id;
		this.name = name;
		this.options = options;
		this.resolvedMentions = resolvedMentions;
	}

	public static InteractionData fromJSON(DataObject json, EntityBuilder entityBuilder, GuildImpl guild){
		return new InteractionData(
			json.getLong("id"),
			json.getString("name"),
			InteractionDataOption.fromJSON(json.optArray("options").orElse(null)),
			new ResolvedMentions(json.optObject("resolved").orElse(DataObject.empty()), entityBuilder, guild)
		);
	}

	public long getId(){
		return this.id;
	}

	public String getName(){
		return this.name;
	}

	public List<InteractionDataOption> getOptions(){
		return this.options;
	}

	public ResolvedMentions getResolvedMentions(){
		return this.resolvedMentions;
	}

}
