package de.kittybot.kittybot.web.login;

import de.kittybot.kittybot.modules.DashboardSessionModule;
import de.kittybot.kittybot.modules.WebModule;
import de.kittybot.kittybot.objects.module.Modules;
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
		this.modules.get(DashboardSessionModule.class).delete(this.modules.get(WebModule.class).getUserId(ctx));
	}

}
