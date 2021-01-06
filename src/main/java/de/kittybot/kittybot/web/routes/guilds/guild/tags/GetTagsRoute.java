package de.kittybot.kittybot.web.routes.guilds.guild.tags;

import de.kittybot.kittybot.module.Modules;
import de.kittybot.kittybot.web.WebService;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;

import java.time.ZoneOffset;
import java.util.stream.Collectors;

public class GetTagsRoute implements Handler{

	private final Modules modules;

	public GetTagsRoute(Modules modules){
		this.modules = modules;
	}

	@Override
	public void handle(@NotNull Context ctx){
		var guild = this.modules.getWebService().getGuild(ctx);
		var tags = this.modules.getTagModule().getAll(guild.getIdLong());
		WebService.ok(ctx, DataObject.empty().put("tags", DataArray.fromCollection(
				tags.stream().map(tag -> DataObject.empty()
						.put("id", tag.getId())
						.put("name", tag.getName())
						.put("content", tag.getContent())
						.put("user_id", tag.getUserId())
						.put("guild_id", tag.getGuildId())
						.put("created_at", tag.getCreatedAt().toEpochSecond(ZoneOffset.UTC))
				).collect(Collectors.toSet())
		)));
	}

}
