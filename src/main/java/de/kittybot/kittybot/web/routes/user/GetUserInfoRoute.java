package de.kittybot.kittybot.web.routes.user;

import de.kittybot.kittybot.exceptions.TooManyRequestResponse;
import de.kittybot.kittybot.module.Modules;
import de.kittybot.kittybot.modules.DashboardSessionModule;
import de.kittybot.kittybot.web.WebService;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.http.NotFoundResponse;
import net.dv8tion.jda.api.exceptions.HttpException;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
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
		var session = dashboardSessionModule.get(userId);
		if(session == null){
			throw new NotFoundResponse("Session not found");
		}
		var user = this.modules.getJDA().retrieveUserById(userId).complete();
		if(user == null){
			throw new NotFoundResponse("User not found");
		}
		try{
			var guildData = DataArray.fromCollection(
					dashboardSessionModule.getGuilds(session).stream().filter(
							guild -> this.modules.getJDA().getGuildCache().stream().anyMatch(g -> g.getIdLong() == guild.getIdLong())
					).map(
							guild -> DataObject.empty().put("id", guild.getId()).put("name", guild.getName()).put("icon", guild.getIconUrl())
					).collect(Collectors.toSet())
			);
			WebService.ok(ctx, DataObject.empty().put("name", user.getName()).put("id", userId).put("icon", user.getEffectiveAvatarUrl()).put("guilds", guildData));
		}
		catch(HttpException e){
			throw new TooManyRequestResponse("Discord ratelimits us please wait with your request");
		}
	}

}
