package de.anteiku.kittybot.commands;

import de.anteiku.emojiutils.EmojiUtils;
import de.anteiku.kittybot.API;
import de.anteiku.kittybot.Emotes;
import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.poll.Poll;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class PollCommand extends Command{
	
	public static String COMMAND = "poll";
	public static String USAGE = "poll <list|create (topic) (<hours>h <minutes>min <seconds>s) (answer 1) (answer 2) ... (answer 9) |delete>";
	public static String DESCRIPTION = "Used to create polls";
	public static String[] ALIAS = {"survey", "umfrage"};
	
	public PollCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}
	
	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		if(args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("help") || args.length > 12 || args.length < 5){
			if(args[0].equalsIgnoreCase("list")){
				EmbedBuilder eb = new EmbedBuilder();
				eb.setDescription("All currently open polls in this guild are:");
				for(Map.Entry<String, Poll> m : main.pollManager.getPolls(event.getGuild().getId()).entrySet()){
					Poll poll = m.getValue();
					if(!poll.isClosed()){
						eb.addField(poll.getTopic(), "Lasts for " + poll.getTimeLeft(), false);
					}
				}
				sendAnswer(event.getChannel(), eb.build());
			}
			else{
				sendUsage(event.getChannel());
			}
		}
		else if(args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("new")){
			String topic = args[1];
			String duration = args[2];
			String[] answers = API.subArray(args, 3);
			if(main.pollManager.pollExists(event.getGuild().getId(), topic)){
				sendError(event.getChannel(), "There is already a poll with that name running!");
				return;
			}
			Poll poll = main.pollManager.createPoll(event.getGuild(), event.getChannel(), topic, duration, answers);
			Message message = newPoll(event.getChannel(), poll);
			main.commandManager.addListenerCmd(message, event.getMessage(), this, -1L);
		}
		else if(args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("remove")){
			main.database.deletePolls();
		}
		else{
			sendUsage(event.getChannel());
		}
	}
	
	@Override
	public void reactionAdd(Message command, GuildMessageReactionAddEvent event){
		Message message = event.getChannel().getMessageById(event.getMessageId()).complete();
		Poll poll = main.pollManager.getPollByName(event.getGuild().getId(), message.getEmbeds().get(0).getDescription().split("\n")[0]);
		if(event.getReactionEmote().getName().equals(Emotes.ZERO)){
			poll.addVote(0, event.getUser());
			event.getReaction().removeReaction(event.getUser()).queue();
			refreshMessage(message, poll);
			return;
		}
		else if(event.getReactionEmote().getName().equals(Emotes.ONE)){
			poll.addVote(1, event.getUser());
			event.getReaction().removeReaction(event.getUser()).queue();
			refreshMessage(message, poll);
			return;
		}
		else if(event.getReactionEmote().getName().equals(Emotes.TWO)){
			poll.addVote(2, event.getUser());
			event.getReaction().removeReaction(event.getUser()).queue();
			refreshMessage(message, poll);
			return;
		}
		else if(event.getReactionEmote().getName().equals(Emotes.THREE)){
			poll.addVote(3, event.getUser());
			event.getReaction().removeReaction(event.getUser()).queue();
			refreshMessage(message, poll);
			return;
		}
		else if(event.getReactionEmote().getName().equals(Emotes.FOUR)){
			poll.addVote(4, event.getUser());
			event.getReaction().removeReaction(event.getUser()).queue();
			refreshMessage(message, poll);
			return;
		}
		else if(event.getReactionEmote().getName().equals(Emotes.FIVE)){
			poll.addVote(5, event.getUser());
			event.getReaction().removeReaction(event.getUser()).queue();
			refreshMessage(message, poll);
			return;
		}
		else if(event.getReactionEmote().getName().equals(Emotes.SIX)){
			poll.addVote(6, event.getUser());
			event.getReaction().removeReaction(event.getUser()).queue();
			refreshMessage(message, poll);
			return;
		}
		else if(event.getReactionEmote().getName().equals(Emotes.SEVEN)){
			poll.addVote(7, event.getUser());
			event.getReaction().removeReaction(event.getUser()).queue();
			refreshMessage(message, poll);
			return;
		}
		else if(event.getReactionEmote().getName().equals(Emotes.EIGHT)){
			poll.addVote(8, event.getUser());
			event.getReaction().removeReaction(event.getUser()).queue();
			refreshMessage(message, poll);
			return;
		}
		else if(event.getReactionEmote().getName().equals(Emotes.NINE)){
			poll.addVote(9, event.getUser());
			event.getReaction().removeReaction(event.getUser()).queue();
			refreshMessage(message, poll);
			return;
		}
		else if(event.getReactionEmote().getName().equals(Emotes.REFRESH)){
			event.getReaction().removeReaction(event.getUser()).queue();
			refreshMessage(message, poll);
			return;
		}
		
		super.reactionAdd(command, event);
	}
	
	public static EmbedBuilder createEmbed(Poll poll){
		Date last = new Date(poll.getEndTime() - System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm.ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.CYAN);
		eb.setTitle("Poll:");
		eb.setDescription(poll.getTopic() + "\n" + "Poll lasts for: " + sdf.format(last));
		eb.setFooter("Vote by clicking the Emotes - You can only vote once!", "https://google.de");
		StringBuilder f1 = new StringBuilder();
		StringBuilder f2 = new StringBuilder();
		int i = 0;
		for(Map.Entry<String, List<String>> a : poll.getVotes().entrySet()){
			f1.append(API.parseDiscordEmoji(i)).append(a.getKey()).append("\n");
			f2.append(poll.getVotes().get(a.getKey()).size()).append(Emotes.BLANK.get()).append("\n");
			i++;
		}
		eb.addField("**Answers:**", f1.toString(), true);
		eb.addField("**Votes:**", f2.toString(), true);
		return eb;
	}
	
	private Message newPoll(TextChannel channel, Poll poll){
		EmbedBuilder eb = createEmbed(poll);
		eb.setTitle("New Poll:");
		
		Message message = channel.sendMessage(eb.build()).complete();
		int i = 0;
		for(Map.Entry<String, List<String>> m : poll.getVotes().entrySet()){
			message.addReaction(EmojiUtils.getEmoji(API.parseEmoji(i))).queue();
			i++;
		}
		message.addReaction(Emotes.REFRESH.get()).queue();
		//message.pin().queue();
		return message;
	}
	
	private Message refreshMessage(Message message, Poll poll){
		return message.editMessage(createEmbed(poll).build()).complete();
	}
	
}
