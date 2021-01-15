package de.kittybot.kittybot.command.interactions.application;

import net.dv8tion.jda.api.utils.data.DataObject;

import java.util.ArrayList;
import java.util.List;

public abstract class ApplicationCommand{

	private final String name, description;
	private final List<ApplicationCommandOption> options;

	protected ApplicationCommand(String name, String description){
		this.name = name;
		this.description = description;
		this.options = new ArrayList<>();
	}

	protected void addOptions(ApplicationCommandOption... options){
		this.options.addAll(List.of(options));
	}

	public String getName(){
		return this.name;
	}

	public String getDescription(){
		return this.description;
	}

	public List<ApplicationCommandOption> getOptions(){
		return this.options;
	}

	public DataObject toJSON(){
		return DataObject.empty()
				.put("name", this.name)
				.put("description", this.description)
				.put("options", ApplicationCommandOption.toJSON(this.options));
	}

}
