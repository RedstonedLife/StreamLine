package net.plasmere.streamline.objects.filters;

import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.objects.configs.ChatFilters;

import java.util.List;
import java.util.Random;

public class ChatFilter {
    public String name;
    public boolean enabled;
    public String regex;
    public List<String> replacements;

    public ChatFilter(String name, boolean enabled, String regex, List<String> replacements) {
        this.name = name;
        this.enabled = enabled;
        this.regex = regex;
        this.replacements = replacements;
    }

    public String applyFilter(String toFilter) {
        Random RNG = new Random();
        int replaceWith = RNG.nextInt(this.replacements.size() - 1);

        return toFilter.replaceAll(this.regex, this.replacements.get(replaceWith));
    }

    public boolean toggleEnabled() {
        this.enabled = ! this.enabled;
        save();
        return this.enabled;
    }

    public void save() {
        StreamLine.chatFilters.saveFilter(this);
    }
}
