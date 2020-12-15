package de.kittybot.kittybot.managers;

import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.objects.API;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class BotListsManager extends ListenerAdapter{

	private final KittyBot main;

	public BotListsManager(KittyBot main){
		this.main = main;
	}

	@Override
	public void onReady(@NotNull ReadyEvent event){
		updateStats((int)event.getJDA().getGuildCache().size());
	}

	@Override
	public void onGuildJoin(@NotNull GuildJoinEvent event){
		updateStats((int)this.main.getJDA().getGuildCache().size());
	}

	@Override
	public void onGuildLeave(@NotNull GuildLeaveEvent event){
		updateStats((int)this.main.getJDA().getGuildCache().size());
	}

	private void updateStats(int guildCount){
		var requestManager = this.main.getRequestManager();
		var config = this.main.getConfig();
		if(config.hasKey("discord_bots_token")){
			requestManager.updateStats(API.DISCORD_BOTS, guildCount, config.getString("discord_bots_token"));
		}
		if(config.hasKey("top_gg_token")){
			requestManager.updateStats(API.TOP_GG, guildCount, config.getString("top_gg_token"));
		}
		if(config.hasKey("discord_extreme_list_token")){
			requestManager.updateStats(API.DISCORD_EXTREME_LIST, guildCount, config.getString("discord_extreme_list_token"));
		}
		if(config.hasKey("discord_boats_token")){
			requestManager.updateStats(API.DISCORD_BOATS, guildCount, config.getString("discord_boats_token"));
		}
		if(config.hasKey("botlist_space")){
			requestManager.updateStats(API.BOTLIST_SPACE, guildCount, config.getString("botlist_space"));
		}
		if(config.hasKey("bots_for_discord_token")){
			requestManager.updateStats(API.BOTS_FOR_DISCORD, guildCount, config.getString("bots_for_discord_token"));
		}
	}

}
