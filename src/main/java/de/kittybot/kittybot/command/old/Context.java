package de.kittybot.kittybot.command.old;

import de.kittybot.kittybot.module.Module;
import de.kittybot.kittybot.module.Modules;
import net.dv8tion.jda.api.entities.Guild;

public class Context{

	protected final Modules modules;
	protected final net.dv8tion.jda.api.entities.Guild guild;

	protected Context(Modules modules, Guild guild){
		this.modules = modules;
		this.guild = guild;
	}

	public Modules getModules(){
		return this.modules;
	}

	public <T extends Module> T get(Class<T> clazz){
		return this.modules.get(clazz);
	}

	public Guild getGuild(){
		return this.guild;
	}

	public long getGuildId(){
		return this.guild.getIdLong();
	}

}
