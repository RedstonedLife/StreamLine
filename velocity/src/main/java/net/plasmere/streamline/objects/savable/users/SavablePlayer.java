package net.plasmere.streamline.objects.savable.users;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.player.PlayerSettings;
import com.velocitypowered.api.proxy.player.SkinParts;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.title.Title;
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
//import org.mariuszgromada.math.mxparser.Function;

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
    public Player player;
    public ChatChannel chatChannel;
    public String chatIdentifier;
    public long discordID;
    public int bypassFor;

    public int defaultLevel = ConfigUtils.statsExperienceStartingLevel();

    public String getLatestIP() {
        if (this.player == null) return MessageConfUtils.nullB();

        String ipSt = player.getRemoteAddress().toString().replace("/", "");
        String[] ipSplit = ipSt.split(":");
        ipSt = ipSplit[0];

        return ipSt;
    }

    public SavablePlayer(Player player) {
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
        DataSource.updatePlayerData(this);
        DataSource.updatePlayerChat(this);
        DataSource.updatePlayerExperience(this);
    }

    public static ChatChannel parseChatLevel(String string) {
        return ChatsHandler.getChannel(string);
    }

    public static void sendMessageFormatted(CommandSource sender, String formatFrom, ChatChannel newLevel, ChatChannel oldLevel) {
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

        DataSource.updatePlayerChat(this);
        return newIdentifier;
    }

    public ChatChannel setChatChannel(String channel) {
        ChatChannel newLevel = parseChatLevel(channel);

        this.chatChannel = newLevel;
        //        saveAll();

        DataSource.updatePlayerChat(this);
        return newLevel;
    }

    public void addName(String name){
        //        loadValues();
        DataSource.updatePlayerData(this);
        DataSource.addNameToPlayer(this, name);
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
        saveAll();
    }

    public void setLatestIP(Player player) {
        setLatestIP(PlayerUtils.parsePlayerIP(player));
    }

    public void addIP(String ip){
        //        loadValues();
        DataSource.updatePlayerData(this);
        DataSource.addIpToPlayer(this, ip);
        if (ipList.contains(ip)) return;

        ipList.add(ip);
        //        saveAll();
    }

    public void addIP(Player player){
        addIP(PlayerUtils.parsePlayerIP(player));
    }

    public void removeIP(String ip){
        //        loadValues();
        if (! ipList.contains(ip)) return;

        ipList.remove(ip);
        //        saveAll();
    }

    public void removeIP(Player player){
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
        DataSource.updatePlayerExperience(this);
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

    public void setDisplayName(String name) {
//        loadValues();
        displayName = name;
        //        saveAll();
    }

    public void connect(ServerInfo target) {
        if (online) {
            Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).createConnectionRequest(StreamLine.getProxy().getServer(target.getName()).get()).connect();
        }
    }

    public void connect(RegisteredServer target) {
        if (online) {
            Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).createConnectionRequest(target).connect();
        }
    }

    
    public ServerConnection getServer() {
        if (online) {
            return Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).getCurrentServer().get();
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
            Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).spoofChatInput(message);
        }
    }
    
    public String getUUID() {
        return uuid;
    }

    public UUID getUniqueId() {
        return UUID.fromString(uuid);
    }

    
    public Locale getLocale() {
        if (online) {
            return Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).getEffectiveLocale();
        }
        return null;
    }

    
    public byte getViewDistance() {
        if (online) {
            return Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).getPlayerSettings().getViewDistance();
        }
        return -1;
    }

    
    public PlayerSettings.ChatMode getChatMode() {
        if (online) {
            return Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).getPlayerSettings().getChatMode();
        }
        return null;
    }

    
    public boolean hasChatColors() {
        if (online) {
            return Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).getPlayerSettings().hasChatColors();
        }
        return false;
    }

    
    public SkinParts getSkinParts() {
        if (online) {
            return Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).getPlayerSettings().getSkinParts();
        }
        return null;
    }

    
    public PlayerSettings.MainHand getMainHand() {
        if (online) {
            return Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).getPlayerSettings().getMainHand();
        }
        return null;
    }

    
    public void setTabHeader(TextComponent header, TextComponent footer) {
        if (online) {
            Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).getTabList().setHeaderAndFooter(header, footer);
        }
    }

    public void resetTabHeader() {
        if (online) {
            Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).getTabList().clearHeaderAndFooter();
        }
    }

    public void sendTitle(Title title) {
        if (online) {
            Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).showTitle(title);
        }
    }
    
    public String getName() {
        return latestName;
    }

    public void sendMessage(Component message) {
        if (online) {
            Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).sendMessage(message);
        }
    }
    public void sendMessage(String message) {
        if (online) {
            Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).sendMessage(TextUtils.codedText(message));
        }
    }
    
    public boolean hasPermission(String permission) {
        if (online) {
            return Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).hasPermission(permission);
        }
        return false;
    }

    @Deprecated
    public InetSocketAddress getAddress() {
        if (online) {
            return Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).getRemoteAddress();
        }
        return InetSocketAddress.createUnresolved(latestIP, new Random().nextInt(26666));
    }

    
    public SocketAddress getSocketAddress() {
        if (online) {
            Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).getRemoteAddress();
        }
        return InetSocketAddress.createUnresolved(latestIP, new Random().nextInt(26666));
    }

    @Deprecated
    public void disconnect(String reason) {
        if (online) {
            Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).disconnect(TextUtils.codedText(reason));
        }
    }
    
    public boolean isConnected() {
        return online;
    }
}
