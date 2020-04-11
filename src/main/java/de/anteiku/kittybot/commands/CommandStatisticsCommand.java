package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.utils.Logger;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.XYChart;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

public class CommandStatisticsCommand extends ACommand{

	public static String COMMAND = "commandstatistics";
	public static String USAGE = "commandstatistics <from(dd-MM-yyyy-HH:mm), to(dd-MM-yyyy-HH:mm)>";
	public static String DESCRIPTION = "Sends a graph with processing time of commands";
	protected static String[] ALIAS = {"stats", "commandstats"};

	public CommandStatisticsCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
		this.main = main;
	}

	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		long to = System.currentTimeMillis();
		long from = to - 864_000_00;
		if(args.length > 0){
			from = LocalDateTime.parse(args[0], main.dateFormatter).toEpochSecond( ZoneOffset.of("Z"));
		}
		if(args.length > 1){
			to = LocalDateTime.parse(args[1], main.dateFormatter).toEpochSecond( ZoneOffset.of("Z"));
		}
		Map<Long, Long> map = main.database.getCommandStatistics(event.getGuild().getId(), from, to);
		for(Map.Entry<Long, Long> m : map.entrySet()){
			Logger.print("Key: '" + m.getKey() + "' Value: '" + m.getValue() + "'");
		}
	}
	
	private void makeChart(Map<Long, Long> data){
		long[] xData = {};
		long[] yData = {};
		QuickChart.getChart("Sample Chart", "X", "Y", "y(x)", xData, yData);
	}

}
