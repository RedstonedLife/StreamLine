package net.plasmere.streamline.objects.enums;

import net.plasmere.streamline.StreamLine;

public enum SavableType {
    PLAYER("players"),
    GUILD("guilds"),
    PARTY("parties")
    ;

    public String table;

    SavableType(String name) {
        this.table = StreamLine.databaseInfo.getPref() + name;
    }

    @Override
    public String toString() {
        return table;
    }
}
