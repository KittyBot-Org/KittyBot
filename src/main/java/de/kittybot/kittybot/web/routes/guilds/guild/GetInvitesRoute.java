package de.kittybot.kittybot.web.routes.guilds.guild;

import de.kittybot.kittybot.module.Modules;
import de.kittybot.kittybot.web.WebService;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class GetInvitesRoute implements Handler{

	private final Modules modules;

	public GetInvitesRoute(Modules modules){
		this.modules = modules;
	}

	@Override
	public void handle(@NotNull Context ctx){
		var guild = this.modules.getWebService().getGuild(ctx);
		var invites = DataArray.fromCollection(
				this.modules.getInviteModule().getGuildInvites(guild.getIdLong()).values().stream().map(
						invite -> DataObject.empty().put("code", invite.getCode()).put("user_id", invite.getUserId()).put("uses", invite.getUses())
				).collect(Collectors.toSet())
		);
		WebService.ok(ctx, DataObject.empty().put("invites", invites));
	}

}
