package de.kittybot.kittybot.web.guilds.guild.settings;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.modules.WebModule;
import de.kittybot.kittybot.objects.module.Modules;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;

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
		if(json.hasKey("join_messages_enabled")){
			settings.setJoinMessagesEnabled(guildId, json.getBoolean("join_messages_enabled"));
		}
		if(json.hasKey("join_messages")){
			settings.setJoinMessage(guildId, json.getString("join_messages"));
		}
		if(json.hasKey("leave_messages_enabled")){
			settings.setLeaveMessagesEnabled(guildId, json.getBoolean("leave_messages_enabled"));
		}
		if(json.hasKey("leave_messages")){
			settings.setLeaveMessage(guildId, json.getString("leave_messages"));
		}
		if(json.hasKey("announcement_channel_id")){
			settings.setAnnouncementChannelId(guildId, json.getLong("announcement_channel_id"));
		}
		if(json.hasKey("nsfw_enabled")){
			settings.setNsfwEnabled(guildId, json.getBoolean("nsfw_enabled"));
		}
		if(json.hasKey("self_assignable_roles")){

		}
		if(json.hasKey("self_assignable_role_groups")){

		}
		WebModule.accepted(ctx);
	}

}
