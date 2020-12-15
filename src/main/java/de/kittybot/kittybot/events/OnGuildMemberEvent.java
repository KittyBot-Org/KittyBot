package de.kittybot.kittybot.events;

import de.kittybot.kittybot.main.KittyBot;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberUpdateEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class OnGuildMemberEvent extends ListenerAdapter{

	private final KittyBot main;

	public OnGuildMemberEvent(KittyBot main){
		this.main = main;
	}

	@Override
	public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event){

	}

	@Override
	public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event){

	}

	@Override
	public void onGuildMemberUpdate(@NotNull GuildMemberUpdateEvent event){

	}

	@Override
	public void onGuildMemberUpdateBoostTime(@NotNull GuildMemberUpdateBoostTimeEvent event){

	}

}
