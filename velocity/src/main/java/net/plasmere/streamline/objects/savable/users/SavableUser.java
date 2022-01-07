package net.plasmere.streamline.objects.savable.users;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.savable.SavableAdapter;
import net.plasmere.streamline.objects.savable.SavableFile;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.TextUtils;
import net.plasmere.streamline.utils.sql.DataSource;

import java.io.File;
import java.util.*;

public abstract class SavableUser extends SavableFile {
    private final SavableUser savableUser;

    public File file;
    public String latestName;
    public String displayName;
    public String guild;
    public String party;
    public List<String> tagList;
    public int points;
    public String lastFromUUID;
    public String lastToUUID;
    public String replyToUUID;
    public String lastMessage;
    public String lastToMessage;
    public String lastFromMessage;
    public List<String> ignoredList;
    public List<String> friendList;
    public List<String> pendingToFriendList;
    public List<String> pendingFromFriendList;
    public String latestVersion;
//    public String latestServer;
    public boolean online;
    public boolean sspy;
    public boolean gspy;
    public boolean pspy;
    public boolean viewsc;
    public boolean sc;
    public boolean sspyvs;
    public boolean pspyvs;
    public boolean gspyvs;
    public boolean scvs;
    public String latestServer;

    public SavableUser getSavableUser() {
        return this.savableUser;
    }

    public String findServer() {
        if (this.uuid.equals("%")) {
            return ConfigUtils.consoleServer();
        } else {
            try {
                Player player = PlayerUtils.getPPlayerByUUID(this.uuid);

                if (player == null) return MessageConfUtils.nullB();

                if (player.getCurrentServer().isPresent()) return ConfigUtils.consoleServer();

                return player.getCurrentServer().get().getServerInfo().getName();
            } catch (Exception e) {
                return MessageConfUtils.nullB();
            }
        }
    }

    public Optional<CommandSource> findSenderOptional() {
        if (this.uuid.equals("%")) return Optional.ofNullable(StreamLine.getProxy().getConsoleCommandSource());
        else return Optional.ofNullable(PlayerUtils.getPPlayerByUUID(this.uuid));
    }

    public CommandSource findSender() {
        if (findSenderOptional().isPresent()) {
            return findSenderOptional().get();
        }
        return null;
    }

    public String grabServer() {
        if (this.uuid.equals("%")) return ConfigUtils.consoleServer();
        else {
            if (findSenderOptional().isPresent()) {
                Player player = (Player) (findSenderOptional().get());
                if (player.getCurrentServer().isPresent()) {
                    return player.getCurrentServer().get().getServer().getServerInfo().getName();
                }
            }
        }

        return MessageConfUtils.nullB();
    }

    public String getLatestVersion() {
        if (this instanceof SavableConsole) return /* TODO: ConfigUtils.consoleVersion() */ MessageConfUtils.nullB();
        if (! StreamLine.viaHolder.isPresent()) return MessageConfUtils.nullB();

        return StreamLine.viaHolder.getProtocol(UUID.fromString(this.uuid)).getName();
    }

    public boolean updateOnline() {
        if (uuid.equals("%")) this.online = false;

        this.online = PlayerUtils.isInOnlineList(this.uuid);
        return this.online;
    }

    public SavableUser(CommandSource sender, SavableAdapter.Type type) {
        this((sender instanceof ConsoleCommandSource) ? "%" : ((Player) sender).getUniqueId().toString(), type);
    }

    public SavableUser(String uuid, SavableAdapter.Type type) {
        super(uuid, type);

        populateDefaults();

        this.savableUser = this;
        this.latestVersion = getLatestVersion();
    }

    @Override
    public void populateDefaults() {
        // Profile.
        latestName = getOrSetDefault("profile.latest.name", PlayerUtils.getSourceName(findSender()));
        latestVersion = getOrSetDefault("profile.latest.version", getLatestVersion());
        latestServer = getOrSetDefault("profile.latest.server", MessageConfUtils.nullB());
        displayName = getOrSetDefault("profile.display-name", latestName);
        tagList = getOrSetDefault("profile.tags", getTagsFromConfig());
        points = getOrSetDefault("profile.points", ConfigUtils.pointsDefault());
        // Savables.
        guild = getOrSetDefault("savables.guild", "");
        party = getOrSetDefault("savables.party", "");
        // Messaging.
        lastFromUUID = getOrSetDefault("messaging.last-from.uuid", "");
        lastFromMessage = getOrSetDefault("messaging.last-from.message", "");
        lastToUUID = getOrSetDefault("messaging.last-to.uuid", "");
        lastToMessage = getOrSetDefault("messaging.last-to.message", "");
        replyToUUID = getOrSetDefault("messaging.reply-to.uuid", "");
        // Friends.
        ignoredList = getOrSetDefault("messaging.ignored", new ArrayList<>());
        friendList = getOrSetDefault("messaging.friends.confirmed", new ArrayList<>());
        pendingToFriendList = getOrSetDefault("messaging.friends.pending.to", new ArrayList<>());
        pendingFromFriendList = getOrSetDefault("messaging.friends.pending.from", new ArrayList<>());
        // Spying.
        sspy = getOrSetDefault("spying.social.view.toggled", true);
        sspyvs = getOrSetDefault("spying.social.view.self", false);
        gspy = getOrSetDefault("spying.guild.view.toggled", true);
        gspyvs = getOrSetDefault("spying.guild.view.self", false);
        gspy = getOrSetDefault("spying.party.view.toggled", true);
        gspyvs = getOrSetDefault("spying.party.view.self", false);
        // Staff.
        sc = getOrSetDefault("staff.chat.channel.toggled", false);
        viewsc = getOrSetDefault("staff.chat.view.toggled", true);
        scvs = getOrSetDefault("staff.chat.view.self", true);

        populateMoreDefaults();
    }

    abstract public List<String> getTagsFromConfig();

    abstract public void populateMoreDefaults();

    @Override
    public void loadValues() {
        // Profile.
        latestName = getOrSetDefault("profile.latest.name", latestName);
        latestVersion = getOrSetDefault("profile.latest.version", latestVersion);
        latestServer = getOrSetDefault("profile.latest.server", latestServer);
        displayName = getOrSetDefault("profile.display-name", displayName);
        tagList = getOrSetDefault("profile.tags", tagList);
        points = getOrSetDefault("profile.points", points);
        // Savables.
        guild = getOrSetDefault("savables.guild", guild);
        party = getOrSetDefault("savables.party", party);
        // Messaging.
        lastFromUUID = getOrSetDefault("messaging.last-from.uuid", lastFromUUID);
        lastFromMessage = getOrSetDefault("messaging.last-from.message", lastFromMessage);
        lastToUUID = getOrSetDefault("messaging.last-to.uuid", lastToUUID);
        lastToMessage = getOrSetDefault("messaging.last-to.message", lastToMessage);
        replyToUUID = getOrSetDefault("messaging.reply-to.uuid", replyToUUID);
        // Friends.
        ignoredList = getOrSetDefault("messaging.ignored", ignoredList);
        friendList = getOrSetDefault("messaging.friends.confirmed", friendList);
        pendingToFriendList = getOrSetDefault("messaging.friends.pending.to", pendingToFriendList);
        pendingFromFriendList = getOrSetDefault("messaging.friends.pending.from", pendingFromFriendList);
        // Spying.
        sspy = getOrSetDefault("spying.social.view.toggled", sspy);
        sspyvs = getOrSetDefault("spying.social.view.self", sspyvs);
        gspy = getOrSetDefault("spying.guild.view.toggled", gspy);
        gspyvs = getOrSetDefault("spying.guild.view.self", gspyvs);
        gspy = getOrSetDefault("spying.party.view.toggled", pspy);
        gspyvs = getOrSetDefault("spying.party.view.self", pspyvs);
        // Staff.
        sc = getOrSetDefault("staff.chat.channel.toggled", sc);
        viewsc = getOrSetDefault("staff.chat.view.toggled", viewsc);
        scvs = getOrSetDefault("staff.chat.view.self", scvs);
        // Online.
        online = updateOnline();
        // More.
        loadMoreValues();
    }

    abstract public void loadMoreValues();

    public void saveAll() {
        // Profile.
        set("profile.latest.name", latestName);
        set("profile.latest.version", latestVersion);
        set("profile.latest.server", latestServer);
        set("profile.display-name", latestName);
        set("profile.tags", tagList);
        set("profile.points", points);
        // Savables.
        set("savables.guild", guild);
        set("savables.party", party);
        // Messaging.
        set("messaging.last-from.uuid", lastFromUUID);
        set("messaging.last-from.message", lastFromMessage);
        set("messaging.last-to.uuid", lastToUUID);
        set("messaging.last-to.message", lastToMessage);
        set("messaging.reply-to.uuid", replyToUUID);
        // Friends.
        set("messaging.ignored", ignoredList);
        set("messaging.friends.confirmed", friendList);
        set("messaging.friends.pending.to", pendingToFriendList);
        set("messaging.friends.pending.from", pendingFromFriendList);
        // Spying.
        set("spying.social.view.toggled", sspy);
        set("spying.social.view.self", sspyvs);
        set("spying.guild.view.toggled", gspy);
        set("spying.guild.view.self", gspyvs);
        set("spying.party.view.toggled", pspy);
        set("spying.party.view.self", pspyvs);
        // Staff.
        set("staff.chat.channel.toggled", sc);
        set("staff.chat.view.toggled", viewsc);
        set("staff.chat.view.self", scvs);
        // More.
        saveMore();
    }

    abstract public void saveMore();

    public void addTag(String tag) {
        //        loadValues();
        if (tagList.contains(tag)) return;

        tagList.add(tag);
        //        saveAll();
    }

    public void removeTag(String tag) {
        //        loadValues();
        if (! tagList.contains(tag)) return;

        tagList.remove(tag);
        //        saveAll();
    }

    public void addIgnoredPlayer(String uuid) {
        //        loadValues();
        SavableUser other = PlayerUtils.getOrGetSavableUser(uuid);
        if (other == null) return;

        if (ConfigUtils.moduleDBUse()) {
            DataSource.ignorePlayer(this, other);
        }
        if (ignoredList.contains(uuid)) return;

        ignoredList.add(uuid);
        //        saveAll();
    }

    public void removeIgnoredPlayer(String uuid) {
        //        loadValues();
        SavableUser other = PlayerUtils.getOrGetSavableUser(uuid);
        if (other == null) return;

        if (ConfigUtils.moduleDBUse()) {
            DataSource.stopIgnoringPlayer(this, other);
        }
        if (! ignoredList.contains(uuid)) return;

        ignoredList.remove(uuid);
        //        saveAll();
    }

    public void addFriend(String uuid) {
        //        loadValues();
        SavableUser other = PlayerUtils.getOrGetSavableUser(uuid);
        if (other == null) return;

        if (ConfigUtils.moduleDBUse()) {
            DataSource.confirmFriendRequest(this, other, true);
        }
        if (friendList.contains(uuid)) return;

        friendList.add(uuid);
        //        saveAll();
    }

    public void setFriendRequestDenied(SavableUser receiver) {
        if (ConfigUtils.moduleDBUse()) {
            DataSource.confirmFriendRequest(this, receiver, false);
        }
    }

    public void removeFriend(String uuid) {
        //        loadValues();
        if (! friendList.contains(uuid)) return;

        friendList.remove(uuid);
        //        saveAll();
    }

    public void addPendingToFriend(String uuid) {
        //        loadValues();
        SavableUser other = PlayerUtils.getOrGetSavableUser(uuid);
        if (other == null) return;

        if (ConfigUtils.moduleDBUse()) {
            DataSource.sendFriendRequest(this, other);
        }
        if (pendingToFriendList.contains(uuid)) return;

        pendingToFriendList.add(uuid);
        //        saveAll();
    }

    public void removePendingToFriend(String uuid) {
        //        loadValues();
        SavableUser other = PlayerUtils.getOrGetSavableUser(uuid);
        if (other == null) return;

        if (ConfigUtils.moduleDBUse()) {
            DataSource.confirmFriendRequest(this, other, false);
        }
        if (! pendingToFriendList.contains(uuid)) return;

        pendingToFriendList.remove(uuid);
        //        saveAll();
    }

    public void addPendingFromFriend(String uuid) {
        //        loadValues();
        SavableUser other = PlayerUtils.getOrGetSavableUser(uuid);
        if (other == null) return;

        if (ConfigUtils.moduleDBUse()) {
            DataSource.sendFriendRequest(other, this);
        }
        if (pendingFromFriendList.contains(uuid)) return;

        pendingFromFriendList.add(uuid);
        //        saveAll();
    }

    public void removePendingFromFriend(String uuid) {
        //        loadValues();
        if (! pendingFromFriendList.contains(uuid)) return;

        pendingFromFriendList.remove(uuid);
        //        saveAll();
    }

    public void setPoints(int amount) {
//        //        loadValues();
        points = amount;
        if (ConfigUtils.moduleDBUse()) {
            if (this instanceof SavablePlayer) DataSource.updatePlayerData((SavablePlayer) this);
        }
        //        saveAll();
    }

    public void addPoints(int amount) {
        //        loadValues();
        setPoints(points + amount);
    }

    public void removePoints(int amount) {
        //        loadValues();
        setPoints(points - amount);
    }

    public void setSSPY(boolean value) {
        sspy = value;
        if (ConfigUtils.moduleDBUse()) {
            if (this instanceof SavablePlayer) DataSource.updatePlayerData((SavablePlayer) this);
        }
        //        saveAll();
    }

    public void toggleSSPY() {
        //        loadValues();
        setSSPY(! sspy);
    }

    public void setSSPYVS(boolean value) {
        sspyvs = value;
        //        saveAll();
    }

    public void toggleSSPYVS() {
        //        loadValues();
        setSSPYVS(! sspyvs);
    }

    public void setGSPY(boolean value) {
        gspy = value;
        if (ConfigUtils.moduleDBUse()) {
            if (this instanceof SavablePlayer) DataSource.updatePlayerData((SavablePlayer) this);
        }
        //        saveAll();
    }

    public void toggleGSPY() {
        //        loadValues();
        setGSPY(! gspy);
    }

    public void setGSPYVS(boolean value) {
        gspyvs = value;
        if (ConfigUtils.moduleDBUse()) {
            if (this instanceof SavablePlayer) DataSource.updatePlayerData((SavablePlayer) this);
        }
        //        saveAll();
    }

    public void toggleGSPYVS() {
        //        loadValues();
        setGSPYVS(! gspyvs);
    }

    public void setPSPY(boolean value) {
        pspy = value;
        if (ConfigUtils.moduleDBUse()) {
            if (this instanceof SavablePlayer) DataSource.updatePlayerData((SavablePlayer) this);
        }
        //        saveAll();
    }

    public void togglePSPY() {
        //        loadValues();
        setPSPY(! pspy);
    }

    public void setPSPYVS(boolean value) {
        pspyvs = value;
        if (ConfigUtils.moduleDBUse()) {
            if (this instanceof SavablePlayer) DataSource.updatePlayerData((SavablePlayer) this);
        }
        //        saveAll();
    }

    public void togglePSPYVS() {
        //        loadValues();
        setPSPYVS(! pspyvs);
    }

    public void setSC(boolean value) {
        sc = value;
        if (ConfigUtils.moduleDBUse()) {
            if (this instanceof SavablePlayer) DataSource.updatePlayerData((SavablePlayer) this);
        }
        //        saveAll();
    }

    public void toggleSC() {
        //        loadValues();
        setSC(! sc);
    }

    public void setSCVS(boolean value) {
        scvs = value;
        if (ConfigUtils.moduleDBUse()) {
            if (this instanceof SavablePlayer) DataSource.updatePlayerData((SavablePlayer) this);
        }
        //        saveAll();
    }

    public void toggleSCVS() {
        //        loadValues();
        setSCVS(! scvs);
    }

    public void setSCView(boolean value) {
        viewsc = value;
        if (ConfigUtils.moduleDBUse()) {
            if (this instanceof SavablePlayer) DataSource.updatePlayerData((SavablePlayer) this);
        }
        //        saveAll();
    }

    public void toggleSCView() {
        //        loadValues();
        setSCView(! viewsc);
    }

    public void updateLastMessage(String message){
//        //        loadValues();
        lastMessage = message;
        //        saveAll();
    }

    public void updateLastToMessage(String message){
//        //        loadValues();
        lastToMessage = message;
        //        saveAll();
    }

    public void updateLastFromMessage(String message){
//        //        loadValues();
        lastFromMessage = message;
        //        saveAll();
    }

    public void updateLastFrom(SavableUser messenger){
//        //        loadValues();
        lastFromUUID = messenger.uuid;
        //        saveAll();
    }

    public void updateLastTo(SavableUser to){
//        //        loadValues();
        lastToUUID = to.uuid;
        //        saveAll();
    }

    public void updateReplyTo(SavableUser to){
        //        loadValues();
        replyToUUID = to.uuid;
        //        saveAll();
    }

    public void setGuild(String uuid) {
        this.guild = uuid;
        //        saveAll();
    }

    public void setParty(String uuid) {
        this.party = uuid;
        //        saveAll();
    }

    public void setLatestServer(String server) {
        //this.latestServer = server;
        latestServer = server;
        //        saveAll();
    }

    public void setLatestName(String name) {
        latestName = name;
    }

    public void setDisplayName(String name) {
//        loadValues();
        displayName = name;
        //        saveAll();
    }

    public String getName() {
        return latestName;
    }

    public boolean hasPermission(String permission) {
        return findSender().hasPermission(permission);
    }

    public void setPermission(String permission, boolean value) {
        findSender().getPermissionChecker().value(permission);
    }

    public void sendMessage(Component message) {
        if (this.findSenderOptional().isPresent()) {
            this.findSenderOptional().get().sendMessage(message);
        }
    }

    public void sendMessage(String message) {
        if (this.findSenderOptional().isPresent()) {
            this.findSenderOptional().get().sendMessage(TextUtils.codedText(message));
        }
    }

    public void dispose() throws Throwable {
        try {
            PlayerUtils.removeStat(this);
            this.uuid = null;
            this.file.delete();
        } finally {
            super.finalize();
        }
    }
}
