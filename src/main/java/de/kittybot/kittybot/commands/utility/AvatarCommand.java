package de.kittybot.kittybot.commands.utility;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.CommandContext;
import de.kittybot.kittybot.utils.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Arrays;
import java.util.stream.Collectors;

public class AvatarCommand extends Command{

	private static final String[] SIZES = {"128", "256", "512", "1024"};

	public AvatarCommand(){
		super("avatar", "Gives avatar links from provided users", Category.UTILITIES);
		setUsage("<@User, UserId, ...>");
		addAliases("ava");
	}

	@Override
	public void run(Args args, CommandContext ctx){
		ctx.collectMentionedUsers(users -> {
					if(users.isEmpty()){
						ctx.sendError("No users found");
						return;
					}
					ctx.sendSuccess(new EmbedBuilder()
							.setTitle(MessageUtils.pluralize("User Avatar", users))
							.setDescription(users.stream().map(
									user -> "**" + user.getAsTag() + "**: " + Arrays.stream(SIZES).map(
											size -> MessageUtils.maskLink(size + "px", user.getEffectiveAvatarUrl() + "?size=" + size)).collect(Collectors.joining(" "))
									).collect(Collectors.joining("\n\n"))
							)
					);
				},
				error -> ctx.sendError("Error while retrieving users:\n" + error.getMessage()));
	}

}
