package de.kittybot.kittybot.module;

import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Set;

public abstract class Module extends ListenerAdapter{

	protected Modules modules;

	public Module init(Modules modules){
		this.modules = modules;
		return this;
	}

	public Set<Class<? extends Module>> getDependencies(){
		return null;
	}

	protected void onEnable(){}

	protected void onDisable(){}

}
