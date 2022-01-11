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
        return new TreeList<>(StreamLine.chatFilters.getConf().singleLayerKeySet());
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
        for (String key : configuration.singleLayerKeySet()) {
            try {
                ConfigSection section = new ConfigSection(configuration.getSection(key));
                boolean enabled = section.s.getBoolean("enabled");
                String scriptName = section.s.getString("runs-script");
                String bypassPermission = section.s.getString("bypass-permission");
                boolean blocked = section.s.getBoolean("blocked");
                String regex = section.s.getString("regex");
                List<String> replacements = section.s.getStringList("replace-with");

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
