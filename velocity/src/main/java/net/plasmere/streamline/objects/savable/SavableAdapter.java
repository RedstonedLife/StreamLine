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

    public static SingleSet<String, Type> parseUUIDAndTypeFromFile(File file) throws Exception {
        if (! file.exists()) throw new FileNotFoundException("File not found: " + file.getAbsolutePath());

        Type type = null;
        for (Type t : Type.values()) {
            if (file.getPath().startsWith(t.path.getPath())) {
                type = t;
            }
        }

        if (type == null) throw new Exception("Could not parse type!");

        if (! file.getName().endsWith(type.suffix)) throw new Exception("Is not right file type! Of: " + type.name());

        String[] fArray = file.getName().split("\\.", 2);

        String uuid = fArray[0];

        return new SingleSet<>(uuid, type);
    }
}
