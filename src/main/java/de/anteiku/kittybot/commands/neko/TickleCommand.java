package de.anteiku.kittybot.commands.neko;

import de.anteiku.kittybot.objects.command.ACommand;
import de.anteiku.kittybot.objects.command.Category;
import de.anteiku.kittybot.objects.command.CommandContext;

public class TickleCommand extends ACommand {

    public static final String COMMAND = "tickle";
    public static final String USAGE = "tickle <@user, ...>";
    public static final String DESCRIPTION = "Tickles a user";
    protected static final String[] ALIASES = {"kitzel"};
    protected static final Category CATEGORY = Category.NEKO;

    public TickleCommand() {
        super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
    }

    @Override
    public void run(CommandContext ctx) {
        if (ctx.getArgs().length == 0) {
            sendUsage(ctx);
            return;
        }
        sendReactionImage(ctx, "tickle", "tickles");
    }

}
