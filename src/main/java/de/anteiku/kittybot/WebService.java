package de.anteiku.kittybot;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jagrosh.jdautilities.oauth2.OAuth2Client;
import com.jagrosh.jdautilities.oauth2.Scope;
import com.jagrosh.jdautilities.oauth2.entities.OAuth2User;
import com.jagrosh.jdautilities.oauth2.entities.impl.OAuth2ClientImpl;
import com.jagrosh.jdautilities.oauth2.exceptions.InvalidStateException;
import com.jagrosh.jdautilities.oauth2.session.DefaultSessionController;
import com.jagrosh.jdautilities.oauth2.session.Session;
import com.jagrosh.jdautilities.oauth2.state.DefaultStateController;
import de.anteiku.kittybot.database.Database;
import de.anteiku.kittybot.objects.Config;
import io.javalin.Javalin;
import io.javalin.http.Context;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static io.javalin.apibuilder.ApiBuilder.*;

public class WebService{

	private static final Logger LOG = LoggerFactory.getLogger(WebService.class);

	private final Scope[] scopes = new Scope[]{Scope.IDENTIFY};
	private final OAuth2Client oAuthClient;

	public WebService(int port){
		DefaultSessionController sessionController = new DefaultSessionController();
		DefaultStateController stateController = new DefaultStateController();
		oAuthClient = new OAuth2ClientImpl(Long.parseLong(Config.BOT_ID), Config.BOT_SECRET, sessionController, stateController, KittyBot.getHttpClient());
		Javalin.create(config -> config.enableCorsForOrigin(Config.ORIGIN_URL)).routes(() -> {
			get("/discord_login", this::discordLogin);
			get("/health_check", ctx -> ctx.result("alive"));
			post("/login", this::login);
			path("/user", () -> {
				before("/*", this::checkDiscordLogin);
				get("/me", this::getUserInfo);
			});
			path("/guilds", () -> {
				before("/*", this::checkDiscordLogin);
				get("/all", this::getAllGuilds);
				path("/:guildId", () -> {
					before("/*", this::checkGuildPerms);
					path("/roles", () -> get("/get", this::getRoles));
					path("/channels", () -> get("/get", this::getChannels));
					path("/emotes", () -> get("/get", this::getEmotes));
					path("/settings", () -> {
						get("/get", this::getGuildSettings);
						post("/set", this::setGuildSettings);
					});
				});
			});
		}).start(port);
	}

	private void discordLogin(Context ctx){
		var key = ctx.header("Authorization");
		if(key == null || !Database.sessionExists(key)){
			ctx.redirect(oAuthClient.generateAuthorizationURL(Config.REDIRECT_URL, scopes));
		}
		else{
			ctx.redirect(Config.REDIRECT_URL);
		}
	}

	private void login(Context ctx){
		var json = JsonParser.parseString(ctx.body()).getAsJsonObject();
		var code = json.get("code").getAsString();
		var state = json.get("state").getAsString();
		try{
			var key = Database.generateUniqueKey();
			var session = oAuthClient.startSession(code, state, key, scopes).complete();
			var user = oAuthClient.getUser(session).complete();
			Database.addSession(user.getId(), key);
			ok(ctx, "{\"key\": " + JSONObject.quote(key) + "}");
		}
		catch(InvalidStateException e){
			error(ctx, 401, "State invalid/expired please try again");
		}
		catch(IOException e){
			LOG.error("State is invalid", e);
			error(ctx, 403, "could not login");
		}
	}

	private void checkDiscordLogin(Context ctx){
		if(!ctx.method().equals("OPTIONS")){
			var key = ctx.header("Authorization");
			if(key == null || !Database.sessionExists(key)){
				error(ctx, 401, "Please login with discord to continue");
			}
		}
	}

	private void getUserInfo(Context ctx){
		var auth = ctx.header("Authorization");
		if(auth == null){
			error(ctx, 401, "Please login");
			return;
		}
		var userId = Database.getSession(auth);
		if(userId == null){
			error(ctx, 404, "Session not found");
			return;
		}
		var user = KittyBot.getJda().retrieveUserById(userId).complete();
		if(user == null){
			error(ctx, 404, "User not found");
			return;
		}
		var guilds = new ArrayList<String>();
		for(Guild guild : KittyBot.getJda().getMutualGuilds(user)){
			var u = guild.getMember(user);
			if(u != null && u.hasPermission(Permission.ADMINISTRATOR)){
				guilds.add(String.format("{\"id\": \"%s\", \"name\": %s, \"icon\": %s}", guild.getId(), JSONObject.quote(guild.getName()), JSONObject.quote(guild.getIconUrl())));
			}
		}
		ok(ctx, String.format("{\"name\": %s, \"id\": \"%s\", \"icon\": %s, \"guilds\": [%s]}", JSONObject.quote(user.getName()), user.getId(), JSONObject.quote(user.getEffectiveAvatarUrl()), String.join(", ", guilds)));
	}

	private void getAllGuilds(Context ctx){
		var auth = ctx.header("Authorization");
		if(auth == null){
			error(ctx, 401, "Please login");
			return;
		}
		var userId = Database.getSession(auth);
		if(userId == null){
			error(ctx, 404, "Session not found");
			return;
		}
		if(!userId.equals(Config.ADMIN_ID)){
			error(ctx, 403, "Only admins have access to this!");
			return;
		}
		var guilds = new ArrayList<String>();
		for(var guild : KittyBot.getJda().getGuildCache()){
			guilds.add(String.format("{\"id\": %s, \"name\": %s, \"icon\": %s, \"count\": %d}", JSONObject.quote(guild.getId()), JSONObject.quote(guild.getName()), JSONObject.quote(guild.getIconUrl()), guild.getMemberCount()));
		}
		ok(ctx, "{\"guilds\": [" + String.join(", ", guilds) + "]}");
	}

	private void checkGuildPerms(Context ctx){
		if(!ctx.method().equals("OPTIONS")){
			var guildId = ctx.pathParam(":guildId");
			var guild = KittyBot.getJda().getGuildById(guildId);
			if(guild == null){
				error(ctx, 404, "guild not found");
				return;
			}
			var userId = Database.getSession(ctx.header("Authorization"));
			if(userId == null){
				error(ctx, 404, "This user does not exist");
				return;
			}
			if(userId.equals(Config.ADMIN_ID)){
				return;
			}
			var member = guild.retrieveMemberById(userId).complete();
			if(member == null){
				error(ctx, 404, "I could not find you in that guild");
				return;
			}
			if(!member.hasPermission(Permission.ADMINISTRATOR)){
				error(ctx, 401, "You have no permission for this guild");
			}
		}
	}

	private void getRoles(Context ctx){
		var guild = KittyBot.getJda().getGuildById(ctx.pathParam(":guildId"));
		if(guild == null){
			error(ctx, 404, "guild not found");
			return;
		}
		var roles = new ArrayList<String>();
		for(var role : guild.getRoles()){
			roles.add(String.format("{\"name\": %s, \"id\": \"%s\"}", JSONObject.quote(role.getName()), role.getId()));
		}
		ok(ctx, String.format("{\"roles\": [%s]}", String.join(", ", roles)));
	}

	private void getChannels(Context ctx){
		var guild = KittyBot.getJda().getGuildById(ctx.pathParam(":guildId"));
		if(guild == null){
			error(ctx, 404, "guild not found");
			return;
		}
		var channels = new ArrayList<String>();
		for(var channel : guild.getTextChannels()){
			channels.add(String.format("{\"name\": %s, \"id\": \"%s\"}", JSONObject.quote(channel.getName()), channel.getId()));
		}
		ok(ctx, String.format("{\"channels\": [%s]}", String.join(", ", channels)));
	}

	private void getEmotes(Context ctx){
		var guild = KittyBot.getJda().getGuildById(ctx.pathParam(":guildId"));
		if(guild == null){
			error(ctx, 404, "guild not found");
			return;
		}
		var emotes = new ArrayList<String>();
		for(var emote : guild.getEmotes()){
			emotes.add(String.format("{\"name\": %s, \"id\": \"%s\", \"url\": %s}", JSONObject.quote(emote.getName()), emote.getId(), JSONObject.quote(emote.getImageUrl())));
		}
		ok(ctx, String.format("{\"emotes\": [%s]}", String.join(", ", emotes)));
	}

	private void getGuildSettings(Context ctx){
		var guildId = ctx.pathParam(":guildId");
		var roles = Database.getSelfAssignableRoles(guildId);
		if(roles == null || KittyBot.getJda().getGuildById(guildId) == null){
			error(ctx, 404, "guild not found");
			return;
		}
		var selfAssignableRoles = new ArrayList<String>();
		for(var role : roles.entrySet()){
			selfAssignableRoles.add(String.format("{\"role\": \"%s\", \"emote\": \"%s\"}", role.getKey(), role.getValue()));
		}
		ok(ctx, String.format("{\"prefix\": %s, " +
						"\"join_messages_enabled\": %b, " +
						"\"join_messages\": %s, " +
						"\"leave_messages_enabled\": %b, " +
						"\"leave_messages\": %s, " +
						"\"boost_messages_enabled\": %b, " +
						"\"boost_messages\": %s, " +
						"\"announcement_channel_id\":%s, " +
						"\"nsfw_enabled\": %b, " +
						"\"self_assignable_roles\": [%s]}",
				JSONObject.quote(Database.getCommandPrefix(guildId)),
				Database.getJoinMessageEnabled(guildId),
				JSONObject.quote(Database.getJoinMessage(guildId)),
				Database.getLeaveMessageEnabled(guildId),
				JSONObject.quote(Database.getLeaveMessage(guildId)),
				Database.getBoostMessageEnabled(guildId),
				JSONObject.quote(Database.getBoostMessage(guildId)),
				JSONObject.quote(Database.getAnnouncementChannelId(guildId)),
				Database.getNSFWEnabled(guildId),
				String.join(", ", selfAssignableRoles)
		));
	}

	private void setGuildSettings(Context ctx){
		var guildId = ctx.pathParam(":guildId");
		if(KittyBot.getJda().getGuildById(guildId) == null){
			error(ctx, 404, "guild not found");
			return;
		}
		var json = JsonParser.parseString(ctx.body()).getAsJsonObject();
		if(json.get("prefix") != null){
			Database.setCommandPrefix(guildId, json.get("prefix").getAsString());
		}
		if(json.get("join_messages_enabled") != null){
			Database.setJoinMessageEnabled(guildId, json.get("join_messages_enabled").getAsBoolean());
		}
		if(json.get("join_messages") != null){
			Database.setJoinMessage(guildId, json.get("join_messages").getAsString());
		}
		if(json.get("leave_messages_enabled") != null){
			Database.setLeaveMessageEnabled(guildId, json.get("leave_messages_enabled").getAsBoolean());
		}
		if(json.get("leave_messages") != null){
			Database.setLeaveMessage(guildId, json.get("leave_messages").getAsString());
		}
		if(json.get("boost_messages_enabled") != null){
			Database.setBoostMessageEnabled(guildId, json.get("boost_messages_enabled").getAsBoolean());
		}
		if(json.get("boost_messages") != null){
			Database.setBoostMessage(guildId, json.get("boost_messages").getAsString());
		}
		if(json.get("announcement_channel_id") != null){
			Database.setAnnouncementChannelId(guildId, json.get("announcement_channel_id").getAsString());
		}
		if(json.get("nsfw_enabled") != null){
			Database.setNSFWEnabled(guildId, json.get("nsfw_enabled").getAsBoolean());
		}
		if(json.get("self_assignable_roles") != null){
			var roles = new HashMap<String, String>();
			json.get("self_assignable_roles").getAsJsonArray().forEach(jsonElement -> {
				var obj = jsonElement.getAsJsonObject();
				roles.put(obj.get("role").getAsString(), obj.get("emote").getAsString());
			});
			Database.setSelfAssignableRoles(guildId, roles);
		}
		ok(ctx);
	}

	private void error(Context ctx, int code, String error){
		result(ctx, code, "{\"error\": \"" + error + "\"}");
	}

	private void ok(Context ctx){
		result(ctx, 200, "{\"status\": \"ok\"}");
	}

	private void ok(Context ctx, String result){
		result(ctx, 200, result);
	}

	private void result(Context ctx, int code, String result){
		ctx.header("Content-Type", "application/json");
		ctx.status(code);
		ctx.result(result);
	}

}
