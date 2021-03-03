package de.kittybot.kittybot.objects.settings.guild;

import de.kittybot.kittybot.objects.settings.IGuildSettings;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class BlacklistGuildSettings implements IGuildSettings{

	private final Set<Long> botDisabledChannels;
	private final Set<Long> botIgnoredUsers;

	public BlacklistGuildSettings(Collection<Long> botDisabledChannels, Collection<Long> botIgnoredUsers){
		this.botDisabledChannels = new HashSet<>(botDisabledChannels);
		this.botIgnoredUsers = new HashSet<>(botIgnoredUsers);
	}


	public boolean isBotDisabledInChannel(long channelId){
		return this.botDisabledChannels.contains(channelId);
	}

	public void setBotDisabledInChannel(long channelId, boolean enabled){
		if(enabled){
			this.botDisabledChannels.add(channelId);
			return;
		}
		this.botDisabledChannels.remove(channelId);
	}

	public Set<Long> getBotDisabledChannels(){
		return this.botDisabledChannels;
	}

	public boolean isBotIgnoredUser(long userId){
		return this.botIgnoredUsers.contains(userId);
	}

	public void setBotIgnoredUsers(Set<Long> userIds, boolean ignored){
		if(ignored){
			this.botIgnoredUsers.addAll(userIds);
			return;
		}
		this.botIgnoredUsers.removeAll(userIds);
	}

	public Set<Long> getBotIgnoredUsers(){
		return botIgnoredUsers;
	}

}
