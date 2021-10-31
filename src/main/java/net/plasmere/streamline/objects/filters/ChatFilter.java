package net.plasmere.streamline.objects.filters;

import java.util.List;

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
}
