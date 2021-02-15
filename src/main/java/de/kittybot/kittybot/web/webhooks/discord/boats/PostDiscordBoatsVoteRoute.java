package de.kittybot.kittybot.web.webhooks.discord.boats;

import de.kittybot.kittybot.objects.module.Modules;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;

public class PostDiscordBoatsVoteRoute implements Handler{

	private final Modules modules;

	public PostDiscordBoatsVoteRoute(Modules modules){
		this.modules = modules;
	}

	@Override
	public void handle(@NotNull Context ctx){
		var json = DataObject.fromJson(ctx.bodyAsBytes());
		// every 12h
		/*{
			"bot": {
				id: String,
				name: String
			},
				"user": {
				id: String,
					username: String,
					discriminator: Number
			}
		}*/
	}

}
