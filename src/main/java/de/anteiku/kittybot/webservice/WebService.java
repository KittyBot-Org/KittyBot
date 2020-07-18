package de.anteiku.kittybot.webservice;

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
import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.utils.Config;
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
import java.util.Map;

import static io.javalin.apibuilder.ApiBuilder.*;

public class WebService{

	private static final Logger LOG = LoggerFactory.getLogger(WebService.class);

	private final KittyBot main;
	private final Scope[] scopes;
	private final OAuth2Client oAuthClient;

	public WebService(KittyBot main, int port){
		this.main = main;

		scopes = new Scope[]{Scope.IDENTIFY};
		DefaultSessionController sessionController = new DefaultSessionController();
		DefaultStateController stateController = new DefaultStateController();
		oAuthClient = new OAuth2ClientImpl(Long.parseLong(Config.DISCORD_BOT_ID), Config.DISCORD_BOT_SECRET, sessionController, stateController, main.httpClient);
		Javalin.create(config -> {
			config.enableCorsForOrigin(Config.DISCORD_ORIGIN_URL);
		}).routes(() -> {
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
		if(key == null || !main.database.sessionExists(key)){
			ctx.redirect(oAuthClient.generateAuthorizationURL(Config.DISCORD_REDIRECT_URL, scopes));
		}
		else{
			ctx.redirect(Config.DISCORD_REDIRECT_URL);
		}
	}

	private void login(Context ctx){
		JsonObject json = JsonParser.parseString(ctx.body()).getAsJsonObject();
		String code = json.get("code").getAsString();
		String state = json.get("state").getAsString();
		try{
			String key = main.database.generateUniqueKey();
			Session session = oAuthClient.startSession(code, state, key, scopes).complete();
			OAuth2User user = oAuthClient.getUser(session).complete();
			main.database.addSession(user.getId(), key);
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
			if(key == null || !main.database.sessionExists(key)){
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
		String userId = main.database.getSession(auth);
		if(userId == null){
			error(ctx, 404, "Session not found");
			return;
		}
		User user = main.jda.retrieveUserById(userId).complete();
		if(user == null){
			error(ctx, 404, "User not found");
			return;
		}
		Collection<String> guilds = new ArrayList<>();
		for(Guild guild : main.jda.getMutualGuilds(user)){
			if(guild.getMember(user).hasPermission(Permission.ADMINISTRATOR)){
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
		String userId = main.database.getSession(auth);
		if(userId == null){
			error(ctx, 404, "Session not found");
			return;
		}
		if(!userId.equals(Config.DISCORD_ADMIN_ID)){
			error(ctx, 403, "Only admins have access to this!");
			return;
		}
		Collection<String> guilds = new ArrayList<>();
		for(Guild guild : main.jda.getGuildCache()){
			guilds.add(String.format("{\"id\": %s, \"name\": %s, \"icon\": %s, \"count\": %d}", JSONObject.quote(guild.getId()), JSONObject.quote(guild.getName()), JSONObject.quote(guild.getIconUrl()), guild.getMemberCount()));
		}
		ok(ctx, "{\"guilds\": [" + String.join(", ", guilds) + "]}");
	}

	private void checkGuildPerms(Context ctx){
		if(!ctx.method().equals("OPTIONS")){
			String guildId = ctx.pathParam(":guildId");
			Guild guild = main.jda.getGuildById(guildId);
			if(guild == null){
				error(ctx, 404, "guild not found");
				return;
			}
			Member member = guild.retrieveMemberById(main.database.getSession(ctx.header("Authorization"))).complete();
			if(member == null){
				error(ctx, 404, "I could not find you int that guild");
				return;
			}
			if(!member.hasPermission(Permission.ADMINISTRATOR)){
				error(ctx, 401, "You have no permission for this guild");
			}
		}
	}

	private void getRoles(Context ctx){
		Guild guild = main.jda.getGuildById(ctx.pathParam(":guildId"));
		if(guild == null){
			error(ctx, 404, "guild not found");
			return;
		}
		Collection<String> roles = new ArrayList<>();
		for(Role role : guild.getRoles()){
			roles.add(String.format("{\"name\": \"%s\", \"id\": \"%s\"}", role.getName(), role.getId()));
		}
		ok(ctx, String.format("{\"roles\": [%s]}", String.join(", ", roles)));
	}

	private void getChannels(Context ctx){
		Guild guild = main.jda.getGuildById(ctx.pathParam(":guildId"));
		if(guild == null){
			error(ctx, 404, "guild not found");
			return;
		}
		Collection<String> channels = new ArrayList<>();
		for(TextChannel channel : guild.getTextChannels()){
			channels.add(String.format("{\"name\": \"%s\", \"id\": \"%s\"}", channel.getName(), channel.getId()));
		}
		ok(ctx, String.format("{\"channels\": [%s]}", String.join(", ", channels)));
	}

	private void getEmotes(Context ctx){
		Guild guild = main.jda.getGuildById(ctx.pathParam(":guildId"));
		if(guild == null){
			error(ctx, 404, "guild not found");
			return;
		}
		Collection<String> emotes = new ArrayList<>();
		for(Emote emote : guild.getEmotes()){
			emotes.add(String.format("{\"name\": \"%s\", \"id\": \"%s\", \"url\": \"%s\"}", emote.getName(), emote.getId(), emote.getImageUrl()));
		}
		ok(ctx, String.format("{\"emotes\": [%s]}", String.join(", ", emotes)));
	}

	private void getGuildSettings(Context ctx){
		String guildId = ctx.pathParam(":guildId");
		Map<String, String> roles = main.database.getSelfAssignableRoles(guildId);
		if(roles == null || main.jda.getGuildById(guildId) == null){
			error(ctx, 404, "guild not found");
			return;
		}
		Collection<String> selfAssignableRoles = new ArrayList<>();
		for(Map.Entry<String, String> role : roles.entrySet()){
			selfAssignableRoles.add(String.format("{\"role\": \"%s\", \"emote\": \"%s\"}", role.getKey(), role.getValue()));
		}
		ok(ctx, String.format("{\"prefix\": %s, \"welcome_message_enabled\": %b, \"welcome_message\": %s, \"welcome_channel_id\":%s, \"nsfw_enabled\": %b, \"self_assignable_roles\": [%s]}",
				JSONObject.quote(main.database.getCommandPrefix(guildId)),
				main.database.getWelcomeMessageEnabled(guildId),
				JSONObject.quote(main.database.getWelcomeMessage(guildId)),
				JSONObject.quote(main.database.getWelcomeChannelId(guildId)),
				main.database.getNSFWEnabled(guildId),
				String.join(", ", selfAssignableRoles)
		));
	}

	private void setGuildSettings(Context ctx){
		String guildId = ctx.pathParam(":guildId");
		if(main.jda.getGuildById(guildId) == null){
			error(ctx, 404, "guild not found");
			return;
		}
		JsonObject json = JsonParser.parseString(ctx.body()).getAsJsonObject();
		if(json.get("prefix") != null){
			main.database.setCommandPrefix(guildId, json.get("prefix").getAsString());
		}
		if(json.get("welcome_message_enabled") != null){
			main.database.setWelcomeMessageEnabled(guildId, json.get("welcome_message_enabled").getAsBoolean());
		}
		if(json.get("welcome_message") != null){
			main.database.setWelcomeMessage(guildId, json.get("welcome_message").getAsString());
		}
		if(json.get("welcome_channel_id") != null){
			main.database.setWelcomeChannelId(guildId, json.get("welcome_channel_id").getAsString());
		}
		if(json.get("nsfw_enabled") != null){
			main.database.setNSFWEnabled(guildId, json.get("nsfw_enabled").getAsBoolean());
		}
		if(json.get("self_assignable_roles") != null){
			main.database.setSelfAssignableRoles(guildId, json.get("self_assignable_roles").getAsJsonArray());
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
