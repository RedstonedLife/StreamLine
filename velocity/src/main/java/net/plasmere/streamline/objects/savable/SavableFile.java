package net.plasmere.streamline.objects.savable;

import de.leonhard.storage.Config;
import de.leonhard.storage.LightningBuilder;
import de.leonhard.storage.Toml;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.objects.lists.SingleSet;
import net.plasmere.streamline.utils.MessagingUtils;

import java.io.File;

public abstract class SavableFile {
    public File file;
    public Toml config;
    public String uuid;
    public SavableAdapter.Type type;
    public boolean enabled;

    public SavableFile(File file) {
        this.file = file;
        SingleSet<String, SavableAdapter.Type> parsed;

        try {
            if (! this.file.exists()) if (! this.file.createNewFile()) if (ConfigUtils.debug()) MessagingUtils.logWarning("Couldn't create file for UUID: " + this.uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            parsed = SavableAdapter.parseUUIDAndTypeFromFile(this.file);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        this.uuid = parsed.key;
        this.type = parsed.value;

        try {
            this.config = loadFile();
            this.enabled = true;
        } catch (Exception e) {
            e.printStackTrace();
            this.enabled = false;
        }

        this.populateDefaults();

        this.loadValues();
    }

    public SavableFile(String uuid, SavableAdapter.Type type) {
        this(new File(type.path, uuid + type.suffix));
    }

    public Toml loadFile() {
        return LightningBuilder.fromFile(this.file).createToml();
    }

    abstract public void populateDefaults();

    public <T> T getOrSetDefault(String key, T def) {
        return this.config.getOrSetDefault(key, def);
    }

    abstract public void loadValues();

    abstract public void saveAll();

    public void set(final String key, final Object value) {
        this.config.set(key, value);
    }

    public String toString() {
        return "[ File: " + file.getName() + " , UUID: " + this.uuid + " ]";
    }

    public void dispose() throws Throwable {
        this.uuid = null;
        this.finalize();
    }
}
