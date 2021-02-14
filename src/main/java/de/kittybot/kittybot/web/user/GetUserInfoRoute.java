package de.kittybot.kittybot.web.user;

import de.kittybot.kittybot.modules.DashboardSessionModule;
import de.kittybot.kittybot.modules.WebModule;
import de.kittybot.kittybot.objects.data.GuildData;
import de.kittybot.kittybot.objects.module.Modules;
import io.javalin.http.*;
import net.dv8tion.jda.api.exceptions.HttpException;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class GetUserInfoRoute implements Handler{

	private final Modules modules;

	public GetUserInfoRoute(Modules modules){
		this.modules = modules;
	}

	@Override
	public void handle(@NotNull Context ctx){
		var userId = this.modules.get(WebModule.class).getUserId(ctx);
		var dashboardSessionModule = this.modules.get(DashboardSessionModule.class);
		List<GuildData> guilds;
		try{
			guilds = dashboardSessionModule.getGuilds(userId);
		}
		catch(HttpException e){
			throw new UnauthorizedResponse("Please login again");
		}
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

		WebModule.ok(ctx, DataObject.empty().put("name", user.getName()).put("id", String.valueOf(userId)).put("icon", user.getEffectiveAvatarUrl()).put("guilds", guildData));
	}

}
