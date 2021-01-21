package de.kittybot.kittybot.slashcommands.application;

import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class CommandOption implements CommandOptionsHolder{

	private final CommandOptionType type;
	private final String name, description;
	private final List<CommandOptionChoice<?>> choices;
	private final List<CommandOption> options;
	private boolean isDefault, isRequired;

	protected CommandOption(CommandOptionType type, String name, String description){
		this.type = type;
		this.name = name;
		this.description = description;
		this.isDefault = false;
		this.isRequired = false;
		this.choices = new ArrayList<>();
		this.options = new ArrayList<>();
	}

	public static DataArray toJSON(Collection<CommandOption> options){
		return DataArray.fromCollection(
			options.stream().map(CommandOption::toJSON).collect(Collectors.toList())
		);
	}

	public CommandOption addChoices(CommandOptionChoice<?>... choices){
		this.choices.addAll(List.of(choices));
		return this;
	}

	public CommandOption addOptions(CommandOption... options){
		this.options.addAll(List.of(options));
		return this;
	}

	public CommandOption setDefault(){
		this.isDefault = true;
		return this;
	}

	public CommandOption required(){
		this.isRequired = true;
		return this;
	}

	public CommandOptionType getType(){
		return this.type;
	}

	public String getName(){
		return this.name;
	}

	public String getDescription(){
		return this.description;
	}

	public boolean isDefault(){
		return this.isDefault;
	}

	public boolean isRequired(){
		return this.isRequired;
	}

	public List<CommandOptionChoice<?>> getChoices(){
		return this.choices;
	}

	@Override
	public List<CommandOption> getOptions(){
		return options;
	}

	public DataObject toJSON(){
		var json = DataObject.empty()
			.put("type", this.type.getType())
			.put("name", this.name)
			.put("description", this.description);
		if(this.isDefault){
			json.put("default", true);
		}
		if(this.isRequired){
			json.put("required", true);
		}
		if(!this.choices.isEmpty()){
			json.put("choices", CommandOptionChoice.toJSON(this.choices));
		}
		if(!this.options.isEmpty()){
			json.put("options", CommandOption.toJSON(this.options));
		}
		return json;
	}

}
