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
		main.database.deleteSession(request.cookie("key"));
		response.cookie("key", "");
		response.removeCookie("key");
		response.redirect("/");
		response.body("<a href='/login'>click here</a>");
		return new ModelAndView(new WebService.HtmlObject(response.body()), "logout");
	}
	
}
