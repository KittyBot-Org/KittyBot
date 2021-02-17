package de.kittybot.kittybot.utils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MiscUtil;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Utils{

	private Utils(){}

	public static int getUserCount(ShardManager shardManager){
		//noinspection ConstantConditions
		return shardManager.getGuildCache().applyStream(guildStream -> guildStream.mapToInt(Guild::getMemberCount).sum());
	}

	public static boolean isSnowflake(String id){
		try{
			MiscUtil.parseSnowflake(id);
			return true;
		}
		catch(NumberFormatException ignored){
			return false;
		}
	}

	public static <T> CompletableFuture<List<T>> all(List<CompletableFuture<T>> futures){
		CompletableFuture<?>[] cfs = futures.toArray(new CompletableFuture<?>[]{});

		return CompletableFuture.allOf(cfs)
			.thenApply(ignored -> futures.stream()
				.map(CompletableFuture::join)
				.collect(Collectors.toList())
			);
	}

}
