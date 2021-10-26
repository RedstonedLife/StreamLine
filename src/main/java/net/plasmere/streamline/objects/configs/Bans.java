package net.plasmere.streamline.objects.configs;

import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.backend.Configuration;
import net.plasmere.streamline.config.backend.ConfigurationProvider;
import net.plasmere.streamline.config.backend.YamlConfiguration;
import net.plasmere.streamline.utils.MessagingUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class Bans {
    private Configuration bans;
    private final File bfile = new File(StreamLine.getInstance().getconfDir(), "bans.yml");

    public Bans(){
        if (! StreamLine.getInstance().getconfDir().exists()) {
            if (StreamLine.getInstance().getconfDir().mkdirs()) {
                if (ConfigUtils.debug) MessagingUtils.logInfo("Made folder: " + StreamLine.getInstance().getconfDir().getName());
            }
        }

        bans = loadBans();
    }

    public Configuration getBans() { return bans; }

    public void reloadBans(){
        try {
            bans = loadBans();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public Configuration loadBans(){
        if (! bfile.exists()){
            try	(InputStream in = StreamLine.getInstance().getResourceAsStream("bans.yml")){
                Files.copy(in, bfile.toPath());
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        try {
            bans = ConfigurationProvider.getProvider(YamlConfiguration.class).load(bfile); // ???
        } catch (Exception e) {
            e.printStackTrace();
        }
        MessagingUtils.logInfo("Loaded bans!");

        return bans;
    }

    public void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(bans, bfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
