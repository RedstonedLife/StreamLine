package net.plasmere.streamline.objects.configs;

import de.leonhard.storage.Config;
import de.leonhard.storage.LightningBuilder;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;

import net.plasmere.streamline.utils.MessagingUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.TreeMap;

public class RanksConfig {
    private Config conf;
    private final String fileString = "ranks.yml";
    private final File file = new File(StreamLine.getInstance().getConfDir(), fileString);

    public RanksConfig(){
        if (! StreamLine.getInstance().getConfDir().exists()) {
            if (StreamLine.getInstance().getConfDir().mkdirs()) {
                if (ConfigUtils.debug()) MessagingUtils.logInfo("Made folder: " + StreamLine.getInstance().getConfDir().getName());
            }
        }

        conf = loadConfig();

        MessagingUtils.logInfo("Loaded chats settings!");
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

    public void setObject(String pathTo, Object object) {
        conf.set(pathTo, object);
        reloadConfig();
    }

    public TreeMap<Integer, String> checkedGroups() {
        reloadConfig();

        TreeMap<Integer, String> groups = new TreeMap<>();

        for (String key : conf.getSection("ranks").keySet()) {
            try {
                groups.put(Integer.parseInt(key), conf.getString("ranks." + key));
            } catch (Exception e) {
                // do nothing and continue...
            }
        }

        return groups;
    }
}
