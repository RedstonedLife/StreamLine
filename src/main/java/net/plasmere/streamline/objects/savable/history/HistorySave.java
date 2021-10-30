package net.plasmere.streamline.objects.savable.history;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.utils.MessagingUtils;
import org.apache.commons.collections4.list.TreeList;

import java.io.*;
import java.nio.file.Files;
import java.time.Instant;
import java.util.Date;
import java.util.TreeMap;

public class HistorySave {
    private Configuration conf;
    public String fileString;
    public File file;
    public String uuid;

    public HistorySave(String uuid) {
        this.fileString = uuid + ".yml";
        this.file = new File(StreamLine.getInstance().getChatHistoryDir(), this.fileString);
        this.uuid = uuid;

        this.conf = loadConfig();
    }

    public void reloadConfig(){
        try {
            conf = loadConfig();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public Configuration loadConfig(){
        if (! file.exists()){
            try {
                if (! file.createNewFile()) if (ConfigUtils.debug()) MessagingUtils.logWarning("Could not make HistorySave file " + file.getName() + "!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Configuration thing = new Configuration();

        try {
            thing = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file); // ???
        } catch (Exception e) {
            e.printStackTrace();
        }

        return thing;
    }

    public void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(conf, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String addLine(String server, String message) {
        return addLine(server, Instant.now().toEpochMilli(), message);
    }

    public String addLine(String server, long milliDate, String message) {
        conf.set(server + "." + milliDate, message);
        saveConfig();
        reloadConfig();
        return message;
    }

    public TreeList<String> getTimestamps(String server) {
        return new TreeList<>(conf.getSection(server).getKeys());
    }

    public TreeList<String> getTalkedInServers() {
        return new TreeList<>(conf.getKeys());
    }

    public TreeMap<Long, String> getTimestampsWithMessageFrom(String timestampFrom, String server) {
        TreeMap<Long, String> map = new TreeMap<>();

        TreeList<String> ts = getTimestamps(server);

        long timestampF;
        try {
            timestampF = Long.parseLong(timestampFrom);
        } catch (Exception e) {
            return map;
        }

        for (String t : ts) {
            try {
                long timestamp = Long.parseLong(t);
                if (timestamp >= timestampF) {
//                    if (map.size() == 0) {
//                        map.put(timestamp, conf.getString(server + "." + t).substring(2));
//                        continue;
//                    }
                    map.put(timestamp, conf.getString(server + "." + t));
                }
            } catch (Exception e) {
                // continue
            }
        }

        return map;
    }

//    public String addLine(String line) {
//        try {
//            if (! file.exists()) if (! file.createNewFile()) if (ConfigUtils.debug()) MessagingUtils.logWarning("Cannot create file for " + uuid);
//
//            TreeMap<Integer, String> lines = new TreeMap<>();
//            int l = 0;
//
//            FileReader fileReader = new FileReader(file);
//            BufferedReader bufferedReader = new BufferedReader(fileReader);
//            String string = "";
//            while ((string = bufferedReader.readLine()) != null) {
//                lines.put(l, string);
//                l ++;
//            }
//            bufferedReader.close();
//
//            file.delete();
//            file.createNewFile();
//
//            FileWriter writer = new FileWriter(file);
//            for (int i = 0; i < lines.size(); i ++) {
//                writer.write(lines.get(i) + "\n");
//            }
//            writer.write(line + "\n");
//            writer.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return line;
//    }
}
