package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.objects.command.Category;
import de.anteiku.kittybot.objects.command.Command;
import de.anteiku.kittybot.objects.command.CommandContext;

public class TestCommand extends Command
{
    public TestCommand()
    {
        super("test", "lmao", "test", Category.ADMIN);
    }

    @Override
    public void execute(final CommandContext ctx)
    {
        ctx.reply("lmao");
    }
}