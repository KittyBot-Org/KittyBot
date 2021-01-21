package de.kittybot.kittybot.modules.web.routes.guilds.guild;

import de.kittybot.kittybot.modules.web.WebService;
import de.kittybot.kittybot.objects.module.Modules;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class GetEmotesRoute implements Handler{

	private final Modules modules;

	public GetEmotesRoute(Modules modules){
		this.modules = modules;
	}

	@Override
	public void handle(@NotNull Context ctx){
		var guild = this.modules.get(WebService.class).getGuild(ctx);
		var emotes = DataArray.fromCollection(
			guild.getEmoteCache().stream().map(emote -> DataObject.empty().put("id", emote.getId()).put("name", emote.getName()).put("url", emote.getImageUrl())).collect(Collectors.toSet())
		);
		WebService.ok(ctx, DataObject.empty().put("emotes", emotes));
	}

}
