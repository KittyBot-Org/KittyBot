package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.Emotes;
import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;

import java.awt.*;

public abstract class Command{
	
	protected KittyBot main;
	protected String command;
	protected String usage;
	protected String description;
	protected String[] alias;
	
	protected Command(KittyBot main, String command, String usage, String description, String[] alias){
		this.main = main;
		this.command = command;
		this.usage = usage;
		this.description = description;
		this.alias = alias;
	}
	
	abstract void run(String[] paramArrayOfString, GuildMessageReceivedEvent paramGuildMessageReceivedEvent);
	
	boolean checkCmd(String cmd){
		if(cmd.equalsIgnoreCase(command)){
			return true;
		}
		for(String a : alias){
			if(a.equalsIgnoreCase(cmd)){
				return true;
			}
		}
		return false;
	}
	
	public String[] getAlias(){
		return alias;
	}
	
	public String getCommand(){
		return command;
	}
	
	public String getDescription(){
		return description;
	}
	
	public String getUsage(){
		return usage;
	}
	
	public void reactionAdd(Message command, GuildMessageReactionAddEvent event){
		if(event.getReactionEmote().getName().equals(Emotes.WASTEBASKET)){
			event.getChannel().deleteMessageById(event.getMessageId()).queue();
			command.delete().queue();
		}
		else if(event.getReactionEmote().getName().equals(Emotes.QUESTIONMARK)){
			event.getReaction().removeReaction(event.getUser()).queue();
			sendUsage(event.getChannel());
		}
	}
	
	protected Message sendPrivate(PrivateChannel channel, String message){
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.GREEN);
		eb.setDescription(message);
		
		return channel.sendMessage(eb.build()).complete();
	}
	
	protected Message sendPrivate(PrivateChannel channel, EmbedBuilder eb){
		return channel.sendMessage(eb.build()).complete();
	}
	
	protected Message sendAnswer(TextChannel channel, String answer){
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.GREEN);
		eb.setDescription(answer);
		
		return channel.sendMessage(eb.build()).complete();
	}
	
	protected Message sendAnswer(TextChannel channel, MessageEmbed answer){
		return channel.sendMessage(answer).complete();
	}
	
	protected Message sendError(TextChannel channel, String error){
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.RED);
		eb.addField("Error:", "`" + error + "`", true);
		
		return channel.sendMessage(eb.build()).complete();
	}
	
	protected Message sendUsage(TextChannel channel){
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.ORANGE);
		eb.addField("Command usage:", "`" + main.database.getCommandPrefix(channel.getGuild().getId()) + usage + "`", true);
		
		return channel.sendMessage(eb.build()).complete();
	}
	
	protected Message sendUsage(TextChannel channel, String usage){
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.ORANGE);
		eb.addField("Command usage:", "`" + main.database.getCommandPrefix(channel.getGuild().getId()) + usage + "`", true);
		
		return channel.sendMessage(eb.build()).complete();
	}
	
}
