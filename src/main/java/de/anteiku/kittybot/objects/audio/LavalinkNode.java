package de.anteiku.kittybot.objects.audio;

public class LavalinkNode
{
    private final String host;
    private final int port;
    private final String password;

    public LavalinkNode(final String host, final int port, final String password)
    {
        this.host = host;
        this.port = port;
        this.password = password;
    }

    public String getHost()
    {
        return host;
    }

    public int getPort()
    {
        return port;
    }

    public String getPassword()
    {
        return password;
    }
}
