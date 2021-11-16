package net.plasmere.streamline.objects;

import net.plasmere.streamline.objects.chats.ChatChannel;
import net.plasmere.streamline.objects.chats.ChatsHandler;

public class DataChannel {
    public ChatChannel chatChannel;
    public String identifier;
    public boolean bypass;
    public boolean joins;
    public boolean leaves;

    public DataChannel(ChatChannel chatChannel, String identifier, boolean bypass, boolean joins, boolean leaves) {
        this.chatChannel = chatChannel;
        this.identifier = identifier;
        this.bypass = bypass;
        this.joins = joins;
        this.leaves = leaves;
    }

    public DataChannel(String chatChannel, String identifier, boolean bypass, boolean joins, boolean leaves) {
        this(ChatsHandler.getChannel(chatChannel), identifier, bypass, joins, leaves);
    }
}
