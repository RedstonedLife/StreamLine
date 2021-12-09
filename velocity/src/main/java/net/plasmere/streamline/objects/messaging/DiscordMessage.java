package net.plasmere.streamline.objects.messaging;

import com.velocitypowered.api.command.CommandSource;

public class DiscordMessage {
    public CommandSource sender;
    public String title;
    public String message;
    public long channel;

    public DiscordMessage(CommandSource sender, String title, String message, long channel){
        this.sender = sender;
        this.title = title;
        this.message = message;
        this.channel = channel;
    }
}
