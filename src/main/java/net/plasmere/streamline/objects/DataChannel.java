package net.plasmere.streamline.objects;

import net.plasmere.streamline.objects.enums.ChatChannel;

public class DataChannel {
    public ChatChannel type;
    public String identifier;
    public boolean bypass;
    public boolean joins;
    public boolean leaves;

    public DataChannel(ChatChannel type, String identifier, boolean bypass, boolean joins, boolean leaves) {
        this.type = type;
        this.identifier = identifier;
        this.bypass = bypass;
        this.joins = joins;
        this.leaves = leaves;
    }

    public DataChannel(String  type, String identifier, boolean bypass, boolean joins, boolean leaves) {
        this.type = ChatChannel.valueOf(type);
        this.identifier = identifier;
        this.bypass = bypass;
        this.joins = joins;
        this.leaves = leaves;
    }
}
