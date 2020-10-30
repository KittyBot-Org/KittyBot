package de.kittybot.kittybot.commands.admin;

import de.kittybot.kittybot.KittyBot;
import de.kittybot.kittybot.objects.Config;
import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Arrays;
import java.util.List;

public class EvalCommand extends ACommand{

	public static final String COMMAND = "eval";
	public static final String USAGE = "eval <code>";
	public static final String DESCRIPTION = "Evals some Java Code";
	protected static final String[] ALIASES = {"e"};
	protected static final Category CATEGORY = Category.ADMIN;
	private static final ScriptEngine SCRIPT_ENGINE = new ScriptEngineManager().getEngineByName("groovy");
	private static final List<String> DEFAULT_IMPORTS = Arrays.asList("net.dv8tion.jda.api.entities.impl", "net.dv8tion.jda.api.managers",
		"net.dv8tion.jda.api.entities", "net.dv8tion.jda.api", "java.lang",
		"java.io", "java.math", "java.util", "java.util.concurrent", "java.time", "java.util.stream"
	);

	public EvalCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		if(!Config.ADMIN_IDS.contains(ctx.getUser().getId())){
			sendNoPermission(ctx);
			return;
		}
		Object out;
		var status = "Success";
		SCRIPT_ENGINE.put("ctx", ctx);
		SCRIPT_ENGINE.put("message", ctx.getMessage());
		SCRIPT_ENGINE.put("channel", ctx.getChannel());
		SCRIPT_ENGINE.put("args", ctx.getArgs());
		SCRIPT_ENGINE.put("scheduler", KittyBot.getScheduler());
		SCRIPT_ENGINE.put("api", ctx.getJDA());
		SCRIPT_ENGINE.put("jda", ctx.getJDA());
		SCRIPT_ENGINE.put("guild", ctx.getGuild());
		SCRIPT_ENGINE.put("member", ctx.getMember());

		var imports = new StringBuilder();
		DEFAULT_IMPORTS.forEach(imp -> imports.append("import ").append(imp).append(".*; "));
		var code = String.join(" ", ctx.getArgs());
		long start = System.currentTimeMillis();
		try{
			out = SCRIPT_ENGINE.eval(imports.toString() + code);
		}
		catch(Exception e){
			out = e.getMessage();
			status = "Failed";
		}
		sendAnswer(ctx, new EmbedBuilder().setTitle("Eval")
				.addField("Status:", status, true)
				.addField("Duration:", (System.currentTimeMillis() - start) + "ms", true)
				.addField("Code:", "```java\n" + code + "\n```", false)
				.addField("Result:", out == null ? "" : out.toString(), false)
		);
	}

}
