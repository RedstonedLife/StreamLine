package net.plasmere.streamline.objects.configs;

import de.leonhard.storage.Config;
import de.leonhard.storage.LightningBuilder;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;

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
    private Config conf;
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

    public Config getConf() {
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

    public Config loadConfig(){
        if (! file.exists()){
            try	(InputStream in = StreamLine.getInstance().getResourceAsStream(fileString)){
                Files.copy(in, file.toPath());
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        return LightningBuilder.fromFile(file).createConfig();
    }

    public void saveFilter(ChatFilter filter) {
        conf.set(filter.name + ".enabled", filter.enabled);
        conf.set(filter.name + ".regex", filter.regex);
        conf.set(filter.name + ".replace-with", filter.replacements);
        reloadConfig();
    }

    public void loadChatFilters() {
        FilterHandler.loadFiltersFromConfiguration(conf);
    }
}
