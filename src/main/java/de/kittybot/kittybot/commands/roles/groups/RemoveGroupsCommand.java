package de.kittybot.kittybot.commands.roles.groups;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.context.CommandContext;
import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.objects.SelfAssignableRoleGroup;
import de.kittybot.kittybot.utils.MessageUtils;

import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class RemoveGroupsCommand extends Command{

	public RemoveGroupsCommand(Command parent){
		super(parent, "remove", "Used to remove a self-assignable role group", Category.ROLES);
		setUsage("<group-name>");
		addAliases("delete", "del");
	}

	@Override
	public void run(Args args, CommandContext ctx){
		if(args.isEmpty()){
			ctx.sendUsage(this);
			return;
		}
		var settings = ctx.get(SettingsModule.class);
		var names = args.subList(1, args.size());
		var groups = settings.getSelfAssignableRoleGroups(ctx.getGuildId()).stream()
				.filter(group -> names.stream().anyMatch(n -> group.getName().equalsIgnoreCase(n)))
				.collect(Collectors.toSet());
		settings.removeSelfAssignableRoleGroups(ctx.getGuildId(), groups.stream().map(SelfAssignableRoleGroup::getId).collect(Collectors.toSet()));

		ctx.sendAnswer(MessageUtils.pluralize("Removed group", groups) + " " + groups.stream().map(SelfAssignableRoleGroup::getName).collect(Collectors.joining(", ")));
	}

}
