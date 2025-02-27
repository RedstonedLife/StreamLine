package net.plasmere.streamline.objects.configs;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.objects.chats.Chat;
import net.plasmere.streamline.objects.chats.ChatChannel;
import net.plasmere.streamline.objects.chats.ChatsHandler;
import net.plasmere.streamline.objects.enums.MessageServerType;
import net.plasmere.streamline.objects.savable.users.SavableUser;
import net.plasmere.streamline.utils.MessagingUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Locale;
import java.util.TreeMap;

public class RanksConfig {
    private Configuration conf;
    private final String fileString = "ranks.yml";
    private final File file = new File(StreamLine.getInstance().getConfDir(), fileString);

    public RanksConfig(){
        if (! StreamLine.getInstance().getConfDir().exists()) {
            if (StreamLine.getInstance().getConfDir().mkdirs()) {
                if (ConfigUtils.debug()) MessagingUtils.logInfo("Made folder: " + StreamLine.getInstance().getConfDir().getName());
            }
        }

        conf = loadConfig();

        MessagingUtils.logInfo("Loaded chats settings!");
    }

    public Configuration getConf() {
        reloadConfig();
        return conf;
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
            try	(InputStream in = StreamLine.getInstance().getResourceAsStream(fileString)){
                Files.copy(in, file.toPath());
            } catch (IOException e){
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

    public void setObject(String pathTo, Object object) {
        conf.set(pathTo, object);
        saveConfig();
        reloadConfig();
    }

    public TreeMap<Integer, String> checkedGroups() {
        reloadConfig();

        TreeMap<Integer, String> groups = new TreeMap<>();

        for (String key : conf.getSection("ranks").getKeys()) {
            try {
                groups.put(Integer.parseInt(key), conf.getString("ranks." + key));
            } catch (Exception e) {
                // do nothing and continue...
            }
        }

        return groups;
    }
}
