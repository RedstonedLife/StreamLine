package net.plasmere.streamline.objects.filters;

import net.md_5.bungee.config.Configuration;
import net.plasmere.streamline.StreamLine;
import org.apache.commons.collections4.list.TreeList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FilterHandler {
    public static List<ChatFilter> filters = new ArrayList<>();

    public static TreeList<String> getAllFiltersByName() {
        return new TreeList<>(StreamLine.chatFilters.getConf().getKeys());
    }

    public static ChatFilter addFilter(ChatFilter filter) {
        if (filters.contains(filter)) return filter;

        filters.add(filter);
        return filter;
    }

    public static ChatFilter remFilter(ChatFilter filter) {
        if (!filters.contains(filter)) return filter;

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
        for (String key : configuration.getKeys()) {
            try {
                Configuration section = configuration.getSection(key);
                boolean enabled = section.getBoolean("enabled");
                String regex = section.getString("regex");
                List<String> replacements = section.getStringList("replace-with");

                ChatFilter filter = new ChatFilter(key, enabled, regex, replacements);
                addFilter(filter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void unloadAllFilters() {
        filters = new ArrayList<>();
    }

    public static void reloadAllFilters() {
        unloadAllFilters();
        StreamLine.chatFilters.reloadConfig();
        StreamLine.chatFilters.loadChatFilters();
    }
}
