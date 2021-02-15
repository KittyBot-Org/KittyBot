package de.kittybot.kittybot.web.webhooks.botlist.space;

import de.kittybot.kittybot.objects.module.Modules;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;

public class PostBotlistSpaceVoteRoute implements Handler{

	private final Modules modules;

	public PostBotlistSpaceVoteRoute(Modules modules){
		this.modules = modules;
	}

	@Override
	public void handle(@NotNull Context ctx){
		var json = DataObject.fromJson(ctx.bodyAsBytes());
		// every 24h
		/*
		{
		    "site": "botlist.space", // String
		    "bot": "508415615036424192", // String
		    "user": { // Object
		        "id": "507329700402561045", // String
		        "username": "PassTheMayo", // String
		        "discriminator": "1281", // String
		        "avatar": "...", // String
		        "short_description": "..." // ?String
		    },
		    "timestamp": 1546190816830 // Number
		}
		*/


	}

}
