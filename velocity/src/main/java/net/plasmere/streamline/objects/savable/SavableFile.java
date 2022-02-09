package net.plasmere.streamline.objects.savable;

import de.leonhard.storage.Config;
import de.leonhard.storage.LightningBuilder;
import de.leonhard.storage.Toml;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.objects.lists.SingleSet;
import net.plasmere.streamline.objects.savable.groups.SavableGuild;
import net.plasmere.streamline.objects.savable.groups.SavableParty;
import net.plasmere.streamline.objects.savable.users.SavableConsole;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.utils.MessagingUtils;

import java.io.File;

public abstract class SavableFile {
    public File file;
    public Toml config;
    public String uuid;
    public boolean enabled;
    public boolean firstLoad;
    public SavableAdapter.Type type;

    public String parseFileName(File file) {
        return file.getName().substring(0, file.getName().lastIndexOf("."));
    }

    public SavableFile(File file) {
        this.file = file;

        try {
            this.uuid = parseFileName(this.file);
            if (this.uuid == null) {
                return;
            }
            if (this.uuid.equals("")) {
                return;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        try {
            if (! this.file.exists()) {
                if (! this.file.createNewFile()) if (ConfigUtils.debug()) MessagingUtils.logWarning("Couldn't create file for UUID: " + this.uuid);
                if (this.file.exists()) firstLoad = true;
            } else {
                firstLoad = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (this instanceof SavableGuild) this.type = SavableAdapter.Type.GUILD;
        if (this instanceof SavableParty) this.type = SavableAdapter.Type.PARTY;
        if (this instanceof SavablePlayer) this.type = SavableAdapter.Type.PLAYER;
        if (this instanceof SavableConsole) this.type = SavableAdapter.Type.CONSOLE;

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
