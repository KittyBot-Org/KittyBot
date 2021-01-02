package de.kittybot.kittybot.web.routes;

import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.managers.DashboardSessionManager;
import de.kittybot.kittybot.utils.Config;
import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.Handler;
import io.javalin.http.UnauthorizedResponse;
import org.jetbrains.annotations.NotNull;

public class GetDiscordLoginRoute implements Handler{

	private final KittyBot main;

	public GetDiscordLoginRoute(KittyBot main){
		this.main = main;
	}

	@Override
	public void handle(@NotNull Context ctx){
		try{
			if(!this.main.getDashboardSessionManager().has(this.main.getWebService().getUserId(ctx))){
				ctx.redirect(Config.REDIRECT_URL);
				return;
			}
		}
		catch(UnauthorizedResponse | ForbiddenResponse ignored){}
		ctx.redirect(this.main.getDashboardSessionManager().getOAuth2Client().generateAuthorizationURL(Config.REDIRECT_URL, DashboardSessionManager.getScopes()));
	}

}
