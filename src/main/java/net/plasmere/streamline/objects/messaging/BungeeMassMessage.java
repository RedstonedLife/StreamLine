package net.plasmere.streamline.objects.messaging;

import net.md_5.bungee.api.CommandSource;

public class BungeeMassMessage {
    public CommandSource sender;
    public String title;
    public String transition;
    public String message;
    public String permission;

    public BungeeMassMessage(CommandSource sender, String title, String transition, String message, String permission) {
        this.sender = sender;
        this.title = title + " ";
        this.transition = transition + " ";
        this.message = message;
        this.permission = permission;
    }

    public BungeeMassMessage(CommandSource sender, String message, String permission) {
        this.sender = sender;
        this.title = "";
        this.transition = "";
        this.message = message;
        this.permission = permission;
    }
}
