package de.anteiku.kittybot.webservice;

import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.core.entities.User;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

public class GuildTemplateRoute implements TemplateViewRoute{
	
	private KittyBot main;
	
	public GuildTemplateRoute(KittyBot main){
		this.main = main;
	}
	
	@Override
	public ModelAndView handle(Request request, Response response){
		ModelAndView model;
		if(main.webService.loggedIn(request)){
			response.body(WebService.readFile("/html/guild-template.html"));
			WebService.HtmlObject obj = new WebService.HtmlObject(response.body());
			User user = main.jda.getUserById(main.database.getSession(request.cookie("key")));
			if(request.pathInfo().equalsIgnoreCase("/guild")){
				obj.addRegex("template", WebService.readFile("/html/overview-template.html"));
			}
			else{
				String guildId = request.params("guildId");
				obj.addRegex("template", WebService.readFile("/html/settings-template.html"));
				obj.addRegex("guildname", main.jda.getGuildById(guildId).getName());
				obj.addRegex("guildid", guildId);
				obj.addRegex("guildiconurl", main.jda.getGuildById(guildId).getIconUrl());
			}
			obj.addRegex("usericonurl", user.getAvatarUrl());
			obj.addRegex("username", user.getName());
			obj.addRegex("usertag", "#" + user.getDiscriminator());
			model = new ModelAndView(obj, "guild.html");
		}
		else{
			response.redirect("/");
			model = new ModelAndView(new WebService.HtmlObject(response.body()), "login.html");
		}
		
		return model;
	}
	
}
