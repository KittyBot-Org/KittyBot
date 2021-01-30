package de.kittybot.kittybot.web.guilds.guild.invites;

import de.kittybot.kittybot.modules.InviteModule;
import de.kittybot.kittybot.modules.WebService;
import de.kittybot.kittybot.objects.module.Modules;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.stream.Collectors;

public class GetInvitesRoute implements Handler{

	private final Modules modules;

	public GetInvitesRoute(Modules modules){
		this.modules = modules;
	}

	@Override
	public void handle(@NotNull Context ctx){
		var guild = this.modules.get(WebService.class).getGuild(ctx);

		var guildInvites = this.modules.get(InviteModule.class).getGuildInvites(guild.getIdLong());
		if(guildInvites == null){
			guildInvites = new HashMap<>();
		}
		var invites = DataArray.fromCollection(
			guildInvites.values().stream().map(
				invite -> DataObject.empty().put("code", invite.getCode()).put("user_id", invite.getUserId()).put("uses", invite.getUses())
			).collect(Collectors.toSet())
		);
		WebService.ok(ctx, DataObject.empty().put("invites", invites));
	}

}
