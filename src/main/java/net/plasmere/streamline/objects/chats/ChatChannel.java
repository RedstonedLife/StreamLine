package net.plasmere.streamline.objects.chats;

import java.util.Locale;

public class ChatChannel {
    public String name;

    public ChatChannel(String name) {
        this.name = name.toLowerCase(Locale.ROOT);
    }

    public String toString() {
        return this.name;
    }
}
