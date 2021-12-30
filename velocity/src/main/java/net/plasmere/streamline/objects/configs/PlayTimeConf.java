package net.plasmere.streamline.objects.configs;

import de.leonhard.storage.Config;
import de.leonhard.storage.LightningBuilder;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.objects.lists.SingleSet;
import net.plasmere.streamline.utils.MessagingUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;

public class PlayTimeConf {
    private Config config;
    private final String cstring = "playtime.yml";
    private final File file = new File(StreamLine.getInstance().getConfDir(), cstring);

    public PlayTimeConf(){
        if (! StreamLine.getInstance().getConfDir().exists()) {
            if (StreamLine.getInstance().getConfDir().mkdir()) {
                MessagingUtils.logInfo("Made folder: " + StreamLine.getInstance().getConfDir().getName());
            }
        }

        config = loadConfig();
    }

    public Config getConfig() { return config; }

    public void reloadConfig(){
        try {
            config = loadConfig();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public Config loadConfig(){
        if (! file.exists()){
            try	(InputStream in = StreamLine.getInstance().getResourceAsStream(cstring)){
                Files.copy(in, file.toPath());
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        return LightningBuilder.fromFile(file).createConfig();
    }

    public boolean hasPlayTime(String uuid) {
        for (String key : config.singleLayerKeySet()) {
            if (key.equals(uuid)) return true;
        }

        return false;
    }

    public int getPlayTime(String uuid){
        if (! hasPlayTime(uuid)) {
            setPlayTime(uuid, 0);
        }
        return config.getInt(uuid);
    }

    public int getPlayTime(UUID uuid){
        if (! hasPlayTime(uuid.toString())) {
            setPlayTime(uuid, 0);
        }
        return config.getInt(uuid.toString());
    }

    public void setPlayTime(String  uuid, int amount){
        config.set(uuid, amount);
    }

    public void setPlayTime(UUID uuid, int amount){
        config.set(uuid.toString(), amount);
    }

    public void addPlayTime(UUID uuid, int amount){
        setPlayTime(uuid, getPlayTime(uuid) + amount);
    }

    public void addPlayTime(String uuid, int amount){
        setPlayTime(uuid, getPlayTime(uuid) + amount);
    }

    public void remPlayTime(UUID uuid, int amount){
        setPlayTime(uuid, getPlayTime(uuid) - amount);
    }

    public void remPlayTime(String uuid, int amount){
        setPlayTime(uuid, getPlayTime(uuid) - amount);
    }

    public int getUniques(){
        return config.singleLayerKeySet().size();
    }

    public boolean getConsole(){
        if (! config.singleLayerKeySet().contains("console")) setConsole(false);

        return config.getBoolean("console");
    }

    public void setConsole(boolean bool){
        config.set("console", bool);
    }

    public void toggleConsole(){
        setConsole(! getConsole());
    }

    public TreeMap<Integer, SingleSet<String, Integer>> getPlayTimeAsMap() {
        reloadConfig();

        TreeMap<Integer, String> map = new TreeMap<>();

        for (String uuid : config.singleLayerKeySet()) {
            if (uuid.equals("console")) continue;

            map.put(config.getInt(uuid), uuid);
        }

        TreeMap<Integer, SingleSet<String, Integer>> toReturn = new TreeMap<>();

        Integer[] integers = map.keySet().toArray(new Integer[0]);

        Arrays.sort(integers);
        Arrays.sort(integers, Comparator.reverseOrder());

        for (int amount : integers) {
            toReturn.put(toReturn.size() + 1, new SingleSet<>(map.get(amount), amount));
        }

        return toReturn;
    }

    public void setObject(String pathTo, Object object) {
        config.set(pathTo, object);
        reloadConfig();
    }
}
