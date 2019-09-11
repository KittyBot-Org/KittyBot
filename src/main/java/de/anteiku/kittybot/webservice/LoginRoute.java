package de.anteiku.kittybot.webservice;

import de.anteiku.kittybot.KittyBot;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

public class LoginRoute implements TemplateViewRoute{
	
	private KittyBot main;
	
	public LoginRoute(KittyBot main){
		this.main = main;
	}
	
	@Override
	public ModelAndView handle(Request request, Response response){
		if(main.webService.loggedIn(request)){
			response.redirect("/guild");
		}
		else{
			response.redirect(main.webService.oAuth.getAuthorizationUrl(null) + "&prompt=none");
		}
		return new ModelAndView(new WebService.HtmlObject(response.body()), "login");
	}
	
}
