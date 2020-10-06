package de.kittybot.kittybot.commands.admin;

import de.kittybot.kittybot.KittyBot;
import de.kittybot.kittybot.objects.Config;
import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class EvalCommand extends ACommand{

	public static final String COMMAND = "eval";
	public static final String USAGE = "eval <code>";
	public static final String DESCRIPTION = "Evals some Java Code";
	protected static final String[] ALIASES = {};
	protected static final Category CATEGORY = Category.ADMIN;
	private static final ScriptEngine SCRIPT_ENGINE = new ScriptEngineManager().getEngineByName("groovy");
	private static final Set<String> DEFAULT_IMPORTS = Set.of("net.dv8tion.jda.api.entities.impl", "net.dv8tion.jda.api.managers", "net.dv8tion.jda.api.entities", "net.dv8tion.jda.api", "java.lang",
			"java.io", "java.math", "java.util", "java.util.concurrent", "java.time");

	public EvalCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		if(!Config.ADMIN_IDS.contains(ctx.getUser().getId())){
			sendNoPermission(ctx);
			return;
		}
		try{
			SCRIPT_ENGINE.put("ctx", ctx);
			SCRIPT_ENGINE.put("message", ctx.getMessage());
			SCRIPT_ENGINE.put("channel", ctx.getChannel());
			SCRIPT_ENGINE.put("args", ctx.getArgs());
			SCRIPT_ENGINE.put("scheduler", KittyBot.getScheduler());
			SCRIPT_ENGINE.put("api", ctx.getJDA());
			SCRIPT_ENGINE.put("jda", ctx.getJDA());
			SCRIPT_ENGINE.put("guild", ctx.getGuild());
			SCRIPT_ENGINE.put("member", ctx.getMember());

			var sb = new StringBuilder();
			DEFAULT_IMPORTS.forEach(imp -> sb.append("import ").append(imp).append(".*; "));
			sb.append(String.join(" ", ctx.getArgs()));
			var out = SCRIPT_ENGINE.eval(sb.toString());
			sendAnswer(ctx, out == null ? "Executed without error." : out.toString());
		}
		catch(Exception e){
			sendError(ctx, e.getMessage());
		}
	}

}
