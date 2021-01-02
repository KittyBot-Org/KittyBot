package de.kittybot.kittybot.managers;

import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.objects.API;
import de.kittybot.kittybot.utils.Config;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class BotListsManager extends ListenerAdapter{

	private final KittyBot main;

	public BotListsManager(KittyBot main){
		this.main = main;
	}

	@Override
	public void onReady(@NotNull ReadyEvent event){
		updateStats((int) event.getJDA().getGuildCache().size());
	}

	@Override
	public void onGuildJoin(@NotNull GuildJoinEvent event){
		updateStats((int) this.main.getJDA().getGuildCache().size());
	}

	@Override
	public void onGuildLeave(@NotNull GuildLeaveEvent event){
		updateStats((int) this.main.getJDA().getGuildCache().size());
	}

	private void updateStats(int guildCount){
		var requestManager = this.main.getRequestManager();
		if(!Config.DISCORD_BOTS_TOKEN.isBlank()){
			requestManager.updateStats(API.DISCORD_BOTS, guildCount, Config.DISCORD_BOTS_TOKEN);
		}
		if(!Config.TOP_GG_TOKEN.isBlank()){
			requestManager.updateStats(API.TOP_GG, guildCount, Config.TOP_GG_TOKEN);
		}
		if(!Config.DISCORD_EXTREME_LIST_TOKEN.isBlank()){
			requestManager.updateStats(API.DISCORD_EXTREME_LIST, guildCount, Config.DISCORD_EXTREME_LIST_TOKEN);
		}
		if(!Config.DISCORD_BOATS_TOKEN.isBlank()){
			requestManager.updateStats(API.DISCORD_BOATS, guildCount, Config.DISCORD_BOATS_TOKEN);
		}
		if(!Config.BOTLIST_SPACE_TOKEN.isBlank()){
			requestManager.updateStats(API.BOTLIST_SPACE, guildCount, Config.BOTLIST_SPACE_TOKEN);
		}
		if(!Config.BOTS_FOR_DISCORD_TOKEN.isBlank()){
			requestManager.updateStats(API.BOTS_FOR_DISCORD, guildCount, Config.BOTS_FOR_DISCORD_TOKEN);
		}
	}

}
