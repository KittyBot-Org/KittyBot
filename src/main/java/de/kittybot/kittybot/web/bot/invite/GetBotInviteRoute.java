package de.kittybot.kittybot.web.bot.invite;

import de.kittybot.kittybot.utils.Config;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class GetBotInviteRoute implements Handler{

	@Override
	public void handle(@NotNull Context ctx){
		ctx.redirect(Config.BOT_INVITE_URL);
	}

}
