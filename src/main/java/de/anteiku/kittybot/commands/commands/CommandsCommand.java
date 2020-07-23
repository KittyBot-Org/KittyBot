package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import de.anteiku.kittybot.commands.CommandContext;
import de.anteiku.kittybot.database.Database;
import de.anteiku.kittybot.objects.Emotes;
import de.anteiku.kittybot.objects.ReactiveMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.awt.*;
import java.time.Instant;
import java.util.Map;

public class CommandsCommand extends ACommand{

	public static String COMMAND = "commands";
	public static String USAGE = "commands <page>";
	public static String DESCRIPTION = "Lists all aviable commands";
	public static double PAGE_COUNT = 5;
	protected static String[] ALIAS = {"cmds"};

	public CommandsCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}

	public void nextPage(Message message){
		int page = getPageByMessage(message) + 1;
		if(page <= getMaxPages()){
			String[] args = {String.valueOf(page)};
			message.editMessage(buildCommands(args, Database.getCommandPrefix(message.getGuild().getId())).build()).queue();
		}
	}

	//TODO a lot of refactoring and optimization
	private int getPageByMessage(Message message){
		String footer = message.getEmbeds().get(0).getFooter().getText();
		return Integer.parseInt(footer.substring(footer.indexOf("Page: ") + 6, footer.indexOf("/")));

	}

	public void prevPage(Message message){
		int page = getPageByMessage(message) - 1;
		if(page >= 1){
			String[] args = {String.valueOf(page)};
			message.editMessage(buildCommands(args, Database.getCommandPrefix(message.getGuild().getId())).build()).queue();
		}
	}

	@Override
	public void run(CommandContext ctx){
		ctx.getChannel().sendMessage(buildCommands(ctx.getArgs(), Database.getCommandPrefix(ctx.getGuild().getId())).build()).queue(message -> {
			KittyBot.commandManager.addReactiveMessage(ctx, message, this, "-1");
			message.addReaction(Emotes.ARROW_LEFT.get()).queue();
			message.addReaction(Emotes.ARROW_RIGHT.get()).queue();
			message.addReaction(Emotes.WASTEBASKET.get()).queue();
		});

	}

	private EmbedBuilder buildCommands(String[] args, String prefix){
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
		for(Map.Entry<String, ACommand> c : KittyBot.commandManager.commands.entrySet()){
			if((i >= page * PAGE_COUNT) && (i < page * PAGE_COUNT + PAGE_COUNT)){
				ACommand cmd = c.getValue();
				eb.addField("**" + prefix + cmd.getCommand() + ":** ", " :small_blue_diamond:" + cmd.getDescription(), false);
			}
			i++;
		}
		eb.setFooter("Page: " + (page + 1) + "/" + getMaxPages() + " - use reaction to navigate!", "https://cdn.discordapp.com/attachments/576923247652634664/589135880963227730/download.png");
		eb.setTimestamp(Instant.now());
		return eb;
	}

	private int getMaxPages(){
		return (int) Math.ceil((double) KittyBot.commandManager.commands.size() / PAGE_COUNT);
	}

	@Override
	public void reactionAdd(ReactiveMessage reactiveMessage, GuildMessageReactionAddEvent event){
		if(event.getReactionEmote().getName().equals(Emotes.ARROW_LEFT.get())){
			event.getChannel().retrieveMessageById(event.getMessageId()).queue(this::prevPage);
			event.getReaction().removeReaction(event.getUser()).queue();
		}
		else if(event.getReactionEmote().getName().equals(Emotes.ARROW_RIGHT.get())){
			event.getChannel().retrieveMessageById(event.getMessageId()).queue(this::nextPage);
			event.getReaction().removeReaction(event.getUser()).queue();
		}
		super.reactionAdd(reactiveMessage, event);
	}

}
