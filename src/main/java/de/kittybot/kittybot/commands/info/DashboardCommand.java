package de.kittybot.kittybot.commands.info;

import de.kittybot.kittybot.objects.Config;
import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;
import de.kittybot.kittybot.utils.MessageUtils;

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
		this.sendAnswer(ctx, "You can find our dashboard " + MessageUtils.maskLink("here", Config.ORIGIN_URL));
	}

}
