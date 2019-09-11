package de.anteiku.kittybot.webservice;

import de.anteiku.kittybot.KittyBot;
import spark.*;

public class LogoutRoute implements TemplateViewRoute{
	
	private KittyBot main;
	
	public LogoutRoute(KittyBot main){
		this.main = main;
	}
	
	@Override
	public ModelAndView handle(Request request, Response response){
		response.cookie("user_id", "");
		response.cookie("user_token", "");
		response.removeCookie("user_id");
		response.removeCookie("user_token");
		response.redirect("/");
		response.body("<a href='/login'>click here</a>");
		return new ModelAndView(new WebService.HtmlObject(response.body()), "tryLogout");
	}
	
}
