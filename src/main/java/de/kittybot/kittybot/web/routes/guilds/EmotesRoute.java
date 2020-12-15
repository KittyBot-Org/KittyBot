package de.kittybot.kittybot.web.routes.guilds;

import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.web.WebService;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class EmotesRoute implements Handler{

	private final KittyBot main;

	public EmotesRoute(KittyBot main){
		this.main = main;
	}

	@Override
	public void handle(@NotNull Context ctx){
		var guild = this.main.getWebService().getGuild(ctx);
		var emotes = DataArray.fromCollection(
				guild.getEmoteCache().stream().map(emote -> DataObject.empty().put("id", emote.getId()).put("name", emote.getName()).put("url", emote.getImageUrl())).collect(Collectors.toSet())
		);
		WebService.ok(ctx, DataObject.empty().put("emotes", emotes));
	}

}
