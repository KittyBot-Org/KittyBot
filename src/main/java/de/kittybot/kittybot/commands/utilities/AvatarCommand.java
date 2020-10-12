package de.kittybot.kittybot.commands.utilities;

import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;
import de.kittybot.kittybot.utils.MessageUtils;
import de.kittybot.kittybot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.MiscUtil;


public class AvatarCommand extends ACommand{

	public static final String COMMAND = "avatar";
	public static final String USAGE = "avatar <@User, UserId, ...>";
	public static final String DESCRIPTION = "Gives some quicklinks to the users avatars";
	protected static final String[] ALIASES = {"ava"};
	protected static final Category CATEGORY = Category.UTILITIES;
	private static final String[] sizes = {"128", "256", "512", "1024"};

	public AvatarCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		var users = ctx.getMentionedUsers();
		for(var arg : ctx.getArgs()){
			try{
				var user = ctx.getJDA().getUserById(MiscUtil.parseSnowflake(arg));
				if(user != null && !users.contains(user)){
					users.add(user);
				}
			}
			catch(NumberFormatException ignore){
			}
		}
		if(users.isEmpty()){
			users.add(ctx.getUser());
		}
		var stringBuilder = new StringBuilder();
		for(var user : users){
			stringBuilder.append(user.getAsTag()).append(": ");
			for(var size : sizes){
				stringBuilder.append(MessageUtils.maskLink(size + "px", user.getAvatarUrl() + "?size=" + size)).append(" ");
			}
			stringBuilder.append("\n\n");
		}
		ACommand.sendAnswer(ctx, new EmbedBuilder().setTitle(Utils.pluralize("User Avatar", users.size())).setDescription(stringBuilder.toString()));
	}

}
