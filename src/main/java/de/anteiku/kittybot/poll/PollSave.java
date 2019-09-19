package de.anteiku.kittybot.poll;

import de.anteiku.emojiutils.EmojiUtils;
import de.anteiku.kittybot.API;
import de.anteiku.kittybot.Emotes;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

public class PollSave{
	
	private String guildId, channelId, topic, id;
	private String[] answers;
	private long creationTime, endTime;
	private Map<String, Integer> ids;
	private boolean closed;
	
	private Map<Integer, List<String>> votes;
	
	public PollSave(Guild guild, TextChannel channel, String id, String topic, String duration, String[] answers){
		guildId = guild.getId();
		this.channelId = channel.getId();
		this.id = id;
		this.topic = topic;
		closed = false;
		ids = new LinkedHashMap<>();
		votes = new LinkedHashMap<>();
		int aId = 0;
		for(String a : answers){
			ids.put(a, aId);
			aId++;
			votes.put(ids.get(a), new ArrayList<>());
		}
		creationTime = System.currentTimeMillis();
		endTime = creationTime + API.parseTimeString(duration);
	}
	
	public Message sendNew(TextChannel channel){
		EmbedBuilder eb = createEmbed();
		eb.setTitle("New Poll:");
		
		Message message = channel.sendMessage(eb.build()).complete();
		for(Map.Entry<String, Integer> i : ids.entrySet()){
			message.addReaction(EmojiUtils.getEmoji(API.parseEmoji(i.getValue()))).queue();
		}
		message.addReaction(Emotes.REFRESH.get()).queue();
		//message.pin().queue();
		return message;
	}
	
	private EmbedBuilder createEmbed(){
		Date last = new Date(endTime - System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm.ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.CYAN);
		eb.setTitle("Poll:");
		eb.setDescription(topic + "\n" + "Poll lasts for: " + sdf.format(last));
		eb.setFooter("Vote by clicking the Emotes - You can only vote once!", "https://google.de");
		String f1 = "";
		String f2 = "";
		for(Map.Entry<Integer, List<String>> a : votes.entrySet()){
			f1 += API.parseDiscordEmoji(a.getKey()) + getAnswer(a.getKey()) + "\n";
			f2 += votes.get(a.getKey()).size() + Emotes.BLANK.get() + "\n";
		}
		eb.addField("**Answers:**", f1, true);
		eb.addField("**Votes:**", f2, true);
		return eb;
	}
	
	public String getAnswer(int id){
		for(Map.Entry<String, Integer> a : ids.entrySet()){
			if(a.getValue() == id){
				return a.getKey();
			}
		}
		return null;
	}
	
	public Message sendAsMessage(TextChannel channel){
		Message message = channel.sendMessage(createEmbed().build()).complete();
		
		for(Map.Entry<String, Integer> i : ids.entrySet()){
			message.addReaction(EmojiUtils.getEmoji(API.parseEmoji(i.getValue()))).queue();
		}
		return message;
	}
	
	public int getAnswerId(String answer){
		return ids.get(answer);
	}
	
	public void addVote(String answer, User user){
		addVote(answer, user.getId());
	}
	
	public void addVote(String answer, String userId){
		if(! hasVoted(userId) && ! isClosed()){
			votes.get(ids.get(answer)).add(userId);
		}
	}
	
	public boolean hasVoted(String userId){
		for(Map.Entry<String, Integer> a : ids.entrySet()){
			if(votes.get(a.getValue()).contains(userId)){
				return true;
			}
		}
		return false;
	}
	
	public boolean isClosed(){
		return closed;
	}
	
	public void addVote(int id, User user){
		addVote(id, user.getId());
	}
	
	public void addVote(int id, String userId){
		if(! hasVoted(userId) && ! isClosed()){
			votes.get(id).add(userId);
		}
	}
	
	public void removeVote(User user){
		removeVote(user.getId());
	}
	
	public void removeVote(String userId){
		if(! isClosed()){
			for(Map.Entry<String, Integer> a : ids.entrySet()){
				votes.get(a.getValue()).remove(userId);
			}
		}
	}
	
	public int getVoteFrom(String userId){
		for(Map.Entry<String, Integer> a : ids.entrySet()){
			if(votes.get(a.getValue()).contains(userId)){
				return a.getValue();
			}
		}
		return - 1;
	}
	
	public String getVote(String userId){
		for(Map.Entry<String, Integer> a : ids.entrySet()){
			if(votes.get(a.getValue()).contains(userId)){
				return a.getKey();
			}
		}
		return null;
	}
	
	public String getGuildId(){
		return guildId;
	}
	
	public String getChannelId(){
		return channelId;
	}
	
	public String getTopic(){
		return topic;
	}
	
	public String[] getAnswers(){
		return answers;
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
		return votes.get(ids.get(answer));
	}
	
	public Map<Integer, List<String>> getVotes(){
		return votes;
	}
	
	public Message refreshMessage(Message message){
		return message.editMessage(createEmbed().build()).complete();
	}
	
	public void close(Guild guild){
		Date duration = new Date(endTime - creationTime);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm.ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		closed = true;
		EmbedBuilder eb = createEmbed();
		eb.setColor(Color.BLUE);
		eb.setTitle("Finished Poll:");
		eb.setDescription(topic + "\nDuration: " + sdf.format(duration));
		eb.setFooter("", "https://google.de");
		
		TextChannel channel = guild.getTextChannelById(channelId);
		Message message = channel.sendMessage(eb.build()).complete();
		//message.pin().queue();
		//message.unpin().queue();
	}
	
	public String getId(){
		return id;
	}
	
}
