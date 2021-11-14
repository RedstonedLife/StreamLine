package net.plasmere.streamline.objects.chats;

import java.util.Locale;

public class ChatChannel {
    public String name;
    public String permission;

    public ChatChannel(String name, String permission) {
        this.name = name.toLowerCase(Locale.ROOT);
    }

    public String toString() {
        return this.name;
    }
}
