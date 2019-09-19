package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.Emotes;
import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;

import java.awt.*;
import java.util.Map;

public class CommandsCommand extends Command{
	
	public static String COMMAND = "commands";
	public static String USAGE = "commands <page>";
	public static String DESCRIPTION = "Lists all aviable commands";
	public static String[] ALIAS = {"cmds"};
	
	public static double PAGECOUNT = 5;
	
	public CommandsCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}
	
	public void nextPage(Message message){
		String footer = message.getEmbeds().get(0).getFooter().getText();
		int page = Integer.parseInt(footer.substring(footer.indexOf("Page: ") + 6, footer.indexOf("/"))) + 1;
		if(page <= getMaxPages()){
			String[] args = {String.valueOf(page)};
			message.editMessage(buildCommands(args, main.database.getCommandPrefix(message.getGuild().getId()))).queue();
		}
	}
	
	public void prevPage(Message message){
		String footer = message.getEmbeds().get(0).getFooter().getText();
		int page = Integer.parseInt(footer.substring(footer.indexOf("Page: ") + 6, footer.indexOf("/"))) - 1;
		if(page >= 1){
			String[] args = {String.valueOf(page)};
			message.editMessage(buildCommands(args, main.database.getCommandPrefix(message.getGuild().getId()))).queue();
		}
	}
	
	@Override
	public void reactionAdd(Message command, GuildMessageReactionAddEvent event){
		if(event.getReactionEmote().getName().equals(Emotes.ARROW_LEFT.get())){
			prevPage(event.getChannel().getMessageById(event.getMessageId()).complete());
			event.getReaction().removeReaction(event.getUser()).queue();
		}
		else if(event.getReactionEmote().getName().equals(Emotes.ARROW_RIGHT.get())){
			nextPage(event.getChannel().getMessageById(event.getMessageId()).complete());
			event.getReaction().removeReaction(event.getUser()).queue();
		}
		super.reactionAdd(command, event);
	}
	
	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		Message message = event.getChannel().sendMessage(buildCommands(args, main.database.getCommandPrefix(event.getGuild().getId()))).complete();
		main.commandManager.addListenerCmd(message, event.getMessage(), this, - 1L);
		
		message.addReaction(Emotes.ARROW_LEFT.get()).queue();
		message.addReaction(Emotes.ARROW_RIGHT.get()).queue();
		message.addReaction(Emotes.WASTEBASKET.get()).queue();
	}
	
	private MessageEmbed buildCommands(String[] args, String prefix){
		Map<String, Command> commands = main.commandManager.commands;
		int page = 0;
		if(args.length == 1){
			page = Integer.parseInt(args[0]) - 1;
		}
		else if(args.length != 0){
			return null;
		}
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.CYAN);
		eb.setDescription("For more specified information use `" + prefix + "<command> ?`");
		
		int i = 0;
		for(Map.Entry<String, Command> c : commands.entrySet()){
			if((i >= page * PAGECOUNT) && (i < page * PAGECOUNT + PAGECOUNT)){
				Command cmd = c.getValue();
				eb.addField("**" + prefix + cmd.getCommand() + ":** ", " :small_blue_diamond:" + cmd.getDescription(), false);
			}
			i++;
		}
		eb.setFooter("Page: " + (page + 1) + "/" + getMaxPages() + " - use reaction to navigate!", "https://cdn.discordapp.com/attachments/576923247652634664/589135880963227730/download.png");
		return eb.build();
	}
	
	private int getMaxPages(){
		return (int)Math.ceil((double) main.commandManager.commands.size() / PAGECOUNT);
	}
	
}
