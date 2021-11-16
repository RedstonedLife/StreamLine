package net.plasmere.streamline.objects.configs;

import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.backend.Configuration;
import net.plasmere.streamline.config.backend.ConfigurationProvider;
import net.plasmere.streamline.config.backend.YamlConfiguration;
import net.plasmere.streamline.objects.DataChannel;
import net.plasmere.streamline.utils.MessagingUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.TreeMap;

public class OfflineStats {
    private Configuration conf;
    private final String fileString = "offline-stats.yml";
    private final File file = new File(StreamLine.getInstance().getPlDir(), fileString);
    public TreeMap<Long, DataChannel> loadedChannels = new TreeMap<>();

    public TreeMap<String, Integer> toVerify = new TreeMap<>();

    public OfflineStats(){
        if (! StreamLine.getInstance().getPlDir().exists()) {
            if (StreamLine.getInstance().getPlDir().mkdirs()) {
                if (ConfigUtils.debug()) MessagingUtils.logInfo("Made folder: " + StreamLine.getInstance().getConfDir().getName());
            }
        }

        conf = loadConfig();

        MessagingUtils.logInfo("Loaded offline stats!");
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

   public void addStat(String uuid, String playername) {
        reloadConfig();
        conf.set(uuid, playername);
        conf.set(playername, uuid);
        saveConfig();
   }

    public void remStat(String uuid, String playername) {
        reloadConfig();
        conf.set(uuid, null);
        conf.set(playername, null);
        saveConfig();
    }

    public String getPlayerName(String uuid) {
        reloadConfig();
        return conf.getString(uuid);
    }

    public String getUUID(String playername) {
        reloadConfig();
        return conf.getString(playername);
    }
}
