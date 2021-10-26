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
import net.plasmere.streamline.objects.chats.Chat;
import net.plasmere.streamline.objects.chats.ChatChannel;
import net.plasmere.streamline.objects.chats.ChatsHandler;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.PluginUtils;
import net.plasmere.streamline.utils.TextUtils;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.*;

public class SavablePlayer extends SavableUser {
    public int totalXP;
    public int currentXP;
    public int lvl;
    public int playSeconds;
    public String ips;
    public String names;
    public String latestIP;
    public List<String> ipList;
    public List<String> nameList;
    public boolean muted;
    public Date mutedTill;
    public Player player;
    public ChatChannel chatChannel;
    public String chatIdentifier;
    public long discordID;

    public int defaultLevel = 1;

    public SavablePlayer(Player player) {
        super(player.getUniqueId().toString());
        this.player = player;
    }

    public SavablePlayer(Player player, boolean create){
        super(player.getUniqueId().toString(), create);
        this.player = player;
    }

    public SavablePlayer(String thing){
        super(PlayerUtils.createCheck(thing), false);
    }

    public SavablePlayer(String thing, boolean createNew){
        super(PlayerUtils.createCheck(thing), createNew);
    }

    public SavablePlayer(UUID uuid) {
        super(uuid.toString(), false);
    }

    public boolean onlineCheck(){
        for (Player p : StreamLine.getInstance().getProxy().getAllPlayers()){
            if (p.getUsername().equals(this.latestName)) return true;
        }

        return false;
    }

    @Override
    public void preConstruct(String string) {
        this.player = PlayerUtils.getPPlayerByUUID(string);

        if (this.player == null) {
            this.uuid = string;
            this.online = false;
            return;
        }

        String ipSt = player.getRemoteAddress().toString().replace("/", "");
        String[] ipSplit = ipSt.split(":");
        ipSt = ipSplit[0];

        this.uuid = player.getUniqueId().toString();
        this.latestIP = ipSt;
        this.latestName = player.getUsername();

        this.ips = ipSt;
        this.names = player.getUsername();
        this.online = onlineCheck();

        String toLatestVersion = "";

        if (StreamLine.viaHolder.enabled) {
            if (StreamLine.geyserHolder.enabled && StreamLine.geyserHolder.file.hasProperty(this.uuid)) {
                toLatestVersion = "GEYSER";
            } else {
                toLatestVersion = StreamLine.viaHolder.getProtocal(UUID.fromString(this.uuid)).getName();
            }
        } else {
            toLatestVersion = "Not Enabled";
        }


        this.latestVersion = toLatestVersion;
        updateKeyNoLoad("latest-version", toLatestVersion);
    }

    @Override
    public int getPointsFromConfig(){
        return ConfigUtils.pointsDefault;
    }

    @Override
    public TreeSet<String> addedProperties() {
        TreeSet<String> defaults = new TreeSet<>();
        defaults.add("ips=" + ips);
        defaults.add("names=" + names);
        defaults.add("latest-ip=" + latestIP);
        defaults.add("lvl=" + defaultLevel);
        defaults.add("total-xp=0");
        defaults.add("currentXP=0");
        defaults.add("playtime=0");
        defaults.add("muted=false");
        defaults.add("muted-till=");
        defaults.add("chat-level=LOCAL");
        defaults.add("chat-identifier=network");
        defaults.add("discord-id=");
        //defaults.add("");
        return defaults;
    }

    @Override
    public List<String> getTagsFromConfig(){
        return ConfigUtils.tagsDefaults;
    }

    @Override
    public void loadMoreVars() {
        this.online = onlineCheck();
        if (! this.online) this.latestVersion = getFromKey("latest-version");

        this.ips = getFromKey("ips");
        this.names = getFromKey("names");
        this.latestIP = getFromKey("latest-ip");
        this.ipList = loadIPs();
        this.nameList = loadNames();
        this.playSeconds = Integer.parseInt(getFromKey("playtime"));
        this.muted = Boolean.parseBoolean(getFromKey("muted"));
        try {
            this.mutedTill = new Date(Long.parseLong(getFromKey("muted-till")));
        } catch (Exception e) {
            this.mutedTill = null;
        }

        this.lvl = Integer.parseInt(getFromKey("lvl"));
        this.totalXP = Integer.parseInt(getFromKey("total-xp"));
        this.currentXP = Integer.parseInt(getFromKey("current-xp"));

        this.chatChannel = parseChatLevel(getFromKey("chat-channel"));
        this.chatIdentifier = getFromKey("chat-identifier");

        try {
            this.discordID = Long.parseLong(getFromKey("discord-id"));
        } catch (Exception e) {
            this.discordID = 0L;
        }
    }

    public static ChatChannel parseChatLevel(String string) {
        return ChatsHandler.getChannel(string);
    }

    @Override
    TreeMap<String, String> addedUpdatableKeys() {
        TreeMap<String, String> thing = new TreeMap<>();

        thing.put("latestip", "latest-ip");
        thing.put("latestname", "latest-name");
        thing.put("displayname", "display-name");
        thing.put("latestversion", "latest-version");
        thing.put("xp", "total-xp");
        thing.put("totalXP", "total-xp");
        thing.put("currentXP", "current-xp");
        thing.put("chat-level", "chat-channel");

        return thing;
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

    public String setChatIdentifier(String newIdentifier) {
        this.chatIdentifier = newIdentifier;
        updateKey("chat-identifier", this.chatIdentifier);

        return newIdentifier;
    }

    public ChatChannel setChatChannel(String channel) {
        ChatChannel newLevel = parseChatLevel(channel);

        this.chatChannel = newLevel;
        updateKey("chat-channel", newLevel.name);

        return newLevel;
    }

    public void tryAddNewName(String name){
        if (nameList == null) this.nameList = new ArrayList<>();

        if (nameList.contains(name)) return;

        this.nameList.add(name);

        this.names = stringifyList(nameList, ",");

        updateKey("names", this.names);
    }

    public void tryRemName(String name){
        if (nameList == null) this.nameList = new ArrayList<>();

        if (! nameList.contains(name)) return;

        this.nameList.remove(name);

        this.names = stringifyList(nameList, ",");

        updateKey("names", this.names);
    }

    public void tryAddNewIP(String ip){
        if (ipList == null) this.ipList = new ArrayList<>();

        if (ipList.contains(ip)) return;

        this.ipList.add(ip);

        this.ips = stringifyList(ipList, ",");

        updateKey("ips", this.ips);
    }

    public void tryAddNewIP(Player player){
        String ipSt = player.getRemoteAddress().toString().replace("/", "");
        String[] ipSplit = ipSt.split(":");
        ipSt = ipSplit[0];

        tryAddNewIP(ipSt);
    }

    public void tryRemIP(String ip){
        if (ipList == null) this.ipList = new ArrayList<>();

        if (! ipList.contains(ip)) return;

        this.ipList.remove(ip);

        this.ips = stringifyList(ipList, ",");

        updateKey("ips", this.ips);
    }

    public void tryRemIP(Player player){
        String ipSt = player.getRemoteAddress().toString().replace("/", "");
        String[] ipSplit = ipSt.split(":");
        ipSt = ipSplit[0];

        tryRemIP(ipSt);
    }

    public void addPlaySecond(int amount){
        setPlaySeconds(playSeconds + amount);
    }

    public void setPlaySeconds(int amount){
        updateKey("playtime", amount);
    }

    public double getPlayDays(){
        return playSeconds / (60.0d * 60.0d * 24.0d);
    }

    public double getPlayHours(){
        return playSeconds / (60.0d * 60.0d);
    }

    public double getPlayMinutes(){
        return playSeconds / (60.0d);
    }

    public List<String> loadIPs(){
        List<String> thing = new ArrayList<>();

        String search = "ips";

        try {
            if (getFromKey(search) == null) return thing;
            if (getFromKey(search).equals("")) return thing;

            if (! getFromKey(search).contains(",")) {
                thing.add(getFromKey(search));
                return thing;
            }

            for (String t : getFromKey(search).split(",")) {
                if (t == null) continue;
                if (t.equals("")) continue;

                try {
                    thing.add(t);
                } catch (Exception e) {
                    //continue;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return thing;
    }

    public List<String> loadNames(){
        List<String> thing = new ArrayList<>();

        String search = "names";

        try {
            if (getFromKey(search) == null) return thing;
            if (getFromKey(search).equals("")) return thing;

            if (! getFromKey(search).contains(",")) {
                thing.add(getFromKey(search));
                return thing;
            }

            for (String t : getFromKey(search).split(",")) {
                if (t == null) continue;
                if (t.equals("")) continue;

                try {
                    thing.add(t);
                } catch (Exception e) {
                    //continue;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return thing;
    }

    /*
   Experience required =
   2 × current_level + 7 (for levels 0–15)
   5 × current_level – 38 (for levels 16–30)
   9 × current_level – 158 (for levels 31+)
    */

    public int getNeededXp(int fromLevel){
        int needed = 0;

        needed = 2500 + (2500 * (fromLevel - defaultLevel));

        return needed;
    }

    public int xpUntilNextLevel(){
        return getNeededXp(this.lvl + 1) - this.totalXP;
    }

    public void addTotalXP(int amount){
        setTotalXP(amount + this.totalXP);
    }

    public void setTotalXP(int amount){
        int setAmount = amount;
        int required = getNeededXp(this.lvl + 1);

        while (setAmount >= required) {
            setAmount -= required;
            int setLevel = this.lvl + 1;
            updateKey("lvl", setLevel);
        }

        updateKey("total-xp", setAmount);
        updateKey("current-xp", getCurrentXP());
    }

    public int getCurrentLevelXP(){
        int xpTill = 0;
        for (int i = 0; i <= this.lvl; i++) {
            xpTill += getNeededXp(i);
        }

        return xpTill;
    }

    public int getCurrentXP(){
        return this.totalXP - getCurrentLevelXP();
    }

    public void setMuted(boolean value) {
        muted = value;
        updateKey("muted", value);
    }

    public void setMutedTill(long value) {
        mutedTill = new Date(value);
        updateKey("muted-till", value);
    }

    public void removeMutedTill(){
        mutedTill = null;
        updateKey("muted-till", "");
    }

    public void updateMute(boolean set, Date newMutedUntil){
        setMuted(set);
        setMutedTill(newMutedUntil.getTime());
    }

    public void toggleMuted() { setMuted(! muted); }

    public void setDiscordID(long id) {
        this.discordID = id;
        updateKey("discord-id", id);
    }
    
    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String name) {
        updateKey("display-name", name);
    }

    public void connect(ServerInfo target) {
        if (online) {
            Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).createConnectionRequest(StreamLine.getProxy().getServer(target.getName()).get());
        }
    }

    public void connect(RegisteredServer target) {
        if (online) {
            Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).createConnectionRequest(target);
        }
    }

    
    public ServerConnection getServer() {
        if (online) {
            return Objects.requireNonNull(PlayerUtils.getPPlayer(latestName)).getCurrentServer().get().get();
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
