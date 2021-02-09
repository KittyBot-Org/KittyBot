package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.modules.MusicModule;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionFloat;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionInteger;
import de.kittybot.kittybot.slashcommands.application.options.SubCommand;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import de.kittybot.kittybot.utils.MusicUtils;
import lavalink.client.io.filters.*;

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
			new RotationCommand(),
			new DistortionCommand(),
			new ClearCommand()
		);
	}

	private static class EqualizerCommand extends SubCommand{

		public EqualizerCommand(){
			super("equalizer", "Lets you set each band(0 to 15) individually from -0.25 to 1.0");
			addOptions(
				new CommandOptionInteger("band", "Which band to set(0 to 15)").required(),
				new CommandOptionFloat("multiplier", "The multiplier for this band(-0.25 to 1.0)").required()
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var scheduler = ctx.get(MusicModule.class).getScheduler(ctx.getGuildId());
			if(!MusicUtils.checkCommandRequirements(ctx, scheduler) || !MusicUtils.checkMusicPermissions(ctx, scheduler)){
				return;
			}
			var band = options.getInt("band");
			var multiplier = options.getFloat("multiplier");
			if(band < 0 || band > 15){
				ctx.error("The band range goes from 0 to 15");
				return;
			}
			if(multiplier < -0.25 || multiplier > 1.0){
				ctx.error("The multiplier goes from -0.25 to 1.0");
				return;
			}
			scheduler.getFilters().setBand(band, multiplier).commit();
			ctx.reply("Set band " + band + " to " + multiplier);
		}

	}

	private static class KaraokeCommand extends SubCommand{

		public KaraokeCommand(){
			super("karaoke", "Uses equalization to eliminate part of a band, usually targeting vocals");
			addOptions(
				new CommandOptionFloat("level", "The level"),
				new CommandOptionFloat("monoLevel", "The mono level"),
				new CommandOptionFloat("filterBand", "The filter band"),
				new CommandOptionFloat("filterWidth", "The filter width")
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var scheduler = ctx.get(MusicModule.class).getScheduler(ctx.getGuildId());
			if(!MusicUtils.checkCommandRequirements(ctx, scheduler) || !MusicUtils.checkMusicPermissions(ctx, scheduler)){
				return;
			}
			var karaoke = new Karaoke();
			if(options.has("level")){
				karaoke = karaoke.setLevel(options.getFloat("level"));
			}
			if(options.has("monoLevel")){
				karaoke = karaoke.setMonoLevel(options.getFloat("monoLevel"));
			}
			if(options.has("filterBand")){
				karaoke = karaoke.setFilterBand(options.getFloat("filterBand"));
			}
			if(options.has("filterWidth")){
				karaoke = karaoke.setFilterWidth(options.getFloat("filterWidth"));
			}
			scheduler.getFilters().setKaraoke(karaoke).commit();
			ctx.reply("Set karaoke filter");
		}

	}

	private static class TimescaleCommand extends SubCommand{

		public TimescaleCommand(){
			super("timescale", "Changes the speed, pitch, and rate. All default to 1");
			addOptions(
				new CommandOptionFloat("speed", "The speed"),
				new CommandOptionFloat("pitch", "The pitch"),
				new CommandOptionFloat("rate", "The rate")
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var scheduler = ctx.get(MusicModule.class).getScheduler(ctx.getGuildId());
			if(!MusicUtils.checkCommandRequirements(ctx, scheduler) || !MusicUtils.checkMusicPermissions(ctx, scheduler)){
				return;
			}
			var timescale = new Timescale();
			if(options.has("speed")){
				timescale = timescale.setSpeed(options.getFloat("speed"));
			}
			if(options.has("pitch")){
				timescale = timescale.setPitch(options.getFloat("pitch"));
			}
			if(options.has("rate")){
				timescale = timescale.setRate(options.getFloat("rate"));
			}
			scheduler.getFilters().setTimescale(timescale).commit();
			ctx.reply("Set timescale filter");
		}

	}

	private static class TremoloCommand extends SubCommand{

		public TremoloCommand(){
			super("tremolo", "Uses amplification to create a shuddering effect, where the volume quickly oscillates.");
			addOptions(
				new CommandOptionFloat("frequency", "The frequency(> 0)"),
				new CommandOptionFloat("depth", "The depth(0 < x ≤ 1)")
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var scheduler = ctx.get(MusicModule.class).getScheduler(ctx.getGuildId());
			if(!MusicUtils.checkCommandRequirements(ctx, scheduler) || !MusicUtils.checkMusicPermissions(ctx, scheduler)){
				return;
			}
			var tremolo = new Tremolo();
			if(options.has("frequency")){
				var frequency = options.getFloat("frequency");
				if(frequency <= 0){
					ctx.error("The frequency needs to be bigger than 0");
					return;
				}
				tremolo = tremolo.setFrequency(frequency);
			}
			if(options.has("depth")){
				var depth = options.getFloat("depth");
				if(depth <= 0 || depth > 1){
					ctx.error("The depth needs to be between 0(excluded) and 1");
					return;
				}
				tremolo = tremolo.setDepth(depth);
			}
			scheduler.getFilters().setTremolo(tremolo).commit();
			ctx.reply("Set tremolo filter");
		}

	}

	private static class VibratoCommand extends SubCommand{

		public VibratoCommand(){
			super("vibrato", "Similar to tremolo. While tremolo oscillates the volume, vibrato oscillates the pitch.");
			addOptions(
				new CommandOptionFloat("frequency", "The frequency(0 < x ≤ 14)"),
				new CommandOptionFloat("depth", "The depth(0 < x ≤ 1)")
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var scheduler = ctx.get(MusicModule.class).getScheduler(ctx.getGuildId());
			if(!MusicUtils.checkCommandRequirements(ctx, scheduler) || !MusicUtils.checkMusicPermissions(ctx, scheduler)){
				return;
			}
			var vibrato = new Vibrato();
			if(options.has("frequency")){
				var frequency = options.getFloat("frequency");
				if(frequency <= 0 || frequency >= 14){
					ctx.error("The frequency needs to be 0(excluded) and 14(excluded)");
					return;
				}
				vibrato = vibrato.setFrequency(frequency);
			}
			if(options.has("depth")){
				var depth = options.getFloat("depth");
				if(depth <= 0 || depth > 1){
					ctx.error("The depth needs to be between 0(excluded) and 1");
					return;
				}
				vibrato = vibrato.setDepth(depth);
			}
			scheduler.getFilters().setVibrato(vibrato).commit();
			ctx.reply("Set vibrato filter");
		}

	}

	private static class RotationCommand extends SubCommand{

		public RotationCommand(){
			super("rotation", "Rotates the sound around the stereo channels/user headphones aka Audio Panning.");
			addOptions(
				new CommandOptionFloat("frequency", "The frequency of the audio rotating around the listener in Hz").required()
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var scheduler = ctx.get(MusicModule.class).getScheduler(ctx.getGuildId());
			if(!MusicUtils.checkCommandRequirements(ctx, scheduler) || !MusicUtils.checkMusicPermissions(ctx, scheduler)){
				return;
			}
			var frequency = options.getFloat("frequency");
			scheduler.getFilters().setRotation(new Rotation().setFrequency(frequency)).commit();
			ctx.reply("Set rotation filter");
		}

	}

	private static class DistortionCommand extends SubCommand{

		public DistortionCommand(){
			super("distortion", "Distortion effect. It can generate some pretty unique audio effects.");
			addOptions(
				new CommandOptionFloat("offset", "The offset"),
				new CommandOptionFloat("sinOffset", "The sinOffset"),
				new CommandOptionFloat("cosOffset", "The cosOffset"),
				new CommandOptionFloat("tanOffset", "The tanOffset"),

				new CommandOptionFloat("scale", "The scale"),
				new CommandOptionFloat("sinScale", "The sinScale"),
				new CommandOptionFloat("cosScale", "The cosScale"),
				new CommandOptionFloat("tanScale", "The tanScale")
			);
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var scheduler = ctx.get(MusicModule.class).getScheduler(ctx.getGuildId());
			if(!MusicUtils.checkCommandRequirements(ctx, scheduler) || !MusicUtils.checkMusicPermissions(ctx, scheduler)){
				return;
			}
			var distortion = new Distortion();
			if(options.has("offset")){
				distortion = distortion.setOffset(options.getFloat("offset"));
			}
			if(options.has("sinOffset")){
				distortion = distortion.setSinOffset(options.getFloat("sinOffset"));
			}
			if(options.has("cosOffset")){
				distortion = distortion.setCosOffset(options.getFloat("cosOffset"));
			}
			if(options.has("tanOffset")){
				distortion = distortion.setTanOffset(options.getFloat("tanOffset"));
			}

			if(options.has("scale")){
				distortion = distortion.setScale(options.getFloat("scale"));
			}
			if(options.has("sinScale")){
				distortion = distortion.setSinScale(options.getFloat("sinScale"));
			}
			if(options.has("cosScale")){
				distortion = distortion.setCosScale(options.getFloat("cosScale"));
			}
			if(options.has("tanScale")){
				distortion = distortion.setTanScale(options.getFloat("tanScale"));
			}

			scheduler.getFilters().setDistortion(distortion).commit();
			ctx.reply("Set distortion filter");
		}

	}

	private static class ClearCommand extends SubCommand{

		public ClearCommand(){
			super("clear", "Clears all filters including volume");
		}

		@Override
		public void run(Options options, CommandContext ctx){
			var scheduler = ctx.get(MusicModule.class).getScheduler(ctx.getGuildId());
			if(!MusicUtils.checkCommandRequirements(ctx, scheduler) || !MusicUtils.checkMusicPermissions(ctx, scheduler)){
				return;
			}
			scheduler.getFilters().clear().commit();
			ctx.reply("Cleared all filters!");
		}

	}

}
