package de.kittybot.kittybot.web.webhooks.votes;

import de.kittybot.kittybot.objects.module.Modules;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;

public class PostVotesBotsForDiscordComRoute implements Handler{

	private final Modules modules;

	public PostVotesBotsForDiscordComRoute(Modules modules){
		this.modules = modules;
	}

	@Override
	public void handle(@NotNull Context ctx){
		var json = DataObject.fromJson(ctx.bodyAsBytes());
		// every 24h?
		/*
		{
		    "user": "000000000000000000",
		    "bot": "123456789012345678",
		    "votes": {
		        "totalVotes": 5,
		        "votes24": 5,
		        "votesMonth": 3,
		        // only visible if webhook secret key is not NULL or EMPTY
		        "hasVoted": [
		            "000000000000000000",
		            "111111111111111111",
		            "222222222222222222"
		        ],
		        // only visible if webhook secret key is not NULL or EMPTY
		        "hasVoted24": [
		            "000000000000000000",
		            "111111111111111111",
		            "222222222222222222",
		            "333333333333333333",
		            "444444444444444444"
		        ]
		    },
		    // will be "test" when using testing button
		    // you can test on your bots edit page
		    "type": "vote"
		}
		*/
	}

}
