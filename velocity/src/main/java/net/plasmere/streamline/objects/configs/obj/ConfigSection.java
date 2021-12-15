package net.plasmere.streamline.objects.configs.obj;

import de.leonhard.storage.internal.FlatFile;
import de.leonhard.storage.sections.FlatFileSection;

import java.util.HashSet;
import java.util.Set;

public class ConfigSection extends FlatFileSection {
    public ConfigSection(FlatFile flatFile, String pathPrefix) {
        super(flatFile, pathPrefix);
    }

    @Override
    public Set<String> keySet() {
        return getKeys();
    }

    public Set<String> getKeys() {
        Set<String> toReturn = new HashSet<>();

        for (String string : flatFile.keySet(getPathPrefix())) {
            if (string.contains(".")) continue;
            toReturn.add(string);
        }

        return toReturn;
    }
}
