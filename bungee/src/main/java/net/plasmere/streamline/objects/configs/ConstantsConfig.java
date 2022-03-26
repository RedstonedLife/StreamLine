package net.plasmere.streamline.objects.configs;

import de.leonhard.storage.Config;
import de.leonhard.storage.LightningBuilder;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.objects.configs.obj.StreamlineConstants;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

public class ConstantsConfig {
    public Config config;
    public String cstring = "constants.yml";
    public File cfile = new File(StreamLine.getInstance().getDataFolder(), cstring);
    public StreamlineConstants streamlineConstants;

    public ConstantsConfig() {
        config = loadConfig();
        streamlineConstants = getStreamlineConstants(false);
    }

    public Config loadConfig() {
        if (! cfile.exists()) {
            try {
                StreamLine.getInstance().getDataFolder().mkdirs();
                try (InputStream in = StreamLine.getInstance().getResourceAsStream(cstring)) {
                    Files.copy(in, cfile.toPath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return LightningBuilder.fromFile(cfile).createConfig();
    }

    public void reloadConfig() {
        config = loadConfig();
    }

    public StreamlineConstants getStreamlineConstants(boolean reload) {
        if (reload) reloadConfig();

        return new StreamlineConstants(
                config.getOrSetDefault("version", StreamLine.getInstance().getDescription().getVersion()),
                config.getOrSetDefault("language", "en_US"),
                config.getOrSetDefault("is.fresh", true),
                config.getOrSetDefault("is.beta", false)
        );
    }

    public void setStreamlineConstants(StreamlineConstants constants) {
        config.set("version", constants.version);
        config.set("language", constants.language);
        config.set("is.fresh", constants.isFresh);
        config.set("is.beta", constants.isBeta);

        reloadConfig();
    }

    public void setVersion(String set) {
        setStreamlineConstants(this.streamlineConstants.setVersion(set));
    }

    public void setLanguage(String set) {
        setStreamlineConstants(this.streamlineConstants.setLanguage(set));
    }

    public void setFresh(boolean set) {
        setStreamlineConstants(this.streamlineConstants.setFresh(set));
    }

    public void setBeta(boolean set) {
        setStreamlineConstants(this.streamlineConstants.setBeta(set));
    }
}