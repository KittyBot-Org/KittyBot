package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.database.Database;
import de.anteiku.kittybot.utils.ReactiveMessage;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class CommandManager{

	private static final Logger LOG = LoggerFactory.getLogger(CommandManager.class);
	public final Map<String, ACommand> commands;
	private final Map<String, String> commandResponses;
	private final Map<String, MusicPlayer> musicPlayers;

	public CommandManager(){
		this.commands = new LinkedHashMap<>();
		this.commandResponses = new HashMap<>();
		this.musicPlayers = new HashMap<>();
	}

	public CommandManager addCommands(ACommand... commands){
		for(ACommand command : commands){
			this.commands.put(command.getCommand(), command);
		}
		return this;
	}

	public void addCommandResponse(Message command, Message response){
		commandResponses.put(command.getId(), response.getId());
	}

	public void processCommandResponseDelete(TextChannel channel, String command){
		var commandResponse = commandResponses.get(command);
		if(commandResponse != null){
			channel.deleteMessageById(commandResponse).queue();
		}
	}

	public void addReactiveMessage(CommandContext ctx, Message message, ACommand cmd, String allowed){
		Database.addReactiveMessage(ctx.getGuild().getId(), ctx.getUser().getId(), message.getId(), ctx.getMessage().getId(), cmd.command, allowed);
	}

	public void removeReactiveMessage(Guild guild, String messageId){
		Database.removeReactiveMessage(guild.getId(), messageId);
	}

	public ReactiveMessage getReactiveMessage(Guild guild, String message){
		return Database.isReactiveMessage(guild.getId(), message);
	}

	public void addMusicPlayer(Guild guild, MusicPlayer player){
		musicPlayers.put(guild.getId(), player);
	}

	public MusicPlayer getMusicPlayer(Guild guild){
		return musicPlayers.get(guild.getId());
	}

	public void destroyMusicPlayer(Guild guild, String controllerId){
		KittyBot.lavalink.getLink(guild).destroy();
		removeReactiveMessage(guild, controllerId);
		musicPlayers.remove(guild.getId());
	}

	public boolean checkCommands(GuildMessageReceivedEvent event){
		long start = System.nanoTime();
		String message = cutCommandPrefix(event.getGuild(), event.getMessage().getContentRaw());
		if(message != null){
			String command = getCommandString(message);
			for(Map.Entry<String, ACommand> c : commands.entrySet()){
				var cmd = c.getValue();
				if(cmd.checkCmd(command)){
					//event.getChannel().sendTyping().queue(); answer is sending too fast and I don't want to block the thread lol
					var ctx = new CommandContextImpl(event, command, getCommandArguments(message));
					LOG.info("Command: {}, args: {}, by: {}, from: {}", command, ctx.getArgs(), event.getAuthor().getName(), event.getGuild().getName());
					cmd.run(ctx);
					Database.addCommandStatistics(event.getGuild().getId(), event.getMessageId(), event.getAuthor().getId(), command, System.nanoTime() - start);
					return true;
				}
			}
		}
		return false;
	}

	private String cutCommandPrefix(Guild guild, String message){
		String prefix;
		var botId = guild.getSelfMember().getId();
		if(message.startsWith(prefix = Database.getCommandPrefix(guild.getId()))||message.startsWith(
			prefix = "<@!" + botId + ">")||message.startsWith(prefix = "<@" + botId + ">")){
			return message.substring(prefix.length()).trim();
		}
		return null;
	}

	private String getCommandString(String raw){
		return raw.split("//s+")[0];
	}

	private String[] getCommandArguments(String message){
		String[] args = message.split(" ");
		return Arrays.copyOfRange(args, 1, args.length);
	}

}
