package de.kittybot.kittybot.slashcommands.application;

import net.dv8tion.jda.api.Permission;

import java.util.Set;

public interface PermissionHolder{

	boolean isGuildOnly();

	void guildOnly();

	void devOnly();

	boolean isDevOnly();

	void addPermissions(Permission... permissions);

	Set<Permission> getPermissions();

}
