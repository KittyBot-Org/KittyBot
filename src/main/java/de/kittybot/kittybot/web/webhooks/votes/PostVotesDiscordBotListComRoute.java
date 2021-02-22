package de.kittybot.kittybot.web.webhooks.votes;

import de.kittybot.kittybot.objects.module.Modules;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;

public class PostVotesDiscordBotListComRoute implements Handler{

	private final Modules modules;

	public PostVotesDiscordBotListComRoute(Modules modules){
		this.modules = modules;
	}

	@Override
	public void handle(@NotNull Context ctx){
		var json = DataObject.fromJson(ctx.bodyAsBytes());
		// every 12h
		/*
		{
		    "admin": "true", // Boolean
		    "avatar": "54ttu7htgrwe3r45tz5gf3r4t5rfgvetrghrf", // String
		    "username": "PassTheMayo", // String
		    "id": 1546190816830 // String
		}
		*/
	}

}
