package de.kittybot.kittybot.command;

import de.kittybot.kittybot.exceptions.CommandException;
import de.kittybot.kittybot.objects.Emoji;
import de.kittybot.kittybot.utils.Config;
import net.dv8tion.jda.api.Permission;

import java.util.*;
import java.util.stream.Collectors;

public abstract class Command{

	private final Command parent;
	private final String command;
	private final Set<String> aliases;
	private final Set<Permission> permissions;
	private final List<Command> children;
	private final String description;
	private final Category category;
	private String usage;
	private boolean botOwnerOnly;

	protected Command(String command, String description, Category category){
		this(null, command, description, category);
	}

	protected Command(Command parent, String command, String description, Category category){
		this.parent = parent;
		this.command = command;
		this.description = description;
		this.usage = "";
		this.category = category;
		this.children = new ArrayList<>();
		this.aliases = new HashSet<>();
		this.botOwnerOnly = false;
		this.permissions = new HashSet<>();
	}

	protected abstract void run(Args args, CommandContext ctx) throws CommandException;

	public void onReactionAdd(ReactionContext ctx){
		var event = ctx.getEvent();
		var reactiveMessage = ctx.getReactiveMessage();
		if(event.getReactionEmote().getName().equals(Emoji.WASTEBASKET.getAsMention()) && (event.getUserIdLong() == reactiveMessage.getUserId() || event.getMember().hasPermission(Permission.MESSAGE_MANAGE))){
			event.getChannel().deleteMessageById(event.getMessageId()).queue();
			event.getChannel().deleteMessageById(reactiveMessage.getMessageId()).queue();
			ctx.getReactiveMessageManager().removeReactiveMessage(event.getMessageIdLong());
		}
	}

	public void process(CommandContext ctx){
		if(isBotOwnerOnly() && !Config.OWNER_IDS.contains(ctx.getMember().getIdLong())){
			ctx.sendNoAdminPermissions();
			return;
		}
		var missingPerms = getMissingPermissions(ctx);
		if(!missingPerms.isEmpty()){
			ctx.sendNoPermissions(missingPerms);
			return;
		}
		try{
			if(this.children.isEmpty() || ctx.getArgs().isEmpty()){
				run(ctx.getArgs(), ctx);
				return;
			}
			var newCtx = ctx.getChildContext(this.getPath());
			for(var child : this.children){
				if(child.check(newCtx.getCommand())){
					child.process(newCtx);
					return;
				}
			}
			run(ctx.getArgs(), ctx);
		}
		catch(CommandException e){
			ctx.sendError(e.getMessage());
		}
	}

	public boolean check(String command){
		return this.command.equalsIgnoreCase(command) || this.aliases.stream().anyMatch(command::equalsIgnoreCase);
	}

	private Set<Permission> getMissingPermissions(CommandContext ctx){
		return this.permissions.stream().dropWhile(ctx.getMember().getPermissions()::contains).collect(Collectors.toSet());
	}

	protected void addChildren(Command... cmds){
		this.children.addAll(Arrays.asList(cmds));
	}

	protected void addAliases(String... aliases){
		this.aliases.addAll(Arrays.asList(aliases));
	}

	protected void addPermissions(Permission... permissions){
		this.permissions.addAll(Arrays.asList(permissions));
	}

	protected void setBotOwnerOnly(){
		this.botOwnerOnly = true;
	}

	public Command getParent(){
		return this.parent;
	}

	public String getUsage(){
		if(this.parent == null){
			if(this.children.isEmpty()){
				return this.command + " " + this.usage;
			}
			return this.command;
		}
		return this.parent.getUsage() + " " + this.command + " " + this.usage;
	}

	protected void setUsage(String usage){
		this.usage = usage;
	}

	public String getPath(){
		if(this.parent == null){
			return this.command;
		}
		return this.parent.getPath() + " " + this.command;
	}

	public String getCommand(){
		return this.command;
	}

	public Set<String> getAliases(){
		return this.aliases;
	}

	public List<Command> getChildren(){
		return this.children;
	}

	public String getDescription(){
		return this.description;
	}

	public String getRawUsage(){
		return this.usage;
	}

	public Category getCategory(){
		return this.category;
	}

	public boolean isBotOwnerOnly(){
		return this.botOwnerOnly;
	}

}
