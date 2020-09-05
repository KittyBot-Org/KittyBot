package de.anteiku.kittybot.commands.info;

import de.anteiku.kittybot.objects.Config;
import de.anteiku.kittybot.objects.command.ACommand;
import de.anteiku.kittybot.objects.command.Category;
import de.anteiku.kittybot.objects.command.CommandContext;
import de.anteiku.kittybot.utils.MessageUtils;

public class DashboardCommand extends ACommand{

	public static final String COMMAND = "dashboard";
	public static final String USAGE = "dashboard";
	public static final String DESCRIPTION = "Shows you our dashboard";
	protected static final String[] ALIASES = {};
	protected static final Category CATEGORY = Category.INFORMATIVE;

	public DashboardCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		sendAnswer(ctx, "You can find our dashbaord " + MessageUtils.maskLink("here", Config.ORIGIN_URL));
	}

}
