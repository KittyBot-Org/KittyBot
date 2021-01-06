package de.kittybot.kittybot.web.routes.guilds;

import de.kittybot.kittybot.module.Modules;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.web.WebService;
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
		var userId = this.modules.getWebService().getUserId(ctx);
		if(!Config.OWNER_IDS.contains(userId)){
			throw new ForbiddenResponse("Only bot owners have access to this");
		}
		var data = DataArray.fromCollection(
				this.modules.getJDA().getGuildCache().stream().map(guild ->
						DataObject.empty().put("id", guild.getId()).put("name", guild.getName()).put("icon", guild.getIconUrl()).put("count", guild.getMemberCount()).put("owner", guild.getOwner() == null ? null : guild.getOwner().getUser().getAsTag())
				).collect(Collectors.toSet())
		);
		WebService.ok(ctx, DataObject.empty().put("guilds", data));
	}

}
