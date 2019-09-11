package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.API;
import de.anteiku.kittybot.Emotes;
import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.Logger;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.*;
import java.util.regex.Pattern;

public class CommandManager{
	
	public Map<String, Command> commands;
	public Map<Long, Long> msgCtrl = new HashMap<>();
	public Map<Long, Command> controllableMsgs = new HashMap<>();
	public Map<Long, Long> commandMessages = new HashMap<>();
	private KittyBot main;
	
	public CommandManager(KittyBot main){
		this.main = main;
		commands = new LinkedHashMap<>();
	}
	
	public void add(Command cmd){
		commands.put(cmd.getCommand(), cmd);
	}
	
	public void addListenerCmd(long message, long command, Command cmd, long allowed){
		msgCtrl.put(message, allowed);
		controllableMsgs.put(message, cmd);
		commandMessages.put(message, command);
	}
	
	public void addListenerCmd(long message, Message command, Command cmd, long allowed){
		addListenerCmd(message, command.getIdLong(), cmd, allowed);
	}
	
	public void addListenerCmd(Message message, long command, Command cmd, long allowed){
		addListenerCmd(message.getIdLong(), command, cmd, allowed);
	}
	
	public void addListenerCmd(Message message, Message command, Command cmd, long allowed){
		addListenerCmd(message.getIdLong(), command.getIdLong(), cmd, allowed);
	}
	
	public void checkCommands(GuildMessageReceivedEvent event){
		long start = System.nanoTime();
		String message = event.getMessage().getContentRaw();
		String prefix = main.database.getCommandPrefix(event.getGuild().getId());
		String command = getCommand(message, prefix);
		for(Map.Entry<String, Command> c : commands.entrySet()){
			Command cmd;
			if((cmd = c.getValue()).checkCmd(command)){
				event.getChannel().sendTyping().complete();
				try{
					String[] args = getArgs(message, prefix);
					cmd.run(args, event);
					Logger.print("Command: '" + command + "' by: '" + event.getAuthor().getName() + "' from: '" + event.getGuild().getName() + "' took '" + API.getMs(start) + "'ms");
				}
				catch(ArgumentException e){
					Message msg = cmd.sendError(event.getChannel(), e.getMessage());
					addListenerCmd(msg, event.getMessage(), cmd, - 1L);
					msg.addReaction(Emotes.QUESTIONMARK).queue();
					Logger.error(e);
				}
				return;
			}
		}
	}
	
	private String getCommand(String raw, String prefix){
		return raw.split(" ")[0].replaceFirst(Pattern.quote(prefix), "");
	}
	
	private String[] getArgs(String raw, String prefix) throws ArgumentException{
		String command = getCommand(raw, prefix);
		String rawraw = raw.substring(command.length() + 1).trim();
		boolean b = false;
		String string = "";
		ArrayList<String> args = new ArrayList<>();
		for(String s : rawraw.split(" ")){
			if(s.startsWith("(") && s.endsWith(")") && ! b){
				args.add(s.substring(1, s.length() - 1));
				continue;
			}
			else if((s.startsWith("(") && b)){
				throw new ArgumentException("Missing '(' in: '" + raw + "'\n" + Emotes.WHITESPACE.repeat("Missing '(' in: '".length() + raw.indexOf(s)) + "^");
			}
			else if(s.endsWith(")") && ! b){
				throw new ArgumentException("Missing ')' in: '" + raw + "'\n" + Emotes.WHITESPACE.repeat("Missing '(' in: '".length() + raw.indexOf(s)) + "^");
			}
			
			if(s.startsWith("(")){
				b = true;
			}
			
			if(b){
				string += s + " ";
			}
			else{
				if(! s.equals("")){
					args.add(s);
				}
			}
			
			if(s.endsWith(")")){
				b = false;
				args.add(string.substring(1, string.length() - 2));
				string = "";
			}
		}
		if(b){
			throw new ArgumentException("Missing ')' in: '" + raw + "'\n" + Emotes.WHITESPACE.repeat(("Missing '(' in: '").length() + raw.length()) + "^");
		}
		return Arrays.copyOf(args.toArray(), args.size(), String[].class);
	}
	
}
