package de.kittybot.kittybot.web.user;

import de.kittybot.kittybot.modules.DashboardSessionModule;
import de.kittybot.kittybot.modules.WebService;
import de.kittybot.kittybot.objects.module.Modules;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.http.NotFoundResponse;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class GetUserInfoRoute implements Handler{

	private final Modules modules;

	public GetUserInfoRoute(Modules modules){
		this.modules = modules;
	}

	@Override
	public void handle(@NotNull Context ctx){
		var userId = this.modules.get(WebService.class).getUserId(ctx);
		var dashboardSessionModule = this.modules.get(DashboardSessionModule.class);
		var guilds = dashboardSessionModule.getGuilds(userId);
		if(guilds == null){
			throw new InternalServerErrorResponse("Failed to retrieve user guilds");
		}
		var user = this.modules.getShardManager().retrieveUserById(userId).complete();
		if(user == null){
			throw new NotFoundResponse("User not found");
		}
		var guildData = DataArray.fromCollection(guilds.stream().map(guild ->
			DataObject.empty().put("id", guild.getIdString()).put("name", guild.getName()).put("icon", guild.getIconUrl())
		).collect(Collectors.toSet()));

		WebService.ok(ctx, DataObject.empty().put("name", user.getName()).put("id", String.valueOf(userId)).put("icon", user.getEffectiveAvatarUrl()).put("guilds", guildData));
	}

}
