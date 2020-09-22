package de.anteiku.kittybot.objects.command;

import net.dv8tion.jda.api.Permission;

public abstract class Command
{
    private final String invoke;
    private final String[] aliases;
    private final String description;
    private final String usage;
    private final Category category;
    private final Permission requiredPermission;
    private final int maxArgs;
    private final int cooldown;

    public Command(final String invoke, final String description, final String usage, final Category category)
    {
        this(invoke, new String[]{}, description, usage, category);
    }

    public Command(final String invoke, final String description, final String usage, final Category category, final int cooldown)
    {
        this(invoke, new String[]{}, description, usage, category, cooldown);
    }

    public Command(final String invoke, final String[] aliases, final String description, final String usage, final Category category)
    {
        this(invoke, aliases, description, usage, category, Permission.UNKNOWN, 0, 0);
    }

    public Command(final String invoke, final String[] aliases, final String description, final String usage, final Category category, final int cooldown)
    {
        this(invoke, aliases, description, usage, category, Permission.UNKNOWN, 0, cooldown);
    }

    public Command(final String invoke, final String[] aliases, final String description, final String usage, final Category category, final Permission requiredPermission, final int maxArgs, final int cooldown)
    {
        this.invoke = invoke;
        this.aliases = aliases;
        this.description = description;
        this.usage = usage;
        this.category = category;
        this.requiredPermission = requiredPermission;
        this.maxArgs = maxArgs;
        this.cooldown = cooldown;
    }

    public abstract void execute(final CommandContext ctx);

    public String getInvoke()
    {
        return invoke;
    }

    public String[] getAliases()
    {
        return aliases;
    }

    public String getDescription()
    {
        return description;
    }

    public String getUsage()
    {
        return usage;
    }

    public Category getCategory()
    {
        return category;
    }

    public Permission getRequiredPermission()
    {
        return requiredPermission;
    }

    public int getMaxArgs()
    {
        return maxArgs;
    }

    public int getCooldown()
    {
        return cooldown;
    }
}