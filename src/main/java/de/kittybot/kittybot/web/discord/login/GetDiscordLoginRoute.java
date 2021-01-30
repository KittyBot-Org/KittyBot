package de.kittybot.kittybot.web.discord.login;

import de.kittybot.kittybot.modules.DashboardSessionModule;
import de.kittybot.kittybot.modules.WebService;
import de.kittybot.kittybot.objects.module.Modules;
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
		var dashboardSessionModule = this.modules.get(DashboardSessionModule.class);
		try{
			if(!dashboardSessionModule.has(this.modules.get(WebService.class).getUserId(ctx))){
				ctx.redirect(Config.REDIRECT_URL);
				return;
			}
		}
		catch(UnauthorizedResponse | ForbiddenResponse ignored){
		}
		ctx.redirect(dashboardSessionModule.getOAuth2Client().generateAuthorizationURL(Config.REDIRECT_URL, DashboardSessionModule.getScopes()));
	}

}
