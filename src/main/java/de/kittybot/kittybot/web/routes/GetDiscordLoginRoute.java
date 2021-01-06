package de.kittybot.kittybot.web.routes;

import de.kittybot.kittybot.module.Modules;
import de.kittybot.kittybot.modules.DashboardSessionModule;
import de.kittybot.kittybot.utils.Config;
import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.Handler;
import io.javalin.http.UnauthorizedResponse;
import org.jetbrains.annotations.NotNull;

public class GetDiscordLoginRoute implements Handler{

	private final Modules modules;

	public GetDiscordLoginRoute(Modules modules){
		this.modules = modules;
	}

	@Override
	public void handle(@NotNull Context ctx){
		try{
			if(!this.modules.getDashboardSessionModule().has(this.modules.getWebService().getUserId(ctx))){
				ctx.redirect(Config.REDIRECT_URL);
				return;
			}
		}
		catch(UnauthorizedResponse | ForbiddenResponse ignored){
		}
		ctx.redirect(this.modules.getDashboardSessionModule().getOAuth2Client().generateAuthorizationURL(Config.REDIRECT_URL, DashboardSessionModule.getScopes()));
	}

}
