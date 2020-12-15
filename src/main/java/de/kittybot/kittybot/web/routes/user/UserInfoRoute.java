package de.kittybot.kittybot.web.routes.user;

import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.web.WebService;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.http.NotFoundResponse;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.stream.Collectors;

public class UserInfoRoute implements Handler{

	private final KittyBot main;

	public UserInfoRoute(KittyBot main){
		this.main = main;
	}

	@Override
	public void handle(@NotNull Context ctx){
		var userId = this.main.getWebService().getUserId(ctx);
		var session = this.main.getDashboardSessionManager().get(userId);
		if(session == null){
			throw new NotFoundResponse("Session not found");
		}
		var user = this.main.getJDA().retrieveUserById(userId).complete();
		if(user == null){
			throw new NotFoundResponse("User not found");
		}
		try{
			var guilds = this.main.getDashboardSessionManager().getOAuth2Client().getGuilds(session).complete();
			var guildData = DataArray.fromCollection(
					guilds.stream().map(guild -> DataObject.empty().put("id", guild.getId()).put("name", guild.getName()).put("icon", guild.getIconUrl())).collect(Collectors.toSet())
			);
			WebService.ok(ctx, DataObject.empty().put("name", user.getName()).put("id", userId).put("icon", user.getEffectiveAvatarUrl()).put("guilds", guildData));
		}
		catch(IOException ex){
			throw new InternalServerErrorResponse("There was a problem while login. Please try again");
		}
	}

}
