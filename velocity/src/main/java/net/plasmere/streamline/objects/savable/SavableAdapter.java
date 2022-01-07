package net.plasmere.streamline.objects.savable;

import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.objects.lists.SingleSet;

import java.io.File;
import java.io.FileNotFoundException;

public class SavableAdapter {
    public enum Type {
        USER(StreamLine.getInstance().getPlDir(), ".toml"),
        CONSOLE(StreamLine.getInstance().getPlDir(), ".toml"),
        PLAYER(StreamLine.getInstance().getPlDir(), ".toml"),
        GUILD(StreamLine.getInstance().getGDir(), ".toml"),
        PARTY(StreamLine.getInstance().getPDir(), ".toml"),
        ;

        public File path;
        public String suffix;

        Type(File path, String suffix) {
            this.path = path;
            this.suffix = suffix;
        }
    }
}
