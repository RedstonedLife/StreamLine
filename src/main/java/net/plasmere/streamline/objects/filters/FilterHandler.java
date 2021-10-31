package net.plasmere.streamline.objects.filters;

import net.md_5.bungee.config.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FilterHandler {
    public static List<ChatFilter> filters = new ArrayList<>();

    public static ChatFilter addFilter(ChatFilter filter) {
        if (filters.contains(filter)) return filter;

        filters.add(filter);
        return filter;
    }

    public static ChatFilter remFilter(ChatFilter filter) {
        if (! filters.contains(filter)) return filter;

        filters.remove(filter);
        return filter;
    }

    public static ChatFilter getFilterByName(String name) {
        for (ChatFilter filter : filters) {
            if (filter.name.equals(name)) return filter;
        }

        return null;
    }

    public static void loadFiltersFromConfiguration(Configuration configuration) {

    }
}
