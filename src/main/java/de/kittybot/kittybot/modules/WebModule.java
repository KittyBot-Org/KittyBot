package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.objects.module.Module;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.Utils;
import de.kittybot.kittybot.web.bot.invite.GetBotInviteRoute;
import de.kittybot.kittybot.web.commands.GetCommandsRoute;
import de.kittybot.kittybot.web.dev.GetDevRoute;
import de.kittybot.kittybot.web.discord.invite.GetDiscordInviteRoute;
import de.kittybot.kittybot.web.discord.login.GetDiscordLoginRoute;
import de.kittybot.kittybot.web.guilds.GetAllGuildsRoute;
import de.kittybot.kittybot.web.guilds.guild.channels.GetChannelsRoute;
import de.kittybot.kittybot.web.guilds.guild.emotes.GetEmotesRoute;
import de.kittybot.kittybot.web.guilds.guild.invites.GetInvitesRoute;
import de.kittybot.kittybot.web.guilds.guild.roles.GetRolesRoute;
import de.kittybot.kittybot.web.guilds.guild.settings.GetGuildSettingsRoute;
import de.kittybot.kittybot.web.guilds.guild.settings.PostGuildSettingsRoute;
import de.kittybot.kittybot.web.guilds.guild.tags.GetTagsRoute;
import de.kittybot.kittybot.web.guilds.guild.tags.tag.DeleteTagRoute;
import de.kittybot.kittybot.web.guilds.guild.tags.tag.PostTagRoute;
import de.kittybot.kittybot.web.guilds.guild.users.GetUsersRoute;
import de.kittybot.kittybot.web.info.GetInfoRoute;
import de.kittybot.kittybot.web.login.DeleteLoginRoute;
import de.kittybot.kittybot.web.login.PostLoginRoute;
import de.kittybot.kittybot.web.shards.GetShardsRoute;
import de.kittybot.kittybot.web.user.GetUserInfoRoute;
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

public class WebModule extends Module{

	private Javalin javalin;

	public static void accepted(Context ctx){
		accepted(ctx, DataObject.empty());
	}

	public static void accepted(Context ctx, DataObject data){
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

	@Override
	public void onEnable(){
		if(Config.BACKEND_PORT == -1){
			return;
		}
		this.javalin = Javalin.create(config ->
			config.enableCorsForOrigin(Config.ORIGIN_URL)
		).routes(() -> {
			path("/info", () ->
				get(new GetInfoRoute(this.modules))
			);
			path("/shards", () ->
				get(new GetShardsRoute(this.modules))
			);
			path("/discord_login", () ->
				get(new GetDiscordLoginRoute(this.modules))
			);
			path("/bot_invite", () ->
				get(new GetBotInviteRoute())
			);
			path("/discord_invite", () ->
				get(new GetDiscordInviteRoute())
			);
			path("/health_check", () ->
				get(ctx -> ctx.result("alive"))
			);
			path("/commands", () ->
				get(new GetCommandsRoute(this.modules))
			);
			path("/dev", () ->{
				before("/*", this::checkDiscordLogin);
				get(new GetDevRoute(this.modules));
			});
			path("/login", () -> {
				post(new PostLoginRoute(this.modules));
				delete(new DeleteLoginRoute(this.modules));
			});
			path("/user/me", () -> {
				before("/*", this::checkDiscordLogin);
				get(new GetUserInfoRoute(this.modules));
			});
			path("/guilds", () -> {
				before("/*", this::checkDiscordLogin);
				get(new GetAllGuildsRoute(this.modules));
				path("/:guildId", () -> {
					before("/*", this::checkGuildPerms);
					path("/roles", () ->
						get(new GetRolesRoute(this.modules))
					);
					path("/channels", () ->
						get(new GetChannelsRoute(this.modules))
					);
					path("/emotes", () ->
						get(new GetEmotesRoute(this.modules))
					);
					path("/users", () ->
						get(new GetUsersRoute(this.modules))
					);
					path("/invites", () ->
						get(new GetInvitesRoute(this.modules))
					);
					path("/tags", () -> {
						get(new GetTagsRoute(this.modules));
						path("/:tagId", () -> {
							post(new PostTagRoute(this.modules));
							delete(new DeleteTagRoute(this.modules));
						});
					});
					path("/settings", () -> {
						get(new GetGuildSettingsRoute(this.modules));
						post(new PostGuildSettingsRoute(this.modules));
					});
				});
			});
		}).start(Config.BACKEND_HOST, Config.BACKEND_PORT);
	}

	@Override
	protected void onDisable(){
		this.javalin.stop();
	}

	private void checkDiscordLogin(Context ctx){
		if(ctx.method().equals("OPTIONS")){
			return;
		}
		var userId = getUserId(ctx);
		if(!this.modules.get(DashboardSessionModule.class).has(userId)){
			throw new UnauthorizedResponse("Invalid token");
		}
	}

	private void checkGuildPerms(Context ctx){
		if(ctx.method().equals("OPTIONS")){
			return;
		}
		var guild = getGuild(ctx);
		var userId = getUserId(ctx);
		if(Config.DEV_IDS.contains(userId)){
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
		try{
			return Long.parseLong(Jwts.parserBuilder()
				.setSigningKey(this.modules.get(DashboardSessionModule.class).getSecretKey())
				.build()
				.parseClaimsJws(token)
				.getBody()
				.getSubject()
			);
		}
		catch(ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e){
			throw new UnauthorizedResponse("provided token is not valid");
		}
	}

	public Guild getGuild(Context ctx){
		var guildId = ctx.pathParam(":guildId");
		if(guildId.isBlank() || !Utils.isSnowflake(guildId)){
			throw new BadRequestResponse("Please provide a valid guild id");
		}
		var guild = this.modules.getGuildById(guildId);
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
