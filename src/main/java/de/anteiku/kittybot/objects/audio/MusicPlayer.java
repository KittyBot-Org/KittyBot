package de.anteiku.kittybot.objects.audio;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lavalink.client.player.LavalinkPlayer;
import lavalink.client.player.event.PlayerEventListenerAdapter;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

public class MusicPlayer extends PlayerEventListenerAdapter
{
    private final Queue<AudioTrack> queue = new LinkedList<>();
    private final Deque<AudioTrack> history = new LinkedList<>();

    private final LavalinkPlayer lavalinkPlayer;

    public MusicPlayer(final LavalinkPlayer lavalinkPlayer)
    {
        this.lavalinkPlayer = lavalinkPlayer;
    }
}