package de.kittybot.kittybot.commands.dev;

import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.RunCommand;
import de.kittybot.kittybot.slashcommands.CommandContext;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class EvalCommand extends RunCommand{

	private final ScriptEngine scriptEngine;
	private final List<String> defaultImports;

	public EvalCommand(){
		super("eval", "Evals some code", Category.DEV);
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
		this.scriptEngine.put("ctx", ctx);
		this.scriptEngine.put("options", options);
		this.scriptEngine.put("jda", ctx.getJDA());

		this.scriptEngine.put("channel", ctx.getChannel());
		this.scriptEngine.put("user", ctx.getUser());
		if(ctx instanceof GuildCommandContext){
			var guildCtx = (GuildCommandContext) ctx;
			this.scriptEngine.put("guild", guildCtx.getGuild());
			this.scriptEngine.put("member", guildCtx.getMember());
		}

		var imports = new StringBuilder();
		this.defaultImports.forEach(imp -> imports.append("import ").append(imp).append(".*; "));
		long start = System.currentTimeMillis();
		try{
			out = this.scriptEngine.eval(imports + code);
		}
		catch(Exception e){
			out = e.getMessage();
			color = Color.RED;
			status = "Failed";
		}
		ctx.acknowledge(true).queue();
		ctx.getHook().sendMessage("").addEmbeds(ctx.getEmbed()
			.setTitle("Eval")
			.setColor(color)
			.addField("Status:", status, true)
			.addField("Duration:", (System.currentTimeMillis() - start) + "ms", true)
			.addField("Code:", "```java\n" + code + "\n```", false)
			.addField("Result:", out == null ? "" : out.toString(), false)
			.build()
		).queue();
	}

}
