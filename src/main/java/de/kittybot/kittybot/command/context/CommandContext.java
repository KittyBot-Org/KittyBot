package de.kittybot.kittybot.command.context;

import com.jagrosh.jdautilities.oauth2.entities.OAuth2Guild;
import de.kittybot.kittybot.command.interaction.Interaction;
import de.kittybot.kittybot.command.response.Response;
import de.kittybot.kittybot.command.response.ResponseData;
import de.kittybot.kittybot.command.response.ResponseType;
import de.kittybot.kittybot.module.Modules;
import de.kittybot.kittybot.modules.InteractionsModule;
import de.kittybot.kittybot.objects.Emoji;
import de.kittybot.kittybot.utils.Colors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.awt.Color;
import java.time.Instant;

public class CommandContext extends Context{

	private final Interaction interaction;

	public CommandContext(Interaction interaction, Modules modules){
		super(modules, interaction.getGuild(), interaction.getGuild().getTextChannelById(interaction.getChannelId()));
		this.interaction = interaction;
	}

	public long getUserId(){
		return this.interaction.getMember().getIdLong();
	}

	public User getUser(){
		return this.interaction.getMember().getUser();
	}

	public Member getMember(){
		return this.interaction.getMember();
	}

	public void reply(String message){
		this.modules.get(InteractionsModule.class).reply(this.interaction, new Response.Builder().addEmbeds(getSuccessEmbed().setDescription(message).build()).build());
	}

	public void reply(EmbedBuilder embed){
		reply(embed.setFooter(getMember().getEffectiveName(), getUser().getEffectiveAvatarUrl())
				.setTimestamp(Instant.now()).build());
	}


	public void reply(MessageEmbed embed){
		this.modules.get(InteractionsModule.class).reply(this.interaction, new Response.Builder().addEmbeds(embed).build());
	}

	public void reply(Response response){
		this.modules.get(InteractionsModule.class).reply(this.interaction, response);
	}

	public void reply(ResponseData data, boolean withSource){
		ResponseType type;
		if(withSource){
			type = ResponseType.CHANNEL_MESSAGE_WITH_SOURCE;
		}
		else{
			type = ResponseType.CHANNEL_MESSAGE;
		}
		this.modules.get(InteractionsModule.class).reply(this.interaction, new Response(type, data));
	}

	public void error(String error){
		this.modules.get(InteractionsModule.class).reply(this.interaction, new Response.Builder().setType(ResponseType.ACKNOWLEDGE).setContent(Emoji.X.get() + " " + error).ephemeral().build());
	}

	public void followup(ResponseData response){
		this.modules.get(InteractionsModule.class).followUp(this.interaction, response);
	}

	public void followup(String message){
		followup(new ResponseData.Builder().addEmbeds(getSuccessEmbed().setDescription(message).build()).build());
	}

	public void followupError(String error){
		followup(new ResponseData.Builder().addEmbeds(getErrorEmbed().setDescription(error).build()).build());
	}

	public void acknowledge(boolean withSource){
		ResponseType type;
		if(withSource){
			type = ResponseType.ACKNOWLEDGE_WITH_SOURCE;
		}
		else{
			type = ResponseType.ACKNOWLEDGE;
		}
		this.modules.get(InteractionsModule.class).reply(this.interaction, new Response(type, null));
	}

	public EmbedBuilder getSuccessEmbed(){
		return getEmbed().setColor(Colors.KITTYBOT_BLUE);
	}

	public EmbedBuilder getEmbed(){
		return new EmbedBuilder()
				.setFooter(getMember().getEffectiveName(), getUser().getEffectiveAvatarUrl())
				.setTimestamp(Instant.now());
	}

	public EmbedBuilder getErrorEmbed(){
		return getEmbed().setColor(Color.RED);
	}

}
