package de.kittybot.kittybot.commands.dev;

import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.SubCommand;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import de.kittybot.kittybot.slashcommands.interaction.response.InteractionResponse;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class EvalCommand extends SubCommand{

	private final ScriptEngine scriptEngine;
	private final List<String> defaultImports;

	public EvalCommand(){
		super("eval", "Evals some code");
		addOptions(
			new CommandOptionString("code", "The code to execute").required()
		);
		devOnly();
		this.scriptEngine = new ScriptEngineManager().getEngineByName("groovy");
		this.defaultImports = Arrays.asList("net.dv8tion.jda.api.entities.impl", "net.dv8tion.jda.api.managers", "net.dv8tion.jda.api.entities", "net.dv8tion.jda.api",
			"java.io", "java.math", "java.util", "java.util.concurrent", "java.time", "java.util.stream"
		);
	}

	@Override
	public void run(Options options, CommandContext ctx){
		var code = options.getString("code");
		Object out;
		var color = Color.GREEN;
		var status = "Success";
		scriptEngine.put("ctx", ctx);
		scriptEngine.put("options", options);
		scriptEngine.put("scheduler", ctx.getModules().getScheduler());
		scriptEngine.put("jda", ctx.getJDA());
		scriptEngine.put("guild", ctx.getGuild());
		scriptEngine.put("channel", ctx.getChannel());
		scriptEngine.put("user", ctx.getUser());
		scriptEngine.put("member", ctx.getMember());

		var imports = new StringBuilder();
		defaultImports.forEach(imp -> imports.append("import ").append(imp).append(".*; "));
		long start = System.currentTimeMillis();
		try{
			out = scriptEngine.eval(imports + code);
		}
		catch(Exception e){
			out = e.getMessage();
			color = Color.RED;
			status = "Failed";
		}
		ctx.reply(new InteractionResponse.Builder()
			.addEmbeds(new EmbedBuilder()
				.setTitle("Eval")
				.setColor(color)
				.addField("Status:", status, true)
				.addField("Duration:", (System.currentTimeMillis() - start) + "ms", true)
				.addField("Code:", "```java\n" + code + "\n```", false)
				.addField("Result:", out == null ? "" : out.toString(), false)
				.build()
			).build()
		);
	}

}