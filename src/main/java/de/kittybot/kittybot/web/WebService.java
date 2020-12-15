package de.kittybot.kittybot.web;

import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.utils.Utils;
import de.kittybot.kittybot.web.routes.CommandsRoute;
import de.kittybot.kittybot.web.routes.DiscordLoginRoute;
import de.kittybot.kittybot.web.routes.guilds.*;
import de.kittybot.kittybot.web.routes.login.LoginRoute;
import de.kittybot.kittybot.web.routes.login.LogoutRoute;
import de.kittybot.kittybot.web.routes.user.UserInfoRoute;
import io.javalin.Javalin;
import io.javalin.http.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.utils.data.DataObject;

import static io.javalin.apibuilder.ApiBuilder.*;

public class WebService{

	private final KittyBot main;

	public WebService(KittyBot main){
		this.main = main;
		initBackend();
	}

	public static void created(Context ctx, DataObject data){
		result(ctx, 202, data);
	}

	public static void result(Context ctx, int code, DataObject data){
		ctx.header("Content-Type", "application/json");
		ctx.status(code);
		ctx.result(data.toString());
	}

	public static void ok(Context ctx, DataObject data){
		result(ctx, 200, data);
	}

	public static void ok(Context ctx){
		result(ctx, 200, DataObject.empty());
	}

	private void initBackend(){
		Javalin.create(
				config -> config.enableCorsForOrigin(this.main.getConfig().getString("origin_url"))
		).routes(() -> {
			path("/discord_login", () -> {
				get(new DiscordLoginRoute(this.main));
			});
			path("/health_check", () -> {
				get(ctx -> ctx.result("alive"));
			});
			path("/commands", () -> {
				get(new CommandsRoute(this.main));
			});
			path("/login", () -> {
				post(new LoginRoute(this.main));
				delete(new LogoutRoute(this.main));
			});
			path("/user/me", () -> {
				before("/*", this::checkDiscordLogin);
				get(new UserInfoRoute(this.main));
			});
			path("/guilds", () -> {
				before("/*", this::checkDiscordLogin);
				path("/all", () -> {
					get(new AllGuildsRoute(this.main));
				});
				path("/:guildId", () -> {
					before("/*", this::checkGuildPerms);
					path("/roles", () -> {
						get(new RolesRoute(this.main));
					});
					path("/channels", () -> {
						get(new ChannelsRoute(this.main));
					});
					path("/emotes", () -> {
						get(new EmotesRoute(this.main));
					});
					path("/settings", () -> {
						get(new GetGuildSettingsRoute(this.main));
						post(new SetGuildSettingsRoute(this.main));
					});
				});
			});
		}).start(this.main.getConfig().getInt("backend_port"));
	}

	private void checkDiscordLogin(Context ctx){
		if(ctx.method().equals("OPTIONS")){
			return;
		}
		var userId = getUserId(ctx);
		if(!this.main.getDashboardSessionManager().has(userId)){
			throw new UnauthorizedResponse("Invalid token");
		}
	}

	private void checkGuildPerms(Context ctx){
		if(ctx.method().equals("OPTIONS")){
			return;
		}
		var guild = getGuild(ctx);
		var userId = getUserId(ctx);
		if(this.main.getConfig().getLongSet("owner_ids").contains(userId)){
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

	public long getUserId(final Context ctx){
		var token = ctx.header("Authorization");
		if(token == null || token.isBlank()){
			throw new UnauthorizedResponse("No token provided");
		}
		var userId = getUserId(token);
		if(userId == -1L){
			throw new ForbiddenResponse("Invalid token");
		}
		return userId;
	}

	public long getUserId(String token){
		try{
			return Long.parseLong(Jwts.parserBuilder()
					.setSigningKey(this.main.getDashboardSessionManager().getSecretKey())
					.build()
					.parseClaimsJws(token)
					.getBody()
					.getSubject()
			);
		}
		catch(ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e){
			return -1L;
		}
	}

	public Guild getGuild(final Context ctx){
		var guildId = ctx.pathParam(":guildId");
		if(guildId.isBlank() || !Utils.isSnowflake(guildId)){
			throw new BadRequestResponse("Please provide a valid guild id");
		}
		var guild = this.main.getJDA().getGuildById(guildId);
		if(guild == null){
			throw new NotFoundResponse("Guild not found");
		}
		return guild;
	}

}
