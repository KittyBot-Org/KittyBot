package de.kittybot.kittybot.web.shards;

import de.kittybot.kittybot.modules.WebModule;
import de.kittybot.kittybot.objects.module.Modules;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class GetShardsRoute implements Handler{

	private final Modules modules;

	public GetShardsRoute(Modules modules){
		this.modules = modules;
	}

	@Override
	public void handle(@NotNull Context ctx){
		WebModule.ok(ctx, DataObject.empty().put("shards", DataArray.fromCollection(modules.getShardManager().getShardCache().stream().map(shard -> DataObject.empty()
				.put("id", shard.getShardInfo().getShardId())
				.put("guilds", shard.getGuildCache().size())
				.put("status", shard.getStatus().name())
				.put("ping", shard.getGatewayPing())
			).collect(Collectors.toList())))
		);
	}

}
