package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.modules.MusicModule;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionFloat;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionInteger;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
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

	private static class EqualizerCommand extends GuildSubCommand{

		public EqualizerCommand(){
			super("equalizer", "Lets you set each band(0 to 15) individually from -0.25 to 1.0");
			addOptions(
				new CommandOptionInteger("band", "Which band to set(0 to 15)").required(),
				new CommandOptionFloat("multiplier", "The multiplier for this band(-0.25 to 1.0)").required()
			);
		}

		@Override
		public void run(Options options, GuildCommandContext ctx){
			var scheduler = ctx.get(MusicModule.class).getScheduler(ctx.getGuildId());
			if(!MusicUtils.checkCommandRequirements(ctx, scheduler) || !MusicUtils.checkMusicPermissions(ctx, scheduler)){
				return;
			}
			var band = options.getInt("band");
			var multiplier = options.getFloat("multiplier");
			scheduler.getFilters().setBand(band, multiplier).commit();
			ctx.reply("Set band " + band + " to " + multiplier);
		}

	}

	private static class KaraokeCommand extends GuildSubCommand{

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
		public void run(Options options, GuildCommandContext ctx){
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

	private static class TimescaleCommand extends GuildSubCommand{

		public TimescaleCommand(){
			super("timescale", "Changes the speed, pitch, and rate. All default to 1");
			addOptions(
				new CommandOptionFloat("speed", "The speed"),
				new CommandOptionFloat("pitch", "The pitch"),
				new CommandOptionFloat("rate", "The rate")
			);
		}

		@Override
		public void run(Options options, GuildCommandContext ctx){
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

	private static class TremoloCommand extends GuildSubCommand{

		public TremoloCommand(){
			super("tremolo", "Uses amplification to create a shuddering effect, where the volume quickly oscillates.");
			addOptions(
				new CommandOptionFloat("frequency", "The frequency(> 0)"),
				new CommandOptionFloat("depth", "The depth(0 < x ≤ 1)")
			);
		}

		@Override
		public void run(Options options, GuildCommandContext ctx){
			var scheduler = ctx.get(MusicModule.class).getScheduler(ctx.getGuildId());
			if(!MusicUtils.checkCommandRequirements(ctx, scheduler) || !MusicUtils.checkMusicPermissions(ctx, scheduler)){
				return;
			}
			var tremolo = new Tremolo();
			if(options.has("frequency")){
				var frequency = options.getFloat("frequency");
				tremolo = tremolo.setFrequency(frequency);
			}
			if(options.has("depth")){
				var depth = options.getFloat("depth");
				tremolo = tremolo.setDepth(depth);
			}
			scheduler.getFilters().setTremolo(tremolo).commit();
			ctx.reply("Set tremolo filter");
		}

	}

	private static class VibratoCommand extends GuildSubCommand{

		public VibratoCommand(){
			super("vibrato", "Similar to tremolo. While tremolo oscillates the volume, vibrato oscillates the pitch.");
			addOptions(
				new CommandOptionFloat("frequency", "The frequency(0 < x ≤ 14)"),
				new CommandOptionFloat("depth", "The depth(0 < x ≤ 1)")
			);
		}

		@Override
		public void run(Options options, GuildCommandContext ctx){
			var scheduler = ctx.get(MusicModule.class).getScheduler(ctx.getGuildId());
			if(!MusicUtils.checkCommandRequirements(ctx, scheduler) || !MusicUtils.checkMusicPermissions(ctx, scheduler)){
				return;
			}
			var vibrato = new Vibrato();
			if(options.has("frequency")){
				var frequency = options.getFloat("frequency");
				vibrato = vibrato.setFrequency(frequency);
			}
			if(options.has("depth")){
				var depth = options.getFloat("depth");
				vibrato = vibrato.setDepth(depth);
			}
			scheduler.getFilters().setVibrato(vibrato).commit();
			ctx.reply("Set vibrato filter");
		}

	}

	private static class RotationCommand extends GuildSubCommand{

		public RotationCommand(){
			super("rotation", "Rotates the sound around the stereo channels/user headphones aka Audio Panning.");
			addOptions(
				new CommandOptionFloat("frequency", "The frequency of the audio rotating around the listener in Hz").required()
			);
		}

		@Override
		public void run(Options options, GuildCommandContext ctx){
			var scheduler = ctx.get(MusicModule.class).getScheduler(ctx.getGuildId());
			if(!MusicUtils.checkCommandRequirements(ctx, scheduler) || !MusicUtils.checkMusicPermissions(ctx, scheduler)){
				return;
			}
			var frequency = options.getFloat("frequency");
			scheduler.getFilters().setRotation(new Rotation().setFrequency(frequency)).commit();
			ctx.reply("Set rotation filter");
		}

	}

	private static class DistortionCommand extends GuildSubCommand{

		public DistortionCommand(){
			super("distortion", "Distortion effect. It can generate some pretty unique audio effects.");
			addOptions(
				new CommandOptionFloat("offset", "The offset"),
				new CommandOptionFloat("sin-offset", "The sinOffset"),
				new CommandOptionFloat("cos-offset", "The cosOffset"),
				new CommandOptionFloat("tan-offset", "The tanOffset"),

				new CommandOptionFloat("scale", "The scale"),
				new CommandOptionFloat("sin-scale", "The sinScale"),
				new CommandOptionFloat("cos-scale", "The cosScale"),
				new CommandOptionFloat("tan-scale", "The tanScale")
			);
		}

		@Override
		public void run(Options options, GuildCommandContext ctx){
			var scheduler = ctx.get(MusicModule.class).getScheduler(ctx.getGuildId());
			if(!MusicUtils.checkCommandRequirements(ctx, scheduler) || !MusicUtils.checkMusicPermissions(ctx, scheduler)){
				return;
			}
			var distortion = new Distortion();
			if(options.has("offset")){
				distortion = distortion.setOffset(options.getFloat("offset"));
			}
			if(options.has("sin-offset")){
				distortion = distortion.setSinOffset(options.getFloat("sin-offset"));
			}
			if(options.has("cos-offset")){
				distortion = distortion.setCosOffset(options.getFloat("cos-offset"));
			}
			if(options.has("tan-offset")){
				distortion = distortion.setTanOffset(options.getFloat("tan-offset"));
			}

			if(options.has("scale")){
				distortion = distortion.setScale(options.getFloat("scale"));
			}
			if(options.has("sin-scale")){
				distortion = distortion.setSinScale(options.getFloat("sin-scale"));
			}
			if(options.has("cos-scale")){
				distortion = distortion.setCosScale(options.getFloat("cos-scale"));
			}
			if(options.has("tan-scale")){
				distortion = distortion.setTanScale(options.getFloat("tan-scale"));
			}

			scheduler.getFilters().setDistortion(distortion).commit();
			ctx.reply("Set distortion filter");
		}

	}

	private static class ClearCommand extends GuildSubCommand{

		public ClearCommand(){
			super("clear", "Clears all filters including volume");
		}

		@Override
		public void run(Options options, GuildCommandContext ctx){
			var scheduler = ctx.get(MusicModule.class).getScheduler(ctx.getGuildId());
			if(!MusicUtils.checkCommandRequirements(ctx, scheduler) || !MusicUtils.checkMusicPermissions(ctx, scheduler)){
				return;
			}
			scheduler.getFilters().clear().commit();
			ctx.reply("Cleared all filters!");
		}

	}

}
