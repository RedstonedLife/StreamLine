package net.plasmere.streamline.objects.configs.obj;

import de.leonhard.storage.sections.FlatFileSection;

import java.util.HashSet;
import java.util.Set;

public class ConfigSection {
    public FlatFileSection s;
    private final String pathPrefix;

    public ConfigSection(FlatFileSection section) {
        this.s = section;
        this.pathPrefix = section.getPathPrefix();
    }

    public Set<String> getKeys() {
        Set<String> toReturn = new HashSet<>();

        for (String string : s.keySet()) {
            if (string.contains(".")) continue;
            toReturn.add(string);
        }

        return toReturn;
    }

    public Set<String> singleLayerKeySet() {
        return s.singleLayerKeySet(pathPrefix);
    }

    public Set<String> singleLayerKeySet(final String key) {
        return s.singleLayerKeySet(createFinalKey(key));
    }

    public Set<String> keySet() {
        return getKeys();
    }

    public Set<String> keySet(final String key) {
        return s.keySet(createFinalKey(key));
    }

    public void remove(final String key) {
        s.remove(createFinalKey(key));
    }

    public void set(final String key, final Object value) {
        s.set(createFinalKey(key), value);
    }

    public boolean contains(final String key) {
        return s.contains(createFinalKey(key));
    }

    public Object get(final String key) {
        return s.get(createFinalKey(key));
    }

    public <E extends Enum<E>> E getEnum(String key, Class<E> enumType) {
        return s.getEnum(createFinalKey(key), enumType);
    }

    private String createFinalKey(final String key) {
        return pathPrefix == null || pathPrefix.isEmpty() ? key : pathPrefix + "." + key;
    }
}
