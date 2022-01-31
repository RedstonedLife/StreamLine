package net.plasmere.streamline.objects.savable.users;

import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.score.Scoreboard;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.chats.ChatChannel;
import net.plasmere.streamline.objects.chats.ChatsHandler;
import net.plasmere.streamline.objects.savable.SavableAdapter;
import net.plasmere.streamline.utils.MathUtils;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.TextUtils;
import net.plasmere.streamline.utils.sql.DataSource;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.*;

public class SavablePlayer extends SavableUser {
    public int totalXP;
    public int currentXP;
    public int level;
    public int playSeconds;
    public String latestIP;
    public List<String> ipList;
    public List<String> nameList;
    public boolean muted;
    public Date mutedTill;
    public ProxiedPlayer player;
    public ChatChannel chatChannel;
    public String chatIdentifier;
    public long discordID;
    public int bypassFor;

    public int defaultLevel = ConfigUtils.statsExperienceStartingLevel();

    public String getLatestIP() {
        if (this.player == null) return MessageConfUtils.nullB();

        String ipSt = player.getSocketAddress().toString().replace("/", "");
        String[] ipSplit = ipSt.split(":");
        ipSt = ipSplit[0];

        return ipSt;
    }

    public SavablePlayer(ProxiedPlayer player) {
        super(player, SavableAdapter.Type.PLAYER);
        this.player = player;
        setLatestIP(getLatestIP());
    }

    public SavablePlayer(String thing){
        super(PlayerUtils.createCheck(thing), SavableAdapter.Type.PLAYER);
    }

    public SavablePlayer(UUID uuid) {
        super(uuid.toString(), SavableAdapter.Type.PLAYER);
    }

    @Override
    public List<String> getTagsFromConfig() {
        return ConfigUtils.tagsDefaults();
    }

    @Override
    public void populateMoreDefaults() {
        // Ips.
        latestIP = getOrSetDefault("player.ips.latest", getLatestIP());
        ipList = getOrSetDefault("player.ips.list", new ArrayList<>());
        // Names.
        nameList = getOrSetDefault("player.names", new ArrayList<>());
        // Stats.
        level = getOrSetDefault("player.stats.level", defaultLevel);
        totalXP = getOrSetDefault("player.stats.experience.total", ConfigUtils.statsExperienceStartingXP());
        currentXP = getOrSetDefault("player.stats.experience.current", ConfigUtils.statsExperienceStartingXP());
        playSeconds = getOrSetDefault("player.stats.playtime.seconds", 0);
        // Punishments.
        muted = getOrSetDefault("player.punishments.mute.toggled", false);
        mutedTill = new Date(getOrSetDefault("player.punishments.mute.expires", new Date(0L).toInstant().toEpochMilli()));
        // Chats.
        if (ConfigUtils.customChats()) {
            chatChannel = parseChatLevel(getOrSetDefault("player.chat.channel", StreamLine.chatConfig.getDefaultChannel()));
            chatIdentifier = getOrSetDefault("player.chat.identifier", StreamLine.chatConfig.getDefaultIdentifier());
            bypassFor = getOrSetDefault("player.chat.bypass-for", 0);
        }
        // Discord.
        discordID = getOrSetDefault("player.discord.id", 0L);
    }

    @Override
    public void loadMoreValues() {
        // Ips.
        latestIP = getOrSetDefault("player.ips.latest", latestIP);
        ipList = getOrSetDefault("player.ips.list", ipList);
        // Names.
        nameList = getOrSetDefault("player.names", nameList);
        // Stats.
        level = getOrSetDefault("player.stats.level", level);
        totalXP = getOrSetDefault("player.stats.experience.total", totalXP);
        currentXP = getOrSetDefault("player.stats.experience.current", currentXP);
        playSeconds = getOrSetDefault("player.stats.playtime.seconds", playSeconds);
        // Punishments.
        muted = getOrSetDefault("player.punishments.mute.toggled", muted);
        mutedTill = new Date(getOrSetDefault("player.punishments.mute.expires", mutedTill.toInstant().toEpochMilli()));
        // Chats.
        if (ConfigUtils.customChats()) {
            chatChannel = parseChatLevel(getOrSetDefault("player.chat.channel", (chatChannel != null ? chatChannel.name : StreamLine.chatConfig.getDefaultChannel())));
            chatIdentifier = getOrSetDefault("player.chat.identifier", chatIdentifier);
            bypassFor = getOrSetDefault("player.chat.bypass-for", bypassFor);
        }
        // Discord.
        discordID = getOrSetDefault("player.discord.id", discordID);
    }

    @Override
    public void saveMore() {
        // Ips.
        set("player.ips.latest", latestIP);
        set("player.ips.list", ipList);
        // Names.
        set("player.names", nameList);
        // Stats.
        set("player.stats.level", level);
        set("player.stats.experience.total", totalXP);
        set("player.stats.experience.current", currentXP);
        set("player.stats.playtime.seconds", playSeconds);
        // Punishments.
        set("player.punishments.mute.toggled", muted);
        set("player.punishments.mute.expires", mutedTill.toInstant().getEpochSecond());
        // Chats.
        if (ConfigUtils.customChats()) {
            set("player.chat.channel", (chatChannel != null ? chatChannel.name : StreamLine.chatConfig.getDefaultChannel()));
            set("player.chat.identifier", chatIdentifier);
            set("player.chat.bypass-for", bypassFor);
        }
        // Discord.
        set("player.discord.id", discordID);

        // Update Player.
        if (ConfigUtils.moduleDBUse()) {
            DataSource.updatePlayerData(this);
            DataSource.updatePlayerChat(this);
            DataSource.updatePlayerExperience(this);
        }
    }

    public static ChatChannel parseChatLevel(String string) {
        return ChatsHandler.getChannel(string);
    }

    public static void sendMessageFormatted(CommandSender sender, String formatFrom, ChatChannel newLevel, ChatChannel oldLevel) {
        MessagingUtils.sendBUserMessage(sender, formatFrom
                .replace("%new_channel%", newLevel.name)
                .replace("%old_channel%", oldLevel.name)
        );
    }

    public void setChat(String channel, String identifier) {
        MessagingUtils.sendBUserMessage(findSender(), MessageConfUtils.chatChannelsSwitch()
                .replace("%old_channel%", chatChannel.name)
                .replace("%new_channel%", channel)
                .replace("%old_identifier%", chatIdentifier)
                .replace("%new_identifier%", identifier)
        );

        setChatChannel(channel);
        setChatIdentifier(identifier);
    }

    public int setBypassFor(int set) {
//        loadValues();
        this.bypassFor = set;
//        saveAll();

        if (this.online) {
            MessagingUtils.sendBUserMessage(this.player, MessageConfUtils.bypassPCMessage().replace("%messages%", String.valueOf(this.bypassFor)));
        }

        return this.bypassFor;
    }

    public int tickBypassFor() {
//        loadValues();
        this.bypassFor --;
//        //        saveAll();

        return this.bypassFor;
    }

    public String setChatIdentifier(String newIdentifier) {
        this.chatIdentifier = newIdentifier;
        //        saveAll();

        if (ConfigUtils.moduleDBUse()) {
            DataSource.updatePlayerChat(this);
        }
        return newIdentifier;
    }

    public ChatChannel setChatChannel(String channel) {
        ChatChannel newLevel = parseChatLevel(channel);

        this.chatChannel = newLevel;
        //        saveAll();

        if (ConfigUtils.moduleDBUse()) {
            DataSource.updatePlayerChat(this);
        }
        return newLevel;
    }

    public void addName(String name){
        //        loadValues();
        if (ConfigUtils.moduleDBUse()) {
            DataSource.updatePlayerData(this);
            DataSource.addNameToPlayer(this, name);
        }
        if (nameList.contains(name)) return;

        nameList.add(name);
        //        saveAll();
    }

    public void removeName(String name){
        //        loadValues();
        if (! nameList.contains(name)) return;

        nameList.remove(name);
        //        saveAll();
    }

    public void setLatestIP(String ip) {
        this.latestIP = ip;
//        saveAll();
    }

    public void setLatestIP(ProxiedPlayer player) {
        setLatestIP(PlayerUtils.parsePlayerIP(player));
    }

    public void addIP(String ip){
        //        loadValues();
        if (ConfigUtils.moduleDBUse()) {
            DataSource.updatePlayerData(this);
            DataSource.addIpToPlayer(this, ip);
        }
        if (ipList.contains(ip)) return;

        ipList.add(ip);
        //        saveAll();
    }

    public void addIP(ProxiedPlayer player){
        addIP(PlayerUtils.parsePlayerIP(player));
    }

    public void removeIP(String ip){
        //        loadValues();
        if (! ipList.contains(ip)) return;

        ipList.remove(ip);
        //        saveAll();
    }

    public void removeIP(ProxiedPlayer player){
        removeIP(PlayerUtils.parsePlayerIP(player));
    }

    public void addPlaySecond(int amount){
        //        loadValues();
        setPlaySeconds(playSeconds + amount);
    }

    public void setPlaySeconds(int amount){
        playSeconds = amount;
        //        saveAll();
    }

    public double getPlayMinutes(){
        //        loadValues();
        return playSeconds / (60.0d);
    }

    public double getPlayHours(){
        //        loadValues();
        return playSeconds / (60.0d * 60.0d);
    }

    public double getPlayDays(){
        //        loadValues();
        return playSeconds / (60.0d * 60.0d * 24.0d);
    }

    public String getPlaySecondsAsString(){
        //        loadValues();
        return TextUtils.truncate(String.valueOf(this.playSeconds), 2);
    }

    public String getPlayMinutesAsString(){
        //        loadValues();
        return TextUtils.truncate(String.valueOf(getPlayMinutes()), 2);
    }

    public String getPlayHoursAsString(){
        //        loadValues();
        return TextUtils.truncate(String.valueOf(getPlayHours()), 2);
    }

    public String getPlayDaysAsString(){
        //        loadValues();
        return TextUtils.truncate(String.valueOf(getPlayDays()), 2);
    }

    /*
   Experience required =
   2 × current_level + 7 (for levels 0–15)
   5 × current_level – 38 (for levels 16–30)
   9 × current_level – 158 (for levels 31+)
    */

    public int getNeededXp(){
        int needed = 0;

        String function = TextUtils.replaceAllPlayerDiscord(ConfigUtils.statsExperienceEquation(), this)
                        .replace("%default_level%", String.valueOf(defaultLevel));

        needed = (int) Math.round(MathUtils.eval(function));

        return needed;
    }

    public int xpUntilNextLevel(){
        //        loadValues();
        return getNeededXp() - this.totalXP;
    }

    public void addTotalXP(int amount){
        //        loadValues();
        setTotalXP(amount + this.totalXP);
    }

    public void setTotalXP(int amount){
        //        loadValues();
        this.totalXP = amount;

        while (xpUntilNextLevel() <= 0) {
            int setLevel = this.level + 1;
            this.level = setLevel;
        }

        currentXP = getCurrentXP();
        if (ConfigUtils.moduleDBUse()) {
            DataSource.updatePlayerExperience(this);
        }
        //        saveAll();
    }

    public int getCurrentLevelXP(){
        //        loadValues();
        int xpTill = 0;
        for (int i = 0; i <= this.level; i++) {
            xpTill += getNeededXp();
        }

        return xpTill;
    }

    public int getCurrentXP(){
        //        loadValues();
        return this.totalXP - getCurrentLevelXP();
    }

    public void setMuted(boolean value) {
        muted = value;
        //        saveAll();
    }

    public void setMutedTill(long value) {
        mutedTill = new Date(value);
        //        saveAll();
    }

    public void removeMutedTill(){
        mutedTill = new Date(0L);
        //        saveAll();
    }

    public void updateMute(boolean set, Date newMutedUntil){
        setMuted(set);
        setMutedTill(newMutedUntil.getTime());
    }

    public void toggleMuted() {
        //        loadValues();
        setMuted(! muted);
    }

    public void setDiscordID(long id) {
        this.discordID = id;
        //        saveAll();
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void connect(ServerInfo target) {
        if (online) {
            Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).connect(target);
        }
    }

    
    public ServerInfo getServer() {
        if (online) {
            return Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).getServer().getInfo();
        }
        return null;
    }


    public long getPing() {
        if (online) {
            return Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).getPing();
        }
        return -1;
    }
    
    public void chat(String message) {
        if (online) {
            Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).chat(message);
        }
    }
    
    public String getUUID() {
        return uuid;
    }

    public UUID getUniqueId() {
        return UUID.fromString(uuid);
    }

    public void setDisplayName(String name) {
        this.displayName = name;
    }

    public void sendMessage(ChatMessageType position, BaseComponent... message) {
        if (online) {
            Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).sendMessage(position, message);
        }
    }

    public void sendMessage(ChatMessageType position, BaseComponent message) {
        if (online) {
            Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).sendMessage(position, message);
        }
    }

    public void sendMessage(UUID sender, BaseComponent... message) {
        if (online) {
            Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).sendMessage(sender, message);
        }
    }

    public void sendMessage(UUID sender, BaseComponent message) {
        if (online) {
            Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).sendMessage(sender, message);
        }
    }

    public void connect(ServerInfo target, ServerConnectEvent.Reason reason) {
        if (online) {
            Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).connect(target, reason);
        }
    }


    public void connect(ServerInfo target, Callback<Boolean> callback) {
        if (online) {
            Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).connect(target, callback);
        }
    }


    public void connect(ServerInfo target, Callback<Boolean> callback, ServerConnectEvent.Reason reason) {
        if (online) {
            Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).connect(target, callback, reason);
        }
    }


    public void connect(ServerConnectRequest request) {
        if (online) {
            Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).connect(request);
        }
    }


    public void sendData(String channel, byte[] data) {
        if (online) {
            Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).sendData(channel, data);
        }
    }


    public PendingConnection getPendingConnection() {
        if (online) {
            return Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).getPendingConnection();
        }
        return null;
    }

    public ServerInfo getReconnectServer() {
        if (online) {
            return Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).getReconnectServer();
        }
        return null;
    }


    public void setReconnectServer(ServerInfo server) {
        if (online) {
            Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).setReconnectServer(server);
        }
    }

    public Locale getLocale() {
        if (online) {
            return Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).getLocale();
        }
        return null;
    }


    public byte getViewDistance() {
        if (online) {
            return Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).getViewDistance();
        }
        return -1;
    }


    public ProxiedPlayer.ChatMode getChatMode() {
        if (online) {
            return Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).getChatMode();
        }
        return null;
    }


    public boolean hasChatColors() {
        if (online) {
            return Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).hasChatColors();
        }
        return false;
    }


    public SkinConfiguration getSkinParts() {
        if (online) {
            return Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).getSkinParts();
        }
        return null;
    }


    public ProxiedPlayer.MainHand getMainHand() {
        if (online) {
            return Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).getMainHand();
        }
        return null;
    }


    public void setTabHeader(BaseComponent header, BaseComponent footer) {
        if (online) {
            Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).setTabHeader(header, footer);
        }
    }


    public void setTabHeader(BaseComponent[] header, BaseComponent[] footer) {
        if (online) {
            Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).setTabHeader(header, footer);
        }
    }


    public void resetTabHeader() {
        if (online) {
            Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).resetTabHeader();
        }
    }


    public void sendTitle(Title title) {
        if (online) {
            Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).sendTitle(title);
        }
    }


    public boolean isForgeUser() {
        if (online) {
            return Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).isForgeUser();
        }
        return false;
    }


    public Map<String, String> getModList() {
        if (online) {
            return Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).getModList();
        }
        return null;
    }


    public Scoreboard getScoreboard() {
        if (online) {
            return Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).getScoreboard();
        }
        return null;
    }


    public String getName() {
        return latestName;
    }

    @Deprecated

    public void sendMessage(String message) {
        if (online) {
            Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).sendMessage(message);
        }
    }

    @Deprecated

    public void sendMessages(String... messages) {
        if (online) {
            Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).sendMessages(messages);
        }
    }


    public void sendMessage(BaseComponent... message) {
        if (online) {
            Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).sendMessage(message);
        }
    }


    public void sendMessage(BaseComponent message) {
        if (online) {
            Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).sendMessage(message);
        }
    }


    public Collection<String> getGroups() {
        if (online) {
            return Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).getGroups();
        }
        return null;
    }


    public void addGroups(String... groups) {
        if (online) {
            Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).addGroups(groups);
        }
    }


    public void removeGroups(String... groups) {
        if (online) {
            Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).removeGroups(groups);
        }
    }


    public boolean hasPermission(String permission) {
        if (online) {
            return Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).hasPermission(permission);
        }
        return false;
    }


    public void setPermission(String permission, boolean value) {
        if (online) {
            Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).setPermission(permission, value);
        }
    }


    public Collection<String> getPermissions() {
        if (online) {
            return Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).getPermissions();
        }
        return null;
    }

    @Deprecated
    public InetSocketAddress getAddress() {
        if (online) {
            return Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).getAddress();
        }
        return InetSocketAddress.createUnresolved(latestIP, new Random().nextInt(26666));
    }


    public SocketAddress getSocketAddress() {
        if (online) {
            Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).getSocketAddress();
        }
        return InetSocketAddress.createUnresolved(latestIP, new Random().nextInt(26666));
    }

    @Deprecated
    public void disconnect(String reason) {
        if (online) {
            Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).disconnect(reason);
        }
    }


    public void disconnect(BaseComponent... reason) {
        if (online) {
            Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).disconnect(reason);
        }
    }


    public void disconnect(BaseComponent reason) {
        if (online) {
            Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).disconnect(reason);
        }
    }


    public boolean isConnected() {
        return online;
    }


    public Connection.Unsafe unsafe() {
        if (online) {
            return Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).unsafe();
        }
        return null;
    }
}
