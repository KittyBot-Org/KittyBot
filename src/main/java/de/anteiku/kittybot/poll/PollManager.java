package de.anteiku.kittybot.poll;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.utils.Logger;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.HashMap;
import java.util.Map;

public class PollManager{
	
	private Map<String, Map<String, Poll>> polls;
	private KittyBot main;
	
	public PollManager(KittyBot main){
		this.main = main;
		polls = new HashMap<>();
		loadPolls();
	}
	
	public void close(){
		savePolls();
	}
	
	public void loadPolls(){
		polls = new HashMap<>();
		for(Guild g : main.jda.getGuilds()){
			polls.put(g.getId(), main.database.getPolls(g.getId()));
		}
	}
	
	public void savePolls(){
		for(Map.Entry<String, Map<String, Poll>> m : polls.entrySet()){
			for(Map.Entry<String, Poll> polls : m.getValue().entrySet()){
				savePoll(polls.getValue());
			}
		}
	}
	
	public void savePoll(Poll poll){
		main.database.setPoll(poll);
	}
	
	public void addVote(String guildId, String pollId, int answerId, String userId){
		polls.get(guildId).get(pollId).addVote(answerId, userId);
	}
	
	public void addVote(String guildId, String pollId, String answer, String userId){
		polls.get(guildId).get(pollId).addVote(answer, userId);
	}
	
	public void removeVote(String guildId, String pollId, String userId){
		polls.get(guildId).get(pollId).removeVote(userId);
	}
	
	public Poll getPoll(String guildId, String pollId){
		return polls.get(guildId).get(pollId);
	}
	
	public Poll getPollByName(String guildId, String pollName){
		for(Map.Entry<String, Poll> p : polls.get(guildId).entrySet()){
			Poll poll;
			if((poll = p.getValue()).getTopic().equalsIgnoreCase(pollName)){
				return poll;
			}
		}
		return null;
	}
	
	public Map<String, Poll> getPolls(String guildId){
		return polls.get(guildId);
	}
	
	public boolean pollExists(String guildId, String topic){
		for(Map.Entry<String, Poll> p : polls.get(guildId).entrySet()){
			if(p.getValue().getTopic().equalsIgnoreCase(topic)){
				return true;
			}
		}
		return false;
	}
	
	public Poll createPoll(Guild guild, TextChannel channel, String topic, String duration, String[] answers){
		if(answers.length < 2 || answers.length > 9){
			return null;
		}
		if(duration.equalsIgnoreCase("")){
			duration = "24h";
		}
		Poll poll = new Poll(channel, newId(), topic, duration, answers);
		Logger.print("Poll created Topic: '" + poll.getTopic() + "' Id: '" + poll.getId() + "' Duration: '" + duration + "'");
		polls.get(guild.getId()).put(poll.getId(), poll);
		main.pollManager.savePoll(poll);
		return poll;
	}
	
	private String newId(){
		return main.database.newPollId();
	}
	
	public void closePoll(Poll poll){
		poll.close();
		Logger.print("Poll closed Topic: '" + poll.getTopic() + "' Id: '" + poll.getId() + "'");
	}
	
}
