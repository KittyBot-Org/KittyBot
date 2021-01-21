package de.kittybot.kittybot.slashcommands.application.options;

import de.kittybot.kittybot.slashcommands.application.CommandOption;
import de.kittybot.kittybot.slashcommands.application.CommandOptionType;
import de.kittybot.kittybot.slashcommands.application.PermissionHolder;
import net.dv8tion.jda.api.Permission;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SubCommandGroup extends CommandOption implements PermissionHolder{

	private final Set<Permission> permissions;
	private boolean devOnly;

	public SubCommandGroup(String name, String description){
		super(CommandOptionType.SUB_COMMAND_GROUP, name, description);
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

}
