package de.kittybot.kittybot.web.routes.guilds.guild;

import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.web.WebService;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class GetGuildSettingsRoute implements Handler{

	private final KittyBot main;

	public GetGuildSettingsRoute(KittyBot main){
		this.main = main;
	}

	@Override
	public void handle(@NotNull Context ctx){
		var guildId = this.main.getWebService().getGuild(ctx).getIdLong();
		var settings = this.main.getGuildSettingsManager().getSettings(guildId);
		var selfAssignableRoles = DataArray.empty();
		//SelfAssignableRoleCache.getSelfAssignableRoles(guildId).forEach((key, value) -> data.add(DataObject.empty().put("role", key).put("emote", value)));
		var selfAssignableRoleGroups = DataArray.empty();
		var inviteRoles = DataArray.fromCollection(
				settings.getInviteRoles().entrySet().stream().map(entry ->
						DataObject.empty().put("code", entry.getKey()).put("roles", DataArray.fromCollection(
								entry.getValue()
						))
				).collect(Collectors.toSet())
		);
		WebService.ok(ctx, DataObject.empty()
				.put("prefix", settings.getCommandPrefix())

				.put("stream_announcement_channel_id", settings.getStreamAnnouncementChannelId())
				.put("stream_announcement_message", settings.getStreamAnnouncementMessage())

				.put("announcement_channel_id", settings.getAnnouncementChannelId())

				.put("request_channel_id", settings.getRequestChannelId())
				.put("requests_enabled", settings.areRequestsEnabled())

				.put("join_messages_enabled", settings.areJoinMessagesEnabled())
				.put("join_messages", settings.getJoinMessage())

				.put("leave_messages_enabled", settings.areLeaveMessagesEnabled())
				.put("leave_messages", settings.getLeaveMessage())

				.put("log_channel_id", settings.getLogChannelId())
				.put("log_messages_enabled", settings.areLogMessagesEnabled())

				.put("nsfw_enabled", settings.isNsfwEnabled())

				.put("inactive_role_id", settings.getInactiveRoleId())
				.put("inactive_duration", settings.getInactiveDuration().toMillis())

				.put("dj_role_id", settings.getDjRoleId())

				.put("snipes_enabled", settings.areSnipesEnabled())
				.put("snipe_disabled_channels", settings.getSnipeDisabledChannels())

				.put("bot_disabled_channels", settings.getBotDisabledChannels())
				.put("bot_ignored_users", settings.getBotIgnoredUsers())

				.put("self_assignable_roles", selfAssignableRoles)
				.put("self_assignable_role_groups", selfAssignableRoleGroups)

				.put("invite_roles", inviteRoles)
		);
	}

}
