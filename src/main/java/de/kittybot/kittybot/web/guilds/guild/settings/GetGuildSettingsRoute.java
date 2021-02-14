package de.kittybot.kittybot.web.guilds.guild.settings;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.modules.WebModule;
import de.kittybot.kittybot.objects.module.Modules;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

public class GetGuildSettingsRoute implements Handler{

	private final Modules modules;

	public GetGuildSettingsRoute(Modules modules){
		this.modules = modules;
	}

	@Override
	public void handle(@NotNull Context ctx){
		var guildId = this.modules.get(WebModule.class).getGuild(ctx).getIdLong();
		var settings = this.modules.get(SettingsModule.class).getSettings(guildId);
		var selfAssignableRoleGroups = DataArray.empty();
		var inviteRoles = DataArray.fromCollection(
			settings.getInviteRoles().entrySet().stream().map(entry ->
				DataObject.empty().put("code", entry.getKey()).put("roles", DataArray.fromCollection(
					entry.getValue().stream().map(String::valueOf).collect(Collectors.toSet())
				))
			).collect(Collectors.toSet())
		);
		WebModule.ok(ctx, DataObject.empty()
			.put("stream_announcement_channel_id", toString(settings.getStreamAnnouncementChannelId()))
			.put("stream_announcement_message", settings.getStreamAnnouncementMessage())

			.put("announcement_channel_id", toString(settings.getAnnouncementChannelId()))

			.put("request_channel_id", toString(settings.getRequestChannelId()))
			.put("requests_enabled", settings.areRequestsEnabled())

			.put("join_messages_enabled", settings.areJoinMessagesEnabled())
			.put("join_message", settings.getJoinMessage())

			.put("leave_messages_enabled", settings.areLeaveMessagesEnabled())
			.put("leave_message", settings.getLeaveMessage())

			.put("log_channel_id", toString(settings.getLogChannelId()))
			.put("log_messages_enabled", settings.areLogMessagesEnabled())

			.put("nsfw_enabled", settings.isNsfwEnabled())

			.put("inactive_role_id", toString(settings.getInactiveRoleId()))
			.put("inactive_duration", settings.getInactiveDuration().toMillis())

			.put("dj_role_id", toString(settings.getDjRoleId()))

			.put("snipes_enabled", settings.areSnipesEnabled())
			.put("snipe_disabled_channels", toString(settings.getSnipeDisabledChannels()))

			.put("bot_disabled_channels", toString(settings.getBotDisabledChannels()))
			.put("bot_ignored_users", toString(settings.getBotIgnoredUsers()))

			.put("self_assignable_role_groups", selfAssignableRoleGroups)

			.put("invite_roles", inviteRoles)
		);
	}

	private String toString(Long value){
		return String.valueOf(value);
	}

	private Set<String> toString(Set<Long> set){
		return set.stream().map(String::valueOf).collect(Collectors.toSet());
	}

}
