package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.utils.Logger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;

import java.awt.*;
import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
		InputStream inputStream = makeChart("KittyBot command processing time", "date", "processing time in ms", main.database.getCommandStatistics(event.getGuild().getId(), from, to), "processing time");
		EmbedBuilder eb = new EmbedBuilder()
			.setDescription("Bla this is a test anyway")
			.setImage("attachment://chart.jpg");
		sendAnswer(event, inputStream, "chart.jpg", eb.build()).queue();
	}
	
	private InputStream makeChart(String title, String xTitle, String yTitle, Map<Long, Long> data, String dataName){
		CategoryChart chart = new CategoryChartBuilder()
			.width(1200)
			.height(600)
			.title(title)
			.xAxisTitle(xTitle)
			.yAxisTitle(yTitle)
			.build();
		Styler styler = chart.getStyler();
		styler.setChartFontColor(Color.white);
		styler.setChartBackgroundColor(new Color(47,49,54));
		styler.setLegendVisible(false);

		chart.


		List<Date> xData = new ArrayList<>();
		List<Double> yData = new ArrayList<>();

		for(Map.Entry<Long, Long> d : data.entrySet()){
			xData.add(Date.from(Instant.ofEpochMilli(d.getKey())));
			yData.add((double)d.getValue());
		}
		chart.addSeries(dataName, xData, yData);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			BitmapEncoder.saveBitmap(chart, outputStream, BitmapEncoder.BitmapFormat.JPG);
			return new ByteArrayInputStream(outputStream.toByteArray());
		}
		catch (IOException e) {
			Logger.error(e);
		}
		return null;
	}

}
