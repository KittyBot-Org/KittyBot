package de.kittybot.kittybot.web.routes.guilds;

import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.web.WebService;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;

public class GetGuildSettingsRoute implements Handler{

	private final KittyBot main;

	public GetGuildSettingsRoute(KittyBot main){
		this.main = main;
	}

	@Override
	public void handle(@NotNull Context ctx){
		var guildId = this.main.getWebService().getGuild(ctx).getIdLong();
		var selfAssignableRole = DataArray.empty();
		//SelfAssignableRoleCache.getSelfAssignableRoles(guildId).forEach((key, value) -> data.add(DataObject.empty().put("role", key).put("emote", value)));
		var settings = this.main.getCommandManager().getGuildSettingsManager().getSettings(guildId);
		WebService.ok(ctx, DataObject.empty()
				.put("prefix", settings.getCommandPrefix())
				.put("join_messages_enabled", settings.areJoinMessagesEnabled())
				.put("join_messages", settings.getJoinMessage())
				.put("leave_messages_enabled", settings.areLeaveMessagesEnabled())
				.put("leave_messages", settings.getLeaveMessage())
				.put("announcement_channel_id", settings.getAnnouncementChannelId())
				.put("nsfw_enabled", settings.isNsfwEnabled())
				.put("self_assignable_roles", selfAssignableRole)
		);
	}

}
