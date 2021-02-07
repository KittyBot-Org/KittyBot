package de.kittybot.kittybot.web.dev;

import de.kittybot.kittybot.modules.MusicModule;
import de.kittybot.kittybot.modules.WebModule;
import de.kittybot.kittybot.objects.module.Modules;
import de.kittybot.kittybot.objects.music.MusicManager;
import de.kittybot.kittybot.utils.Config;
import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.Handler;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class GetDevRoute implements Handler{

	private final Modules modules;

	public GetDevRoute(Modules modules){
		this.modules = modules;
	}

	@Override
	public void handle(@NotNull Context ctx){
		var userId = this.modules.get(WebModule.class).getUserId(ctx);
		if(!Config.DEV_IDS.contains(userId)){
			throw new ForbiddenResponse("Only bot devs have access to this");
		}
		var json = DataObject.empty()
			.put("music_players", DataArray.fromCollection(this.modules.get(MusicModule.class).getPlayers().stream().map(MusicManager::toJSON).collect(Collectors.toList())));
		WebModule.ok(ctx, json.toData());
	}

}
