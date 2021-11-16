package net.plasmere.streamline.objects.messaging;

import com.velocitypowered.api.command.CommandSource;

public class BungeeMessage {
    public CommandSource sender;
    public CommandSource to;
    public String title;
    public String transition;
    public String message;

    public BungeeMessage(CommandSource sender, CommandSource to, String title, String transition, String message){
        this.sender = sender;
        this.to = to;
        this.title = title + " ";
        this.transition = transition + " ";
        this.message = message;
    }

    public BungeeMessage(CommandSource sender, CommandSource to, String message){
        this.sender = sender;
        this.to = to;
        this.title = "";
        this.transition = "";
        this.message = message;
    }
}
