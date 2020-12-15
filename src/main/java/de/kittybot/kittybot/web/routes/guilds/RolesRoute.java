package de.kittybot.kittybot.web.routes.guilds;

import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.web.WebService;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class RolesRoute implements Handler{

	private final KittyBot main;

	public RolesRoute(KittyBot main){
		this.main = main;
	}

	@Override
	public void handle(@NotNull Context ctx){
		var guild = this.main.getWebService().getGuild(ctx);
		var roles = DataArray.fromCollection(
				guild.getRoleCache().stream().map(channel -> DataObject.empty().put("id", channel.getId()).put("name", channel.getName())).collect(Collectors.toSet())
		);
		WebService.ok(ctx, DataObject.empty().put("roles", roles));
	}

}
