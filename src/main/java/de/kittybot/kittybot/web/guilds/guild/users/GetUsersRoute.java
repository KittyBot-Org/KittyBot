package de.kittybot.kittybot.web.guilds.guild.users;

import de.kittybot.kittybot.modules.WebModule;
import de.kittybot.kittybot.objects.module.Modules;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class GetUsersRoute implements Handler{

	private final Modules modules;

	public GetUsersRoute(Modules modules){
		this.modules = modules;
	}

	@Override
	public void handle(@NotNull Context ctx){
		var guild = this.modules.get(WebModule.class).getGuild(ctx);
		var users = DataArray.fromCollection(
			guild.loadMembers().get().stream().map(user ->
				DataObject.empty()
					.put("id", user.getId())
					.put("name", user.getUser().getAsTag())
					.put("url", user.getUser().getEffectiveAvatarUrl())
					.put("color", user.getColor() == null ? "" : "#" + Integer.toHexString(user.getColor().getRGB()).substring(2))
			).collect(Collectors.toSet())
		);
		WebModule.ok(ctx, DataObject.empty().put("users", users));
	}

}
