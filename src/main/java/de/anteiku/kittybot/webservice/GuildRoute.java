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
		if(main.webService.loggedIn(request)){
			response.body(WebService.readFile("/html/guild-template.html"));
			WebService.HtmlObject obj = new WebService.HtmlObject(response.body());
			User user = main.jda.getUserById(main.database.getSession(request.cookie("key")));
			if(request.pathInfo().equalsIgnoreCase("/guild")){
				obj.addRegex("template", WebService.readFile("/html/overview-template.html"));
				obj.addRegex("headertext", "Select Guild");
				obj.addRegex("title", "Select Guild");
			}
			else{
				String guildId = request.params("guildId");
				obj.addRegex("title", main.jda.getGuildById(guildId).getName());
				obj.addRegex("headertext", main.jda.getGuildById(guildId).getName());
				obj.addRegex("template", WebService.readFile("/html/settings-template.html"));
				obj.addRegex("guildname", main.jda.getGuildById(guildId).getName());
				obj.addRegex("guildiconurl", main.jda.getGuildById(guildId).getIconUrl());
			}
			obj.addRegex("usericonurl", user.getAvatarUrl());
			obj.addRegex("username", user.getName());
			obj.addRegex("usertag", "#" + user.getDiscriminator());
			model = new ModelAndView(obj, "guild.html");
		}
		else{
			response.redirect("/");
			model = new ModelAndView(new WebService.HtmlObject(response.body()), "guild.html");
		}
		
		return model;
	}
	
}
