package de.kittybot.kittybot.web.webhooks.twitch;

import de.kittybot.kittybot.modules.StreamModule;
import de.kittybot.kittybot.objects.enums.AnnouncementType;
import de.kittybot.kittybot.objects.module.Modules;
import de.kittybot.kittybot.objects.streams.StreamType;
import de.kittybot.kittybot.objects.streams.twitch.Subscription;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostTwitchRoute implements Handler{

	private static final Logger LOG = LoggerFactory.getLogger(PostTwitchRoute.class);

	private final Modules modules;

	public PostTwitchRoute(Modules modules){
		this.modules = modules;
	}

	@Override
	public void handle(@NotNull Context ctx){
		// var signature = ctx.header("Twitch-Eventsub-Message-Signature");
		// TODO verify requests
		var data = DataObject.fromJson(ctx.bodyAsInputStream());
		System.out.println(data.toString());
		var subscription = Subscription.fromJSON(data.getObject("subscription"));
		System.out.println("subscription: " + subscription.toString());
		if(data.hasKey("challenge")){
			ctx.status(200);
			ctx.result(data.getString("challenge"));
			this.modules.get(StreamModule.class).getTwitchWrapper().getSubscriptions().get(subscription.getId()).setStatus(Subscription.Status.ENABLED);
		}
		else if(subscription.getType() == Subscription.Type.STREAM_ONLINE){
			var event = data.getObject("event");
			var streamModule = this.modules.get(StreamModule.class);
			var stream = streamModule.getTwitchWrapper().getStream(event.getLong("broadcaster_user_id"), true);
			var streamAnnouncements = streamModule.getStreamAnnouncements(event.getLong("broadcaster_user_id"), StreamType.TWITCH);
			streamModule.sendAnnouncementMessage(streamAnnouncements, stream, AnnouncementType.START);
		}
		else if(subscription.getType() == Subscription.Type.STREAM_OFFLINE){
			// TODO
		}
		else{
			LOG.error("unhandled event type received from twitch Body: {}", ctx.body());
		}
	}

}
