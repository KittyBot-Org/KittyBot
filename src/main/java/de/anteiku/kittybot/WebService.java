package de.anteiku.kittybot;

import com.jagrosh.jdautilities.oauth2.OAuth2Client;
import com.jagrosh.jdautilities.oauth2.Scope;
import com.jagrosh.jdautilities.oauth2.entities.impl.OAuth2ClientImpl;
import com.jagrosh.jdautilities.oauth2.exceptions.InvalidStateException;
import com.jagrosh.jdautilities.oauth2.session.DefaultSessionController;
import com.jagrosh.jdautilities.oauth2.state.DefaultStateController;
import de.anteiku.kittybot.database.Database;
import de.anteiku.kittybot.objects.Config;
import de.anteiku.kittybot.objects.command.Category;
import de.anteiku.kittybot.objects.command.CommandManager;
import io.javalin.Javalin;
import io.javalin.http.Context;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;

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
			get("/commands/get", this::getCommands);
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
					get("/roles/get", this::getRoles);
					get("/channels/get", this::getChannels);
					get("/emotes/get", this::getEmotes);
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
		var json = DataObject.fromJson(ctx.body());
		var code = json.getString("code");
		var state = json.getString("state");
		try{
			var key = Database.generateUniqueKey();
			var session = oAuthClient.startSession(code, state, key, scopes).complete();
			var user = oAuthClient.getUser(session).complete();
			Database.addSession(user.getId(), key);
			ok(ctx, DataObject.empty().put("key", key));
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
		var data = DataArray.empty();
		for(var guild : KittyBot.getJda().getMutualGuilds(user)){
			var u = guild.getMember(user);
			if(u != null && u.hasPermission(Permission.ADMINISTRATOR)){
				data.add(DataObject.empty()
						.put("id", guild.getId())
						.put("name", guild.getName())
						.put("icon", guild.getIconUrl()));
			}
		}
		ok(ctx, DataObject.empty().put("name", user.getName()).put("id", user.getId()).put("icon", user.getEffectiveAvatarUrl()).put("guilds", data));
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
		if(!Config.ADMIN_IDS.contains(userId)){
			error(ctx, 403, "Only admins have access to this!");
			return;
		}
		var data = DataArray.empty();
		for(var guild : KittyBot.getJda().getGuildCache()){
			var owner = guild.getOwner();
			var obj = DataObject.empty()
					.put("id", guild.getId())
					.put("name", guild.getName())
					.put("icon", guild.getIconUrl())
					.put("count", guild.getMemberCount());
			if(owner != null){
				obj.put("owner", owner.getUser().getAsTag());
			}
			data.add(obj);
		}
		ok(ctx, DataObject.empty().put("guilds", data));
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
			if(Config.ADMIN_IDS.contains(userId)){
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

	private void getCommands(Context ctx){
		var commandSet = CommandManager.getDistinctCommands().entrySet();
		var data = DataArray.empty();
		for(var cat : Category.values()){
			var commands = DataArray.empty();
			for(var cmd : commandSet){
				var command = cmd.getValue();
				if(cat.equals(command.getCategory())){
					commands.add(DataObject.empty().put("command", command.getCommand()).put("description", command.getDescription()));
				}
			}
			data.add(DataObject.empty().put("name", cat.getFriendlyName()).put("emote_url", cat.getEmoteUrl()).put("commands", commands));
		}

		ok(ctx, DataObject.empty().put("prefix", Config.DEFAULT_PREFIX).put("categories", data));
	}

	private void getRoles(Context ctx){
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
			data.add(DataObject.empty().put("name", role.getName()).put("id", role.getId()));
		}
		ok(ctx, DataObject.empty().put("roles", data));
	}

	private void getChannels(Context ctx){
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

	private void getEmotes(Context ctx){
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

	private void getGuildSettings(Context ctx){
		var guildId = ctx.pathParam(":guildId");
		var roles = Database.getSelfAssignableRoles(guildId);
		if(roles == null || KittyBot.getJda().getGuildById(guildId) == null){
			error(ctx, 404, "guild not found");
			return;
		}
		var data = DataArray.empty();
		for(var role : roles.entrySet()){
			data.add(DataObject.empty().put("role", role.getKey()).put("emote", role.getValue()));
		}
		ok(ctx, DataObject.empty()
				.put("prefix", Database.getCommandPrefix(guildId))
				.put("join_messages_enabled", Database.getJoinMessageEnabled(guildId))
				.put("join_messages", Database.getJoinMessage(guildId))
				.put("leave_messages_enabled", Database.getLeaveMessageEnabled(guildId))
				.put("leave_messages", Database.getLeaveMessage(guildId))
				.put("boost_messages_enabled", Database.getBoostMessageEnabled(guildId))
				.put("boost_messages", Database.getBoostMessage(guildId))
				.put("announcement_channel_id", Database.getAnnouncementChannelId(guildId))
				.put("nsfw_enabled", Database.getNSFWEnabled(guildId))
				.put("self_assignable_roles", data));
	}

	private void setGuildSettings(Context ctx){
		var guildId = ctx.pathParam(":guildId");
		if(KittyBot.getJda().getGuildById(guildId) == null){
			error(ctx, 404, "guild not found");
			return;
		}
		var json = DataObject.fromJson(ctx.body());
		if(json.hasKey("prefix")){
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
			var roles = new HashMap<String, String>();
			var dataArray = json.getArray("self_assignable_roles");
			for(var i = 0; i < dataArray.length(); i++){
				var obj = dataArray.getObject(i);
				roles.put(obj.getString("role"), obj.getString("emote"));
			}
			Database.setSelfAssignableRoles(guildId, roles);
		}
		ok(ctx);
	}

	private void error(Context ctx, int code, String error){
		result(ctx, code, DataObject.empty().put("error", error));
	}

	private void ok(Context ctx){
		result(ctx, 200, DataObject.empty().put("status", 200));
	}

	private void ok(Context ctx, DataObject data){
		result(ctx, 200, data);
	}

	private void result(Context ctx, int code, DataObject data){
		ctx.header("Content-Type", "application/json");
		ctx.status(code);
		ctx.result(data.toString());
	}

}
