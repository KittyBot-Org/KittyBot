package de.kittybot.kittybot.web.guilds;

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
		var data = DataArray.fromCollection(
			this.modules.getShardManager().getGuildCache().stream().map(guild ->
				DataObject.empty().put("id", guild.getId()).put("name", guild.getName()).put("icon", guild.getIconUrl()).put("count", guild.getMemberCount()).put("owner", guild.getOwnerId())
			).collect(Collectors.toSet())
		);
		WebModule.ok(ctx, DataObject.empty().put("guilds", data));
	}

}
