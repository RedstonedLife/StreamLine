package net.plasmere.streamline.objects.configs;

import de.leonhard.storage.Config;
import de.leonhard.storage.LightningBuilder;
import net.plasmere.streamline.StreamLine;

import net.plasmere.streamline.utils.MessagingUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.UUID;

public class Votes {
    private Config votes;
    private final File file = new File(StreamLine.getInstance().getConfDir(), "votes.yml");

    public Votes(){
        if (! StreamLine.getInstance().getConfDir().exists()) {
            if (StreamLine.getInstance().getConfDir().mkdir()) {
                MessagingUtils.logInfo("Made folder: " + StreamLine.getInstance().getConfDir().getName());
            }
        }

        votes = loadVotes();
    }

    public Config getVotes() { return votes; }

    public void reloadvotes(){
        try {
            votes = loadVotes();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public Config loadVotes(){
        if (! file.exists()){
            try	(InputStream in = StreamLine.getInstance().getResourceAsStream("votes.yml")){
                Files.copy(in, file.toPath());
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        return LightningBuilder.fromFile(file).createConfig();
    }

    public boolean hasVotes(String uuid) {
        for (String key : votes.singleLayerKeySet()) {
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
    }

    public void addVotes(UUID uuid, int amount){
        setVotes(uuid, getVotes(uuid) + amount);
    }

    public void remVotes(UUID uuid, int amount){
        setVotes(uuid, getVotes(uuid) - amount);
    }

    public int getUniques(){
        return votes.singleLayerKeySet().size();
    }

    public boolean getConsole(){
        if (! votes.singleLayerKeySet().contains("console")) setConsole(false);

        return votes.getBoolean("console");
    }

    public void setConsole(boolean bool){
        votes.set("console", bool);
    }

    public void toggleConsole(){
        setConsole(! getConsole());
    }

    public void setObject(String pathTo, Object object) {
        votes.set(pathTo, object);
        reloadvotes();
    }
}
