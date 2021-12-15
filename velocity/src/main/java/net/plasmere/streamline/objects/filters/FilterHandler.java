package net.plasmere.streamline.objects.filters;


import de.leonhard.storage.Config;
import de.leonhard.storage.sections.FlatFileSection;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.objects.configs.obj.ConfigSection;
import org.apache.commons.collections4.list.TreeList;

import java.util.ArrayList;
import java.util.List;

public class FilterHandler {
    public static List<ChatFilter> filters = new ArrayList<>();

    public static TreeList<String> getAllFiltersByName() {
        return new TreeList<>(StreamLine.chatFilters.getConf().keySet());
    }

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

    public static void loadFiltersFromConfiguration(Config configuration) {
        for (String key : configuration.keySet()) {
            try {
                ConfigSection section = (ConfigSection) configuration.getSection(key);
                boolean enabled = section.getBoolean("enabled");
                String scriptName = section.getString("runs-script");
                String bypassPermission = section.getString("bypass-permission");
                boolean blocked = section.getBoolean("blocked");
                String regex = section.getString("regex");
                List<String> replacements = section.getStringList("replace-with");

                ChatFilter filter = new ChatFilter(key, enabled, scriptName, bypassPermission, blocked, regex, replacements);
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
