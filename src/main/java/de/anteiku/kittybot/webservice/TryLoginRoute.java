package de.anteiku.kittybot.webservice;

import de.anteiku.kittybot.KittyBot;
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
		if(main.webService.oAuth.exchange(code) != bell.oauth.discord.main.Response.ERROR){
			String userId =  main.webService.oAuth.getUser().getId();
			String key = main.database.addSession(userId);
			response.cookie("key", key);
			response.redirect("/guild");
		}
		else{
			response.redirect("/");
		}
		return new ModelAndView(new WebService.HtmlObject(response.body()), "tryLogin");
	}
	
}
