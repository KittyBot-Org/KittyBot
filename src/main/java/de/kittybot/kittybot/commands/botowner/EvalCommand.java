package de.kittybot.kittybot.commands.botowner;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;
import de.kittybot.kittybot.main.KittyBot;
import net.dv8tion.jda.api.EmbedBuilder;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class EvalCommand extends Command{

	private final KittyBot main;
	private final ScriptEngine scriptEngine;
	private final List<String> defaultImports;

	public EvalCommand(KittyBot main){
		super("eval", "Evals some Java Code", Category.BOT_OWNER);
		addAliases("e");
		setUsage("<code>");
		setBotOwnerOnly();
		this.main = main;
		this.scriptEngine = new ScriptEngineManager().getEngineByName("groovy");
		this.defaultImports = Arrays.asList("net.dv8tion.jda.api.entities.impl", "net.dv8tion.jda.api.managers", "net.dv8tion.jda.api.entities", "net.dv8tion.jda.api",
				"java.io", "java.math", "java.util", "java.util.concurrent", "java.time", "java.util.stream"
		);
	}

	@Override
	public void run(List<String> args, CommandContext ctx){
		Object out;
		var color = Color.GREEN;
		var status = "Success";
		scriptEngine.put("ctx", ctx);
		scriptEngine.put("message", ctx.getMessage());
		scriptEngine.put("channel", ctx.getChannel());
		scriptEngine.put("args", ctx.getArgs());
		scriptEngine.put("scheduler", this.main.getScheduler());
		scriptEngine.put("api", ctx.getJDA());
		scriptEngine.put("jda", ctx.getJDA());
		scriptEngine.put("guild", ctx.getGuild());
		scriptEngine.put("member", ctx.getMember());

		var imports = new StringBuilder();
		defaultImports.forEach(imp -> imports.append("import ").append(imp).append(".*; "));
		var code = String.join(" ", ctx.getArgs());
		long start = System.currentTimeMillis();
		try{
			out = scriptEngine.eval(imports + code);
		}
		catch(Exception e){
			out = e.getMessage();
			color = Color.RED;
			status = "Failed";
		}
		ctx.sendAnswer(new EmbedBuilder().setTitle("Eval")
				.setColor(color)
				.addField("Status:", status, true)
				.addField("Duration:", (System.currentTimeMillis() - start) + "ms", true)
				.addField("Code:", "```java\n" + code + "\n```", false)
				.addField("Result:", out == null ? "" : out.toString(), false)
		);
	}

}
