package de.kittybot.kittybot.web.guilds.guild.roles;

import de.kittybot.kittybot.modules.WebService;
import de.kittybot.kittybot.objects.module.Modules;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class GetRolesRoute implements Handler{

	private final Modules modules;

	public GetRolesRoute(Modules modules){
		this.modules = modules;
	}

	@Override
	public void handle(@NotNull Context ctx){
		var guild = this.modules.get(WebService.class).getGuild(ctx);
		var roles = DataArray.fromCollection(
			guild.getRoleCache().stream().filter(role -> !role.isPublicRole()).map(role -> DataObject.empty().put("id", role.getIdLong()).put("name", role.getName()).put("color", role.getColor() == null ? "" : "#" + Integer.toHexString(role.getColor().getRGB()).substring(2))).collect(Collectors.toSet())
		);
		WebService.ok(ctx, DataObject.empty().put("roles", roles));
	}

}
