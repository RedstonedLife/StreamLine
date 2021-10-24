package net.plasmere.streamline.objects.configs;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.objects.chats.ChatChannel;
import net.plasmere.streamline.objects.enums.MessageServerType;
import net.plasmere.streamline.objects.savable.users.SavableUser;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Objects;
import java.util.TreeMap;
import java.util.TreeSet;

public class ServerConfig {
    private Configuration serverConfig;
    private final String setstring = "settings.yml";
    private final File scfile = new File(StreamLine.getInstance().getConfDir(), setstring);

    public ServerConfig(){
        if (! StreamLine.getInstance().getConfDir().exists()) {
            if (! ConfigUtils.scMakeDefault) return;

            if (StreamLine.getInstance().getConfDir().mkdirs()) {
                if (ConfigUtils.debug) MessagingUtils.logInfo("Made folder: " + StreamLine.getInstance().getConfDir().getName());
            }
        }

        serverConfig = loadConfig();

        MessagingUtils.logInfo("Loaded serverConfig!");
    }

    public Configuration getServerConfig() {
        reloadConfig();
        return serverConfig;
    }

    public void reloadConfig(){
        try {
            serverConfig = loadConfig();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public Configuration loadConfig(){
        if (! scfile.exists()){
            try	(InputStream in = StreamLine.getInstance().getResourceAsStream(setstring)){
                Files.copy(in, scfile.toPath());
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        Configuration thing = new Configuration();

        try {
            thing = ConfigurationProvider.getProvider(YamlConfiguration.class).load(scfile); // ???
        } catch (Exception e) {
            e.printStackTrace();
        }

        return thing;
    }

    public void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(serverConfig, scfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TreeMap<Integer, String> getComparedMOTD() {
        try {
            return TextUtils.comparedConfiguration(serverConfig.getSection("motd"));
        } catch (Exception e) {
            e.printStackTrace();
            return new TreeMap<>();
        }
    }

    public TreeMap<Integer, String> getComparedSample() {
        try {
            return TextUtils.comparedConfiguration(serverConfig.getSection("sample"));
        } catch (Exception e) {
            e.printStackTrace();
            return new TreeMap<>();
        }
    }

    public String[] getSampleArray() {
        String[] array = new String[getComparedSample().size()];
        int i = 0;
        for (int it : getComparedSample().keySet()) {
            array[i] = getComparedSample().get(it);
            i ++;
        }

        return array;
    }

    public void setMOTD(String integer, String motd) {
        serverConfig.set("motd." + integer, motd);
        saveConfig();
        reloadConfig();
    }

    public String getMOTDat(int at) {
        reloadConfig();
        return serverConfig.getString("motd." + at);
    }

    public void setMOTDTime(int time) {
        serverConfig.set("motd-time", time);
        saveConfig();
        reloadConfig();
    }

    public int getMOTDTime() {
        reloadConfig();

        String string = serverConfig.getString("motd-time");

        if (string.contains("'") || string.contains("\"")) {
            return Integer.parseInt(string);
        }

        return serverConfig.getInt("motd-time");
    }

    public void setVersion(String version) {
        serverConfig.set("version", version);
        saveConfig();
        reloadConfig();
    }

    public String getVersion() {
        reloadConfig();
        return serverConfig.getString("version");
    }

    public void setSample(String integer, String sample) {
        serverConfig.set("sample." + integer, sample);
        saveConfig();
        reloadConfig();
    }

    public String getSampleAt(int at) {
        reloadConfig();
        return serverConfig.getString("sample." + at);
    }

    public void setMaxPlayers(String value) {
        serverConfig.set("max-players", value);
        saveConfig();
        reloadConfig();
    }

    public String getMaxPlayers() {
        reloadConfig();
        String string = serverConfig.getString("max-players");

        string = string.replace("'", "");

        try {
            return (string.equals("") ? Integer.toString(serverConfig.getInt("max-players")) : string);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public int maxPlayers() {
        String thing = getMaxPlayers();

        if (thing.equals("exact")) {
            return StreamLine.getInstance().getProxy().getPlayers().size();
        }

        if (thing.startsWith("exact")) {
            int i = Integer.parseInt(thing.substring("exact+".length()));
            if (thing.startsWith("exact+")){
                try {
                    return StreamLine.getInstance().getProxy().getPlayers().size() + i;
                } catch (Exception e) {
                    e.printStackTrace();
                    return StreamLine.getInstance().getProxy().getConfig().getPlayerLimit();
                }
            } else if (thing.startsWith("exact-")) {
                try {
                    return StreamLine.getInstance().getProxy().getPlayers().size() - i;
                } catch (Exception e) {
                    e.printStackTrace();
                    return StreamLine.getInstance().getProxy().getConfig().getPlayerLimit();
                }
            }
        }

        if (thing.startsWith("+")){
            try {
                return StreamLine.getInstance().getProxy().getConfig().getPlayerLimit() + Integer.parseInt(thing.substring(1));
            } catch (Exception e) {
                e.printStackTrace();
                return StreamLine.getInstance().getProxy().getConfig().getPlayerLimit();
            }
        } else if (thing.startsWith("-")) {
            try {
                return StreamLine.getInstance().getProxy().getConfig().getPlayerLimit() - Integer.parseInt(thing.substring(1));
            } catch (Exception e) {
                e.printStackTrace();
                return StreamLine.getInstance().getProxy().getConfig().getPlayerLimit();
            }
        } else {
            try {
                return Integer.parseInt(thing);
            } catch (Exception e) {
                e.printStackTrace();
                return StreamLine.getInstance().getProxy().getConfig().getPlayerLimit();
            }
        }
    }

    public void setOnlinePlayers(String value) {
        serverConfig.set("online-players", value);
        saveConfig();
        reloadConfig();
    }

    public String getOnlinePlayers() {
        reloadConfig();
        String string = serverConfig.getString("online-players");

        string = string.replace("'", "");

        try {
            return (string.equals("") ? Integer.toString(serverConfig.getInt("online-players")) : string);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public int onlinePlayers() {
        String thing = getOnlinePlayers();

        if (thing.equals("exact")) {
            return StreamLine.getInstance().getProxy().getPlayers().size();
        }

        if (thing.startsWith("exact")) {
            int i = Integer.parseInt(thing.substring("exactx".length()));
            if (thing.startsWith("exact+")){
                try {
                    return StreamLine.getInstance().getProxy().getPlayers().size() + i;
                } catch (Exception e) {
                    e.printStackTrace();
                    return StreamLine.getInstance().getProxy().getConfig().getPlayerLimit();
                }
            } else if (thing.startsWith("exact-")) {
                try {
                    return StreamLine.getInstance().getProxy().getPlayers().size() - i;
                } catch (Exception e) {
                    e.printStackTrace();
                    return StreamLine.getInstance().getProxy().getConfig().getPlayerLimit();
                }
            }
        }


        if (thing.startsWith("+")) {
            try {
                return StreamLine.getInstance().getProxy().getPlayers().size() + Integer.parseInt(thing.substring(1));
            } catch (Exception e) {
                e.printStackTrace();
                return StreamLine.getInstance().getProxy().getPlayers().size();
            }
        } else if (thing.startsWith("-")) {
            try {
                return StreamLine.getInstance().getProxy().getPlayers().size() - Integer.parseInt(thing.substring(1));
            } catch (Exception e) {
                e.printStackTrace();
                return StreamLine.getInstance().getProxy().getPlayers().size();
            }
        } else {
            try {
                return Integer.parseInt(thing);
            } catch (Exception e) {
                e.printStackTrace();
                return StreamLine.getInstance().getProxy().getPlayers().size();
            }
        }
    }

    public void setProxyChatEnabled(boolean bool) {
        serverConfig.set("proxy-chat.enabled", bool);
        saveConfig();
        reloadConfig();
    }

    public void toggleProxyChatEnabled() {
        setProxyChatEnabled(! getProxyChatEnabled());
    }

    public boolean getProxyChatEnabled() {
        reloadConfig();
        return serverConfig.getBoolean("proxy-chat.enabled");
    }

    public void setProxyChatConsoleEnabled(boolean bool) {
        serverConfig.set("proxy-chat.to-console", bool);
        saveConfig();
        reloadConfig();
    }

    public void toggleProxyChatConsoleEnabled() {
        setProxyChatConsoleEnabled(! getProxyChatConsoleEnabled());
    }

    public boolean getProxyChatConsoleEnabled() {
        reloadConfig();
        return serverConfig.getBoolean("proxy-chat.to-console");
    }

    public void setChatBasePerm(String set) {
        serverConfig.set("proxy-chat.base-perm", set);
        saveConfig();
        reloadConfig();
    }

    public String getProxyChatChatsAt(int integer, ChatChannel chatChannel, MessageServerType messageServerType) {
        reloadConfig();
        return serverConfig.getString("proxy-chat.chats." + chatChannel.toString().toLowerCase(Locale.ROOT) + "." + messageServerType.toString().toLowerCase(Locale.ROOT) + "." + integer);
    }

    public void setProxyChatChatsAt(int integer, ChatChannel chatChannel, MessageServerType messageServerType, String set) {
        serverConfig.set("proxy-chat.chats." + chatChannel.toString().toLowerCase(Locale.ROOT) + "." + messageServerType.toString().toLowerCase(Locale.ROOT) + "." + integer, set);
        saveConfig();
        reloadConfig();
    }

    public void setTagsPingEnabled(boolean bool) {
        serverConfig.set("proxy-chat.tags.enable-ping", bool);
        saveConfig();
        reloadConfig();
    }

    public boolean getTagsPingEnabled() {
        reloadConfig();
        return serverConfig.getBoolean("proxy-chat.tags.enable-ping");
    }

    public void setTagsPrefix(String prefix) {
        serverConfig.set("proxy-chat.tags.tag-prefix", prefix);
        saveConfig();
        reloadConfig();
    }

    public String getTagsPrefix() {
        reloadConfig();
        return serverConfig.getString("proxy-chat.tags.tag-prefix");
    }

    public void setEmote(String emote, String value) {
        serverConfig.set("proxy-chat.emotes." + emote + ".emote", value);
        if (getEmotePermission(emote) == null) setEmotePermission(emote, "");
        if (Objects.equals(getEmotePermission(emote), "")) setEmotePermission(emote, "");
        saveConfig();
        reloadConfig();
    }

    public String getEmote(String emote) {
        reloadConfig();
        return serverConfig.getString("proxy-chat.emotes." + emote + ".emote");
    }

    public void setEmotePermission(String emote, String permission) {
        serverConfig.set("proxy-chat.emotes." + emote + ".permission", permission);
        saveConfig();
        reloadConfig();
    }

    public String getEmotePermission(String emote) {
        reloadConfig();
        return serverConfig.getString("proxy-chat.emotes." + emote + ".permission");
    }

    public TreeSet<String> getEmotes() {
        reloadConfig();
        return new TreeSet<>(serverConfig.getSection("proxy-chat.emotes").getKeys());
    }

    public void setAllowGlobal(boolean bool) {
        serverConfig.set("proxy-chat.allow.global", bool);
        saveConfig();
        reloadConfig();
    }

    public boolean getAllowGlobal() {
        reloadConfig();
        return serverConfig.getBoolean("proxy-chat.allow.global");
    }

    public void setAllowLocal(boolean bool) {
        serverConfig.set("proxy-chat.allow.local", bool);
        saveConfig();
        reloadConfig();
    }

    public boolean getAllowLocal() {
        reloadConfig();
        return serverConfig.getBoolean("proxy-chat.allow.local");
    }

    public void setMaintenanceMode(boolean bool) {
        serverConfig.set("maintenance-mode.enabled", bool);
        saveConfig();
        reloadConfig();
    }

    public boolean getMaintenanceMode() {
        reloadConfig();
        return serverConfig.getBoolean("maintenance-mode.enabled");
    }

    public void setObject(String pathTo, Object object) {
        serverConfig.set(pathTo, object);
        saveConfig();
        reloadConfig();
    }
}
