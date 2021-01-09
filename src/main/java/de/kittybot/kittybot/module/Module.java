package de.kittybot.kittybot.module;

import net.dv8tion.jda.api.hooks.ListenerAdapter;

public abstract class Module extends ListenerAdapter{

	protected Modules modules;

	public final Module init(Modules modules){
		this.modules = modules;
		return this;
	}

	protected void onEnable(){}

	protected void onDisable(){}

}
