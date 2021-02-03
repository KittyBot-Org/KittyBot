package de.kittybot.kittybot.web.guilds.guild.settings;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.modules.WebModule;
import de.kittybot.kittybot.objects.module.Modules;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class PostGuildSettingsRoute implements Handler{

	private final Modules modules;

	public PostGuildSettingsRoute(Modules modules){
		this.modules = modules;
	}

	@Override
	public void handle(@NotNull Context ctx){
		var guildId = this.modules.get(WebModule.class).getGuild(ctx).getIdLong();
		var json = DataObject.fromJson(ctx.body());
		var settings = this.modules.get(SettingsModule.class);

		settings.setStreamAnnouncementChannelId(guildId, json.getLong("stream_announcement_channel_id"));
		settings.setStreamAnnouncementMessage(guildId, json.getString("stream_announcement_message"));

		settings.setAnnouncementChannelId(guildId, json.getLong("announcement_channel_id"));

		settings.setRequestChannelId(guildId, json.getLong("request_channel_id"));
		settings.setRequestsEnabled(guildId, json.getBoolean("requests_enabled"));

		settings.setJoinMessagesEnabled(guildId, json.getBoolean("join_messages_enabled"));
		settings.setJoinMessage(guildId, json.getString("join_message"));

		settings.setLeaveMessagesEnabled(guildId, json.getBoolean("leave_messages_enabled"));
		settings.setLeaveMessage(guildId, json.getString("leave_message"));

		settings.setLogChannelId(guildId, json.getLong("log_channel_id"));
		settings.setLogMessagesEnabled(guildId, json.getBoolean("log_messages_enabled"));

		settings.setNsfwEnabled(guildId, json.getBoolean("nsfw_enabled"));

		//settings.setInactiveRoleId(guildId, json.getLong("inactive_role_id"));
		//settings.setInactiveDuration(guildId, json.getString("inactive_duration"));

		settings.setDjRoleId(guildId, json.getLong("dj_role_id"));

		settings.setSnipesEnabled(guildId, json.getBoolean("snipes_enabled"));
		//settings.setSnipesDisabledInChannels(guildId, json.getArray("snipe_disabled_channels"));

		//settings.setBotDisabledInChannels(guildId, json.getArray("bot_disabled_channels"));
		//settings.addBotIgnoredUsers(guildId, json.getArray("bot_ignored_users"));

		//settings.addSelfAssignableRoleGroups(guildId, json.getArray("self_assignable_role_groups"));

		//settings.setInviteRoles(guildId, json.getArray("invite_roles"));

		WebModule.accepted(ctx);
	}

}
