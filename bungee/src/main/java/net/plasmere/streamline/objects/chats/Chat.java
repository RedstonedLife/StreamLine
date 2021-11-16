package net.plasmere.streamline.objects.chats;

import java.util.TreeMap;

public class Chat {
    public String name;
    public ChatChannel chatChannel;
    public String identifier;
    public TreeMap<Integer, String> permissionedFormatsBungee;
    public TreeMap<Integer, String> permissionedFormatsDiscord;
    public String bypassPermission;

    public Chat(String name, ChatChannel chatChannel, String identifier, TreeMap<Integer, String> permissionedFormatsBungee, TreeMap<Integer, String> permissionedFormatsDiscord, String bypassPermission) {
        this.name = name;
        this.chatChannel = chatChannel;
        this.identifier = identifier;
        this.permissionedFormatsBungee = permissionedFormatsBungee;
        this.permissionedFormatsDiscord = permissionedFormatsDiscord;
        this.bypassPermission = bypassPermission;
    }

    public String toString() {
        return this.name;
    }

    public String toSavable() {
        return this.chatChannel.name + "." + this.identifier;
    }
}
