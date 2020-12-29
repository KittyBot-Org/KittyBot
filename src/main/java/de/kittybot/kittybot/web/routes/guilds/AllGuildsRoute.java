package de.kittybot.kittybot.web.routes.guilds;

import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.web.WebService;
import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.Handler;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class AllGuildsRoute implements Handler{

	private final KittyBot main;

	public AllGuildsRoute(KittyBot main){
		this.main = main;
	}

	@Override
	public void handle(@NotNull Context ctx){
		var userId = this.main.getWebService().getUserId(ctx);
		if(!this.main.getConfig().getLongSet("owner_ids").contains(userId)){
			throw new ForbiddenResponse("Only bot owners have access to this");
		}
		var data = DataArray.fromCollection(
				this.main.getJDA().getGuildCache().stream().map(guild ->
						DataObject.empty().put("id", guild.getId()).put("name", guild.getName()).put("icon", guild.getIconUrl()).put("count", guild.getMemberCount()).put("owner", guild.getOwner() == null ? null : guild.getOwner().getUser().getAsTag())
				).collect(Collectors.toSet())
		);
		WebService.ok(ctx, DataObject.empty().put("guilds", data));
	}

}