package de.anteiku.kittybot.webservice;

import de.anteiku.kittybot.KittyBot;
import spark.*;

public class IndexRoute implements TemplateViewRoute{
	
	private KittyBot main;
	
	public IndexRoute(KittyBot main){
		this.main = main;
	}
	
	@Override
	public ModelAndView handle(Request request, Response response){
		response.body(WebService.readFile("/html/index.html"));
		return new ModelAndView(new WebService.HtmlObject(response.body()), "index.html");
	}
	
}
