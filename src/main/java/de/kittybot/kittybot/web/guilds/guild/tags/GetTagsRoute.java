package de.kittybot.kittybot.web.guilds.guild.tags;

import de.kittybot.kittybot.modules.TagsModule;
import de.kittybot.kittybot.modules.WebModule;
import de.kittybot.kittybot.objects.module.Modules;
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
		var guild = this.modules.get(WebModule.class).getGuild(ctx);
		var tags = this.modules.get(TagsModule.class).get(guild.getIdLong());
		WebModule.ok(ctx, DataObject.empty().put("tags", DataArray.fromCollection(
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
