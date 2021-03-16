package de.kittybot.kittybot.slashcommands.application;

import net.dv8tion.jda.api.entities.Command;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class CommandOption<T> implements CommandOptionsHolder{

	private final Command.OptionType type;
	private final String name, description;
	private final List<OptionChoice> choices;
	private final List<CommandOption<?>> options;
	private boolean isRequired;

	protected CommandOption(Command.OptionType type, String name, String description){
		this.type = type;
		this.name = name.toLowerCase();
		this.description = description;
		this.isRequired = false;
		this.choices = new ArrayList<>();
		this.options = new ArrayList<>();
	}

	public abstract T parseValue(SlashCommandEvent.OptionData optionData);

	public CommandOption<?> addChoices(OptionChoice... choices){
		if(this.choices.size() + choices.length > 25){
			throw new IllegalArgumentException("Options can have up to 25 choices");
		}
		this.choices.addAll(List.of(choices));
		return this;
	}

	public CommandOption<?> addOptions(CommandOption<?>... options){
		this.options.addAll(List.of(options));
		return this;
	}

	public CommandOption<?> required(){
		this.isRequired = true;
		return this;
	}

	public Command.OptionType getType(){
		return this.type;
	}

	public String getName(){
		return this.name;
	}

	public String getDescription(){
		return this.description;
	}

	public boolean isRequired(){
		return this.isRequired;
	}

	public List<OptionChoice> getChoices(){
		return this.choices;
	}

	@Override
	public List<CommandOption<?>> getOptions(){
		return options;
	}

	public CommandUpdateAction.OptionData toData(){
		var data = new CommandUpdateAction.OptionData(this.type, this.name, this.description);
		data.setRequired(this.isRequired);
		for(var choice : this.choices){
			var value = choice.getValue();
			if(value instanceof Integer){
				data.addChoice(choice.getName(), (Integer) choice.getValue());
			}
			else if(value instanceof String){
				data.addChoice(choice.getName(), (String) choice.getValue());
			}
		}
		return data;
	}

	public DataObject toJSON(){
		var json = DataObject.empty()
			.put("type", this.type.getKey())
			.put("name", this.name)
			.put("description", this.description);
		if(this.isRequired){
			json.put("required", true);
		}
		if(!this.choices.isEmpty()){
			json.put("choices",  DataArray.fromCollection(choices.stream().map(OptionChoice::toJSON).collect(Collectors.toList())));
		}
		if(!this.options.isEmpty()){
			json.put("options", DataArray.fromCollection(options.stream().map(CommandOption::toJSON).collect(Collectors.toList())));
		}
		return json;
	}

}
