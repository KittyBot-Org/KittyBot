package de.kittybot.kittybot.web.routes.login;

import de.kittybot.kittybot.module.Modules;
import de.kittybot.kittybot.modules.DashboardSessionModule;
import de.kittybot.kittybot.web.WebService;
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
		this.modules.get(DashboardSessionModule.class).delete(this.modules.get(WebService.class).getUserId(ctx));
	}

}
