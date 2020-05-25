package de.anteiku.kittybot.webservice;

import com.jagrosh.jdautilities.oauth2.OAuth2Client;
import com.jagrosh.jdautilities.oauth2.Scope;
import com.jagrosh.jdautilities.oauth2.entities.OAuth2User;
import com.jagrosh.jdautilities.oauth2.entities.impl.OAuth2ClientImpl;
import com.jagrosh.jdautilities.oauth2.exceptions.InvalidStateException;
import com.jagrosh.jdautilities.oauth2.session.DefaultSessionController;
import com.jagrosh.jdautilities.oauth2.session.Session;
import com.jagrosh.jdautilities.oauth2.state.DefaultStateController;
import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.objects.ValuePair;
import de.anteiku.kittybot.utils.Config;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static spark.Spark.*;

public class WebService{
	
	private static final Logger LOG = LoggerFactory.getLogger(WebService.class);
	private static final String OK = "{\"status\": 200}";;
	
	private KittyBot main;
	private Scope[] scopes;
	private OAuth2Client oAuthClient;
	private DefaultSessionController sessionController;
	private DefaultStateController stateController;
	
	public WebService(KittyBot main, int port){
		this.main = main;
		
		scopes = new Scope[]{Scope.IDENTIFY};
		sessionController = new DefaultSessionController();
		stateController = new DefaultStateController();
		oAuthClient = new OAuth2ClientImpl(Long.parseLong(Config.DISCORD_BOT_ID), Config.DISCORD_BOT_SECRET, sessionController, stateController, main.httpClient);
		
		String url = Config.DISCORD_REDIRECT_URL;
		final String originUrl;
		int i = url.indexOf(":");
		if(i == -1){
			originUrl = url;
		}
		else{
			originUrl = url.substring(i, url.length() - 1);
		}
		port(port);
		before((request, response) -> {
			response.header("Access-Control-Allow-Origin", originUrl);
			response.header("Access-Control-Allow-Credentials", "true");
			response.header("Content-Type", "application/json");
		});
		path("/user", () -> {
			before("", this::checkDiscordLogin);
			get("/me", this::getUserInfo);
		});
		get("/discord_login", this::discordLogin);
		get("/login", this::login);
		path("/guild", () -> {
			before("", this::checkDiscordLogin);
			path("/:guildId", () -> {
				before("", this::checkGuildPerms);
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
	
	private String buildError(int code, String error){
		return String.format("{\"status\": %d, \"error\": \"%s\"}", code, error);
	}
	
	private void checkDiscordLogin(Request request, Response response){
		String key = request.cookie("key");
		if(key == null || !main.database.sessionExists(key)){
			halt(401, buildError(401, "Please login with discord to continue"));
		}
	}
	
	private void checkGuildPerms(Request request, Response response){
		String guildId = request.params(":guildId");
		Guild guild = main.jda.getGuildById(guildId);
		if(guild == null){
			halt(404, buildError(404, "I'm not in that guild"));
		}
		Member member = guild.retrieveMemberById(main.database.getSession(request.cookie("key"))).complete();
		if(member == null){
			halt(404, buildError(404, "I was not able to find that member"));
		}
		if(!member.hasPermission(Permission.ADMINISTRATOR)){
			halt(401, buildError(401, "You have no permission for this guild"));
		}
	}
	
	private Guild getGuild(Request request){
		Guild guild = main.jda.getGuildById(request.params(":guildId"));
		if(guild == null){
			halt(404, buildError(404, "Guild not found"));
		}
		return guild;
	}
	
	private String discordLogin(Request request, Response response){
		String key = request.cookie("key");
		if(key == null || !main.database.sessionExists(key)){
			response.redirect(oAuthClient.generateAuthorizationURL(Config.DISCORD_REDIRECT_URL + "/login", scopes));
		}
		response.redirect(Config.DISCORD_WEBSITE_URL);
		return OK;
	}
	
	private String login(Request request, Response response){
		String code = request.queryParams("code");
		String state = request.queryParams("state");
		try{
			String key = main.database.generateUniqueKey();
			Session session = oAuthClient.startSession(code, state, key, scopes).complete();
			OAuth2User user = oAuthClient.getUser(session).complete();
			main.database.addSession(user.getId(), key);
			response.cookie("key", key);
			response.redirect(Config.DISCORD_WEBSITE_URL);
		}
		catch(InvalidStateException | IOException e){
			LOG.error("State is invalid", e);
		}
		return OK;
	}
	
	private String getUserInfo(Request request, Response response){
		String cookie = request.cookie("key");
		if(cookie == null){
			response.redirect(Config.DISCORD_REDIRECT_URL + "/discord_login");
			return buildError(401, "Please login");
		}
		String userId = main.database.getSession(cookie);
		if(userId == null){
			response.redirect(Config.DISCORD_REDIRECT_URL + "/discord_login");
			return buildError(404, "Session not found");
		}
		User user = main.jda.retrieveUserById(userId).complete();
		if(user == null){
			response.redirect(Config.DISCORD_REDIRECT_URL + "/discord_login");
			return buildError(404, "User not found");
		}
		Collection<String> guilds = new ArrayList<>();
		for(Guild guild : main.jda.getMutualGuilds(user)){
			if(guild.getMember(user).hasPermission(Permission.ADMINISTRATOR)){
				guilds.add(String.format("{\"id\": \"%s\", \"name\": \"%s\", \"icon\": \"%s\"}", guild.getId(), guild.getName(), guild.getIconUrl()));
			}
		}
		return String.format("{\"name\": \"%s\", \"icon\": \"%s\", \"guilds\": [%s]}", user.getName(), user.getEffectiveAvatarUrl(), String.join(", ", guilds));
	}
	
	private String getRoles(Request request, Response response){
		Guild guild = getGuild(request);
		Collection<String> roles = new ArrayList<>();
		for(Role role : guild.getRoles()){
			roles.add(String.format("{\"name\": \"%s\", \"id\": \"%s\"}", role.getName(), role.getId()));
		}
		return String.format("{\"roles\": [%s]}", String.join(", ", roles));
	}
	
	private String getChannels(Request request, Response response){
		Guild guild = getGuild(request);
		Collection<String> channels = new ArrayList<>();
		for(TextChannel channel : guild.getTextChannels()){
			channels.add(String.format("{\"name\": \"%s\", \"id\": \"%s\"}", channel.getName(), channel.getId()));
		}
		return String.format("{\"channels\": [%s]}", String.join(", ", channels));
	}
	
	private String getEmotes(Request request, Response response){
		Guild guild = getGuild(request);
		Collection<String> emotes = new ArrayList<>();
		for(Emote emote : guild.getEmotes()){
			emotes.add(String.format("{\"name\": \"%s\", \"id\": \"%s\"}", emote.getName(), emote.getId()));
		}
		return String.format("{\"emotes\": [%s]}", String.join(", ", emotes));
	}
	
	private String getGuildSettings(Request request, Response response){
		String guildId = request.params(":guildId");
		Collection<String> selfAssignableRoles = new ArrayList<>();
		for(ValuePair<String, String> role : main.database.getSelfAssignableRoles(guildId)){
			selfAssignableRoles.add(String.format("{\"role\": \"%s\", \"emote\": \"%s\"}", role.getKey(), role.getValue()));
		}
		return String.format("{\"prefix\": \"%s\", \"welcome_message_enabled\": %b, \"welcome_message\": \"%s\", \"welcome_channel_id\": \"%s\", \"nsfw_enabled\": %b, \"self_assignable_roles\": [%s]}",
			main.database.getCommandPrefix(guildId),
			main.database.getWelcomeMessageEnabled(guildId),
			main.database.getWelcomeMessage(guildId),
			main.database.getWelcomeChannelId(guildId),
			main.database.getNSFWEnabled(guildId),
			String.join(", ", selfAssignableRoles)
		);
	}
	
	private String setGuildSettings(Request request, Response response){
		return "";
	}
	
}
