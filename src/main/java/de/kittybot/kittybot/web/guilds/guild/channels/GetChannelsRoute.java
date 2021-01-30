package de.kittybot.kittybot.web.guilds.guild.channels;

import de.kittybot.kittybot.modules.WebService;
import de.kittybot.kittybot.objects.module.Modules;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class GetChannelsRoute implements Handler{

	private final Modules modules;

	public GetChannelsRoute(Modules modules){
		this.modules = modules;
	}

	@Override
	public void handle(@NotNull Context ctx){
		var guild = this.modules.get(WebService.class).getGuild(ctx);
		var channels = DataArray.fromCollection(
			guild.getTextChannelCache().stream().map(channel -> DataObject.empty().put("id", channel.getId()).put("name", channel.getName())).collect(Collectors.toSet())
		);
		WebService.ok(ctx, DataObject.empty().put("channels", channels));
	}

}
