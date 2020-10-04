package de.kittybot.kittybot;

import com.jagrosh.jdautilities.oauth2.OAuth2Client;
import com.jagrosh.jdautilities.oauth2.Scope;
import com.jagrosh.jdautilities.oauth2.exceptions.InvalidStateException;
import de.kittybot.kittybot.cache.DashboardSessionCache;
import de.kittybot.kittybot.cache.GuildCache;
import de.kittybot.kittybot.cache.PrefixCache;
import de.kittybot.kittybot.cache.SelfAssignableRoleCache;
import de.kittybot.kittybot.database.Database;
import de.kittybot.kittybot.objects.Config;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandManager;
import de.kittybot.kittybot.objects.guilds.GuildData;
import de.kittybot.kittybot.objects.session.DashboardSessionController;
import io.javalin.Javalin;
import io.javalin.http.Context;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static io.javalin.apibuilder.ApiBuilder.*;

public class WebService{

	private static final Logger LOG = LoggerFactory.getLogger(WebService.class);

	private static final Scope[] SCOPES = new Scope[]{Scope.IDENTIFY, Scope.GUILDS};
	private static final OAuth2Client O_AUTH_2_CLIENT = new OAuth2Client.Builder()
			.setClientId(Long.parseLong(Config.BOT_ID))
			.setClientSecret(Config.BOT_SECRET)
			.setOkHttpClient(KittyBot.getHttpClient())
			.setSessionController(new DashboardSessionController())
			.build();

	public WebService(int port){
		Javalin.create(config -> config.enableCorsForOrigin(Config.ORIGIN_URL)).routes(() -> {
			get("/discord_login", this::discordLogin);
			get("/health_check", ctx -> ctx.result("alive"));
			get("/commands/get", this::getCommands);
			post("/login", this::login);
			post("/logout", this::logout);
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

	public static OAuth2Client getOAuth2Client(){
		return O_AUTH_2_CLIENT;
	}

	public static Scope[] getScopes(){
		return SCOPES;
	}

	private void discordLogin(Context ctx){
		var key = ctx.header("Authorization");
		if(key == null || !DashboardSessionCache.sessionExists(key)){
			ctx.redirect(O_AUTH_2_CLIENT.generateAuthorizationURL(Config.REDIRECT_URL, SCOPES));
		}
		else{
			ctx.redirect(Config.REDIRECT_URL);
		}
	}

	private void login(Context ctx){
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

	private void logout(Context ctx){
		var auth = ctx.header("Authorization");
		if(auth != null){
			DashboardSessionCache.deleteSession(auth);
		}
	}

	private void checkDiscordLogin(Context ctx){
		if(!ctx.method().equals("OPTIONS")){
			var key = ctx.header("Authorization");
			if(key == null || !DashboardSessionCache.sessionExists(key)){
				error(ctx, 401, "Please login with discord to continue");
			}
		}
	}

	private void getUserInfo(Context ctx){
		var auth = ctx.header("Authorization");
		var session = DashboardSessionCache.getSession(auth);
		if(session == null){
			error(ctx, 404, "Session not found");
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

	private void getAllGuilds(Context ctx){
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
		//noinspection ConstantConditions shut
		for(var guild : KittyBot.getJda().getGuildCache().applyStream(stream -> stream.sorted(Comparator.comparing(Guild::getName, String.CASE_INSENSITIVE_ORDER)).collect(Collectors.toList()))){
			var owner = guild.getOwner();
			var obj = DataObject.empty().put("id", guild.getId()).put("name", guild.getName()).put("icon", guild.getIconUrl()).put("count", guild.getMemberCount());
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
		var roles = SelfAssignableRoleCache.getSelfAssignableRoles(guildId);
		var groups = SelfAssignableRoleGroupCache.getSelfAssignableRoleGroups(guildId);
		if(roles == null || groups == null || KittyBot.getJda().getGuildById(guildId) == null){
			error(ctx, 404, "guild not found");
			return;
		}
		var selfAssignableRoles = DataArray.empty();
		for(var role : roles){
			selfAssignableRoles.add(DataObject.empty().put("role", role.getRoleId()).put("emote", role.getEmoteId()));
		}
		var selfAssignableRoleGroups = DataArray.empty();
		for(var group : groups){
			selfAssignableRoleGroups.add(DataObject.empty().put("id", group.getId()).put("name", group.getName()).put("only_one", group.getOnlyOne()));
		}
		ok(ctx, DataObject.empty()
				.put("prefix", PrefixCache.getCommandPrefix(guildId))
				.put("join_messages_enabled", Database.getJoinMessageEnabled(guildId))
				.put("join_messages", Database.getJoinMessage(guildId))
				.put("leave_messages_enabled", Database.getLeaveMessageEnabled(guildId))
				.put("leave_messages", Database.getLeaveMessage(guildId))
				.put("boost_messages_enabled", Database.getBoostMessageEnabled(guildId))
				.put("boost_messages", Database.getBoostMessage(guildId))
				.put("announcement_channel_id", Database.getAnnouncementChannelId(guildId))
				.put("nsfw_enabled", Database.getNSFWEnabled(guildId))
				.put("self_assignable_roles", selfAssignableRoles)
				.put("self_assignable_role_groups", selfAssignableRoleGroups));
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
			var roles = new HashSet<SelfAssignableRole>();
			var dataArray = json.getArray("self_assignable_roles");
			for(var i = 0; i < dataArray.length(); i++){
				var obj = dataArray.getObject(i);
				roles.add(new SelfAssignableRole(guildId, obj.getString("group"), obj.getString("role"), obj.getString("emote")));
			}
			SelfAssignableRoleCache.setSelfAssignableRoles(guildId, roles);
		}
		if(json.hasKey("self_assignable_role_groups")){
			var groups = new HashSet<SelfAssignableRoleGroup>();
			var dataArray = json.getArray("self_assignable_roles");
			for(var i = 0; i < dataArray.length(); i++){
				var obj = dataArray.getObject(i);
				groups.add(new SelfAssignableRoleGroup(guildId, obj.getString("id"), obj.getString("group_name"), obj.getBoolean("only_one")));
			}
			//SelfAssignableRoleGroupCache.setSelfAssignableRoleGroups(guildId, groups);
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
