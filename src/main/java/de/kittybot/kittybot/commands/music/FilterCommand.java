package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.modules.MusicModule;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionInteger;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.SubCommand;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import de.kittybot.kittybot.utils.MusicUtils;
import lavalink.client.io.filters.Karaoke;
import lavalink.client.io.filters.Timescale;
import lavalink.client.io.filters.Tremolo;
import lavalink.client.io.filters.Vibrato;

@SuppressWarnings("unused")
public class FilterCommand extends Command{

	public FilterCommand(){
		super("filter", "Applies a filter to music", Category.MUSIC);
		addOptions(
			new EqualizerCommand(),
			new KaraokeCommand(),
			new TimescaleCommand(),
			new TremoloCommand(),
			new VibratoCommand(),
			new ClearCommand()
		);
	}

	private static class EqualizerCommand extends SubCommand{

		public EqualizerCommand(){
			super("equalizer", "Lets you set each band(0 to 15) individually from -0.25 to 1.0");
			addOptions(
				new CommandOptionInteger("band", "Which band to set(0 to 15)").required(),
				new CommandOptionString("multiplier", "The multiplier for this band(-0.25 to 1.0)").required()
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var player = ctx.get(MusicModule.class).get(ctx.getGuildId());
			if(!MusicUtils.checkCommandRequirements(ctx, player) || !MusicUtils.checkMusicPermissions(ctx, player)){
				return;
			}
			var band = options.getInt("band");
			var multiplier = 1f;
			try{
				multiplier = options.getFloat("multiplier");
			}
			catch(NumberFormatException e){
				ctx.error("Please provide a valid float number for multiplier");
				return;
			}
			if(band < 0 || band > 15){
				ctx.error("The band range goes from 0 to 15");
				return;
			}
			if(multiplier < -0.25 || multiplier > 1.0){

				ctx.error("The multiplier goes from -0.25 to 1.0");
				return;
			}
			player.getPlayer().getFilters().setBand(band, multiplier).commit();
			ctx.reply("Set band " + band + " to " + multiplier);
		}

	}

	private static class KaraokeCommand extends SubCommand{

		public KaraokeCommand(){
			super("karaoke", "Uses equalization to eliminate part of a band, usually targeting vocals");
			addOptions(
				new CommandOptionString("level", "The level"),
				new CommandOptionString("monoLevel", "The mono level"),
				new CommandOptionString("filterBand", "The filter band"),
				new CommandOptionString("filterWidth", "The filter width")
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var player = ctx.get(MusicModule.class).get(ctx.getGuildId());
			if(!MusicUtils.checkCommandRequirements(ctx, player) || !MusicUtils.checkMusicPermissions(ctx, player)){
				return;
			}
			var karaoke = new Karaoke();
			if(options.has("level")){
				try{
					karaoke = karaoke.setLevel(options.getFloat("level"));
				}
				catch(NumberFormatException e){
					ctx.error("Please provide a valid float number for level");
					return;
				}
			}
			if(options.has("monoLevel")){
				try{
					karaoke = karaoke.setMonoLevel(options.getFloat("monoLevel"));
				}
				catch(NumberFormatException e){
					ctx.error("Please provide a valid float number for monoLevel");
					return;
				}
			}
			if(options.has("filterBand")){
				try{
					karaoke = karaoke.setFilterBand(options.getFloat("filterBand"));
				}
				catch(NumberFormatException e){
					ctx.error("Please provide a valid float number for filterBand");
					return;
				}
			}
			if(options.has("filterWidth")){
				try{
					karaoke = karaoke.setFilterWidth(options.getFloat("filterWidth"));
				}
				catch(NumberFormatException e){
					ctx.error("Please provide a valid float number for filterWidth");
					return;
				}
			}
			player.getPlayer().getFilters().setKaraoke(karaoke).commit();
			ctx.reply("Set karaoke filter");
		}

	}

	private static class TimescaleCommand extends SubCommand{

		public TimescaleCommand(){
			super("timescale", "Changes the speed, pitch, and rate. All default to 1");
			addOptions(
				new CommandOptionString("speed", "The speed"),
				new CommandOptionString("pitch", "The pitch"),
				new CommandOptionString("rate", "The rate")
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var player = ctx.get(MusicModule.class).get(ctx.getGuildId());
			if(!MusicUtils.checkCommandRequirements(ctx, player) || !MusicUtils.checkMusicPermissions(ctx, player)){
				return;
			}
			var timescale = new Timescale();
			if(options.has("speed")){
				try{
					timescale = timescale.setSpeed(options.getFloat("speed"));
				}
				catch(NumberFormatException e){
					ctx.error("Please provide a valid float number for speed");
					return;
				}
			}
			if(options.has("pitch")){
				try{
					timescale = timescale.setPitch(options.getFloat("pitch"));
				}
				catch(NumberFormatException e){
					ctx.error("Please provide a valid float number for pitch");
					return;
				}
			}
			if(options.has("rate")){
				try{
					timescale = timescale.setRate(options.getFloat("rate"));
				}
				catch(NumberFormatException e){
					ctx.error("Please provide a valid float number for rate");
					return;
				}
			}
			player.getPlayer().getFilters().setTimescale(timescale).commit();
			ctx.reply("Set timescale filter");
		}

	}

	private static class TremoloCommand extends SubCommand{

		public TremoloCommand(){
			super("tremolo", "Uses amplification to create a shuddering effect, where the volume quickly oscillates.");
			addOptions(
				new CommandOptionString("frequency", "The frequency(> 0)"),
				new CommandOptionString("depth", "The depth(0 < x ≤ 1)")
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var player = ctx.get(MusicModule.class).get(ctx.getGuildId());
			if(!MusicUtils.checkCommandRequirements(ctx, player) || !MusicUtils.checkMusicPermissions(ctx, player)){
				return;
			}
			var tremolo = new Tremolo();
			if(options.has("frequency")){
				try{
					var frequency = options.getFloat("frequency");
					if(frequency <= 0){
						ctx.error("The frequency needs to be bigger than 0");
						return;
					}
					tremolo = tremolo.setFrequency(frequency);
				}
				catch(NumberFormatException e){
					ctx.error("Please provide a valid float number for rate");
					return;
				}
			}
			if(options.has("depth")){
				try{
					var depth = options.getFloat("depth");
					if(depth <= 0 || depth > 1){
						ctx.error("The depth needs to be between 0(excluded) and 1");
						return;
					}
					tremolo = tremolo.setDepth(depth);
				}
				catch(NumberFormatException e){
					ctx.error("Please provide a valid float number for rate");
					return;
				}
			}
			player.getPlayer().getFilters().setTremolo(tremolo).commit();
			ctx.reply("Set tremolo filter");
		}

	}

	private static class VibratoCommand extends SubCommand{

		public VibratoCommand(){
			super("vibrato", "Similar to tremolo. While tremolo oscillates the volume, vibrato oscillates the pitch.");
			addOptions(
				new CommandOptionString("frequency", "The frequency(0 < x ≤ 14)"),
				new CommandOptionString("depth", "The depth(0 < x ≤ 1)")
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var player = ctx.get(MusicModule.class).get(ctx.getGuildId());
			if(!MusicUtils.checkCommandRequirements(ctx, player) || !MusicUtils.checkMusicPermissions(ctx, player)){
				return;
			}
			var vibrato = new Vibrato();
			if(options.has("frequency")){
				try{
					var frequency = options.getFloat("frequency");
					if(frequency <= 0 || frequency >= 14){
						ctx.error("The frequency needs to be 0(excluded) and 14(excluded)");
						return;
					}
					vibrato = vibrato.setFrequency(frequency);
				}
				catch(NumberFormatException e){
					ctx.error("Please provide a valid float number for rate");
					return;
				}
			}
			if(options.has("depth")){
				try{
					var depth = options.getFloat("depth");
					if(depth <= 0 || depth > 1){
						ctx.error("The depth needs to be between 0(excluded) and 1");
						return;
					}
					vibrato = vibrato.setDepth(depth);
				}
				catch(NumberFormatException e){
					ctx.error("Please provide a valid float number for rate");
					return;
				}
			}
			player.getPlayer().getFilters().setVibrato(vibrato).commit();
			ctx.reply("Set vibrato filter");
		}

	}

	private static class ClearCommand extends SubCommand{

		public ClearCommand(){
			super("clear", "Clears all filters including volume");
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var player = ctx.get(MusicModule.class).get(ctx.getGuildId());
			if(!MusicUtils.checkCommandRequirements(ctx, player) || !MusicUtils.checkMusicPermissions(ctx, player)){
				return;
			}
			player.getPlayer().getFilters().clear().commit();
			ctx.reply("Cleared all filters!");
		}

	}

}
