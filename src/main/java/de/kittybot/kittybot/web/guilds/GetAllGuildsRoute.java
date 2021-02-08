package de.kittybot.kittybot.web.guilds;

import de.kittybot.kittybot.modules.MusicModule;
import de.kittybot.kittybot.modules.WebModule;
import de.kittybot.kittybot.objects.module.Modules;
import de.kittybot.kittybot.utils.Config;
import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.Handler;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class GetAllGuildsRoute implements Handler{

	private final Modules modules;

	public GetAllGuildsRoute(Modules modules){
		this.modules = modules;
	}

	@Override
	public void handle(@NotNull Context ctx){
		var userId = this.modules.get(WebModule.class).getUserId(ctx);
		if(!Config.DEV_IDS.contains(userId)){
			throw new ForbiddenResponse("Only bot devs have access to this");
		}
		var players = this.modules.get(MusicModule.class).getPlayers();
		var data = DataArray.fromCollection(
			this.modules.getShardManager().getGuildCache().stream().map(guild -> {
				var json = DataObject.empty()
					.put("id", guild.getId())
					.put("name", guild.getName())
					.put("icon", guild.getIconUrl())
					.put("count", guild.getMemberCount())
					.put("owner", guild.getOwnerId());
				var player = players.get(guild.getIdLong());
				var playsMusic = player != null;
				json.put("plays_music", playsMusic)
					.put("queue_size", playsMusic ? player.getScheduler().getQueue().size() : 0)
					.put("history_size", playsMusic ? player.getScheduler().getHistory().size() : 0);
				return json;
			}).collect(Collectors.toSet())
		);
		WebModule.ok(ctx, DataObject.empty().put("guilds", data));
	}

}
