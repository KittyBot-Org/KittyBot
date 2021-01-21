package de.kittybot.kittybot.slashcommands.context;

import de.kittybot.kittybot.modules.InteractionsModule;
import de.kittybot.kittybot.objects.enums.Emoji;
import de.kittybot.kittybot.objects.module.Modules;
import de.kittybot.kittybot.slashcommands.interaction.Interaction;
import de.kittybot.kittybot.slashcommands.interaction.response.FollowupMessage;
import de.kittybot.kittybot.slashcommands.interaction.response.InteractionRespondAction;
import de.kittybot.kittybot.slashcommands.interaction.response.InteractionResponse;
import de.kittybot.kittybot.slashcommands.interaction.response.InteractionResponseType;
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

	public Interaction getInteraction(){
		return this.interaction;
	}

	public long getUserId(){
		return this.interaction.getMember().getIdLong();
	}

	public void reply(String message){
		this.modules.get(InteractionsModule.class).reply(this.interaction).embeds(getSuccessEmbed().setDescription(message).build()).queue();
	}

	public EmbedBuilder getSuccessEmbed(){
		return getEmbed().setColor(Colors.KITTYBOT_BLUE);
	}

	public EmbedBuilder getEmbed(){
		return new EmbedBuilder()
			.setFooter(getMember().getEffectiveName(), getUser().getEffectiveAvatarUrl())
			.setTimestamp(Instant.now());
	}

	public Member getMember(){
		return this.interaction.getMember();
	}

	public User getUser(){
		return this.interaction.getMember().getUser();
	}

	public void reply(EmbedBuilder embed){
		reply(embed.setFooter(getMember().getEffectiveName(), getUser().getEffectiveAvatarUrl())
			.setTimestamp(Instant.now()).build());
	}

	public void reply(MessageEmbed embed){
		this.modules.get(InteractionsModule.class).reply(this.interaction).embeds(embed).queue();
	}

	public void reply(InteractionResponse response){
		this.modules.get(InteractionsModule.class).reply(this.interaction).fromData(response).queue();
	}

	public void reply(InteractionResponse data, boolean withSource){
		this.modules.get(InteractionsModule.class).reply(this.interaction, withSource).fromData(data).queue();
	}

	public void error(String error){
		this.modules.get(InteractionsModule.class).reply(this.interaction).type(InteractionResponseType.ACKNOWLEDGE).content(Emoji.X.get() + " " + error).ephemeral().queue();
	}

	public void followup(String message){
		followup(new FollowupMessage.Builder().setEmbeds(getSuccessEmbed().setDescription(message).build()).build());
	}

	public void followup(FollowupMessage message){
		this.modules.get(InteractionsModule.class).followup(this.interaction, message).queue(null);
	}

	public void followupError(String error){
		followup(new FollowupMessage.Builder().setEmbeds(getErrorEmbed().setDescription(error).build()).build());
	}

	public EmbedBuilder getErrorEmbed(){
		return getEmbed().setColor(Color.RED);
	}

	public void sendAcknowledge(){
		sendAcknowledge(true);
	}

	public void sendAcknowledge(boolean withSource){
		acknowledge(withSource).queue();
	}

	public InteractionRespondAction acknowledge(boolean withSource){
		return this.modules.get(InteractionsModule.class).acknowledge(interaction, withSource);
	}

}
