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
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static spark.Spark.*;

public class WebService{

	private static final Logger LOG = LoggerFactory.getLogger(WebService.class);
	private static final String OK = "{\"status\": 200}";

	private final KittyBot main;
	private final Scope[] scopes;
	private final OAuth2Client oAuthClient;
	private final String originUrl;

	public WebService(KittyBot main, int port){
		this.main = main;

		scopes = new Scope[]{Scope.IDENTIFY};
		DefaultSessionController sessionController = new DefaultSessionController();
		DefaultStateController stateController = new DefaultStateController();
		oAuthClient = new OAuth2ClientImpl(Long.parseLong(Config.DISCORD_BOT_ID), Config.DISCORD_BOT_SECRET, sessionController, stateController, main.httpClient);

		URL url = null;
		try{
			url = new URL(Config.DISCORD_WEBSITE_URL);
		}
		catch(MalformedURLException e){
			LOG.error("Invalid redirect Url provided", e);
		}
		originUrl = String.format("%s://%s", url.getProtocol(), url.getHost());

		port(port);
		options("/*", this::cors);
		before(this::corsHeaders);
		get("/discord_login", this::discordLogin);
		post("/login", this::login);
		path("/user", () -> {
			before("/*", this::checkDiscordLogin);
			get("/me", this::getUserInfo);
		});
		path("/guilds", () -> {
			before("/*", (request, response) -> response.header("Content-Type", "application/json"));
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
	}

	private String cors(Request request, Response response){
		String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
		if(accessControlRequestHeaders != null){
			response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
		}
		String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
		if(accessControlRequestMethod != null){
			response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
		}
		return "OK";
	}

	private void corsHeaders(Request request, Response response){
		response.header("Access-Control-Allow-Origin", originUrl);
		response.header("Access-Control-Allow-Methods", "GET, POST, PATCH, PUT, DELETE, OPTIONS");
		response.header("Access-Control-Allow-Headers", "Origin, Content-Type, X-Auth-Token");
	}

	private String discordLogin(Request request, Response response){
		String key = request.headers("Authorization");
		if(key == null || !main.database.sessionExists(key)){
			response.redirect(oAuthClient.generateAuthorizationURL(Config.DISCORD_WEBSITE_URL, scopes));
		}
		else{
			response.redirect(Config.DISCORD_WEBSITE_URL);
		}
		return OK;
	}

	private String login(Request request, Response response){
		JsonObject json = JsonParser.parseString(request.body()).getAsJsonObject();
		String code = json.get("code").getAsString();
		String state = json.get("state").getAsString();
		try{
			String key = main.database.generateUniqueKey();
			Session session = oAuthClient.startSession(code, state, key, scopes).complete();
			OAuth2User user = oAuthClient.getUser(session).complete();
			main.database.addSession(user.getId(), key);
			return "{\"key\": " + JSONObject.quote(key) + "}";
		}
		catch(InvalidStateException e){
			response.status(401);
			return error("State invalid/expired please try again");
		}
		catch(IOException e){
			LOG.error("State is invalid", e);
		}
		response.status(403);
		return error("could not login");
	}

	private void checkDiscordLogin(Request request, Response response){
		if(!request.requestMethod().equals("OPTIONS")){
			String key = request.headers("Authorization");
			if(key == null || !main.database.sessionExists(key)){
				halt(401, error("Please login with discord to continue"));
			}
		}
	}

	private String getUserInfo(Request request, Response response){
		String auth = request.headers("Authorization");
		if(auth == null){
			response.status(401);
			return error("Please login");
		}
		String userId = main.database.getSession(auth);
		if(userId == null){
			response.status(404);
			return error("Session not found");
		}
		User user = main.jda.retrieveUserById(userId).complete();
		if(user == null){
			response.status(404);
			return error("User not found");
		}
		Collection<String> guilds = new ArrayList<>();
		for(Guild guild : main.jda.getMutualGuilds(user)){
			if(guild.getMember(user).hasPermission(Permission.ADMINISTRATOR)){
				guilds.add(String.format("{\"id\": %s, \"name\": %s, \"icon\": %s}", JSONObject.quote(guild.getId()), JSONObject.quote(guild.getName()), JSONObject.quote(guild.getIconUrl())));
			}
		}
		return String.format("{\"name\": %s, \"id\": %s, \"icon\": %s, \"guilds\": [%s]}", JSONObject.quote(user.getName()), JSONObject.quote(user.getId()), JSONObject.quote(user.getEffectiveAvatarUrl()), String.join(", ", guilds));
	}

	private String getAllGuilds(Request request, Response response){
		String auth = request.headers("Authorization");
		if(auth == null){
			response.status(401);
			return error("Please login");
		}
		String userId = main.database.getSession(auth);
		if(userId == null){
			response.status(404);
			return error("Session not found");
		}
		if(!userId.equals(Config.DISCORD_ADMIN_ID)){
			response.status(403);
			return error("Only admins have access to this!");
		}
		Collection<String> guilds = new ArrayList<>();
		for(Guild guild : main.jda.getGuildCache()){
			guilds.add(String.format("{\"id\": %s, \"name\": %s, \"icon\": %s, \"count\": %d}", JSONObject.quote(guild.getId()), JSONObject.quote(guild.getName()), JSONObject.quote(guild.getIconUrl()), guild.getMemberCount()));
		}
		return "{\"guilds\": [" + String.join(", ", guilds) + "]}";
	}

	private void checkGuildPerms(Request request, Response response){
		if(!request.requestMethod().equals("OPTIONS")){
			String guildId = request.params(":guildId");
			Guild guild = main.jda.getGuildById(guildId);
			if(guild == null){
				halt(404, error("guild not found"));
				return;
			}
			Member member = guild.retrieveMemberById(main.database.getSession(request.headers("Authorization"))).complete();
			if(member == null){
				halt(404, error("I could not find you int that guild"));
				return;
			}
			if(!member.hasPermission(Permission.ADMINISTRATOR)){
				halt(401, error("You have no permission for this guild"));
				return;
			}
		}
	}

	private String getRoles(Request request, Response response){
		Guild guild = main.jda.getGuildById(request.params(":guildId"));
		if(guild == null){
			response.status(404);
			return error("guild not found");
		}
		Collection<String> roles = new ArrayList<>();
		for(Role role : guild.getRoles()){
			roles.add(String.format("{\"name\": \"%s\", \"id\": \"%s\"}", role.getName(), role.getId()));
		}
		return String.format("{\"roles\": [%s]}", String.join(", ", roles));
	}

	private String getChannels(Request request, Response response){
		Guild guild = main.jda.getGuildById(request.params(":guildId"));
		if(guild == null){
			response.status(404);
			return error("guild not found");
		}
		Collection<String> channels = new ArrayList<>();
		for(TextChannel channel : guild.getTextChannels()){
			channels.add(String.format("{\"name\": \"%s\", \"id\": \"%s\"}", channel.getName(), channel.getId()));
		}
		return String.format("{\"channels\": [%s]}", String.join(", ", channels));
	}

	private String getEmotes(Request request, Response response){
		Guild guild = main.jda.getGuildById(request.params(":guildId"));
		if(guild == null){
			response.status(404);
			return error("guild not found");
		}
		Collection<String> emotes = new ArrayList<>();
		for(Emote emote : guild.getEmotes()){
			emotes.add(String.format("{\"name\": \"%s\", \"id\": \"%s\", \"url\": \"%s\"}", emote.getName(), emote.getId(), emote.getImageUrl()));
		}
		return String.format("{\"emotes\": [%s]}", String.join(", ", emotes));
	}

	private String getGuildSettings(Request request, Response response){
		String guildId = request.params(":guildId");
		Map<String, String> roles = main.database.getSelfAssignableRoles(guildId);
		if(roles == null || main.jda.getGuildById(guildId) == null){
			response.status(404);
			return error("guild not found");
		}
		Collection<String> selfAssignableRoles = new ArrayList<>();
		for(Map.Entry<String, String> role : roles.entrySet()){
			selfAssignableRoles.add(String.format("{\"role\": \"%s\", \"emote\": \"%s\"}", role.getKey(), role.getValue()));
		}
		return String.format("{\"prefix\": %s, \"welcome_message_enabled\": %b, \"welcome_message\": %s, \"welcome_channel_id\":%s, \"nsfw_enabled\": %b, \"self_assignable_roles\": [%s]}",
				JSONObject.quote(main.database.getCommandPrefix(guildId)),
				main.database.getWelcomeMessageEnabled(guildId),
				JSONObject.quote(main.database.getWelcomeMessage(guildId)),
				JSONObject.quote(main.database.getWelcomeChannelId(guildId)),
				main.database.getNSFWEnabled(guildId),
				String.join(", ", selfAssignableRoles)
		);
	}

	private String setGuildSettings(Request request, Response response){
		String guildId = request.params(":guildId");
		if(main.jda.getGuildById(guildId) == null){
			response.status(404);
			return error("guild not found");
		}
		JsonObject json = JsonParser.parseString(request.body()).getAsJsonObject();
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
		return "";
	}

	private String error(String error){
		return "{\"error\": \"" + error + "\"}";
	}

}
