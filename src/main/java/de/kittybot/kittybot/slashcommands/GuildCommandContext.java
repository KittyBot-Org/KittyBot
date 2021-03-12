package de.kittybot.kittybot.slashcommands;

import de.kittybot.kittybot.objects.module.Modules;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class GuildCommandContext extends CommandContext{

	public GuildCommandContext(Modules modules, Options options, SlashCommandEvent event){
		super(modules, options, event);
	}

	public Guild getGuild(){
		return this.event.getGuild();
	}

	public long getGuildId(){
		return getGuild().getIdLong();
	}

	public Member getMember(){
		return this.event.getMember();
	}

	public Member getSelfMember(){
		return getGuild().getSelfMember();
	}

	@Override
	public TextChannel getChannel(){
		return (TextChannel) super.getChannel();
	}

}
