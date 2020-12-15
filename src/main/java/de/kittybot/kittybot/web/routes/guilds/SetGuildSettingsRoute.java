package de.kittybot.kittybot.web.routes.guilds;

import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.web.WebService;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;

public class SetGuildSettingsRoute implements Handler{

	private final KittyBot main;

	public SetGuildSettingsRoute(KittyBot main){
		this.main = main;
	}

	@Override
	public void handle(@NotNull Context ctx){
		var guildId = this.main.getWebService().getGuild(ctx).getIdLong();
		var json = DataObject.fromJson(ctx.body());
		var settings = this.main.getCommandManager().getGuildSettingsManager();
		if(json.hasKey("prefix")){
			settings.setPrefix(guildId, json.getString("prefix"));
		}
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
		WebService.ok(ctx);
	}

}
