package de.kittybot.kittybot.commands.botowner;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.context.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class EvalCommand extends Command{

	private final ScriptEngine scriptEngine;
	private final List<String> defaultImports;

	public EvalCommand(){
		super("eval", "Evals some Java Code", Category.BOT_OWNER);
		addAliases("e");
		setUsage("<code>");
		setBotOwnerOnly();
		this.scriptEngine = new ScriptEngineManager().getEngineByName("groovy");
		this.defaultImports = Arrays.asList("net.dv8tion.jda.api.entities.impl", "net.dv8tion.jda.api.managers", "net.dv8tion.jda.api.entities", "net.dv8tion.jda.api",
				"java.io", "java.math", "java.util", "java.util.concurrent", "java.time", "java.util.stream"
		);
	}

	@Override
	public void run(Args args, CommandContext ctx){
		Object out;
		var color = Color.GREEN;
		var status = "Success";
		scriptEngine.put("ctx", ctx);
		scriptEngine.put("message", ctx.getMessage());
		scriptEngine.put("channel", ctx.getChannel());
		scriptEngine.put("args", ctx.getArgs());
		scriptEngine.put("scheduler", ctx.getModules().getScheduler());
		scriptEngine.put("api", ctx.getJDA());
		scriptEngine.put("jda", ctx.getJDA());
		scriptEngine.put("guild", ctx.getGuild());
		scriptEngine.put("member", ctx.getMember());

		var imports = new StringBuilder();
		defaultImports.forEach(imp -> imports.append("import ").append(imp).append(".*; "));
		var code = ctx.getRawMessage();
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
