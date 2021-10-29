package net.plasmere.streamline.objects.configs;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.DataChannel;
import net.plasmere.streamline.objects.SavableGuild;
import net.plasmere.streamline.objects.Party;
import net.plasmere.streamline.objects.chats.ChatChannel;
import net.plasmere.streamline.objects.chats.ChatsHandler;
import net.plasmere.streamline.objects.enums.MessageServerType;
import net.plasmere.streamline.objects.lists.SingleSet;
import net.plasmere.streamline.objects.messaging.DiscordMessage;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.objects.savable.users.SavableUser;
import net.plasmere.streamline.utils.*;
import org.apache.commons.collections4.list.TreeList;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;

public class DiscordData {
    private Configuration conf;
    private final String fileString = "discord-data.yml";
    private final File file = new File(StreamLine.getInstance().getConfDir(), fileString);
    public TreeMap<Long, DataChannel> loadedChannels = new TreeMap<>();

    public TreeMap<String, Integer> toVerify = new TreeMap<>();

    public DiscordData(){
        if (! StreamLine.getInstance().getConfDir().exists()) {
            if (StreamLine.getInstance().getConfDir().mkdirs()) {
                if (ConfigUtils.debug) MessagingUtils.logInfo("Made folder: " + StreamLine.getInstance().getConfDir().getName());
            }
        }

        conf = loadConfig();
        loadChannels();

        MessagingUtils.logInfo("Loaded discord data!");
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

    public void purgeChannels() {
        loadedChannels = new TreeMap<>();
    }

    public void loadChannels() {
        purgeChannels();

        for (String key : conf.getSection("channels").getKeys()) {
            if (key.contains("put")) continue;

            try {
                long l = Long.parseLong(key);
                loadedChannels.put(l, getChannel(l));

//                if (ConfigUtils.debug) MessagingUtils.logInfo("ID: " + l + " | Channel: " + loadedChannels.get(l).chat.chatChannel.name + " , " + loadedChannels.get(l).chat.identifier);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    public boolean hasGlobalChannel() {
//
//    }

    public boolean ifHasChannels(ChatChannel type, String identifier) {
        TreeSet<Long> channels = getChannelsByData(type, identifier);

        if (channels == null) return false;
        if (channels.size() <= 0) return false;

        return true;
    }

    public SingleSet<Boolean, ChatChannel> ifHasChannelsAsSet(ChatChannel type, String identifier) {
        return new SingleSet<>(ifHasChannels(type, identifier), type);
    }

    public void sendDiscordChannel(CommandSender sender, ChatChannel type, String identifier, String message) {
        if (! ConfigUtils.moduleDEnabled) return;

        TreeSet<Long> channels = getChannelsByData(type, identifier);

        for (Long channel : channels) {
            if (type.equals(ChatsHandler.getChannel("local"))) {
                MessagingUtils.sendDiscordEBMessage(
                        new DiscordMessage(
                                sender,
                                TextUtils.formatted(ConfigUtils.moduleDPCDDLocalTitle.replace("%server%", PlayerUtils.getServer(sender))),
                                ConfigUtils.moduleDPCDDLocalMessage
                                        .replace("%message%", message),
                                channel.toString()),
                        ConfigUtils.moduleDPCDDLocalUseAvatar
                );
            }

            if (type.equals(ChatsHandler.getChannel("global"))) {
                MessagingUtils.sendDiscordEBMessage(
                        new DiscordMessage(
                                sender,
                                TextUtils.formatted(ConfigUtils.moduleDPCDDGlobalTitle.replace("%server%", "network")),
                                ConfigUtils.moduleDPCDDGlobalMessage
                                        .replace("%message%", message),
                                channel.toString()),
                        ConfigUtils.moduleDPCDDGlobalUseAvatar
                );
            }

            if (type.equals(ChatsHandler.getChannel("guild"))) {
                SavableGuild guild = GuildUtils.getGuild(PlayerUtils.getOrGetSavableUser(sender));

                if (guild == null) return;

                MessagingUtils.sendDiscordEBMessage(
                        new DiscordMessage(
                                sender,
                                TextUtils.formatted(ConfigUtils.moduleDPCDDGuildTitle
                                        .replace("%guild_name%", guild.name)
                                        .replace("%leader_absolute%", PlayerUtils.getOrGetSavableUser(guild.leaderUUID).latestName)
                                        .replace("%leader_normal%", PlayerUtils.getOffOnRegDiscord(PlayerUtils.getOrGetSavableUser(guild.leaderUUID)))
                                        .replace("%leader_display%", PlayerUtils.getOffOnDisplayDiscord(PlayerUtils.getOrGetSavableUser(guild.leaderUUID)))
                                        .replace("%leader_formatted%", PlayerUtils.getJustDisplayDiscord(PlayerUtils.getOrGetSavableUser(guild.leaderUUID)))
                                ),
                                ConfigUtils.moduleDPCDDGuildMessage
                                        .replace("%message%", message),
                                channel.toString()),
                        ConfigUtils.moduleDPCDDGuildUseAvatar
                );
            }

            if (type.equals(ChatsHandler.getChannel("party"))) {
                Party party = PartyUtils.getParty(PlayerUtils.getOrGetSavableUser(sender).uuid);

                if (party == null) return;

                MessagingUtils.sendDiscordEBMessage(
                        new DiscordMessage(
                                sender,
                                TextUtils.formatted(ConfigUtils.moduleDPCDDPartyTitle
                                        .replace("%leader_absolute%", PlayerUtils.getOrGetSavableUser(party.leaderUUID).latestName)
                                        .replace("%leader_normal%", PlayerUtils.getOffOnRegDiscord(PlayerUtils.getOrGetSavableUser(party.leaderUUID)))
                                        .replace("%leader_display%", PlayerUtils.getOffOnDisplayDiscord(PlayerUtils.getOrGetSavableUser(party.leaderUUID)))
                                        .replace("%leader_formatted%", PlayerUtils.getJustDisplayDiscord(PlayerUtils.getOrGetSavableUser(party.leaderUUID)))
                                ),
                                ConfigUtils.moduleDPCDDPartyMessage
                                        .replace("%message%", message),
                                channel.toString()),
                        ConfigUtils.moduleDPCDDPartyUseAvatar
                );
            }
        }
    }

    public void sendDiscordJoinChannel(CommandSender player, ChatChannel chatChannel, String identifier) {
        if (! ConfigUtils.moduleDEnabled) return;

        TreeSet<Long> channels = getChannelsByData(chatChannel, identifier);

        for (Long channel : channels) {
            if (chatChannel.equals(ChatsHandler.getChannel("local"))) {
                MessagingUtils.sendDiscordEBMessage(
                        new DiscordMessage(
                                player,
                                TextUtils.formatted(ConfigUtils.moduleDPCDDLocalTitle.replace("%server%", PlayerUtils.getServer(player))),
                                TextUtils.replaceAllPlayerDiscord(ConfigUtils.moduleDPCDDLocalJoins, player),
                                channel.toString()),
                        ConfigUtils.moduleDPCDDLocalUseAvatar
                );
            }

            if (chatChannel.equals(ChatsHandler.getChannel("global"))) {
                MessagingUtils.sendDiscordEBMessage(
                        new DiscordMessage(
                                player,
                                TextUtils.formatted(ConfigUtils.moduleDPCDDGlobalTitle.replace("%server%", "network")),
                                TextUtils.replaceAllPlayerDiscord(ConfigUtils.moduleDPCDDGlobalJoins, player),
                                channel.toString()),
                        ConfigUtils.moduleDPCDDGlobalUseAvatar
                );
            }

            if (chatChannel.equals(ChatsHandler.getChannel("guild"))) {
                SavableGuild guild = GuildUtils.getGuild(PlayerUtils.getOrGetSavableUser(player));

                if (guild == null) return;

                MessagingUtils.sendDiscordEBMessage(
                        new DiscordMessage(
                                player,
                                TextUtils.formatted(ConfigUtils.moduleDPCDDGuildTitle
                                        .replace("%guild_name%", guild.name)
                                        .replace("%leader_absolute%", PlayerUtils.getOrGetSavableUser(guild.leaderUUID).latestName)
                                        .replace("%leader_normal%", PlayerUtils.getOffOnRegDiscord(PlayerUtils.getOrGetSavableUser(guild.leaderUUID)))
                                        .replace("%leader_display%", PlayerUtils.getOffOnDisplayDiscord(PlayerUtils.getOrGetSavableUser(guild.leaderUUID)))
                                        .replace("%leader_formatted%", PlayerUtils.getJustDisplayDiscord(PlayerUtils.getOrGetSavableUser(guild.leaderUUID)))
                                ),
                                TextUtils.replaceAllPlayerDiscord(ConfigUtils.moduleDPCDDGuildJoins, player),
                                channel.toString()),
                        ConfigUtils.moduleDPCDDGuildUseAvatar
                );
            }

            if (chatChannel.equals(ChatsHandler.getChannel("party"))) {
                Party party = PartyUtils.getParty(PlayerUtils.getOrGetSavableUser(player).uuid);

                if (party == null) return;

                MessagingUtils.sendDiscordEBMessage(
                        new DiscordMessage(
                                player,
                                TextUtils.formatted(ConfigUtils.moduleDPCDDPartyTitle
                                        .replace("%leader_absolute%", PlayerUtils.getOrGetSavableUser(party.leaderUUID).latestName)
                                        .replace("%leader_normal%", PlayerUtils.getOffOnRegDiscord(PlayerUtils.getOrGetSavableUser(party.leaderUUID)))
                                        .replace("%leader_display%", PlayerUtils.getOffOnDisplayDiscord(PlayerUtils.getOrGetSavableUser(party.leaderUUID)))
                                        .replace("%leader_formatted%", PlayerUtils.getJustDisplayDiscord(PlayerUtils.getOrGetSavableUser(party.leaderUUID)))
                                ),
                                TextUtils.replaceAllPlayerDiscord(ConfigUtils.moduleDPCDDPartyJoins, player),
                                channel.toString()),
                        ConfigUtils.moduleDPCDDPartyUseAvatar
                );
            }
        }
    }

    public void sendDiscordLeaveChannel(CommandSender player, ChatChannel type, String identifier) {
        if (! ConfigUtils.moduleDEnabled) return;

        TreeSet<Long> channels = getChannelsByData(type, identifier);

        for (Long channel : channels) {
            if (type.equals(ChatsHandler.getChannel("local"))) {
                MessagingUtils.sendDiscordEBMessage(
                        new DiscordMessage(
                                player,
                                TextUtils.formatted(ConfigUtils.moduleDPCDDLocalTitle.replace("%server%", PlayerUtils.getServer(player))),
                                TextUtils.replaceAllPlayerDiscord(ConfigUtils.moduleDPCDDLocalLeaves, player),
                                channel.toString()),
                        ConfigUtils.moduleDPCDDLocalUseAvatar
                );
            }

            if (type.equals(ChatsHandler.getChannel("global"))) {
                MessagingUtils.sendDiscordEBMessage(
                        new DiscordMessage(
                                player,
                                TextUtils.formatted(ConfigUtils.moduleDPCDDGlobalTitle.replace("%server%", "network")),
                                TextUtils.replaceAllPlayerDiscord(ConfigUtils.moduleDPCDDGlobalLeaves, player),
                                channel.toString()),
                        ConfigUtils.moduleDPCDDGlobalUseAvatar
                );
            }

            if (type.equals(ChatsHandler.getChannel("guild"))) {
                SavableGuild guild = GuildUtils.getGuild(PlayerUtils.getOrGetSavableUser(player));

                if (guild == null) return;

                MessagingUtils.sendDiscordEBMessage(
                        new DiscordMessage(
                                player,
                                TextUtils.formatted(ConfigUtils.moduleDPCDDGuildTitle
                                        .replace("%guild_name%", guild.name)
                                        .replace("%leader_absolute%", PlayerUtils.getOrGetSavableUser(guild.leaderUUID).latestName)
                                        .replace("%leader_normal%", PlayerUtils.getOffOnRegDiscord(PlayerUtils.getOrGetSavableUser(guild.leaderUUID)))
                                        .replace("%leader_display%", PlayerUtils.getOffOnDisplayDiscord(PlayerUtils.getOrGetSavableUser(guild.leaderUUID)))
                                        .replace("%leader_formatted%", PlayerUtils.getJustDisplayDiscord(PlayerUtils.getOrGetSavableUser(guild.leaderUUID)))
                                ),
                                TextUtils.replaceAllPlayerDiscord(ConfigUtils.moduleDPCDDGuildLeaves, player),
                                channel.toString()),
                        ConfigUtils.moduleDPCDDGuildUseAvatar
                );
            }

            if (type.equals(ChatsHandler.getChannel("party"))) {
                Party party = PartyUtils.getParty(PlayerUtils.getOrGetSavableUser(player).uuid);

                if (party == null) return;

                MessagingUtils.sendDiscordEBMessage(
                        new DiscordMessage(
                                player,
                                TextUtils.formatted(ConfigUtils.moduleDPCDDPartyTitle
                                        .replace("%leader_absolute%", PlayerUtils.getOrGetSavableUser(party.leaderUUID).latestName)
                                        .replace("%leader_normal%", PlayerUtils.getOffOnRegDiscord(PlayerUtils.getOrGetSavableUser(party.leaderUUID)))
                                        .replace("%leader_display%", PlayerUtils.getOffOnDisplayDiscord(PlayerUtils.getOrGetSavableUser(party.leaderUUID)))
                                        .replace("%leader_formatted%", PlayerUtils.getJustDisplayDiscord(PlayerUtils.getOrGetSavableUser(party.leaderUUID)))
                                ),
                                TextUtils.replaceAllPlayerDiscord(ConfigUtils.moduleDPCDDPartyLeaves, player),
                                channel.toString()),
                        ConfigUtils.moduleDPCDDPartyUseAvatar
                );
            }
        }
    }

    public void sendBungeeChannel(long userID, long channelID, String message) {
        User user = StreamLine.getJda().getUserById(userID);

        if (user == null) return;
        if (! isChannel(channelID)) return;

        DataChannel channelData = getChannel(channelID);

        if (! isVerified(userID)) {
            if (channelData.chatChannel.equals(ChatsHandler.getChannel("global"))) {
                if (channelData.identifier.equals("")) {
                    MessagingUtils.sendGlobalMessageFromDiscord(user.getName(), StreamLine.chatConfig.getDefaultFormat(ChatsHandler.getOrGetChat(channelData.chatChannel.name, channelData.identifier).chatChannel, MessageServerType.DISCORD), message);
                } else {
                    MessagingUtils.sendPermissionedMessage(channelData.identifier, StreamLine.chatConfig.getDefaultFormat(ChatsHandler.getOrGetChat(channelData.chatChannel.name, channelData.identifier).chatChannel, MessageServerType.DISCORD)
                            .replace("%sender_display%", user.getName())
                            .replace("%message%", message));
                }
            }

            if (channelData.chatChannel.equals(ChatsHandler.getChannel("local"))) {
                ServerInfo server = StreamLine.getInstance().getProxy().getServerInfo(channelData.identifier);

                if (server == null) return;

                MessagingUtils.sendServerMessageFromDiscord(user.getName(), server, StreamLine.chatConfig.getDefaultFormat(ChatsHandler.getOrGetChat(channelData.chatChannel.name, channelData.identifier).chatChannel, MessageServerType.DISCORD), message);
            }

            if (channelData.chatChannel.equals(ChatsHandler.getChannel("guild"))) {
                SavableGuild guild = GuildUtils.getGuild(channelData.identifier);

                if (guild == null) return;

                GuildUtils.sendChatFromDiscord(user.getName(), guild, StreamLine.chatConfig.getDefaultFormat(ChatsHandler.getOrGetChat(channelData.chatChannel.name, channelData.identifier).chatChannel, MessageServerType.DISCORD), message);
            }

            if (channelData.chatChannel.equals(ChatsHandler.getChannel("party"))) {
                Party party = PartyUtils.getParty(channelData.identifier);

                if (party == null) return;

                PartyUtils.sendChatFromDiscord(user.getName(), party, StreamLine.chatConfig.getDefaultFormat(ChatsHandler.getOrGetChat(channelData.chatChannel.name, channelData.identifier).chatChannel, MessageServerType.DISCORD), message);
            }
        } else {
            SavablePlayer player = PlayerUtils.getOrGetPlayerStatByUUID(StreamLine.discordData.getVerified(userID));

            if (player == null) {
                MessagingUtils.logWarning("Could not send bungee message for " + userID);
                return;
            }

            if (channelData.chatChannel.equals(ChatsHandler.getChannel("global"))) {
                if (channelData.identifier.equals("")) {
                    MessagingUtils.sendGlobalMessageFromDiscord(player
                            , StreamLine.chatConfig.getDefaultFormat(ChatsHandler.getOrGetChat(channelData.chatChannel.name, channelData.identifier).chatChannel, MessageServerType.DISCORD), message);
                } else {
                    MessagingUtils.sendPermissionedMessage(channelData.identifier, StreamLine.chatConfig.getDefaultFormat(ChatsHandler.getOrGetChat(channelData.chatChannel.name, channelData.identifier).chatChannel, MessageServerType.DISCORD)
                            .replace("%sender_display%", PlayerUtils.getJustDisplayBungee(player))
                            .replace("%message%", message));
                }
            }

            if (channelData.chatChannel.equals(ChatsHandler.getChannel("local"))) {
                ServerInfo server = StreamLine.getInstance().getProxy().getServerInfo(channelData.identifier);

                if (server == null) {
                    MessagingUtils.logWarning("Could not send bungee message for " + userID + " | Context : server == null");
                    return;
                }

                MessagingUtils.sendServerMessageFromDiscord(player, server, StreamLine.chatConfig.getDefaultFormat(ChatsHandler.getOrGetChat(channelData.chatChannel.name, channelData.identifier).chatChannel, MessageServerType.DISCORD), message);
            }

            if (channelData.chatChannel.equals(ChatsHandler.getChannel("guild"))) {
                SavableGuild guild = GuildUtils.getGuild(channelData.identifier);

                if (guild == null) {
                    return;
                }

                GuildUtils.sendChatFromDiscord(player, guild, StreamLine.chatConfig.getDefaultFormat(ChatsHandler.getOrGetChat(channelData.chatChannel.name, channelData.identifier).chatChannel, MessageServerType.DISCORD), message);
            }

            if (channelData.chatChannel.equals(ChatsHandler.getChannel("party"))) {
                Party party = PartyUtils.getParty(channelData.identifier);

                if (party == null) return;

                PartyUtils.sendChatFromDiscord(player, party, StreamLine.chatConfig.getDefaultFormat(ChatsHandler.getOrGetChat(channelData.chatChannel.name, channelData.identifier).chatChannel, MessageServerType.DISCORD), message);
            }
        }
    }

    public TreeSet<Long> getChannelsByData(ChatChannel type, String identifier) {
        loadChannels();

        TreeSet<Long> toReturn = new TreeSet<>();

        for (Long id : loadedChannels.keySet()) {
            DataChannel set = loadedChannels.get(id);

//            if (ConfigUtils.debug) MessagingUtils.logInfo("ID: " + id);

            if (type.equals(ChatsHandler.getChannel("global"))) {
                if (set.chatChannel.equals(type)) toReturn.add(id);
                continue;
            }

            try {
                if (set.chatChannel.equals(type) && set.identifier.equals(identifier)) toReturn.add(id);
            } catch (Exception e) {
                if (ConfigUtils.debug) MessagingUtils.logInfo("ID that broke: " + id);
                e.printStackTrace();
            }
        }

        return toReturn;
    }

    public void addVerified(long discordID, String uuid) {
        conf.set("verified." + discordID, uuid);

        saveConfig();
        reloadConfig();
    }

    public void remVerified(long discordID) {
        conf.set("verified." + discordID, null);

        saveConfig();
        reloadConfig();
    }

    public String getVerified(long discordID) {
        reloadConfig();

        return conf.getString("verified." + discordID);
    }

    public boolean isVerified(Long discordID) {
        reloadConfig();

        for (String keys : conf.getSection("verified").getKeys()) {
            if (keys.equals(discordID.toString())) return true;
        }

        return false;
    }

    public void addChannel(long channelID, String type, String identifier, boolean bypass, boolean joins, boolean leaves) {
        addChannel(channelID, new DataChannel(type, identifier, bypass, joins, leaves));
    }

    public void addChannel(long channelID, DataChannel dataChannel) {
        conf.set("channels." + channelID + ".type", dataChannel.chatChannel.name);
        conf.set("channels." + channelID + ".identifier", dataChannel.identifier);
        conf.set("channels." + channelID + ".bypass", dataChannel.bypass);
        conf.set("channels." + channelID + ".joins", dataChannel.joins);
        conf.set("channels." + channelID + ".leaves", dataChannel.leaves);
        saveConfig();
        reloadConfig();
        loadChannels();
    }

    public void remChannel(long channelID) {
        conf.set("channels." + channelID + ".type", null);
        conf.set("channels." + channelID + ".identifier", null);
        conf.set("channels." + channelID + ".bypass", null);
        conf.set("channels." + channelID, null);
        saveConfig();
        reloadConfig();
    }

    public TreeMap<Long, Boolean> ifChannelBypasses(ChatChannel type, String identifier) {
        TreeSet<Long> channels = getChannelsByData(type, identifier);
        TreeMap<Long, Boolean> toReturn = new TreeMap<>();

        for (long l : channels) {
            toReturn.put(l, ifChannelBypassesByChannelId(l));
        }

        return toReturn;
    }

    public boolean ifChannelBypassesByChannelId(long channelID) {
        return getChannel(channelID).bypass;
    }

    public TreeMap<Long, Boolean> ifChannelJoins(ChatChannel type, String identifier) {
        TreeSet<Long> channels = getChannelsByData(type, identifier);
        TreeMap<Long, Boolean> toReturn = new TreeMap<>();

        for (long l : channels) {
            toReturn.put(l, ifChannelJoinsByChannelId(l));
        }

        return toReturn;
    }

    public boolean ifChannelJoinsByChannelId(long channelID) {
        return getChannel(channelID).joins;
    }

    public TreeMap<Long, Boolean> ifChannelLeaves(ChatChannel type, String identifier) {
        TreeSet<Long> channels = getChannelsByData(type, identifier);
        TreeMap<Long, Boolean> toReturn = new TreeMap<>();

        for (long l : channels) {
            toReturn.put(l, ifChannelLeavesByChannelId(l));
        }

        return toReturn;
    }

    public boolean ifChannelLeavesByChannelId(long channelID) {
        return getChannel(channelID).leaves;
    }

    public DataChannel getChannel(long channelID) {
        reloadConfig();

        return new DataChannel(
                conf.getString("channels." + channelID + ".type"),
                conf.getString("channels." + channelID + ".identifier"),
                conf.getBoolean("channels." + channelID + ".bypass"),
                conf.getBoolean("channels." + channelID + ".joins"),
                conf.getBoolean("channels." + channelID + ".leaves")
        );
    }

    public boolean isChannel(Long channelID) {
        reloadConfig();

        for (String keys : conf.getSection("channels").getKeys()) {
            if (keys.equals(channelID.toString())) return true;
        }

        return false;
    }

    public void setObject(String pathTo, Object object) {
        conf.set(pathTo, object);
        saveConfig();
        reloadConfig();
    }

    public int getVerification(String uuid) {
        Random RNG = new Random();
        Integer number = toVerify.get(uuid);

        if (number == null) {
            int v = RNG.nextInt(9999);
            toVerify.put(uuid, v);

            return v;
        }

        return number;
    }

    public boolean tryVerify(String uuid, int verify) {
        int number = getVerification(uuid);

        return number == verify;
    }

    public void doVerify(String uuid, User user, Guild g) {
        SavablePlayer player = PlayerUtils.getOrGetPlayerStatByUUID(uuid);
        if (player == null) return;

        toVerify.remove(uuid);

        addVerified(user.getIdLong(), uuid);
        player.setDiscordID(user.getIdLong());

        SavableGuild guild = GuildUtils.getGuild(player.uuid);

        if (g == null) {
            if (ConfigUtils.debug) MessagingUtils.logInfo("Guild returned null!");
            return;
        }
        Member member = g.getMember(user);
        if (member == null) {
            if (ConfigUtils.debug) MessagingUtils.logInfo("Member returned null!");
            return;
        }

        if (ConfigUtils.debug) MessagingUtils.logInfo("Member " + member.getNickname() + " or " + member.getEffectiveName() + " has ID: " + member.getIdLong());

        try {
            if (ConfigUtils.moduleDPCChangeOnVerify) {
                if (ConfigUtils.debug) MessagingUtils.logInfo(ConfigUtils.moduleDPCChangeOnVerifyTo);
                if (ConfigUtils.debug) MessagingUtils.logInfo(ConfigUtils.moduleDPCChangeOnVerifyType);

                String newName = MessageConfUtils.nullD();
                if (ConfigUtils.moduleDPCChangeOnVerifyType.equals("discord")) {
                    newName = TextUtils.replaceAllPlayerDiscord(ConfigUtils.moduleDPCChangeOnVerifyTo, player)
                            .replace("%player_uuid%", player.uuid)
                            .replace("%guild_uuid%", guild == null ? "" : guild.leaderUUID)
                            .replace("%guild_name%", guild == null ? "" : guild.name);
                }
                if (ConfigUtils.moduleDPCChangeOnVerifyType.equals("bungee")) {
                    newName = TextUtils.replaceAllPlayerBungee(ConfigUtils.moduleDPCChangeOnVerifyTo, player)
                            .replace("%player_uuid%", player.uuid)
                            .replace("%guild_uuid%", guild == null ? "" : guild.leaderUUID)
                            .replace("%guild_name%", guild == null ? "" : guild.name);
                }

                if (ConfigUtils.debug) MessagingUtils.logInfo("New name = " + newName);
                g.modifyNickname(member, newName);
            }
        } catch (HierarchyException e) {
            MessagingUtils.logSevere("Tried to modify a user with higher permissions than me (on Discord)!");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (ConfigUtils.debug) MessagingUtils.logInfo("Roles as String : " + ConfigUtils.moduleDPCOnVerifyAdd);

            for (String roleID : ConfigUtils.moduleDPCOnVerifyAdd) {
                if (ConfigUtils.debug) MessagingUtils.logInfo("Role String : " + roleID);

                Role role = StreamLine.getJda().getRoleById(roleID);
                if (role == null) {
                    MessagingUtils.logInfo("Role (" + roleID + ") was not found!");
                    continue;
                }

                if (ConfigUtils.debug) MessagingUtils.logInfo("Role Name : " + role.getName());

                g.addRoleToMember(user.getIdLong(), role);
            }

            if (ConfigUtils.debug) MessagingUtils.logInfo("Roles as String : " + ConfigUtils.moduleDPCOnVerifyRemove);

            for (String roleID : ConfigUtils.moduleDPCOnVerifyRemove) {
                Role role = StreamLine.getJda().getRoleById(roleID);
                if (role == null) {
                    MessagingUtils.logInfo("Role (" + roleID + ") was not found!");
                    continue;
                }

                g.removeRoleFromMember(user.getIdLong(), role);
            }
        } catch (HierarchyException e) {
            MessagingUtils.logSevere("Tried to modify a user with higher permissions than me (on Discord)!");
        } catch (Exception e) {
            e.printStackTrace();
        }

        reloadConfig();
    }

    public void addToBoostQueue(SavableUser user) {
        reloadConfig();
        List<String> boostQueueUUIDs = conf.getStringList("boosters.queue");
//        if (boostQueueUUIDs == null) boostQueueUUIDs = new ArrayList<>();

        boostQueueUUIDs.add(user.uuid);
        conf.set("boosters.queue", boostQueueUUIDs);
        saveConfig();
        reloadConfig();
    }

    public void remFromBoostQueue(SavableUser user) {
        reloadConfig();
        List<String> boostQueueUUIDs = conf.getStringList("boosters.queue");
//        if (boostQueueUUIDs == null) boostQueueUUIDs = new ArrayList<>();

        boostQueueUUIDs.remove(user.uuid);
        conf.set("boosters.queue", boostQueueUUIDs);
        saveConfig();
        reloadConfig();
    }

    public TreeList<String> getBoostQueue() {
        reloadConfig();
        return new TreeList<>(conf.getStringList("boosters.queue"));
    }
}
