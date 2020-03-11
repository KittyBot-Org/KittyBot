package de.anteiku.kittybot.tasks;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.PollCommand;
import de.anteiku.kittybot.poll.Poll;
import de.anteiku.kittybot.utils.Logger;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PollTask extends Task{
	
	public static String NAME = "PollTask";
	public static long SLEEP = 30000;
	
	public PollTask(KittyBot main){
		super(main, NAME, SLEEP);
	}
	
	@Override
	void task(){
		int polls = 0;
		List<Poll> finished = new ArrayList<>();
		for(Guild guild : main.jda.getGuilds()){
			for(Map.Entry<String, Poll> a : main.pollManager.getPolls(guild.getId()).entrySet()){
				polls++;
				Poll poll = a.getValue();
				if(!poll.isClosed()){
					long currentTime = System.currentTimeMillis();
					if(poll.getEndTime() <= currentTime){
						main.pollManager.closePoll(poll);
						main.pollManager.savePoll(poll);
						EmbedBuilder eb = PollCommand.createEmbed(poll);
						eb.setTitle("Finished Poll:");
						eb.setDescription(poll.getTopic());
						eb.setFooter("", "");
						guild.getTextChannelById(poll.getChannelId()).sendMessage(eb.build());
						finished.add(poll);
					}
				}
			}
		}
		Logger.debug("'" + (polls - finished.size()) + "' polls running!");
	}
	
}
