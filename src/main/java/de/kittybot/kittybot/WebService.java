package de.kittybot.kittybot;

import com.jagrosh.jdautilities.oauth2.OAuth2Client;
import com.jagrosh.jdautilities.oauth2.Scope;
import com.jagrosh.jdautilities.oauth2.exceptions.InvalidStateException;
import de.kittybot.kittybot.cache.DashboardSessionCache;
import de.kittybot.kittybot.cache.GuildCache;
import de.kittybot.kittybot.cache.GuildSettingsCache;
import de.kittybot.kittybot.cache.SelfAssignableRoleCache;
import de.kittybot.kittybot.database.Database;
import de.kittybot.kittybot.objects.Config;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandManager;
import de.kittybot.kittybot.objects.guilds.GuildData;
import de.kittybot.kittybot.objects.session.DashboardSessionController;
import io.javalin.Javalin;
import io.javalin.apibuilder.ApiBuilder;
import io.javalin.http.Context;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class WebService{

	private static final Logger LOG = LoggerFactory.getLogger(WebService.class);

	private static final Scope[] SCOPES = {Scope.IDENTIFY, Scope.GUILDS};
	private static final OAuth2Client O_AUTH_2_CLIENT = new OAuth2Client.Builder()
			.setClientId(Long.parseLong(Config.BOT_ID))
			.setClientSecret(Config.BOT_SECRET)
			.setOkHttpClient(KittyBot.getHttpClient())
			.setSessionController(new DashboardSessionController())
			.build();

	public WebService(int port){
		Javalin.create(config -> config.enableCorsForOrigin(Config.ORIGIN_URL)).routes(() -> {
			ApiBuilder.get("/discord_login", WebService::discordLogin);
			ApiBuilder.get("/health_check", ctx -> ctx.result("alive"));
			ApiBuilder.get("/commands/get", WebService::getCommands);
			ApiBuilder.post("/login", WebService::login);
			ApiBuilder.post("/logout", WebService::logout);
			ApiBuilder.path("/user", () -> {
				ApiBuilder.before("/*", WebService::checkDiscordLogin);
				ApiBuilder.get("/me", WebService::getUserInfo);
			});
			ApiBuilder.path("/guilds", () -> {
				ApiBuilder.before("/*", WebService::checkDiscordLogin);
				ApiBuilder.get("/all", WebService::getAllGuilds);
				ApiBuilder.path("/:guildId", () -> {
					ApiBuilder.before("/*", WebService::checkGuildPerms);
					ApiBuilder.get("/roles/get", WebService::getRoles);
					ApiBuilder.get("/channels/get", WebService::getChannels);
					ApiBuilder.get("/emotes/get", WebService::getEmotes);
					ApiBuilder.path("/settings", () -> {
						ApiBuilder.get("/get", WebService::getGuildSettings);
						ApiBuilder.post("/set", WebService::setGuildSettings);
					});
				});
			});
		}).start(port);
	}

	public static OAuth2Client getOAuth2Client(){
		return O_AUTH_2_CLIENT;
	}

	public static Scope[] getScopes(){
		return SCOPES;
	}

	private static void discordLogin(Context ctx){
		var key = ctx.header("Authorization");
		if(key == null || !DashboardSessionCache.sessionExists(key)){
			ctx.redirect(O_AUTH_2_CLIENT.generateAuthorizationURL(Config.REDIRECT_URL, SCOPES));
			return;
		}
		ctx.redirect(Config.REDIRECT_URL);
	}

	private static void login(Context ctx){
		var json = DataObject.fromJson(ctx.body());
		var code = json.getString("code", null);
		var state = json.getString("state", null);
		if(code == null || code.isBlank() || state == null || state.isBlank()){
			error(ctx, 401, "State or code is invalid");
			return;
		}
		try{
			var sessionKey = Database.generateUniqueKey();
			O_AUTH_2_CLIENT.startSession(code, state, sessionKey, SCOPES).complete();
			ok(ctx, DataObject.empty().put("key", sessionKey));
		}
		catch(InvalidStateException e){
			error(ctx, 401, "State invalid/expired. Please try again");
		}
		catch(IOException e){
			LOG.error("State is invalid", e);
			error(ctx, 403, "could not login");
		}
	}

	private static void logout(Context ctx){
		var auth = ctx.header("Authorization");
		if(auth == null){
			return;
		}
		DashboardSessionCache.deleteSession(auth);
	}

	private static void checkDiscordLogin(Context ctx){
		if(!ctx.method().equals("OPTIONS")){
			return;
		}
		var key = ctx.header("Authorization");
		if(key == null || !DashboardSessionCache.sessionExists(key)){
			error(ctx, 401, "Please login with discord to continue");
		}
	}

	private static void getUserInfo(Context ctx){
		var auth = ctx.header("Authorization");
		var session = DashboardSessionCache.getSession(auth);
		if(session == null){
			error(ctx, 404, "Please login again");
			return;
		}
		var userId = session.getUserId();
		var user = KittyBot.getJda().retrieveUserById(userId).complete();
		if(user == null){
			error(ctx, 404, "User not found");
			return;
		}
		List<GuildData> guilds;
		try{
			guilds = GuildCache.getGuilds(session);
		}
		catch(Exception ex){
			LOG.error("Error while retrieving user guilds for user: {}", userId, ex);
			error(ctx, 500, "There was an internal error");
			return;
		}
		var guildData = DataArray.empty();
		guilds.forEach(guild -> guildData.add(DataObject.empty().put("id", guild.getId()).put("name", guild.getName()).put("icon", guild.getIconUrl())));
		ok(ctx, DataObject.empty().put("name", user.getName()).put("id", userId).put("icon", user.getEffectiveAvatarUrl()).put("guilds", guildData));
	}

	private static void getAllGuilds(Context ctx){
		var auth = ctx.header("Authorization");
		var session = DashboardSessionCache.getSession(auth);
		if(session == null){
			error(ctx, 404, "Session not found");
			return;
		}
		if(!Config.ADMIN_IDS.contains(session.getUserId())){
			error(ctx, 403, "Only admins have access to this!");
			return;
		}
		var data = DataArray.empty();
		KittyBot.getJda().getGuildCache().forEach(guild -> {
			var owner = guild.getOwner();
			var obj = DataObject.empty().put("id", guild.getId()).put("name", guild.getName()).put("icon", guild.getIconUrl()).put("count", guild.getMemberCount());
			if(owner != null){
				obj.put("owner", owner.getUser().getAsTag());
			}
			data.add(obj);
		});
		ok(ctx, DataObject.empty().put("guilds", data));
	}

	private static void checkGuildPerms(Context ctx){
		if(!ctx.method().equals("OPTIONS")){
			return;
		}
		var guildId = ctx.pathParam(":guildId");
		var guild = KittyBot.getJda().getGuildById(guildId);
		if(guild == null){
			error(ctx, 404, "guild not found");
			return;
		}
		var auth = ctx.header("Authorization");
		if(auth == null){
			error(ctx, 401, "Please login");
			return;
		}
		var session = DashboardSessionCache.getSession(auth);
		if(session == null){
			error(ctx, 404, "This user does not exist");
			return;
		}
		if(Config.ADMIN_IDS.contains(session.getUserId())){
			return;
		}
		var member = guild.retrieveMemberById(session.getUserId()).complete();
		if(member == null){
			error(ctx, 404, "I could not find you in that guild");
			return;
		}
		if(!member.hasPermission(Permission.ADMINISTRATOR)){
			error(ctx, 401, "You have no permission for this guild");
		}
	}

	private static void getCommands(Context ctx){
		var commandSet = CommandManager.getDistinctCommands().entrySet();
		var data = DataArray.empty();
		for(var cat : Category.values()){
			var commands = DataArray.empty();
			for(var cmd : commandSet){
				var command = cmd.getValue();
				if(cat == command.getCategory()){
					commands.add(DataObject.empty().put("command", command.getCommand()).put("description", command.getDescription()));
				}
			}
			data.add(DataObject.empty().put("name", cat.getFriendlyName()).put("emote_url", cat.getEmoteUrl()).put("commands", commands));
		}

		ok(ctx, DataObject.empty().put("prefix", Config.DEFAULT_PREFIX).put("categories", data));
	}

	private static void getRoles(Context ctx){
		var guild = KittyBot.getJda().getGuildById(ctx.pathParam(":guildId"));
		if(guild == null){
			error(ctx, 404, "guild not found");
			return;
		}
		var data = DataArray.empty();
		for(var role : guild.getRoles()){
			if(role.isPublicRole()){
				continue;
			}
			var color = role.getColor();
			data.add(DataObject.empty().put("name", role.getName()).put("id", role.getId()).put("color", color == null ? "" : "#"+Integer.toHexString(color.getRGB()).substring(2)));
		}
		ok(ctx, DataObject.empty().put("roles", data));
	}

	private static void getChannels(Context ctx){
		var guild = KittyBot.getJda().getGuildById(ctx.pathParam(":guildId"));
		if(guild == null){
			error(ctx, 404, "guild not found");
			return;
		}
		var data = DataArray.empty();
		for(var channel : guild.getTextChannels()){
			data.add(DataObject.empty().put("name", channel.getName()).put("id", channel.getId()));
		}
		ok(ctx, DataObject.empty().put("channels", data));
	}

	private static void getEmotes(Context ctx){
		var guild = KittyBot.getJda().getGuildById(ctx.pathParam(":guildId"));
		if(guild == null){
			error(ctx, 404, "guild not found");
			return;
		}
		var data = DataArray.empty();
		for(var emote : guild.getEmotes()){
			data.add(DataObject.empty().put("name", emote.getName()).put("id", emote.getId()).put("url", emote.getImageUrl()));
		}
		ok(ctx, DataObject.empty().put("emotes", data));
	}

	private static void getGuildSettings(Context ctx){
		var guildId = ctx.pathParam(":guildId");
		var roles = SelfAssignableRoleCache.getSelfAssignableRoles(guildId);
		if(roles == null || KittyBot.getJda().getGuildById(guildId) == null){
			error(ctx, 404, "guild not found");
			return;
		}
		var data = DataArray.empty();
		for(var role : roles.entrySet()){
			data.add(DataObject.empty().put("role", role.getKey()).put("emote", role.getValue()));
		}
		var settings = GuildSettingsCache.getGuildSettings(guildId);
		ok(ctx, DataObject.empty()
				.put("prefix", settings.getCommandPrefix())
				.put("join_messages_enabled", settings.areJoinMessagesEnabled())
				.put("join_messages", settings.getJoinMessage())
				.put("leave_messages_enabled", settings.areLeaveMessagesEnabled())
				.put("leave_messages", settings.getLeaveMessage())
				.put("boost_messages_enabled", Database.getBoostMessageEnabled(guildId))
				.put("boost_messages", settings.getBoostMessage())
				.put("announcement_channel_id", settings.getAnnouncementChannelId())
				.put("nsfw_enabled", settings.isNSFWEnabled())
				.put("self_assignable_roles", data));
	}

	private static void setGuildSettings(Context ctx){
		var guildId = ctx.pathParam(":guildId");
		if(KittyBot.getJda().getGuildById(guildId) == null){
			error(ctx, 404, "guild not found");
			return;
		}
		var json = DataObject.fromJson(ctx.body());
		if(json.hasKey("prefix")){
			GuildSettingsCache.setCommandPrefix(guildId, json.getString("prefix"));
		}
		if(json.hasKey("join_messages_enabled")){
			GuildSettingsCache.setJoinMessagesEnabled(guildId, json.getBoolean("join_messages_enabled"));
		}
		if(json.hasKey("join_messages")){
			GuildSettingsCache.setJoinMessage(guildId, json.getString("join_messages"));
		}
		if(json.hasKey("leave_messages_enabled")){
			GuildSettingsCache.setLeaveMessagesEnabled(guildId, json.getBoolean("leave_messages_enabled"));
		}
		if(json.hasKey("leave_messages")){
			GuildSettingsCache.setLeaveMessage(guildId, json.getString("leave_messages"));
		}

		if(json.hasKey("boost_messages_enabled")){
			GuildSettingsCache.setBoostMessagesEnabled(guildId, json.getBoolean("boost_messages_enabled"));
		}

		if(json.hasKey("boost_messages")){
			GuildSettingsCache.setBoostMessage(guildId, json.getString("boost_messages"));
		}

		if(json.hasKey("announcement_channel_id")){
			GuildSettingsCache.setAnnouncementChannelId(guildId, json.getString("announcement_channel_id"));
		}

		if(json.hasKey("nsfw_enabled")){
			GuildSettingsCache.setNSFWEnabled(guildId, json.getBoolean("nsfw_enabled"));
		}
		ok(ctx);
	}

	private static void error(Context ctx, int code, String error){
		result(ctx, code, DataObject.empty().put("error", error));
	}

	private static void ok(Context ctx){
		result(ctx, 200, DataObject.empty().put("status", 200));
	}

	private static void ok(Context ctx, DataObject data){
		result(ctx, 200, data);
	}

	private static void result(Context ctx, int code, DataObject data){
		ctx.header("Content-Type", "application/json");
		ctx.status(code);
		ctx.result(data.toString());
	}

}
