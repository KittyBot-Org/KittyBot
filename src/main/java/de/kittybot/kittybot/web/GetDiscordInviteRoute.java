package de.kittybot.kittybot.web;

import de.kittybot.kittybot.utils.Config;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class GetDiscordInviteRoute implements Handler{

	@Override
	public void handle(@NotNull Context ctx){
		ctx.redirect(Config.SUPPORT_GUILD_INVITE_URL);
	}

}
