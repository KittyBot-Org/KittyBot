package de.anteiku.kittybot;

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
import net.dv8tion.jda.api.utils.data.DataObject;
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

	private final Scope[] scopes;
	private final OAuth2Client oAuthClient;

	public WebService(int port){

		scopes = new Scope[]{Scope.IDENTIFY};
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
		String key = ctx.header("Authorization");
		if(key == null || !Database.sessionExists(key)){
			ctx.redirect(oAuthClient.generateAuthorizationURL(Config.REDIRECT_URL, scopes));
		}
		else{
			ctx.redirect(Config.REDIRECT_URL);
		}
	}

	private void login(Context ctx){
		DataObject json = DataObject.fromJson(ctx.body());
		String code = json.getString("code");
		String state = json.getString("state");
		try{
			String key = Database.generateUniqueKey();
			Session session = oAuthClient.startSession(code, state, key, scopes).complete();
			OAuth2User user = oAuthClient.getUser(session).complete();
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
			String key = ctx.header("Authorization");
			if(key == null || !Database.sessionExists(key)){
				error(ctx, 401, "Please login with discord to continue");
			}
		}
	}

	private void getUserInfo(Context ctx){
		String auth = ctx.header("Authorization");
		if(auth == null){
			error(ctx, 401, "Please login");
			return;
		}
		String userId = Database.getSession(auth);
		if(userId == null){
			error(ctx, 404, "Session not found");
			return;
		}
		User user = KittyBot.getJda().retrieveUserById(userId).complete();
		if(user == null){
			error(ctx, 404, "User not found");
			return;
		}
		Collection<String> guilds = new ArrayList<>();
		for(Guild guild : KittyBot.getJda().getMutualGuilds(user)){
			var u = guild.getMember(user);
			if(u != null && u.hasPermission(Permission.ADMINISTRATOR)){
				guilds.add(String.format("{\"id\": %s, \"name\": %s, \"icon\": %s}", JSONObject.quote(guild.getId()), JSONObject.quote(guild.getName()), JSONObject.quote(guild.getIconUrl())));
			}
		}
		ok(ctx, String.format("{\"name\": %s, \"id\": %s, \"icon\": %s, \"guilds\": [%s]}", JSONObject.quote(user.getName()), JSONObject.quote(user.getId()), JSONObject.quote(user.getEffectiveAvatarUrl()), String.join(", ", guilds)));
	}

	private void getAllGuilds(Context ctx){
		String auth = ctx.header("Authorization");
		if(auth == null){
			error(ctx, 401, "Please login");
			return;
		}
		String userId = Database.getSession(auth);
		if(userId == null){
			error(ctx, 404, "Session not found");
			return;
		}
		if(!userId.equals(Config.ADMIN_ID)){
			error(ctx, 403, "Only admins have access to this!");
			return;
		}
		Collection<String> guilds = new ArrayList<>();
		for(Guild guild : KittyBot.getJda().getGuildCache()){
			guilds.add(String.format("{\"id\": %s, \"name\": %s, \"icon\": %s, \"count\": %d}", JSONObject.quote(guild.getId()), JSONObject.quote(guild.getName()), JSONObject.quote(guild.getIconUrl()), guild.getMemberCount()));
		}
		ok(ctx, "{\"guilds\": [" + String.join(", ", guilds) + "]}");
	}

	private void checkGuildPerms(Context ctx){
		if(!ctx.method().equals("OPTIONS")){
			String guildId = ctx.pathParam(":guildId");
			Guild guild = KittyBot.getJda().getGuildById(guildId);
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
			Member member = guild.retrieveMemberById(userId).complete();
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
		Guild guild = KittyBot.getJda().getGuildById(ctx.pathParam(":guildId"));
		if(guild == null){
			error(ctx, 404, "guild not found");
			return;
		}
		Collection<String> roles = new ArrayList<>();
		for(Role role : guild.getRoles()){
			roles.add(String.format("{\"name\": \"%s\", \"id\": \"%s\"}", JSONObject.quote(role.getName()), role.getId()));
		}
		ok(ctx, String.format("{\"roles\": [%s]}", String.join(", ", roles)));
	}

	private void getChannels(Context ctx){
		Guild guild = KittyBot.getJda().getGuildById(ctx.pathParam(":guildId"));
		if(guild == null){
			error(ctx, 404, "guild not found");
			return;
		}
		Collection<String> channels = new ArrayList<>();
		for(TextChannel channel : guild.getTextChannels()){
			channels.add(String.format("{\"name\": \"%s\", \"id\": \"%s\"}", JSONObject.quote(channel.getName()), channel.getId()));
		}
		ok(ctx, String.format("{\"channels\": [%s]}", String.join(", ", channels)));
	}

	private void getEmotes(Context ctx){
		Guild guild = KittyBot.getJda().getGuildById(ctx.pathParam(":guildId"));
		if(guild == null){
			error(ctx, 404, "guild not found");
			return;
		}
		Collection<String> emotes = new ArrayList<>();
		for(Emote emote : guild.getEmotes()){
			emotes.add(String.format("{\"name\": \"%s\", \"id\": \"%s\", \"url\": \"%s\"}", JSONObject.quote(emote.getName()), emote.getId(), JSONObject.quote(emote.getImageUrl())));
		}
		ok(ctx, String.format("{\"emotes\": [%s]}", String.join(", ", emotes)));
	}

	private void getGuildSettings(Context ctx){
		String guildId = ctx.pathParam(":guildId");
		Map<String, String> roles = Database.getSelfAssignableRoles(guildId);
		if(roles == null || KittyBot.getJda().getGuildById(guildId) == null){
			error(ctx, 404, "guild not found");
			return;
		}
		Collection<String> selfAssignableRoles = new ArrayList<>();
		for(Map.Entry<String, String> role : roles.entrySet()){
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
		String guildId = ctx.pathParam(":guildId");
		if(KittyBot.getJda().getGuildById(guildId) == null){
			error(ctx, 404, "guild not found");
			return;
		}
		DataObject json = DataObject.fromJson(ctx.body());
		if(json.hasKey("code")){
			Database.setCommandPrefix(guildId, json.getString("prefix"));
		}
		if(json.hasKey("join_messages_enabled")){
			Database.setJoinMessageEnabled(guildId, json.getBoolean("join_messages_enabled"));
		}
		if(json.hasKey("join_messages")){
			Database.setJoinMessage(guildId, json.getString("join_messages"));
		}
		if(json.hasKey("leave_messages_enabled")){
			Database.setLeaveMessageEnabled(guildId, json.getBoolean("leave_messages_enabled"));
		}
		if(json.hasKey("leave_messages")){
			Database.setLeaveMessage(guildId, json.getString("leave_messages"));
		}
		if(json.hasKey("boost_messages_enabled")){
			Database.setBoostMessageEnabled(guildId, json.getBoolean("boost_messages_enabled"));
		}
		if(json.hasKey("boost_messages")){
			Database.setBoostMessage(guildId, json.getString("boost_messages"));
		}
		if(json.hasKey("announcement_channel_id")){
			Database.setAnnouncementChannelId(guildId, json.getString("announcement_channel_id"));
		}
		if(json.hasKey("nsfw_enabled")){
			Database.setNSFWEnabled(guildId, json.getBoolean("nsfw_enabled"));
		}
		if(json.hasKey("self_assignable_roles")){
			Map<String, String> roles = new HashMap<>();
			var dataArray = json.getArray("self_assignable_roles");
			for (var i = 0; i < dataArray.length(); i++){
				var obj = dataArray.getObject(i);
				roles.put(obj.getString("role"), obj.getString("emote"));
			}
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
