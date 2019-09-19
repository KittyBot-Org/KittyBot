package de.anteiku.kittybot.webservice;

import bell.oauth.discord.main.OAuthBuilder;
import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.Logger;
import de.anteiku.kittybot.poll.Poll;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateEngine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static spark.Spark.*;

public class WebService{
	
	private KittyBot main;
	public OAuthBuilder oAuth;
	
	public WebService(KittyBot main, int port){
		this.main = main;
		oAuth = new OAuthBuilder(main.config.get("discord_client_id"), main.config.get("discord_client_secret"));
		oAuth.setScopes(new String[]{"guilds", "identify"});
		oAuth.setRedirectURI("http://anteiku.de/trylogin");
		//oAuth.setRedirectURI("http://localhost/trylogin");
		port(port);
		staticFileLocation("/public");
		get("/", new IndexRoute(main), new HtmlTemplateEngine());
		get("/documentation", new DocumentationRoute(main), new HtmlTemplateEngine());
		get("/login", new LoginRoute(main), new HtmlTemplateEngine());
		get("/trylogin", new TryLoginRoute(main), new HtmlTemplateEngine());
		get("/logout", new LogoutRoute(main), new HtmlTemplateEngine());
		redirect.get("/guild/", "/guild");
		
		path("/poll", () -> {
			path("/:pollId", () -> {
				get("/get", this::getPoll);
			});
		});
		
		path("/user", () -> {
			before("", this::checkDiscordLogin);
			path("/:userId", () -> {
				get("/guilds/get", this::getGuilds);
			});
		});
		path("/guild", () -> {
			before("", this::checkDiscordLogin);
			get("", new GuildsRoute(main), new HtmlTemplateEngine());
			path("/:guildId", () -> {
				before("", this::checkGuildPerms);
				get("", new GuildRoute(main), new HtmlTemplateEngine());
				redirect.get("/", "/login");
				path("/commandprefix", () -> {
					get("/get", this::getCommandPrefix);
					get("/set/:value", this::setCommandPrefix);
					get("/set/", this::setCommandPrefix);
				});
				path("/roles", () -> {
					get("/get", this::getAllRoles);
				});
				path("/polls", () -> {
					get("/get", this::getAllPolls);
				});
				path("/icon", () -> {
					get("/get", this::getIcon);
				});
				path("/channels", () -> {
					get("/get", this::getAllChannels);
				});
				path("/selfassignableroles", () -> {
					get("/get", this::getSelfAssignableRoles);
					get("/add/:value", this::addSelfAssignableRole);
					get("/remove/:value", this::removeSelfAssignableRole);
				});
				path("/welcomechannel", () -> {
					get("/get", this::getWelcomeChannel);
					get("/set/:value", this::setWelcomeChannel);
				});
				path("/nsfw", () -> {
					get("/get", this::getNSFWEnabled);
					get("/set/:value", this::setNSFWEnabled);
				});
				path("/welcomemessage", () -> {
					get("/get", this::getWelcomeMessage);
					get("/set/:value", this::setWelcomeMessage);
					get("/enabled/set/:value", this::setWelcomeMessageEnabled);
					get("/enabled/get", this::getWelcomeMessageEnabled);
				});
			});
		});
	}
	
	private void checkDiscordLogin(Request request, Response response){
		if(!loggedIn(request)){
			halt(401, "<a href='/login'>Please login with discord!</a>");
		}
	}
	
	private void checkGuildPerms(Request request, Response response){
		String guildId = request.params(":guildId");
		Guild guild = main.jda.getGuildById(guildId);
		if(guild == null){
			halt(401, "<a href='/login'>Please login with discord!</a>");
		}
		else{
			Member member = guild.getMemberById(main.database.getSession(request.cookie("key")));
			if(member == null){
				halt(401, "<a href='/login'>Please login with discord!</a>");
			}
			else{
				if(!member.hasPermission(Permission.ADMINISTRATOR)){
					halt(401, "<a href='/login'>Please login with discord!</a>");
				}
			}
		}
	}
	
	public boolean loggedIn(Request request){
		String key = request.cookie("key");
		if(key == null){
			return false;
		}
		else{
			return main.database.sessionExists(key);
		}
	}
	
	private String getIcon(Request request, Response response){
		//System.out.println("getIcon());
		return main.jda.getGuildById(request.params(":guildId")).getIconUrl();
	}
	
	private String getAllPolls(Request request, Response response){
		StringBuilder json = new StringBuilder("{\"polls\": [");
		User user = main.jda.retrieveUserById(request.cookie("user_id")).complete();
		for(Guild guild : main.jda.getMutualGuilds(user)){
			json.append("\"").append(guild.getId()).append("\":[");
			for(Map.Entry<String, Poll> p : main.database.getPolls(guild.getId()).entrySet()){
				Poll poll = p.getValue();
				json.append("{\"id\": \"").append(poll.getId()).append("\", \"topic\": \"").append(poll.getTopic()).append("\", \"iconurl\": \"").append(guild.getIconUrl()).append("\"}, ");
			}
		}
		json.append("]}");
		if(json.lastIndexOf(",") != -1){
			json.deleteCharAt(json.lastIndexOf(","));
		}
		return json.toString();
	}
	
	private String getPoll(Request request, Response response){
		StringBuilder json = new StringBuilder("{\"poll\": {");
		Poll poll = main.database.getPoll(request.params(":pollId:"));
		json.append("{\"id\": \"").append(poll.getId()).append("\", \"topic\": \"").append(poll.getTopic()).append("\", \"iconurl\": \"").append(" ").append("\"}");
		
		json.append("}}");
		if(json.lastIndexOf(",") != -1){
			json.deleteCharAt(json.lastIndexOf(","));
		}
		return json.toString();
	}
	
	private String getGuilds(Request request, Response response){
		StringBuilder json = new StringBuilder("{\"guilds\": [");
		User user = main.jda.getUserById(main.database.getSession(request.cookie("key")));
		for(Guild guild : main.jda.getMutualGuilds(user)){
			if(guild.getMember(user).hasPermission(Permission.ADMINISTRATOR)){
				json.append("{\"name\": \"").append(guild.getName()).append("\", \"id\": \"").append(guild.getId()).append("\", \"iconurl\": \"").append(guild.getIconUrl()).append("\"}, ");
			}
		}
		json.append("]}");
		if(json.lastIndexOf(",") != -1){
			json.deleteCharAt(json.lastIndexOf(","));
		}
		return json.toString();
	}
	
	private String getAllChannels(Request request, Response response){
		List<TextChannel> channels = main.jda.getGuildById(request.params(":guildId")).getTextChannels();
		StringBuilder json = new StringBuilder("{\"channels\": [");
		for(TextChannel c : channels){
			json.append("{\"name\": \"").append(c.getName()).append("\", \"id\": \"").append(c.getId()).append("\"}, ");
		}
		json.append("]}");
		if(json.lastIndexOf(",") != -1){
			json.deleteCharAt(json.lastIndexOf(","));
		}
		//System.out.println("getAllChannels(): '" + json.toString() + "'");
		return json.toString();
	}
	
	private String getAllRoles(Request request, Response response){
		List<Role> roles = main.jda.getGuildById(request.params(":guildId")).getRoles();
		StringBuilder json = new StringBuilder("{\"roles\": [");
		for(Role r : roles){
			json.append("{\"name\": \"").append(r.getName()).append("\", \"id\": \"").append(r.getId()).append("\"}, ");
		}
		json.append("]}");
		if(json.lastIndexOf(",") != -1){
			json.deleteCharAt(json.lastIndexOf(","));
		}
		//System.out.println("getAllRoles(): '" + json.toString() + "'");
		return json.toString();
	}
	
	private String getSelfAssignableRoles(Request request, Response response){
		Set<String> roles = main.database.getSelfAssignableRoles(request.params(":guildId"));
		Guild guild = main.jda.getGuildById(request.params(":guildId"));
		StringBuilder json = new StringBuilder("{\"selfassignableroles\": [");
		if(!roles.isEmpty()){
			for(String r : roles){
				json.append("{\"name\": \"").append(guild.getRoleById(r).getName()).append("\", \"id\": \"").append(r).append("\"}, ");
			}
		}
		json.append("]}");
		if(json.lastIndexOf(",") != -1){
			json.deleteCharAt(json.lastIndexOf(","));
		}
		//System.out.println("getSelfAssignableRoles(): '" + json.toString() + "'");
		return json.toString();
	}
	
	private String removeSelfAssignableRole(Request request, Response response){
		main.database.removeSelfAssignableRoles(request.params(":guildId"), request.params(":value"));
		//System.out.println("removeSelfAssignableRole()");
		return "{\"status\": \"ok\" }";
	}
	
	private String addSelfAssignableRole(Request request, Response response){
		main.database.addSelfAssignableRoles(request.params(":guildId"), request.params(":value"));
		//System.out.println("addSelfAssignableRole()");
		return "{\"status\": \"ok\"}";
	}
	
	private String getWelcomeChannel(Request request, Response response){
		StringBuilder json = new StringBuilder("{");
		String channelId = main.database.getWelcomeChannelId(request.params(":guildId"));
		json.append("\"welcomechannel\": \"").append(channelId).append("\"").append("}");
		//System.out.println("getWelcomeChannel(): '" + json.toString() + "'");
		return json.toString();
	}
	
	private String setWelcomeChannel(Request request, Response response){
		main.database.setWelcomeChannelId(request.params(":guildId"), request.params(":value"));
		//System.out.println("setWelcomeChannel()");
		return "{\"status\": \"ok\"}";
	}
	
	private String getWelcomeMessage(Request request, Response response){
/*		StringBuilder json = new StringBuilder("{");
		json.append("\"welcomemessage\": \"").append(main.database.getWelcomeMessage(request.params(":guildId"))).append("\"");
		json.append("}");
		//System.out.println("getWelcomeMessage(): '" + json.toString() + "'"); */
		return main.database.getWelcomeMessage(request.params(":guildId"));
	}
	
	private String setWelcomeMessage(Request request, Response response){
		main.database.setWelcomeMessage(request.params(":guildId"), request.params(":value").trim());
		//System.out.println("setWelcomeMessage()");
		return "{\"status\": \"ok\"}";
	}
	
	private String getWelcomeMessageEnabled(Request request, Response response){
		//System.out.println("getWelcomeMessageEnabled(): '" + main.database.getWelcomeMessageEnabled(request.params(":guildId")) + "'");
		return "{\"welcomemessageenabled\": " + main.database.getWelcomeMessageEnabled(request.params(":guildId")) + "}";
	}
	
	private String setWelcomeMessageEnabled(Request request, Response response){
		main.database.setWelcomeMessageEnabled(request.params(":guildId"), Boolean.parseBoolean(request.params(":value")));
		//System.out.println("setWelcomeMessageEnabled()");
		return "{\"status\": \"ok\"}";
	}
	
	private String getNSFWEnabled(Request request, Response response){
		//System.out.println("getNSFWEnabled(): '" + main.database.getNSFWEnabled(request.params(":guildId")) + "'");
		return "{\"nsfwenabled\": " + main.database.getNSFWEnabled(request.params(":guildId")) + "}";
	}
	
	private String setNSFWEnabled(Request request, Response response){
		main.database.setNSFWEnabled(request.params(":guildId"), Boolean.parseBoolean(request.params(":value")));
		//System.out.println("setNSFWEnabled()");
		return "{\"status\": \"ok\"}";
	}
	
	private String getCommandPrefix(Request request, Response response){
		//System.out.println("getCommandPrefix(): '" + json.toString() + "'");
		return "{\"commandprefix\": \"" + main.database.getCommandPrefix(request.params(":guildId")) + "\"}";
	}
	
	private String setCommandPrefix(Request request, Response response){
		if(request.params(":value") == null){
			main.database.setCommandPrefix(request.params(":guildId"), ".");
		}
		else{
			main.database.setCommandPrefix(request.params(":guildId"), request.params(":value"));
		}
		//System.out.println("setCommandPrefix()");
		return "{\"status\": \"ok\"}";
	}
	
	public static class HtmlObject{
		
		private String body;
		private Map<String, String > map;
		
		public HtmlObject(String body, Map<String, String> map){
			this.body = body;
			this.map = map;
		}
		
		public HtmlObject(String body){
			this.body = body;
			this.map = new HashMap<>();
		}
		
		public String getBody(){
			for(Map.Entry<String, String> e : map.entrySet()){
				if(e.getKey() != null && e.getValue() != null){
					body = body.replaceAll(e.getKey(), e.getValue());
				}
			}
			return body;
		}
		
		public void addRegex(String regex, String value){
			regex = regex.toLowerCase();
			map.put(":" + regex + ":", value);
		}
		
	}
	
	private static class HtmlTemplateEngine extends TemplateEngine{
		
		@Override
		public String render(ModelAndView modelAndView){
			return ((HtmlObject)modelAndView.getModel()).getBody();
		}
		
	}
	
	public static String readFile(String path){
		StringBuilder text = new StringBuilder();
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(WebService.class.getResourceAsStream(path)));
			String line;
			while((line = br.readLine()) != null){
				text.append(line).append(System.lineSeparator());
			}
		}
		catch(IOException | NullPointerException e){
			Logger.error(e);
		}
		return text.toString();
	}
	
}
