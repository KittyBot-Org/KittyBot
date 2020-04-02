package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.utils.API;
import de.anteiku.kittybot.utils.Logger;
import de.anteiku.kittybot.utils.ReactiveMessage;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.*;
import java.util.regex.Pattern;

public class CommandManager{
	
	public Map<String, ACommand> commands;
	private KittyBot main;
	
	public CommandManager(KittyBot main){
		this.main = main;
		commands = new LinkedHashMap<>();
	}
	
	public void add(ACommand cmd){
		commands.put(cmd.getCommand(), cmd);
	}
	
	public void addReactiveMessage(GuildMessageReceivedEvent event, Message message, ACommand cmd, String allowed){
		main.database.addReactiveMessage(event.getGuild().getId(), event.getAuthor().getId(), message.getId(), event.getMessage().getId(), cmd.command, allowed);
	}
	
	public void removeReactiveMessage(Guild guild, String messageId) {
		main.database.removeReactiveMessage(guild.getId(), messageId);
	}
	
	public ReactiveMessage getReactiveMessage(Guild guild, String message) {
		return main.database.isReactiveMessage(guild.getId(), message);
	}

	public void checkCommands(GuildMessageReceivedEvent event){
		long start = System.nanoTime();
		String message = event.getMessage().getContentRaw();
		String prefix = main.database.getCommandPrefix(event.getGuild().getId());
		String command = getCommand(message, prefix);
		for(Map.Entry<String, ACommand> c : commands.entrySet()){
			ACommand cmd = c.getValue();
			if(cmd.checkCmd(command)){
				event.getChannel().sendTyping().queue();
				String[] args = getArgs(message, prefix);
				if(args == null){
					cmd.sendError(event.getMessage(), "Please fix your");
					return;
				}
				cmd.run(args, event);
				Logger.print("Command: '" + command + "' by: '" + event.getAuthor().getName() + "' from: '" + event.getGuild().getName() + "' took '" + API.getMs(start) + "'ms");
			}
		}
	}
	
	private String getCommand(String raw, String prefix){
		return raw.split(" ")[0].replaceFirst(Pattern.quote(prefix), "");
	}
	
	private String[] getArgs(String message, String prefix){
		String command = getCommand(message, prefix);
		String raw = message.substring(command.length() + 1).trim();
		boolean b = false;
		StringBuilder string = new StringBuilder();
		ArrayList<String> args = new ArrayList<>();
		for(String s : raw.split(" ")){
			if(s.startsWith("(") && s.endsWith(")") && ! b){
				args.add(s.substring(1, s.length() - 1));
				continue;
			}
			else if((s.startsWith("(") && b)){
				return null;
				//throw new ArgumentException("Missing '(' in: '" + raw + "'\n" + Emotes.WHITESPACE.repeat("Missing '(' in: '".length() + raw.indexOf(s)) + "^");
			}
			else if(s.endsWith(")") && ! b){
				return null;
				//throw new ArgumentException("Missing ')' in: '" + raw + "'\n" + Emotes.WHITESPACE.repeat("Missing '(' in: '".length() + raw.indexOf(s)) + "^");
			}
			if(s.startsWith("(")){
				b = true;
			}
			if(b){
				string.append(s).append(" ");
			}
			else{
				if(! s.equals("")){
					args.add(s);
				}
			}
			if(s.endsWith(")")){
				b = false;
				args.add(string.substring(1, string.length() - 2));
				string.delete(0, string.length());
			}
		}
		if(b){
			return null;
			//throw new ArgumentException("Missing ')' in: '" + raw + "'\n" + Emotes.WHITESPACE.repeat(("Missing '(' in: '").length() + raw.length()) + "^");
		}
		return Arrays.copyOf(args.toArray(), args.size(), String[].class);
	}
	
}
