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

public class Bans {
    private Config bans;
    private final File bfile = new File(StreamLine.getInstance().getConfDir(), "bans.yml");

    public Bans(){
        if (! StreamLine.getInstance().getConfDir().exists()) {
            if (StreamLine.getInstance().getConfDir().mkdirs()) {
                if (ConfigUtils.debug()) MessagingUtils.logInfo("Made folder: " + StreamLine.getInstance().getConfDir().getName());
            }
        }

        bans = loadBans();
    }

    public Config getBans() { return bans; }

    public void reloadBans(){
        try {
            bans = loadBans();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public Config loadBans(){
        if (! bfile.exists()){
            try	(InputStream in = StreamLine.getInstance().getResourceAsStream("bans.yml")){
                Files.copy(in, bfile.toPath());
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        return LightningBuilder.fromFile(bfile).createConfig();
    }
}
