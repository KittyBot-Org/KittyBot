package de.kittybot.kittybot.web.login;

import com.jagrosh.jdautilities.oauth2.exceptions.InvalidStateException;
import de.kittybot.kittybot.modules.DashboardSessionModule;
import de.kittybot.kittybot.modules.WebService;
import de.kittybot.kittybot.objects.module.Modules;
import de.kittybot.kittybot.objects.session.DashboardSession;
import io.javalin.http.*;
import io.jsonwebtoken.Jwts;
import net.dv8tion.jda.api.exceptions.HttpException;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Date;

public class PostLoginRoute implements Handler{

	private final Modules modules;

	public PostLoginRoute(Modules modules){
		this.modules = modules;
	}

	@Override
	public void handle(@NotNull Context ctx){
		var json = DataObject.fromJson(ctx.body());
		var code = json.getString("code", null);
		var state = json.getString("state", null);
		if(code == null || code.isBlank() || state == null || state.isBlank()){
			throw new UnauthorizedResponse("State or code is invalid");
		}
		try{
			var sessionManager = this.modules.get(DashboardSessionModule.class);
			var session = (DashboardSession) sessionManager.getOAuth2Client().startSession(code, state, "", DashboardSessionModule.getScopes()).complete();
			WebService.accepted(ctx, DataObject.empty().put("token", Jwts.builder().setIssuedAt(new Date()).setSubject(String.valueOf(session.getUserId())).signWith(sessionManager.getSecretKey()).compact()));
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

}
