package de.kittybot.kittybot.web.routes.login;

import de.kittybot.kittybot.module.Modules;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class DeleteLoginRoute implements Handler{

	private final Modules modules;

	public DeleteLoginRoute(Modules modules){
		this.modules = modules;
	}

	@Override
	public void handle(@NotNull Context ctx){
		this.modules.getDashboardSessionModule().delete(this.modules.getWebService().getUserId(ctx));
	}

}
