package net.plasmere.streamline.objects.messaging;

import net.md_5.bungee.api.CommandSource;

public class DiscordMessage {
    public CommandSource sender;
    public String title;
    public String message;
    public String channel;

    public DiscordMessage(CommandSource sender, String title, String message, String channel){
        this.sender = sender;
        this.title = title;
        this.message = message;
        this.channel = channel;
    }
}
