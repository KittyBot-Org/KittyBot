package de.anteiku.kittybot.commands.commands;

import com.google.gson.JsonParser;
import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import de.anteiku.kittybot.commands.CommandContext;
import net.dv8tion.jda.api.entities.Message;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class HastebinCommand extends ACommand{

	public static String COMMAND = "hastebin";
	public static String USAGE = "hastebin <file>";
	public static String DESCRIPTION = "creates a hastebin from the file";
	protected static String[] ALIAS = {};

	public HastebinCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
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
						Request request = new Request.Builder().url("https://hastebin.com/documents").method("POST", body).build();
						String result = main.httpClient.newCall(request).execute().body().string();
						sendAnswer(ctx, "[here](https://hastebin.com/" + JsonParser.parseString(result).getAsJsonObject().get("key").getAsString() + ") is a hastebin.com");
					}
					catch(IOException e){
						e.printStackTrace();
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
