package de.kittybot.kittybot.web.info;

import de.kittybot.kittybot.modules.CommandsModule;
import de.kittybot.kittybot.modules.MusicModule;
import de.kittybot.kittybot.modules.WebModule;
import de.kittybot.kittybot.objects.module.Modules;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;

public class GetInfoRoute implements Handler{

	private final Modules modules;

	public GetInfoRoute(Modules modules){
		this.modules = modules;
	}

	@Override
	public void handle(@NotNull Context ctx){
		var shardManager = modules.getShardManager();
		WebModule.ok(ctx, DataObject.empty()
			.put("shards", shardManager.getShardCache().size())
			.put("guilds", shardManager.getGuildCache().size())
			.put("users", shardManager.getGuildCache().applyStream(guildStream -> guildStream.mapToInt(Guild::getMemberCount).sum()))
			.put("jda_version", JDAInfo.VERSION)
			.put("players", modules.get(MusicModule.class).getActivePlayers())
			.put("commands", modules.get(CommandsModule.class).getCommands().size())
		);
	}

}
