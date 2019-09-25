package de.anteiku.kittybot.database;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.poll.Poll;
import de.anteiku.kittybot.utils.API;
import de.anteiku.kittybot.utils.Logger;
import de.anteiku.kittybot.utils.RandomKey;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import net.dv8tion.jda.core.entities.Guild;

import java.util.*;

public class Database{
	
	private static final String SEP = ":";
	private static final long TIMEOUT = 30 * 60000L;
	
	private static class GUILD{
		static final String COMMANDPREFIX = "command_prefix";
		static final String SELFASSIGNABLEROLES = "self_assignable_roles";
		static final String WELCOMECHANNELID = "welcome_channel_id";
		static final String WELCOMEMESSAGE = "welcome_message";
		static final String WELCOMEMESSAGEENABLED = "welcome_message_enabled";
		static final String NSFWENABLED = "nsfw_enabled";
		static final String SESSIONKEY = "session_key";
		static final String LASTIP = "last_ip";
		static final String POLLS = "polls";
	}
	
	private static class POLL{
		static final String GUILD_ID = "guild_id";
		static final String CHANNEL_ID = "channel_id";
		static final String TOPIC = "topic";
		static final String CREATION_TIME = "creation_time";
		static final String END_TIME = "end_time";
		static final String CLOSED = "closed";
		static final String ANSWERS = "answers";
		static final String UNIQUE_ID = "last_unique_id";
	}
	
	private RedisClient client;
	private StatefulRedisConnection<String, String> conn;
	private RedisCommands<String, String> guilds;
	private RedisCommands<String, String> polls;
	private RedisCommands<String, String> users;
	private RedisCommands<String, String> sessions;
	
	public Database(KittyBot main){
		try{
			client = RedisClient.create("redis://localhost:6379");
			conn = client.connect();
			
			guilds = conn.sync();
			guilds.select(0);
			
			polls = conn.sync();
			polls.select(1);
			
			users = conn.sync();
			users.select(2);
			
			sessions = conn.sync();
			sessions.select(3);
		}
		catch(Exception e){
			Logger.print("Connection to Redis Server failed!");
			main.close();
		}
		init(main);
	}
	
	private void init(KittyBot main){
		for(Guild guild : main.jda.getGuilds()){
			if(!isGuildRegistered(guild)){
				registerGuild(guild);
			}
		}
	}
	
	public void flush(){
		guilds.flushall();
		guilds.save();
		polls.flushall();
		polls.save();
		users.flushall();
		users.save();
	}

	public void close(){
		guilds.save();
		polls.save();
		users.save();
		Logger.print("Databases saved...");
		conn.close();
		client.shutdown();
	}
	
	public void setUserToken(String userId, String token){
		users.set(userId, token);
	}
	
	public String getUserToken(String userId){
		return users.get(userId);
	}
	
	/*
	 * Guild specified methods
	 */
	
	private boolean isGuildRegistered(Guild guild){
		return guilds.exists(guild.getId() + SEP + GUILD.COMMANDPREFIX) > 0;
	}
	
	private void registerGuild(Guild guild){
		registerGuild(guild.getId());
	}
	
	private void registerGuild(String guildId){
		Logger.print("Registering new guild: '" + guildId + "'");
		setCommandPrefix(guildId, ".");
		//addSelfAssignableRoles(guildId);
		setWelcomeChannelId(guildId, "-1");
		setWelcomeMessage(guildId, "Welcome [username] to this server!");
		set(guildId, GUILD.WELCOMEMESSAGEENABLED, "false");
		set(guildId, GUILD.NSFWENABLED, "true");
	}
	
	private String get(String guildId, String key){
		return guilds.get(guildId + SEP + key);
	}
	
	private Set<String> getSet(String guildId, String key){
		return guilds.smembers(guildId + SEP + key);
	}
	
	private void set(String guildId, String key, String value){
		guilds.set(guildId + SEP + key, value);
	}
	
	private void add(String guildId, String key, String... value){
		guilds.sadd(guildId + SEP + key, value);
	}
	
	private void remove(String guildId, String key, String... value){
		guilds.srem(guildId + SEP + key, value);
	}
	
	public String getCommandPrefix(String guildId){
		return get(guildId, GUILD.COMMANDPREFIX);
	}
	
	public void setCommandPrefix(String guildId, String prefix){
		set(guildId, GUILD.COMMANDPREFIX, prefix);
	}
	
	public Set<String> getSelfAssignableRoles(String guildId){
		return getSet(guildId, GUILD.SELFASSIGNABLEROLES);
	}
	
	public void addSelfAssignableRoles(String guildId, String... roleId){
		add(guildId, GUILD.SELFASSIGNABLEROLES, roleId);
	}
	
	public void removeSelfAssignableRoles(String guildId, String... roleId){
		remove(guildId, GUILD.SELFASSIGNABLEROLES, roleId);
	}
	
	public String getWelcomeChannelId(String guildId){
		return get(guildId, GUILD.WELCOMECHANNELID);
	}
	
	public void setWelcomeChannelId(String guildId, String channelId){
		set(guildId, GUILD.WELCOMECHANNELID, channelId);
	}
	
	public String getWelcomeMessage(String guildId){
		return get(guildId, GUILD.WELCOMEMESSAGE);
	}
	
	public boolean getWelcomeMessageEnabled(String guildId){
		return Boolean.parseBoolean(get(guildId, GUILD.WELCOMEMESSAGEENABLED));
	}
	
	public void setWelcomeMessageEnabled(String guildId, boolean enabled){
		set(guildId, GUILD.WELCOMEMESSAGEENABLED, String.valueOf(enabled));
	}
	
	public boolean getNSFWEnabled(String guildId){
		return Boolean.parseBoolean(get(guildId, GUILD.NSFWENABLED));
	}
	
	public void setNSFWEnabled(String guildId, boolean enabled){
		set(guildId, GUILD.NSFWENABLED, String.valueOf(enabled));
	}
	
	public void setWelcomeMessage(String guildId, String message){
		set(guildId, GUILD.WELCOMEMESSAGE, message);
	}
	
	/*
	 * Poll specified methods
	 */
	
	public String newPollId(){
		String value = polls.get(POLL.UNIQUE_ID);
		int id;
		if(value == null){
			id = 0;
		}
		else{
			id = Integer.parseInt(value);
			id++;
		}
		polls.set(POLL.UNIQUE_ID, String.valueOf(id));
		return String.valueOf(id);
	}
	
	public String getFromPolls(String pollId, String key){
		return polls.get(pollId + SEP + key);
	}
	
	private Set<String> getPollsSet(String pollId, String key){
		return polls.smembers(pollId + SEP + key);
	}
	
	private void setFromPoll(String pollId, String key, String value){
		polls.set(pollId + SEP + key, value);
	}
	
	private void setSet(String pollId, String key, String[] value){
		if(value.length > 0){
			polls.sadd(pollId + SEP + key, value);
		}
	}
	
	public Map<String, Poll> getPolls(String guildId){
		Map<String, Poll> polls = new HashMap<>();
		for(String p : getSet(guildId, GUILD.POLLS)){
			polls.put(p, getPoll(p));
		}
		return polls;
	}
	
	public Poll getPoll(String pollId){
		Map<String, List<String>> map = new LinkedHashMap<>();
		for(String a : getPollsSet(pollId, POLL.ANSWERS)){
			map.put(a, new ArrayList<>(getSet(pollId, a)));
		}
		return new Poll(
			getFromPolls(pollId, POLL.CHANNEL_ID),
			getFromPolls(pollId, pollId),
			getFromPolls(pollId, POLL.GUILD_ID),
			getFromPolls(pollId, POLL.TOPIC),
			Boolean.parseBoolean(getFromPolls(pollId, POLL.CLOSED)),
			map,
			Long.parseLong(getFromPolls(pollId, POLL.CREATION_TIME)),
			Long.parseLong(getFromPolls(pollId, POLL.END_TIME))
			);
	}
	
	public void setPoll(Poll poll){
		add(GUILD.POLLS, poll.getId());
		setFromPoll(poll.getId(), POLL.CHANNEL_ID, poll.getChannelId());
		setFromPoll(poll.getId(), POLL.GUILD_ID, poll.getChannelId());
		setFromPoll(poll.getId(), POLL.CLOSED, String.valueOf(poll.isClosed()));
		setFromPoll(poll.getId(), POLL.CREATION_TIME, String.valueOf(poll.getCreationTime()));
		setFromPoll(poll.getId(), POLL.END_TIME, String.valueOf(poll.getEndTime()));
		setFromPoll(poll.getId(), POLL.TOPIC, poll.getTopic());
		for(Map.Entry<String, List<String>> a : poll.getVotes().entrySet()){
			setSet(poll.getId(), a.getKey(), API.toArrayy(a.getValue()));
		}
	}
	
	public void deletePolls(){
	/*	for(Guild g : main.jda.getGuilds()){
			guilds.srem(g.getId() + SEP + GUILD.POLLS, API.toArray(getSet(g.getId(), GUILD.POLLS)));
		} */
		polls.flushdb();
		polls.save();
	}
	
	/*
	 * Session specified methods
	 */
	
	public boolean sessionExists(String key){
		return sessions.exists(key) > 0;
	}
	
	private String generateUniqueKey(){
		String key = RandomKey.generate(32);
		while(sessionExists(key)){
			key = RandomKey.generate(32);
		}
		return key;
	}
	
	public String addSession(String userId){
		String key = generateUniqueKey();
		sessions.set(key, userId);
		return key;
	}
	
	public void deleteSession(String key){
		sessions.del(key);
	}
	
	public String getSession(String key){
		return sessions.get(key);
	}
	
}
