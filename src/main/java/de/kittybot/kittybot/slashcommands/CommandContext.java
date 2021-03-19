package de.kittybot.kittybot.slashcommands;

import de.kittybot.kittybot.objects.enums.Emoji;
import de.kittybot.kittybot.objects.module.Module;
import de.kittybot.kittybot.objects.module.Modules;
import de.kittybot.kittybot.utils.Colors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.commands.CommandHook;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.CommandReplyAction;

import java.time.Instant;
import java.util.function.Consumer;

public class CommandContext{

	protected final Modules modules;
	protected final Options options;
	protected final SlashCommandEvent event;

	public CommandContext(Modules modules, Options options, SlashCommandEvent event){
		this.event = event;
		this.options = options;
		this.modules = modules;
	}

	public SlashCommandEvent getEvent(){
		return this.event;
	}

	public CommandHook getHook() {
		return this.event.getHook();
	}

	public Options getOptions(){
		return this.options;
	}

	public long getChannelId(){
		return getChannel().getIdLong();
	}

	public User getUser(){
		return this.event.getUser();
	}

	public long getUserId(){
		return getUser().getIdLong();
	}

	public User getSelfUser(){
		return this.event.getJDA().getSelfUser();
	}

	public MessageChannel getChannel(){
		return this.event.getChannel();
	}

	public boolean isFromGuild(){
		return this.event.getGuild() != null && this.event.getMember() != null;
	}

	public Modules getModules(){
		return this.modules;
	}

	public <T extends Module> T get(Class<T> clazz){
		return this.modules.get(clazz);
	}

	public JDA getJDA(){
		return this.event.getJDA();
	}

	public CommandReplyAction acknowledge(){
		return this.event.acknowledge();
	}

	public CommandReplyAction acknowledge(boolean ephemeral){
		return this.event.acknowledge(ephemeral);
	}

	public void reply(String message){
		this.event.reply(getEmbed().setDescription(message).build()).queue();
	}

	public void replyEphemeral(String message){
		this.event.reply(message).setEphemeral(true).queue();
	}

	public EmbedBuilder getEmbed(){
		return applyDefaultStyle(new EmbedBuilder());
	}

	public EmbedBuilder applyDefaultStyle(EmbedBuilder embedBuilder){
		String name;
		if(this instanceof GuildCommandContext){
			name = ((GuildCommandContext) this).getMember().getEffectiveName();
		}
		else{
			name = getUser().getName();
		}
		return embedBuilder.setColor(Colors.KITTYBOT_BLUE).setFooter(name, getUser().getEffectiveAvatarUrl()).setTimestamp(Instant.now());
	}

	public void reply(Consumer<EmbedBuilder> consumer){
		var embedBuilder = applyDefaultStyle(new EmbedBuilder());
		consumer.accept(embedBuilder);
		this.event.reply(embedBuilder.build()).queue();
	}

	public void error(String error){
		this.event.reply(Emoji.X.get() + " " + error).setEphemeral(true).queue();
	}

}
