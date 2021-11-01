package net.plasmere.streamline.objects.configs;

import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.backend.Configuration;
import net.plasmere.streamline.config.backend.ConfigurationProvider;
import net.plasmere.streamline.config.backend.YamlConfiguration;
import net.plasmere.streamline.objects.DataChannel;
import net.plasmere.streamline.objects.filters.ChatFilter;
import net.plasmere.streamline.objects.filters.FilterHandler;
import net.plasmere.streamline.utils.MessagingUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.TreeMap;

public class ChatFilters {
    private Configuration conf;
    private final String fileString = "chat-filters.yml";
    private final File file = new File(StreamLine.getInstance().getConfDir(), fileString);
    public TreeMap<Long, DataChannel> loadedChannels = new TreeMap<>();

    public TreeMap<String, Integer> toVerify = new TreeMap<>();

    public ChatFilters(){
        if (! StreamLine.getInstance().getConfDir().exists()) {
            if (StreamLine.getInstance().getConfDir().mkdirs()) {
                if (ConfigUtils.debug()) MessagingUtils.logInfo("Made folder: " + StreamLine.getInstance().getConfDir().getName());
            }
        }

        conf = loadConfig();

        loadChatFilters();

        MessagingUtils.logInfo("Loaded chat filters!");
    }

    public Configuration getConf() {
        reloadConfig();
        return conf;
    }

    public void reloadConfig(){
        try {
            conf = loadConfig();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public Configuration loadConfig(){
        if (! file.exists()){
            try	(InputStream in = StreamLine.getInstance().getResourceAsStream(fileString)){
                Files.copy(in, file.toPath());
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        Configuration thing = new Configuration();

        try {
            thing = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file); // ???
        } catch (Exception e) {
            e.printStackTrace();
        }

        return thing;
    }

    public void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(conf, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveFilter(ChatFilter filter) {
        conf.set(filter.name + "enabled", filter.enabled);
        conf.set(filter.name + "regex", filter.regex);
        conf.set(filter.name + "replace-with", filter.replacements);

        saveConfig();
        reloadConfig();
    }

    public void loadChatFilters() {
        FilterHandler.loadFiltersFromConfiguration(conf);
    }
}
