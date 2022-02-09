package net.plasmere.streamline.objects.configs;

import de.leonhard.storage.Config;
import de.leonhard.storage.LightningBuilder;
import de.leonhard.storage.sections.FlatFileSection;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.objects.configs.obj.ConfigSection;
import net.plasmere.streamline.objects.configs.obj.TablistFormat;
import net.plasmere.streamline.objects.lists.SingleSet;
import net.plasmere.streamline.utils.MessagingUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;

public class TablistConfig {
    private Config config;
    private final String cstring = "tab-list.yml";
    private final File file = new File(StreamLine.getInstance().getConfDir(), cstring);

    public TreeMap<Integer, TablistFormat> loadedTablistFormats = new TreeMap<>();

    public TablistConfig(){
        if (! StreamLine.getInstance().getConfDir().exists()) {
            if (StreamLine.getInstance().getConfDir().mkdir()) {
                MessagingUtils.logInfo("Made folder: " + StreamLine.getInstance().getConfDir().getName());
            }
        }

        config = loadConfig();
        reloadLoadedTablistFormats();
    }

    public Config getConfig() { return config; }

    public void reloadConfig(){
        try {
            config = loadConfig();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public Config loadConfig(){
        if (! file.exists()){
            try	(InputStream in = StreamLine.getInstance().getResourceAsStream(cstring)){
                Files.copy(in, file.toPath());
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        return LightningBuilder.fromFile(file).createConfig();
    }

    public void reloadLoadedTablistFormats() {
        loadedTablistFormats.clear();

        loadedTablistFormats = getAllTablistFormats();
    }

    public String getBasePermission() {
        reloadConfig();
        return config.getString("base-permission");
    }

    public TablistFormat getGeneralTablistFormat() {
        reloadConfig();

        FlatFileSection section = config.getSection("general");

        TablistFormat format = new TablistFormat("general",
                section.getString("player-name"),
                section.getStringList("header"),
                section.getStringList("footer")
        );

        if (! format.enabled) return null;

        return format;
    }

    public List<TablistFormat> getRawGroupedTablistFormats() {
        reloadConfig();

        FlatFileSection groupedSection = config.getSection("grouped");

        List<TablistFormat> formats = new ArrayList<>();

        for (String key : groupedSection.singleLayerKeySet()) {
            FlatFileSection section = config.getSection("grouped." + key);

            TablistFormat format = new TablistFormat(key,
                    section.getString("player-name"),
                    section.getStringList("header"),
                    section.getStringList("footer")
            );

            if (! format.enabled) continue;

            formats.add(format);
        }

        return formats;
    }

    public TreeMap<Integer, TablistFormat> getAllTablistFormats() {
        TablistFormat general = getGeneralTablistFormat();
        List<TablistFormat> grouped = getRawGroupedTablistFormats();

        TreeMap<Integer, TablistFormat> all = new TreeMap<>();

        all.put(general.id, general);

        for (TablistFormat format : grouped) {
            all.put(format.id, format);
        }

//        if (ConfigUtils.debug()) {
//            MessagingUtils.logInfo("=== Configured Tab Lists ===");
//            for (TablistFormat format : all.values()) {
//                MessagingUtils.logInfo("Identifier: " + format.identifier + " | ID (number): " + format.id + " | PlayerName Format: " + format.playerName);
//            }
//        }

        return all;
    }

    public boolean isGlobal() {
        reloadConfig();
        return config.getBoolean("is-global");
    }
}
