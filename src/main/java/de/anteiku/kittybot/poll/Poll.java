package de.anteiku.kittybot.poll;

import de.anteiku.kittybot.API;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Poll{
	
	private String channelId, topic, id, guildId;
	private Map<String, List<String>> votes;
	private long creationTime, endTime;
	private boolean closed;
	
	public Poll(TextChannel channel, String id, String topic, String duration, String[] answers){
		this.channelId = channel.getId();
		this.id = id;
		this.guildId = guildId;
		this.topic = topic;
		this.closed = false;
		this.votes = new LinkedHashMap<>();
		for(String a : answers){
			this.votes.put(a, new ArrayList<>());
		}
		this.creationTime = System.currentTimeMillis();
		this.endTime = creationTime + API.parseTimeString(duration);
	}
	
	public Poll(String channel, String id, String guildId, String topic, boolean closed, Map<String, List<String>> votes, long creationTime, long endTime){
		this.channelId = channel;
		this.id = id;
		this.guildId = guildId;
		this.topic = topic;
		this.closed = closed;
		this.votes = votes;
		this.creationTime = creationTime;
		this.endTime = endTime;
	}
	
	public String getAnswer(int id){
		int i = 0;
		for(Map.Entry<String, List<String>> a : votes.entrySet()){
			if(i == id){
				return a.getKey();
			}
			i++;
		}
		return null;
	}
	
	public int getAnswerId(String answer){
		int i = 0;
		for(Map.Entry<String, List<String>> a : votes.entrySet()){
			if(answer.equals(a.getKey())){
				return i;
			}
			i++;
		}
		return -1;
	}
	
	public void addVote(String answer, String userId){
		if(!hasVoted(userId) && !isClosed()){
			votes.get(answer).add(userId);
		}
	}
	
	public void addVote(int id, User user){
		addVote(id, user.getId());
	}
	
	public void addVote(int id, String userId){
		if(!hasVoted(userId) && !isClosed()){
			int i = 0;
			for(Map.Entry<String, List<String>> a : votes.entrySet()){
				if(i == id){
					a.getValue().add(userId);
				}
				i++;
			}
			
		}
	}
	
	public boolean hasVoted(String userId){
		for(Map.Entry<String, List<String>> a : votes.entrySet()){
			if(a.getValue().contains(userId)){
				return true;
			}
		}
		return false;
	}
	
	public boolean isClosed(){
		return closed;
	}
	
	public void removeVote(User user){
		removeVote(user.getId());
	}
	
	public void removeVote(String userId){
		if(! isClosed()){
			for(Map.Entry<String, List<String>> a : votes.entrySet()){
				a.getValue().remove(userId);
			}
		}
	}
	
	public int getVoteFrom(String userId){
		int i = 0;
		for(Map.Entry<String, List<String>> a : votes.entrySet()){
			if(a.getValue().contains(userId)){
				return i;
			}
			i++;
		}
		return -1;
	}
	
	public String getVote(String userId){
		for(Map.Entry<String, List<String>> a : votes.entrySet()){
			if(a.getValue().contains(userId)){
				return a.getKey();
			}
		}
		return null;
	}
	
	public String getChannelId(){
		return channelId;
	}
	
	public String getGuildId(){
		return guildId;
	}
	
	public String getTopic(){
		return topic;
	}
	
	public long getCreationTime(){
		return creationTime;
	}
	
	public long getEndTime(){
		return endTime;
	}
	
	public long getTimeLeft(){
		return endTime - System.currentTimeMillis();
	}
	
	public List<String> getVotes(String answer){
		return votes.get(answer);
	}
	
	public Map<String, List<String>> getVotes(){
		return votes;
	}
	
	public void close(){
		closed = true;
	}
	
	public String getId(){
		return id;
	}
	
}
