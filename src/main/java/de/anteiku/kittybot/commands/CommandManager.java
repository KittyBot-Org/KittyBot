package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.utils.API;
import de.anteiku.kittybot.utils.Logger;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.*;
import java.util.regex.Pattern;

public class CommandManager{
	
	public Map<String, ACommand> commands;
	public Map<Long, Long> msgCtrl = new HashMap<>();
	public Map<Long, ACommand> controllableMsgs = new HashMap<>();
	public Map<Long, Long> commandMessages = new HashMap<>();
	private KittyBot main;
	
	public CommandManager(KittyBot main){
		this.main = main;
		commands = new LinkedHashMap<>();
	}
	
	public void add(ACommand cmd){
		commands.put(cmd.getCommand(), cmd);
	}
	
	public void addListenerCmd(long message, Message command, ACommand cmd, long allowed){
		addListenerCmd(message, command.getIdLong(), cmd, allowed);
	}
	
	public void addListenerCmd(long message, long command, ACommand cmd, long allowed){
		msgCtrl.put(message, allowed);
		controllableMsgs.put(message, cmd);
		commandMessages.put(message, command);
	}
	
	public void addListenerCmd(Message message, long command, ACommand cmd, long allowed){
		addListenerCmd(message.getIdLong(), command, cmd, allowed);
	}
	
	public void addListenerCmd(Message message, Message command, ACommand cmd, long allowed){
		addListenerCmd(message.getIdLong(), command.getIdLong(), cmd, allowed);
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
