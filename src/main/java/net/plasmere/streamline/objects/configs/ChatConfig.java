package net.plasmere.streamline.objects.configs;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.objects.DataChannel;
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

public class ChatConfig {
    private Configuration conf;
    private final String fileString = "chats.yml";
    private final File file = new File(StreamLine.getInstance().getConfDir(), fileString);

    public ChatConfig(){
        if (! StreamLine.getInstance().getConfDir().exists()) {
            if (StreamLine.getInstance().getConfDir().mkdirs()) {
                if (ConfigUtils.debug) MessagingUtils.logInfo("Made folder: " + StreamLine.getInstance().getConfDir().getName());
            }
        }

        conf = loadConfig();

        createChannels();
        createChats();

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

    public void createChannels() {
        for (String key : conf.getSection("chats").getKeys()){
            if (key.equals("base-permission")) continue;

            ChatsHandler.createChatChannel(key);
        }
    }

    public void createChats() {
        for (ChatChannel chatChannel : ChatsHandler.createdChannels) {
            for (String chatName : conf.getSection("chats." + chatChannel.name).getKeys()) {
                String identifier = conf.getString("chats." + chatChannel.name + "." + chatName + ".identifier");
                TreeMap<Integer, String> bungee = getFormatsFromSection(getFormatConfig(chatChannel, chatName, MessageServerType.BUNGEE));
                TreeMap<Integer, String> discord = getFormatsFromSection(getFormatConfig(chatChannel, chatName, MessageServerType.DISCORD));

                Chat chat = new Chat(chatName, chatChannel, identifier, bungee, discord, getByPassPerm(chatChannel, chatName));

                ChatsHandler.createChat(chat);
            }
        }
    }

    public String getByPassPerm(ChatChannel chatChannel, String chatName) {
        reloadConfig();
        return conf.getString("chats." + chatChannel.name + "." + chatName + ".bypass-permission");
    }

    public String getDefaultPerm(ChatChannel chatChannel) {
        reloadConfig();

        for (Chat chat : ChatsHandler.activeChats) {
            if (chat.chatChannel.name.equals(chatChannel.name) && chat.identifier.equals("network")) {
                return conf.getString("chats." + chatChannel.name + "." + chat.name + ".bypass-permission");
            }
        }

        return "";
    }

    public void setChatBasePerm(String set) {
        conf.set("chats.base-permission", set);
        saveConfig();
        reloadConfig();
    }

    public String getChatBasePerm() {
        reloadConfig();
        return conf.getString("chats.base-permission");
    }

    public TreeMap<Integer, String> getFormatsFromSection(Configuration section) {
        reloadConfig();
        TreeMap<Integer, String> map = new TreeMap<>();

        if (section == null) return map;
        if (section.getKeys().size() <= 0) return map;

        for (String key : section.getKeys()) {
            int thing = 0;

            try {
                thing = Integer.parseInt(key);
            } catch (Exception e) {
                MessagingUtils.logSevere("You have an error with your chats! Keys can only be integers!");
                e.printStackTrace();
                continue;
            }

            if (map.containsKey(thing)) {
                MessagingUtils.logSevere("You have an error with your chats! Keys cannot be the same! Skipping...");
                continue;
            }
            map.put(thing, section.getString(key));
        }

        return map;
    }

    public Configuration getFormatConfig(ChatChannel chatChannel, String chatName, MessageServerType messageServerType) {
        reloadConfig();
        return conf.getSection("chats." + chatChannel.name + "." + chatName + "." + messageServerType.toString().toLowerCase(Locale.ROOT));
    }

    public boolean hasChatPermission(SavableUser user, Chat chat, MessageServerType messageServerType) {
        for (Integer integer : getFormatsFromSection(getFormatConfig(chat.chatChannel, chat.name, messageServerType)).keySet()) {
            if (user.hasPermission(getChatBasePerm() + integer)) return true;
        }
        return false;
    }

    public boolean hasChatPermission(ProxiedPlayer user, Chat chat, MessageServerType messageServerType) {
        for (Integer integer : getFormatsFromSection(getFormatConfig(chat.chatChannel, chat.name, messageServerType)).keySet()) {
            if (user.hasPermission(getChatBasePerm() + integer)) return true;
        }
        return false;
    }

    public Chat getDefaultChat(ChatChannel chatChannel) {
        return ChatsHandler.getChat(chatChannel, "network");
    }

    public String getDefaultFormat(ChatChannel chatChannel, MessageServerType messageServerType) {
        Chat chat = getDefaultChat(chatChannel);

        if (chat == null) return "&r<&d%sender_display%&r> %message%";

        return getFormatsFromSection(getFormatConfig(chatChannel, chat.name, messageServerType)).firstEntry().getValue();
    }

    public String getDefaultPermissionedChatMessage(SavableUser user, String chatChannel, MessageServerType messageServerType){
        Chat chat = getDefaultChat(ChatsHandler.getChannel(chatChannel));

        if (getFormatsFromSection(getFormatConfig(chat.chatChannel, chat.name, messageServerType)).size() <= 0) return "&r<&d%sender_display%&r> %message%";

        if (! hasChatPermission(user, chat, messageServerType)) return getDefaultFormat(ChatsHandler.getChannel(chatChannel), messageServerType);

        String msg = "";
        int highest = getFormatsFromSection(getFormatConfig(chat.chatChannel, chat.name, messageServerType)).lastKey();

        return findFromChatsMap(user, chat, highest, 0, messageServerType); // Allow for one extra run?
    }

    public String getDefaultPermissionedChatMessage(ProxiedPlayer user, String chatChannel, MessageServerType messageServerType){
        Chat chat = getDefaultChat(ChatsHandler.getChannel(chatChannel));

        if (getFormatsFromSection(getFormatConfig(chat.chatChannel, chat.name, messageServerType)).size() <= 0) return "&r<&d%sender_display%&r> %message%";

        if (! hasChatPermission(user, chat, messageServerType)) return getDefaultFormat(ChatsHandler.getChannel(chatChannel), messageServerType);

        String msg = "";
        int highest = getFormatsFromSection(getFormatConfig(chat.chatChannel, chat.name, messageServerType)).lastKey();

        return findFromChatsMap(user, chat, highest, 0, messageServerType); // Allow for one extra run?
    }

    public String getPermissionedChatMessage(SavableUser user, Chat chat, String chatChannel, MessageServerType messageServerType){
        if (chat == null) {
            return getDefaultPermissionedChatMessage(user, chatChannel, messageServerType);
        }
        if (getFormatsFromSection(getFormatConfig(chat.chatChannel, chat.name, messageServerType)).size() <= 0) return "&r<&d%sender_display%&r> %message%";

        if (! hasChatPermission(user, chat, messageServerType)) return getDefaultFormat(ChatsHandler.getChannel(chatChannel), messageServerType);

        String msg = "";
        int highest = getFormatsFromSection(getFormatConfig(chat.chatChannel, chat.name, messageServerType)).lastKey();

        return findFromChatsMap(user, chat, highest, 0, messageServerType); // Allow for one extra run?
    }

    public String getPermissionedChatMessage(ProxiedPlayer user, Chat chat, String chatChannel, MessageServerType messageServerType){
        if (chat == null) {
            return getDefaultPermissionedChatMessage(user, chatChannel, messageServerType);
        }
        if (getFormatsFromSection(getFormatConfig(chat.chatChannel, chat.name, messageServerType)).size() <= 0) return "&r<&d%sender_display%&r> %message%";

        if (! hasChatPermission(user, chat, messageServerType)) return getDefaultFormat(ChatsHandler.getChannel(chatChannel), messageServerType);

        String msg = "";
        int highest = getFormatsFromSection(getFormatConfig(chat.chatChannel, chat.name, messageServerType)).lastKey();

        return findFromChatsMap(user, chat, highest, 0, messageServerType); // Allow for one extra run?
    }

    public String findFromChatsMap(SavableUser user, Chat chat, int trial, int running, MessageServerType messageServerType) {
        if (running > getFormatsFromSection(getFormatConfig(chat.chatChannel, chat.name, messageServerType)).size()) return "&r<&d%sender_display%&r> %message%";

        running ++;

        String perm = getChatBasePerm() + trial;
        if (user.hasPermission(perm)) return getFormatsFromSection(getFormatConfig(chat.chatChannel, chat.name, messageServerType)).get(trial);
        return findFromChatsMap(user, chat, iterateChatsMapFromHigher(trial, chat, messageServerType), running, messageServerType);
    }

    public String findFromChatsMap(ProxiedPlayer user, Chat chat, int trial, int running, MessageServerType messageServerType) {
        if (running > getFormatsFromSection(getFormatConfig(chat.chatChannel, chat.name, messageServerType)).size()) return "&r<&d%sender_display%&r> %message%";

        running ++;

        String perm = getChatBasePerm() + trial;
        if (user.hasPermission(perm)) return getFormatsFromSection(getFormatConfig(chat.chatChannel, chat.name, messageServerType)).get(trial);
        return findFromChatsMap(user, chat, iterateChatsMapFromHigher(trial, chat, messageServerType), running, messageServerType);
    }

    public int iterateChatsMapFromHigher(int fromHigher, Chat chat, MessageServerType messageServerType){
        return getFormatsFromSection(getFormatConfig(chat.chatChannel, chat.name, messageServerType)).lowerKey(fromHigher);
    }
}
