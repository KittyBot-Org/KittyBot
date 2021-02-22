package de.kittybot.kittybot.slashcommands.interaction;

import de.kittybot.kittybot.objects.module.Modules;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.utils.data.DataObject;

public class GuildInteraction extends Interaction{

	private final Guild guild;
	private final Member member;

	public GuildInteraction(DataObject json, InteractionData data, Guild guild, Member member, TextChannel channel, Modules modules, JDA jda){
		super(json, data, member.getUser(), channel, modules, jda);
		this.guild = guild;
		this.member = member;
	}

	public Guild getGuild(){
		return this.guild;
	}

	public long getGuildId(){
		return this.guild.getIdLong();
	}

	public Member getMember(){
		return this.member;
	}

	public Member getSelfMember(){
		return this.guild.getSelfMember();
	}

	@Override
	public TextChannel getChannel(){
		return (TextChannel) this.channel;
	}

}
