package de.kittybot.kittybot.slashcommands.application.options;

import de.kittybot.kittybot.slashcommands.application.CommandOption;
import de.kittybot.kittybot.slashcommands.application.PermissionHolder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Command;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class SubBaseCommand extends CommandOption<Void> implements PermissionHolder{

	private final Set<Permission> permissions;
	private boolean guildOnly;
	private boolean devOnly;

	public SubBaseCommand(String name, String description, boolean guildOnly){
		super(Command.OptionType.SUB_COMMAND, name, description);
		this.guildOnly = guildOnly;
		this.devOnly = false;
		this.permissions = new HashSet<>();
	}

	public void guildOnly() {
		this.guildOnly = true;
	}

	public boolean isGuildOnly(){
		return this.guildOnly;
	}

	public void devOnly(){
		this.devOnly = true;
	}

	public boolean isDevOnly(){
		return this.devOnly;
	}

	public void addPermissions(Permission... permissions){
		this.permissions.addAll(Arrays.asList(permissions));
	}

	public Set<Permission> getPermissions(){
		return this.permissions;
	}

	@Override
	public Void parseValue(SlashCommandEvent.OptionData optionData){
		throw new UnsupportedOperationException("This is SubCommand");
	}

	@Override
	public DataObject toJSON(){
		return super.toJSON()
			.put("permissions", this.permissions.stream().map(Permission::getName).collect(Collectors.toList()))
			.put("dev_only", this.devOnly);
	}

}
