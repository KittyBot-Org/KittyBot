package de.kittybot.kittybot.web.routes.login;

import de.kittybot.kittybot.main.KittyBot;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class LogoutRoute implements Handler{

	private final KittyBot main;

	public LogoutRoute(KittyBot main){
		this.main = main;
	}

	@Override
	public void handle(@NotNull Context ctx){
		this.main.getDashboardSessionManager().delete(this.main.getWebService().getUserId(ctx));
	}

}
