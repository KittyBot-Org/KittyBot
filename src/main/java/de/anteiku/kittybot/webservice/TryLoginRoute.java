package de.anteiku.kittybot.webservice;

import bell.oauth.discord.main.OAuthBuilder;
import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.Password;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

public class TryLoginRoute implements TemplateViewRoute{
	
	private KittyBot main;
	
	public TryLoginRoute(KittyBot main){
		this.main = main;
	}
	
	@Override
	public ModelAndView handle(Request request, Response response){
		String code = request.queryParams("code");
		OAuthBuilder oAuth = main.webService.oAuth;
		if(oAuth.exchange(code) != bell.oauth.discord.main.Response.ERROR){
			String userId = oAuth.getUser().getId();
			String token = Password.generate(20);
			main.database.setUserToken(userId, token);
			response.cookie("user_id", userId);
			response.cookie("user_token", token);
			response.redirect("/guild");
		}
		else{
			response.redirect("/");
		}
		return new ModelAndView(new WebService.HtmlObject(response.body()), "tryLogin");
	}
	
}
