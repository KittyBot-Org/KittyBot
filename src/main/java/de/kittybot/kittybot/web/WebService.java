package de.kittybot.kittybot.web;

import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.objects.Tag;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.Utils;
import de.kittybot.kittybot.web.routes.commands.GetCommandsRoute;
import de.kittybot.kittybot.web.routes.GetDiscordLoginRoute;
import de.kittybot.kittybot.web.routes.guilds.*;
import de.kittybot.kittybot.web.routes.guilds.guild.*;
import de.kittybot.kittybot.web.routes.guilds.guild.tags.GetTagsRoute;
import de.kittybot.kittybot.web.routes.guilds.guild.tags.tag.DeleteTagRoute;
import de.kittybot.kittybot.web.routes.guilds.guild.tags.tag.PostTagRoute;
import de.kittybot.kittybot.web.routes.login.PostLoginRoute;
import de.kittybot.kittybot.web.routes.login.DeleteLoginRoute;
import de.kittybot.kittybot.web.routes.user.GetUserInfoRoute;
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

	public static void accepted(Context ctx, DataObject data){
		result(ctx, 202, data);
	}

	public static void accepted(Context ctx){
		accepted(ctx, DataObject.empty());
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
		if(Config.BACKEND_PORT == -1){
			return;
		}
		Javalin.create(config -> {
			if(Config.ORIGIN_URL.isBlank()){
				config.enableCorsForOrigin(Config.ORIGIN_URL);
			}
			else{
				config.enableCorsForAllOrigins();
			}
		}).routes(() -> {
			path("/discord_login", () -> {
				get(new GetDiscordLoginRoute(this.main));
			});
			path("/health_check", () -> {
				get(ctx -> ctx.result("alive"));
			});
			path("/commands", () -> {
				get(new GetCommandsRoute(this.main));
			});
			path("/login", () -> {
				post(new PostLoginRoute(this.main));
				delete(new DeleteLoginRoute(this.main));
			});
			path("/user/me", () -> {
				before("/*", this::checkDiscordLogin);
				get(new GetUserInfoRoute(this.main));
			});
			path("/guilds", () -> {
				before("/*", this::checkDiscordLogin);
				get(new GetAllGuildsRoute(this.main));
				path("/:guildId", () -> {
					before("/*", this::checkGuildPerms);
					path("/roles", () -> {
						get(new GetRolesRoute(this.main));
					});
					path("/channels", () -> {
						get(new GetChannelsRoute(this.main));
					});
					path("/emotes", () -> {
						get(new GetEmotesRoute(this.main));
					});
					path("/invites", () -> {
						get(new GetInvitesRoute(this.main));
					});
					path("/tags", () -> {
						get(new GetTagsRoute(this.main));
						path("/:tagId", () -> {
							post(new PostTagRoute(this.main));
							delete(new DeleteTagRoute(this.main));
						});
					});
					path("/settings", () -> {
						get(new GetGuildSettingsRoute(this.main));
						post(new PostGuildSettingsRoute(this.main));
					});
				});
			});
		}).start(Config.BACKEND_PORT);
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
		if(Config.OWNER_IDS.contains(userId)){
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

	public long getUserId(Context ctx){
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

	public Guild getGuild(Context ctx){
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

	public long getTagId(Context ctx){
		var tagId = ctx.pathParam(":tagId");
		try{
			return Long.parseLong(tagId);
		}
		catch(NumberFormatException e){
			throw new NotFoundResponse("Invalid tag id");
		}
	}

}
