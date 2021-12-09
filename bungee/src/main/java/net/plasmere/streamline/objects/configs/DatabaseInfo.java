package net.plasmere.streamline.objects.configs;

import de.leonhard.storage.Config;
import de.leonhard.storage.LightningBuilder;
import net.plasmere.streamline.StreamLine;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class DatabaseInfo {
    public String fstring = "database-info.yml";
    public File file = new File(StreamLine.getInstance().getDataFolder(), fstring);
    public Config config;

    public DatabaseInfo() {
        config = loadFile();
    }

    public Config loadFile() {
        if (! file.exists()){
            try	(InputStream in = StreamLine.getInstance().getResourceAsStream(fstring)){
                Files.copy(in, file.toPath());
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        return LightningBuilder.fromFile(file).createConfig();
    }

    public void reloadFile() {
        config = loadFile();
    }

    public String getHost() {
        reloadFile();
        return config.getOrSetDefault("host", "0.0.0.0");
    }

    public int getPort() {
        reloadFile();
        return config.getOrSetDefault("port", 3306);
    }

    public String getUser() {
        reloadFile();
        return config.getOrSetDefault("user", "admin");
    }

    public String getPass() {
        reloadFile();
        return config.getOrSetDefault("pass", "ChangeMeThisWillNotWorkLikeThis");
    }

    public String getPref() {
        reloadFile();
        return config.getOrSetDefault("pref", "sl_");
    }

    public String getDatabase() {
        reloadFile();
        return config.getOrSetDefault("database", "streamline");
    }
}
