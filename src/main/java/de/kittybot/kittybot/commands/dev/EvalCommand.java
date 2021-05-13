package de.kittybot.kittybot.commands.dev;

import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.RunCommand;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Interaction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.slashcommands.interaction.response.FollowupMessage;
import de.kittybot.kittybot.slashcommands.interaction.response.InteractionResponse;
import de.kittybot.kittybot.slashcommands.interaction.response.InteractionResponseType;
import de.kittybot.kittybot.utils.Colors;
import net.dv8tion.jda.api.EmbedBuilder;

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
	public void run(Options options, Interaction ia){
		var code = options.getString("code");
		var responseEmbed = ia.getEmbed()
			.setTitle("Eval")
			.addField("Status:", "Loading...", true)
			.addField("Duration:", "...", true)
			.addField("Code:", "```java\n" + code + "\n```", false)
			.addField("Result:", "...", false);
		ia.reply().embeds(responseEmbed.build()).queue();

		Object out;
		var color = Color.GREEN;
		var status = "Success";
		this.scriptEngine.put("ia", ia);
		this.scriptEngine.put("options", options);
		this.scriptEngine.put("jda", ia.getJDA());

		this.scriptEngine.put("channel", ia.getChannel());
		this.scriptEngine.put("user", ia.getUser());
		if(ia instanceof GuildInteraction){
			var guildIa = (GuildInteraction) ia;
			this.scriptEngine.put("guild", guildIa.getGuild());
			this.scriptEngine.put("member", guildIa.getMember());
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
		responseEmbed.clearFields();
		responseEmbed.setColor(color)
			.addField("Status:", status, true)
			.addField("Duration:", (System.currentTimeMillis() - start) + "ms", true)
			.addField("Code:", "```java\n" + code + "\n```", false)
			.addField("Result:", out == null ? "" : out.toString(), false);

		ia.edit(new FollowupMessage.Builder().setEmbeds(responseEmbed.build()).build()).queue();
	}

}