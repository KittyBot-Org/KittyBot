package de.kittybot.kittybot.command.interactions.application;

import de.kittybot.kittybot.command.interactions.interaction.Interaction;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ApplicationCommandOption{

	private final ApplicationCommandOptionType type;
	private final String name, description;
	private final boolean isDefault, isRequired;
	private final List<ApplicationCommandOptionChoice> choices;
	private final List<ApplicationCommandOption> options;

	protected ApplicationCommandOption(ApplicationCommandOptionType type, String name, String description, boolean isDefault, boolean isRequired){
		this.type = type;
		this.name = name;
		this.description = description;
		this.isDefault = isDefault;
		this.isRequired = isRequired;
		this.choices = new ArrayList<>();
		this.options = new ArrayList<>();
	}

	public abstract void run(Interaction interaction);

	protected void addChoices(ApplicationCommandOptionChoice... choices){
		this.choices.addAll(List.of(choices));
	}

	protected void addOptions(ApplicationCommandOption... options){
		this.options.addAll(List.of(options));
	}

	public DataObject toJSON(){
		return DataObject.empty()
				.put("type", this.type.getType())
				.put("name", this.name)
				.put("description", this.description)
				.put("default", this.isDefault)
				//.put("required", this.isRequired)
				.put("choices", ApplicationCommandOptionChoice.toJSON(this.choices))
				.put("options", ApplicationCommandOption.toJSON(this.options));
	}

	public static DataArray toJSON(Collection<ApplicationCommandOption> options){
		return DataArray.fromCollection(
				options.stream().map(ApplicationCommandOption::toJSON).collect(Collectors.toList())
		);
	}

}
