package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.objects.enums.API;
import de.kittybot.kittybot.objects.module.Module;
import de.kittybot.kittybot.utils.Config;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class BotListsModule extends Module{

	@Override
	public void onReady(@NotNull ReadyEvent event){
		updateStats();
	}

	@Override
	public void onGuildJoin(@NotNull GuildJoinEvent event){
		updateStats();
	}

	@Override
	public void onGuildLeave(@NotNull GuildLeaveEvent event){
		updateStats();
	}

	private void updateStats(){
		var guildCount = getTotalGuilds();
		var requestModule = this.modules.get(RequestModule.class);
		if(!Config.DISCORD_BOTS_TOKEN.isBlank()){
			requestModule.updateStats(API.DISCORD_BOTS, guildCount, Config.DISCORD_BOTS_TOKEN);
		}
		if(!Config.TOP_GG_TOKEN.isBlank()){
			requestModule.updateStats(API.TOP_GG, guildCount, Config.TOP_GG_TOKEN);
		}
		if(!Config.DISCORD_EXTREME_LIST_TOKEN.isBlank()){
			requestModule.updateStats(API.DISCORD_EXTREME_LIST, guildCount, Config.DISCORD_EXTREME_LIST_TOKEN);
		}
		if(!Config.DISCORD_BOATS_TOKEN.isBlank()){
			requestModule.updateStats(API.DISCORD_BOATS, guildCount, Config.DISCORD_BOATS_TOKEN);
		}
		if(!Config.BOTLIST_SPACE_TOKEN.isBlank()){
			requestModule.updateStats(API.BOTLIST_SPACE, guildCount, Config.BOTLIST_SPACE_TOKEN);
		}
		if(!Config.BOTS_FOR_DISCORD_TOKEN.isBlank()){
			requestModule.updateStats(API.BOTS_FOR_DISCORD, guildCount, Config.BOTS_FOR_DISCORD_TOKEN);
		}
		if(!Config.DISCORDBOTLIST_TOKEN.isBlank()){
			requestModule.updateStats(API.DISCORDBOTLIST_COM, guildCount, Config.DISCORDBOTLIST_TOKEN);
		}
		if(!Config.DISCORD_SERVICES_TOKEN.isBlank()){
			requestModule.updateStats(API.DISCORD_SERVICES, guildCount, Config.DISCORD_SERVICES_TOKEN);
		}
	}

	private int getTotalGuilds(){
		return (int) this.modules.getShardManager().getGuildCache().size();
	}

}
