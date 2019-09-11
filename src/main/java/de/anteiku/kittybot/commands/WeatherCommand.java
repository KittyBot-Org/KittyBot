package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.Emotes;
import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.Logger;
import net.aksingh.owmjapis.api.APIException;
import net.aksingh.owmjapis.core.OWM;
import net.aksingh.owmjapis.model.CurrentWeather;
import net.aksingh.owmjapis.model.param.Main;
import net.aksingh.owmjapis.model.param.Weather;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;

import java.awt.*;

public class WeatherCommand extends Command{

	public static String COMMAND = "weather";
	public static String USAGE = "weather <time> <city> <country>";
	public static String DESCRIPTION = "Used to look up weather";
	public static String[] ALIAS = {"wetter"};

	public WeatherCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}
	
	private double toC(double k){
		return k - 273.15;
	}
	
	private EmbedBuilder buildEmbed(CurrentWeather weather){
		EmbedBuilder eb = new EmbedBuilder();
		Color color;
		Main main = weather.getMainData();
		double deg = (main.getTempMax() + main.getTempMin()) / 2;
		if(deg >= 40){
			color = Color.red;
		}
		else if(deg >= 30){
			color = Color.orange;
		}
		else if(deg >= 20){
			color = Color.yellow;
		}
		else if(deg >= 10){
			color = Color.green;
		}
		else if(deg >= 0){
			color = Color.cyan;
		}
		else{
			color = Color.blue;
		}
		eb.setColor(color);
		eb.setTitle("Weather for: " + weather.getCityName() + " at " + weather.getDateTime().toString());
		eb.addField(":sunny:*Temperature:*", main.getTempMin() + "-" + main.getTempMax() + "°C", true);
		eb.addField(":sunny:*Temperature:*", main.getTemp() + "°C", true);
		String string = "";
		for(Weather w : weather.getWeatherList()){
			string += w.toString() + " | ";
		}
		eb.addField("*Weather:*", string, true);
		return eb;
	}

	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		try{
			if(args.length > 0){
				if(args[0].equals("now") || args[0].equals("jetzt")){
					if(args.length == 2){
						CurrentWeather weather = main.owm.currentWeatherByCityName(args[1], OWM.Country.GERMANY);
						Message message = event.getChannel().sendMessage(buildEmbed(weather).build()).complete();
						main.commandManager.addListenerCmd(message, event.getMessage(), this, -1L);
						message.addReaction(Emotes.REFRESH).queue();
					}
					else if(args.length == 3){
					
					}
					else{
						sendUsage(event.getChannel(), "weather now <city> <country>");
					}
				}
				else{
					sendUsage(event.getChannel());
				}
			}
			else{
				sendUsage(event.getChannel());
			}
		}
		catch(APIException e){
			Logger.error(e);
			sendError(event.getChannel(), "Something went wrong while processing your command!");
		}
	}
	
	@Override
	public void reactionAdd(Message command, GuildMessageReactionAddEvent event){
		if(event.getReactionEmote().getName().equals(Emotes.REFRESH)){
			try{
				CurrentWeather weather = main.owm.currentWeatherByCityName("Hornberg", OWM.Country.GERMANY);
				event.getChannel().getMessageById(event.getMessageId()).complete().editMessage(buildEmbed(weather).build()).queue();
				event.getReaction().removeReaction(event.getUser()).queue();
			}
			catch(APIException e){
				Logger.error(e);
			}
		}
		super.reactionAdd(command, event);
	}
	
}
