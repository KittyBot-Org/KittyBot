package de.anteiku.kittybot.commands.utilities;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.command.ACommand;
import de.anteiku.kittybot.command.Category;
import de.anteiku.kittybot.command.CommandContext;
import de.anteiku.kittybot.objects.Config;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.data.DataObject;
import okhttp3.*;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

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
						Request request = new Request.Builder().url(Config.HASTEBIN_URL + "/documents").post(RequestBody.create(MediaType.parse("text/html; charset=utf-8"), text)).build();
						KittyBot.getHttpClient().newCall(request).enqueue(new Callback(){
							@Override
							public void onFailure(@NotNull Call call, @NotNull IOException e){
								LOG.error("Error while creating hastebin", e);
								sendError(ctx, "Error while creating hastebin");
							}

							@Override
							public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException{
								try(var body = response.body()){
									if(body == null){
										sendError(ctx, "Error while creating hastebin");
										return;
									}
									sendAnswer(ctx, "[here](" + Config.HASTEBIN_URL + "/" + DataObject.fromJson(body.string()).getString("key") + ") is a hastebin");
								}
							}
						});
					}
					catch(IOException | InterruptedException | ExecutionException e){
						LOG.error("Error while creating hastebin", e);
						sendError(ctx, "Error while creating hastebin");
					}
				}
			}
		}
		else{
			sendError(ctx, "Please provide a file");
		}
	}

}
