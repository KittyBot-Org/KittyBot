package de.kittybot.kittybot.commands.utility;

import de.kittybot.kittybot.modules.RequestModule;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.RunCommand;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionUrl;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Interaction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.Config;
import net.dv8tion.jda.api.Permission;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static de.kittybot.kittybot.utils.MessageUtils.maskLink;

@SuppressWarnings("unused")
public class HastebinCommand extends RunCommand{

	public HastebinCommand(){
		super("hastebin", "Creates a hastebin from the latest attachment in this channel or a provided url", Category.UTILITIES);
		addOptions(
			new CommandOptionUrl("url", "The url to create a hastebin from")
		);
	}

	@Override
	public void run(Options options, Interaction ia){
		if(ia instanceof GuildInteraction &&  !((GuildInteraction) ia).getSelfMember().hasPermission(((GuildInteraction) ia).getChannel(), Permission.VIEW_CHANNEL, Permission.MESSAGE_HISTORY)){
			ia.error("Please make sure I have following permissions: `VIEW_CHANNEL`, `MESSAGE_HISTORY`");
			return;
		}
		if(Config.HASTEBIN_URL.isBlank()){
			ia.error("No hastebin url configured");
			return;
		}
		if(options.has("url")){
			ia.get(RequestModule.class).retrieveUrlContent(options.getString("url"),
				(call, response) -> {
					var body = response.body();
					if(body == null){
						ia.error("Provided link has no content");
						return;
					}
					try{
						ia.get(RequestModule.class).postToHastebin(body.string(), key -> {
							if(key == null){
								ia.error("Unexpected error while creating hastebin");
								return;
							}
							ia.reply(maskLink("here is a hastebin", Config.HASTEBIN_URL + "/" + key));
						});
					}
					catch(IOException e){
						ia.error("Error while getting body data from link");
					}
				}
				, (call, response) -> ia.error("Error while retrieving data from link"));
			return;
		}
		ia.getChannel().getIterableHistory().takeAsync(25).thenAcceptAsync(messages -> {
			var message = messages.stream().filter(msg -> !msg.getAttachments().isEmpty() && msg.getAttachments().stream().anyMatch(attachment -> !attachment.isImage() && !attachment.isVideo())).findFirst();
			if(message.isEmpty()){
				ia.error("No file found to in recent 25 messages");
				return;
			}
			message.get().getAttachments().forEach(attachment ->
				attachment.retrieveInputStream().thenAcceptAsync(inputStream -> {
					try(inputStream){
						var text = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
						ia.get(RequestModule.class).postToHastebin(text, key -> {
							if(key == null){
								ia.error("Unexpected error while creating hastebin");
								return;
							}
							ia.reply(maskLink("here is a hastebin", Config.HASTEBIN_URL + "/" + key));
						});
					}
					catch(IOException e){
						ia.error("Error while creating hastebin\nError: " + e.getMessage());
					}
				})
			);
		});
	}

}
