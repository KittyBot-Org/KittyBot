package de.anteiku.kittybot.webservice;

import de.anteiku.kittybot.KittyBot;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

public class DocumentationRoute implements TemplateViewRoute{
	
	private KittyBot main;
	
	public DocumentationRoute(KittyBot main){
		this.main = main;
	}
	
	@Override
	public ModelAndView handle(Request request, Response response){
		response.body(WebService.readFile("/html/documentation.html"));
		WebService.HtmlObject obj = new WebService.HtmlObject(response.body());
		return new ModelAndView(obj, "guild.html");
	}
	
}
