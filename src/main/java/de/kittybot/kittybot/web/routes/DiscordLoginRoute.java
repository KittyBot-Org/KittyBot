package de.kittybot.kittybot.web.routes;

import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.managers.DashboardSessionManager;
import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.Handler;
import io.javalin.http.UnauthorizedResponse;
import org.jetbrains.annotations.NotNull;

public class DiscordLoginRoute implements Handler{

	private final KittyBot main;

	public DiscordLoginRoute(KittyBot main){
		this.main = main;
	}

	@Override
	public void handle(@NotNull Context ctx){
		var redirectUrl = this.main.getConfig().getString("redirect_url");
		try{
			if(!this.main.getDashboardSessionManager().has(this.main.getWebService().getUserId(ctx))){
				ctx.redirect(redirectUrl);
				return;
			}
		}
		catch(UnauthorizedResponse | ForbiddenResponse ignored){
		}
		ctx.redirect(this.main.getDashboardSessionManager().getOAuth2Client().generateAuthorizationURL(redirectUrl, DashboardSessionManager.getScopes()));
	}

}
