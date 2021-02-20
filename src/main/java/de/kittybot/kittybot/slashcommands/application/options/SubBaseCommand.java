package de.kittybot.kittybot.slashcommands.application.options;

import de.kittybot.kittybot.slashcommands.application.CommandOption;
import de.kittybot.kittybot.slashcommands.application.CommandOptionType;
import de.kittybot.kittybot.slashcommands.application.PermissionHolder;
import de.kittybot.kittybot.slashcommands.application.RunnableCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class SubBaseCommand extends CommandOption<Void> implements PermissionHolder{

	private final Set<Permission> permissions;
	private boolean devOnly;

	public SubBaseCommand(String name, String description){
		super(CommandOptionType.SUB_COMMAND, name, description);
		this.devOnly = false;
		this.permissions = new HashSet<>();
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
	public Void parseValue(Object value){
		throw new UnsupportedOperationException("This is SubCommand");
	}

	@Override
	public DataObject toDetailedJSON(){
		return super.toDetailedJSON()
			.put("permissions", this.permissions.stream().map(Permission::getName).collect(Collectors.toList()))
			.put("dev_only", this.devOnly);
	}

}
