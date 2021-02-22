package de.kittybot.kittybot.web.webhooks.votes;

import de.kittybot.kittybot.modules.VoteModule;
import de.kittybot.kittybot.objects.enums.BotList;
import de.kittybot.kittybot.objects.module.Modules;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.UnauthorizedResponse;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostVotesRoute implements Handler{

	private static final Logger LOG = LoggerFactory.getLogger(PostVotesRoute.class);

	private final Modules modules;

	public PostVotesRoute(Modules modules){
		this.modules = modules;
	}

	@Override
	public void handle(@NotNull Context ctx){
		LOG.info("Received webhook on: " + ctx.url());
		var rawBotlist = ctx.pathParam("botlist");
		if(rawBotlist.isBlank()){
			return;
		}

		var json = DataObject.fromJson(ctx.bodyAsBytes());
		BotList botlist;
		long userId;
		var multiplier = 1;
		switch(rawBotlist){
			case "top_gg":
				botlist = BotList.TOP_GG;
				userId = json.getLong("user");
				multiplier = json.getBoolean("isWeekend") ? 2 : 1;
				break;
			case "botlist_space":
				botlist = BotList.BOTLIST_SPACE;
				userId = json.getObject("user").getLong("id");
				break;
			case "bots_for_discord_com":
				botlist = BotList.BOTS_FOR_DISCORD_COM;
				userId = json.getLong("user");
				break;
			case "discord_boats":
				botlist = BotList.DISCORD_BOATS;
				userId = json.getObject("user").getLong("id");
				break;
			case "discord_bot_list_com":
				botlist = BotList.DISCORD_BOT_LIST_COM;
				userId = json.getLong("id");
				break;
			default:
				botlist = null;
				userId = -1L;
		}
		if(botlist == null){
			throw new NotFoundResponse("botlist not found");
		}
		if(!botlist.verify(ctx)){
			LOG.error("Unauthorized webhook url received on: " + ctx.url());
			throw new UnauthorizedResponse("You are not authorized to use this endpoint");
		}
		this.modules.get(VoteModule.class).addVote(userId, botlist, multiplier);
	}


}
