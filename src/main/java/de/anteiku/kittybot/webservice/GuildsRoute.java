package de.anteiku.kittybot.webservice;

import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.core.entities.User;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

public class GuildsRoute implements TemplateViewRoute{
	
	private KittyBot main;
	
	public GuildsRoute(KittyBot main){
		this.main = main;
	}
	
	@Override
	public ModelAndView handle(Request request, Response response){
		ModelAndView model;
		if(main.webService.loggedIn(request)){
			response.body(WebService.readFile("/html/guilds.html"));
			WebService.HtmlObject obj = new WebService.HtmlObject(response.body());
			User user = main.jda.getUserById(main.database.getSession(request.cookie("key")));
			obj.addRegex("userid", user.getId());
			obj.addRegex("usericonurl", user.getAvatarUrl());
			obj.addRegex("username", user.getName());
			obj.addRegex("usertag", user.getAsTag());
			model = new ModelAndView(obj, "guilds.html");
		}
		else{
			response.redirect("/");
			model = new ModelAndView(new WebService.HtmlObject(response.body()), "login.html");
		}
		
		return model;
	}
	
}
