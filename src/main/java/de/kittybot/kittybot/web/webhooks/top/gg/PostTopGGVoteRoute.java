package de.kittybot.kittybot.web.webhooks.top.gg;

import de.kittybot.kittybot.modules.WebModule;
import de.kittybot.kittybot.objects.module.Modules;
import de.kittybot.kittybot.utils.Config;
import edu.umd.cs.findbugs.annotations.Confidence;
import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.Handler;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;

public class PostTopGGVoteRoute implements Handler{

	private final Modules modules;

	public PostTopGGVoteRoute(Modules modules){
		this.modules = modules;
	}

	@Override
	public void handle(@NotNull Context ctx){
		if(!WebModule.verify(ctx, Config.TOP_GG_TOKEN)){
			throw new ForbiddenResponse();
		}
		var json = DataObject.fromJson(ctx.bodyAsBytes());
		// every 12h
		/*
		{
			"bot": Number,
			"user": Number,
			"type": String,
			"isWeekend": Boolean,
			"query": String
		}
		*/
	}



}
