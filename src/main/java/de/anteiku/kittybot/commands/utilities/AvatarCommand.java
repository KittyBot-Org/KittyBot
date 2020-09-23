package de.anteiku.kittybot.commands.utilities;

import de.anteiku.kittybot.objects.command.ACommand;
import de.anteiku.kittybot.objects.command.Category;
import de.anteiku.kittybot.objects.command.CommandContext;
import de.anteiku.kittybot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.MiscUtil;

import static de.anteiku.kittybot.utils.MessageUtils.maskLink;


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
				if(!users.contains(user)){
					users.add(user);
				}
			}
			catch(NumberFormatException ignore){
			}
		}
		if(users.isEmpty()){
			sendError(ctx, "Please mention a user or provide a valid id");
		}
		var stringBuilder = new StringBuilder();
		for(var user : users){
			stringBuilder.append("(").append(user.getAsTag()).append(")").append(": ");
			for(var size : sizes){
				stringBuilder.append(maskLink(size + "px", user.getAvatarUrl() + "?size=" + size)).append(" ");
			}
			stringBuilder.append("\n\n");
		}
		sendAnswer(ctx, new EmbedBuilder().setTitle(Utils.pluralize("User Avatar", users.size())).setDescription(stringBuilder.toString()));
	}

}
