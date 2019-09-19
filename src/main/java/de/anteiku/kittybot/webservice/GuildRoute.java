package de.anteiku.kittybot.webservice;

import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.core.entities.User;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

public class GuildRoute implements TemplateViewRoute{
	
	private KittyBot main;
	
	public GuildRoute(KittyBot main){
		this.main = main;
	}
	
	@Override
	public ModelAndView handle(Request request, Response response){
		ModelAndView model;
		String guildId = request.params("guildId");
		if(main.webService.loggedIn(request)){
			response.body(WebService.readFile("/html/guild.html"));
			WebService.HtmlObject obj = new WebService.HtmlObject(response.body());
			User user = main.jda.getUserById(main.database.getSession(request.cookie("key")));
			obj.addRegex("guildname", main.jda.getGuildById(guildId).getName());
			obj.addRegex("guildid", guildId);
			obj.addRegex("guildiconurl", main.jda.getGuildById(guildId).getIconUrl());
			obj.addRegex("usericonurl", user.getAvatarUrl());
			obj.addRegex("username", user.getName());
			obj.addRegex("usertag", user.getAsTag());
			model = new ModelAndView(obj, "guild.html");
		}
		else{
			response.redirect("/login");
			model = new ModelAndView(new WebService.HtmlObject(response.body()), "login.html");
		}
		
		return model;
	}
	
}
