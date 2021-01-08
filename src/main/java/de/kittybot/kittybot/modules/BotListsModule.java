package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.module.Module;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import de.kittybot.kittybot.module.Modules;
import de.kittybot.kittybot.objects.API;
import de.kittybot.kittybot.utils.Config;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import org.jetbrains.annotations.NotNull;

public class BotListsModule extends Module{

	private final Modules modules;

	public BotListsModule(Modules modules){
		this.modules = modules;
	}

	@Override
	public void onReady(@NotNull ReadyEvent event){
		updateStats((int) event.getJDA().getGuildCache().size());
	}

	@Override
	public void onGuildJoin(@NotNull GuildJoinEvent event){
		updateStats((int) this.modules.getJDA().getGuildCache().size());
	}

	@Override
	public void onGuildLeave(@NotNull GuildLeaveEvent event){
		updateStats((int) this.modules.getJDA().getGuildCache().size());
	}

	private void updateStats(int guildCount){
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
	}

}
