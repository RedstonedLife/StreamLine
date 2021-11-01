package net.plasmere.streamline.objects.configs;

import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.backend.Configuration;
import net.plasmere.streamline.config.backend.ConfigurationProvider;
import net.plasmere.streamline.config.backend.YamlConfiguration;
import net.plasmere.streamline.utils.MessagingUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.UUID;

public class Votes {
    private Configuration votes;
    private final File bfile = new File(StreamLine.getInstance().getConfDir(), "votes.yml");

    public Votes(){
        if (! StreamLine.getInstance().getConfDir().exists()) {
            if (StreamLine.getInstance().getConfDir().mkdir()) {
                MessagingUtils.logInfo("Made folder: " + StreamLine.getInstance().getConfDir().getName());
            }
        }

        votes = loadVotes();
    }

    public Configuration getVotes() { return votes; }

    public void reloadvotes(){
        try {
            votes = loadVotes();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public Configuration loadVotes(){
        if (! bfile.exists()){
            try	(InputStream in = StreamLine.getInstance().getResourceAsStream("votes.yml")){
                Files.copy(in, bfile.toPath());
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        try {
            votes = ConfigurationProvider.getProvider(YamlConfiguration.class).load(bfile); // ???
        } catch (Exception e) {
            e.printStackTrace();
        }
        MessagingUtils.logInfo("Loaded votes!");

        return votes;
    }

    public void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(votes, bfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasVotes(String uuid) {
        for (String key : votes.getKeys()) {
            if (key.equals(uuid)) return true;
        }

        return false;
    }

    public int getVotes(UUID uuid){
        if (! hasVotes(uuid.toString())) {
            setVotes(uuid, 0);
        }
        return votes.getInt(uuid.toString());
    }

    public void setVotes(UUID uuid, int amount){
        votes.set(uuid.toString(), amount);
        saveConfig();
    }

    public void addVotes(UUID uuid, int amount){
        setVotes(uuid, getVotes(uuid) + amount);
    }

    public void remVotes(UUID uuid, int amount){
        setVotes(uuid, getVotes(uuid) - amount);
    }

    public int getUniques(){
        return votes.getKeys().size();
    }

    public boolean getConsole(){
        if (! votes.getKeys().contains("console")) setConsole(false);

        return votes.getBoolean("console");
    }

    public void setConsole(boolean bool){
        votes.set("console", bool);
        saveConfig();
    }

    public void toggleConsole(){
        setConsole(! getConsole());
    }

    public void setObject(String pathTo, Object object) {
        votes.set(pathTo, object);
        saveConfig();
        reloadvotes();
    }
}
