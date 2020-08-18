package de.anteiku.kittybot.commands.utilities;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.objects.Config;
import de.anteiku.kittybot.objects.command.ACommand;
import de.anteiku.kittybot.objects.command.Category;
import de.anteiku.kittybot.objects.command.CommandContext;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.data.DataObject;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class HastebinCommand extends ACommand{

	public static final String COMMAND = "hastebin";
	public static final String USAGE = "hastebin <file>";
	public static final String DESCRIPTION = "creates a " + Config.HASTEBIN_URL + " from the file";
	protected static final String[] ALIASES = {};
	protected static final Category CATEGORY = Category.UTILITIES;

	public HastebinCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		List<Message.Attachment> attachments = ctx.getMessage().getAttachments();
		if(!attachments.isEmpty()){
			for(Message.Attachment attachment : attachments){
				if(!attachment.isImage() && !attachment.isVideo()){
					try{
						String text = IOUtils.toString(attachment.retrieveInputStream().get(), StandardCharsets.UTF_8.name());
						RequestBody body = RequestBody.create(MediaType.parse("text/html; charset=utf-8"), text);
						Request request = new Request.Builder().url(Config.HASTEBIN_URL + "/documents").method("POST", body).build();
						var response = KittyBot.getHttpClient().newCall(request).execute().body();
						if(response == null){
							sendError(ctx, "Error while trying to create hastebin");
							response.close();
							return;
						}
						sendAnswer(ctx, "[here](" + Config.HASTEBIN_URL + "/" + DataObject.fromJson(response.string()).getString("key") + ") is a hastebin");
						response.close();
					}
					catch(IOException e){
						LOG.error("Error while creating hastebin", e);
					}
					catch(InterruptedException e){
						LOG.error("File download got interrupted ", e);
					}
					catch(ExecutionException e){
						LOG.error("Error while getting file from Discord", e);
					}
				}
			}
		}
		else{
			sendError(ctx, "Please provide a file");
		}
	}

}
