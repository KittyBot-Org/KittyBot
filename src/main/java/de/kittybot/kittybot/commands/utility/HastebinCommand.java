package de.kittybot.kittybot.commands.utility;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;
import net.dv8tion.jda.api.entities.Message;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static de.kittybot.kittybot.utils.MessageUtils.maskLink;

public class HastebinCommand extends Command{

	public HastebinCommand(){
		super("hastebin", "Creates a hastebin from the newly provided or last file in this channel", Category.UTILITIES);
		setUsage("<file>");
	}

	@Override
	public void run(Args args, CommandContext ctx){
		var attachments = ctx.getMessage().getAttachments();
		if(attachments.isEmpty()){
			ctx.getChannel().getIterableHistory().takeAsync(25).thenAcceptAsync(messages -> {
				var message = messages.stream().filter(msg -> !msg.getAttachments().isEmpty() && msg.getAttachments().stream().anyMatch(attachment -> !attachment.isImage() && !attachment.isVideo())).findFirst();
				if(message.isEmpty()){
					ctx.sendError("No file found to post to hastebin");
					return;
				}
				postAttachment(ctx, message.get().getAttachments());
			});
			return;
		}
		postAttachment(ctx, attachments);
	}

	private void postAttachment(CommandContext ctx, List<Message.Attachment> attachments){
		attachments.stream().filter(attachment -> !attachment.isImage() && !attachment.isVideo()).forEach(attachment -> {
			attachment.retrieveInputStream().thenAcceptAsync(inputStream -> {
				try(inputStream){
					var text = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
					ctx.sendSuccess(maskLink("here is a hastebin", ctx.getMain().getRequestManager().postToHastebin(text)));
				}
				catch(NullPointerException | IOException e){
					ctx.sendError("Error while creating hastebin\nError: " + e.getMessage());
				}
			});
		});
	}

}
