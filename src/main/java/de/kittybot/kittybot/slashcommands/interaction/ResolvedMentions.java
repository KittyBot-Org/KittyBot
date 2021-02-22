package de.kittybot.kittybot.slashcommands.interaction;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.entities.GuildImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ResolvedMentions{

	private final EntityBuilder entityBuilder;
	private final GuildImpl guild;
	private final DataObject rawChannels;
	private final DataObject rawMembers;
	private final DataObject rawRoles;
	private final DataObject rawUsers;
	private Map<Long, GuildChannel> channels;
	private Map<Long, Member> members;
	private Map<Long, Role> roles;
	private Map<Long, User> users;

	public ResolvedMentions(DataObject json, EntityBuilder entityBuilder, GuildImpl guild){
		this.entityBuilder = entityBuilder;
		this.guild = guild;
		this.channels = null;
		this.rawChannels = json.optObject("channels").orElse(null);
		this.members = null;
		this.rawMembers = json.optObject("members").orElse(null);
		this.roles = null;
		this.rawRoles = json.optObject("roles").orElse(null);
		this.users = null;
		this.rawUsers = json.optObject("users").orElse(null);
	}

	public Map<Long, GuildChannel> getMentionedChannels(){
		if(this.channels == null){
			if(this.rawChannels == null){
				this.channels = new HashMap<>();
			}
			else{
				this.channels = this.rawChannels.keys().stream()
					.map(this::createChannel)
					.filter(Objects::nonNull)
					.collect(Collectors.toMap(ISnowflake::getIdLong, Function.identity()));
			}
		}
		return this.channels;
	}

	private GuildChannel createChannel(String key){
		GuildChannel channel;
		var json = this.rawChannels.getObject(key);
		switch(ChannelType.fromId(json.getInt("type"))){
			case CATEGORY:
				channel = this.entityBuilder.createCategory(guild, json, guild.getIdLong());
				break;
			case TEXT:
				channel = this.entityBuilder.createTextChannel(guild, json, guild.getIdLong());
				break;
			case STORE:
				channel = this.entityBuilder.createStoreChannel(guild, json, guild.getIdLong());
				break;
			case VOICE:
				channel = this.entityBuilder.createVoiceChannel(guild, json, guild.getIdLong());
				break;
			default:
				channel = null;
				break;
		}
		return channel;
	}

	public Map<Long, Member> getMentionedMembers(){
		if(this.members == null){
			if(this.rawMembers == null){
				this.members = new HashMap<>();
			}
			else{
				this.members = this.rawMembers.keys().stream()
					.map(this::createMember)
					.collect(Collectors.toMap(ISnowflake::getIdLong, Function.identity()));
			}
		}
		return this.members;
	}

	private Member createMember(String key){
		return this.entityBuilder.createMember(this.guild, this.rawMembers.getObject(key).put("user", this.rawUsers.getObject(key)));
	}

	public Map<Long, User> getMentionedUsers(){
		if(this.users == null){
			if(this.rawUsers == null){
				this.users = new HashMap<>();
			}
			else{
				this.users = this.rawUsers.keys().stream()
					.map(this::createUser)
					.collect(Collectors.toMap(ISnowflake::getIdLong, Function.identity()));
			}
		}
		return this.users;
	}

	private User createUser(String key){
		return this.entityBuilder.createUser(this.rawUsers.getObject(key));
	}

	public Map<Long, Role> getMentionedRoles(){
		if(this.roles == null){
			if(this.rawRoles == null){
				this.roles = new HashMap<>();
			}
			else{
				this.roles = this.rawRoles.keys().stream()
					.map(this::createRole)
					.collect(Collectors.toMap(ISnowflake::getIdLong, Function.identity()));
			}
		}
		return this.roles;
	}

	private Role createRole(String key){
		return this.entityBuilder.createRole(this.guild, this.rawRoles.getObject(key), this.guild.getIdLong());
	}

}
