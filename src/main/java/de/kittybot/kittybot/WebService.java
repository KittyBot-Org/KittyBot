package de.kittybot.kittybot;

import com.jagrosh.jdautilities.oauth2.OAuth2Client;
import com.jagrosh.jdautilities.oauth2.Scope;
import com.jagrosh.jdautilities.oauth2.exceptions.InvalidStateException;
import de.kittybot.kittybot.cache.DashboardSessionCache;
import de.kittybot.kittybot.cache.GuildCache;
import de.kittybot.kittybot.cache.GuildSettingsCache;
import de.kittybot.kittybot.cache.SelfAssignableRoleCache;
import de.kittybot.kittybot.objects.Config;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandManager;
import de.kittybot.kittybot.objects.data.GuildData;
import de.kittybot.kittybot.objects.requests.Requester;
import de.kittybot.kittybot.objects.session.DashboardSession;
import de.kittybot.kittybot.objects.session.DashboardSessionController;
import de.kittybot.kittybot.utils.Utils;
import io.javalin.Javalin;
import io.javalin.http.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.exceptions.HttpException;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static io.javalin.apibuilder.ApiBuilder.*;

public class WebService{

	private static final SecretKey KEY = Keys.hmacShaKeyFor(Config.SIGNING_KEY.getBytes(StandardCharsets.UTF_8));
	private static final Scope[] SCOPES = {Scope.IDENTIFY, Scope.GUILDS};
	private static final OAuth2Client O_AUTH_2_CLIENT = new OAuth2Client.Builder()
			.setClientId(Long.parseLong(Config.BOT_ID))
			.setClientSecret(Config.BOT_SECRET)
			.setOkHttpClient(Requester.getHttpClient())
			.setSessionController(new DashboardSessionController())
			.build();

	public WebService(int port){
		Javalin.create(config -> config.enableCorsForOrigin(Config.ORIGIN_URL)).routes(() -> {
			get("/discord_login", this::discordLogin);
			get("/twitch", this::twitch);
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
		try{
			if(!DashboardSessionCache.hasSession(getUserId(ctx))){
				ctx.redirect(Config.REDIRECT_URL);
				return;
			}
		}
		catch(UnauthorizedResponse | ForbiddenResponse ignored){}
		ctx.redirect(O_AUTH_2_CLIENT.generateAuthorizationURL(Config.REDIRECT_URL, SCOPES));
	}

	private void twitch(Context ctx){
		System.out.println("Twitch Request: " + ctx.body());
	}

	private void login(Context ctx){
		var json = DataObject.fromJson(ctx.body());
		var code = json.getString("code", null);
		var state = json.getString("state", null);
		if(code == null || code.isBlank() || state == null || state.isBlank()){
			throw new UnauthorizedResponse("State or code is invalid");
		}
		try{
			var session = (DashboardSession) O_AUTH_2_CLIENT.startSession(code, state, "", SCOPES).complete();
			created(ctx, DataObject.empty().put("token", Jwts.builder().setIssuedAt(new Date()).setSubject(session.getUserId()).signWith(KEY).compact()));
		}
		catch(HttpException e){
			throw new BadRequestResponse("Don't spam login");
		}
		catch(InvalidStateException e){
			throw new UnauthorizedResponse("State invalid/expired. Please try again");
		}
		catch(IOException e){
			throw new ForbiddenResponse("Could not login");
		}
	}

	private void logout(Context ctx){
		DashboardSessionCache.deleteSession(getUserId(ctx));
	}

	private void checkDiscordLogin(Context ctx){
		if(ctx.method().equals("OPTIONS")){
			return;
		}
		var userId = getUserId(ctx);
		if(!DashboardSessionCache.hasSession(userId)){
			throw new UnauthorizedResponse("Invalid token");
		}
	}

	private void getUserInfo(Context ctx){
		var userId = getUserId(ctx);
		var session = DashboardSessionCache.getSession(userId);
		if(session == null){
			throw new NotFoundResponse("Session not found");
		}
		var user = KittyBot.getJda().retrieveUserById(userId).complete();
		if(user == null){
			throw new NotFoundResponse("User not found");
		}
		List<GuildData> guilds;
		try{
			guilds = GuildCache.getGuilds(session);
		}
		catch(IOException ex){
			throw new InternalServerErrorResponse("There was a problem while login. Please try again");
		}
		var guildData = DataArray.empty();
		guilds.forEach(guild -> guildData.add(DataObject.empty().put("id", guild.getId()).put("name", guild.getName()).put("icon", guild.getIconUrl())));
		ok(ctx, DataObject.empty().put("name", user.getName()).put("id", userId).put("icon", user.getEffectiveAvatarUrl()).put("guilds", guildData));
	}

	private void getAllGuilds(Context ctx){
		var userId = getUserId(ctx);
		if(!Config.ADMIN_IDS.contains(userId)){
			throw new ForbiddenResponse("Only admins have access to this!");
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

	private void checkGuildPerms(Context ctx){
		if(ctx.method().equals("OPTIONS")){
			return;
		}
		var guild = getGuild(ctx);
		var userId = getUserId(ctx);
		if(Config.ADMIN_IDS.contains(userId)){
			return;
		}
		var member = guild.retrieveMemberById(userId).complete();
		if(member == null){
			throw new NotFoundResponse("I could not find you in that guild");
		}
		if(!member.hasPermission(Permission.ADMINISTRATOR)){
			throw new ForbiddenResponse("You have no permission for this guild");
		}
	}

	private void getCommands(Context ctx){
		var commandSet = CommandManager.getDistinctCommands().entrySet();
		var data = DataArray.empty();
		for(var cat : Category.values()){
			var commands = DataArray.empty();
			for(var cmd : commandSet){
				var command = cmd.getValue();
				if(cat == command.getCategory()){
					commands.add(DataObject.empty().put("command", command.getCommand()).put("usage", command.getRawUsage()).put("description", command.getDescription()));
				}
			}
			data.add(DataObject.empty().put("name", cat.getFriendlyName()).put("emote_url", cat.getEmoteUrl()).put("commands", commands));
		}

		ok(ctx, DataObject.empty().put("prefix", Config.DEFAULT_PREFIX).put("categories", data));
	}

	private void getRoles(Context ctx){
		var guild = getGuild(ctx);
		var data = DataArray.empty();
		guild.getRoleCache().forEach(role -> {
			if(!role.isPublicRole()){
				var color = role.getColor();
				data.add(DataObject.empty().put("name", role.getName()).put("id", role.getId()).put("color", color == null ? "" : "#" + Integer.toHexString(color.getRGB()).substring(2)));
			}
		});
		ok(ctx, DataObject.empty().put("roles", data));
	}

	private void getChannels(Context ctx){
		var guild = getGuild(ctx);
		var data = DataArray.empty();
		guild.getTextChannelCache().forEach(channel -> data.add(DataObject.empty().put("name", channel.getName()).put("id", channel.getId())));
		ok(ctx, DataObject.empty().put("channels", data));
	}

	private void getEmotes(Context ctx){
		var guild = getGuild(ctx);
		var data = DataArray.empty();
		guild.getEmoteCache().forEach(emote -> data.add(DataObject.empty().put("name", emote.getName()).put("id", emote.getId()).put("url", emote.getImageUrl())));
		ok(ctx, DataObject.empty().put("emotes", data));
	}

	private void getGuildSettings(Context ctx){
		var guild = getGuild(ctx);
		var guildId = guild.getId();
		var data = DataArray.empty();
		SelfAssignableRoleCache.getSelfAssignableRoles(guildId).forEach((key, value) -> data.add(DataObject.empty().put("role", key).put("emote", value)));
		var settings = GuildSettingsCache.getGuildSettings(guildId);
		ok(ctx, DataObject.empty()
				.put("prefix", settings.getCommandPrefix())
				.put("join_messages_enabled", settings.areJoinMessagesEnabled())
				.put("join_messages", settings.getJoinMessage())
				.put("leave_messages_enabled", settings.areLeaveMessagesEnabled())
				.put("leave_messages", settings.getLeaveMessage())
				.put("boost_messages_enabled", settings.areBoostMessagesEnabled())
				.put("boost_messages", settings.getBoostMessage())
				.put("announcement_channel_id", settings.getAnnouncementChannelId())
				.put("nsfw_enabled", settings.isNSFWEnabled())
				.put("self_assignable_roles", data));
	}

	private void setGuildSettings(Context ctx){
		var guild = getGuild(ctx);
		var guildId = guild.getId();
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
		if(json.hasKey("self_assignable_roles")){
			var roles = new HashMap<String, String>();
			var dataArray = json.getArray("self_assignable_roles");
			for(var i = 0; i < dataArray.length(); i++){
				var obj = dataArray.getObject(i);
				roles.put(obj.getString("role"), obj.getString("emote"));
			}
			SelfAssignableRoleCache.setSelfAssignableRoles(guildId, roles);
		}
		ok(ctx);
	}

	private Guild getGuild(final Context ctx){
		var guildId = ctx.pathParam(":guildId");
		if(guildId.isBlank() || !Utils.isSnowflake(guildId)){
			throw new BadRequestResponse("Please provide a valid guild id");
		}
		var guild = KittyBot.getJda().getGuildById(guildId);
		if(guild == null){
			throw new NotFoundResponse("Guild not found");
		}
		return guild;
	}

	private String getUserId(final Context ctx){
		var token = ctx.header("Authorization");
		if(token == null || token.isBlank()){
			throw new UnauthorizedResponse("No token provided");
		}
		var userId = getUserId(token);
		if(userId == null){
			throw new ForbiddenResponse("Invalid token");
		}
		return userId;
	}

	private String getUserId(String token){
		try{
			return Jwts.parserBuilder()
					.setSigningKey(KEY)
					.build()
					.parseClaimsJws(token)
					.getBody()
					.getSubject();
		}
		catch(ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e){
			return null;
		}
	}


	private void result(Context ctx, int code, DataObject data){
		ctx.header("Content-Type", "application/json");
		ctx.status(code);
		ctx.result(data.toString());
	}

	private void created(Context ctx, DataObject data){
		result(ctx, 202, data);
	}

	private void ok(Context ctx){
		result(ctx, 200, DataObject.empty().put("status", 200));
	}

	private void ok(Context ctx, DataObject data){
		result(ctx, 200, data);
	}

}
