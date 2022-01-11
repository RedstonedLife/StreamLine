package net.plasmere.streamline.objects.configs;

import de.leonhard.storage.Config;
import de.leonhard.storage.LightningBuilder;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;

import net.plasmere.streamline.objects.DataChannel;
import net.plasmere.streamline.utils.MessagingUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Locale;
import java.util.TreeMap;

public class OfflineStats {
    private Config conf;
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

   public void addStat(String uuid, String playername) {
       reloadConfig();

       playername = playername.replace(".", "*").toLowerCase(Locale.ROOT);

       conf.set(uuid, playername);
       conf.set(playername, uuid);
   }

    public void remStat(String uuid, String playername) {
        reloadConfig();
        conf.set(uuid, null);
        conf.set(playername, null);
    }

    public String getPlayerName(String uuid) {
        reloadConfig();
        return conf.getString(uuid).replace("*", ".");
    }

    public String getUUID(String playername) {
        playername = playername.replace(".", "*");
        reloadConfig();
        return conf.getString(playername);
    }
}
