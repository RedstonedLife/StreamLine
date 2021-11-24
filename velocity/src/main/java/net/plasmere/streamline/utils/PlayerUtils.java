package net.plasmere.streamline.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.node.types.SuffixNode;
import net.luckperms.api.query.QueryMode;
import net.luckperms.api.query.QueryOptions;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.CommandsConfUtils;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.config.backend.Configuration;
import net.plasmere.streamline.objects.SavableGuild;
import net.plasmere.streamline.objects.chats.Chat;
import net.plasmere.streamline.objects.lists.SingleSet;
import net.plasmere.streamline.objects.savable.history.HistorySave;
import net.plasmere.streamline.objects.savable.users.SavableConsole;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.objects.savable.users.SavableUser;
import net.plasmere.streamline.scripts.Script;
import net.plasmere.streamline.scripts.ScriptsHandler;
import org.apache.commons.collections4.list.TreeList;

import java.io.File;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class PlayerUtils {
    /* ----------------------------

    PlayerUtils <-- Setup.

    ---------------------------- */

    private static final String pathToPlayers = StreamLine.getInstance().getDataFolder() + File.separator + "players" + File.separator;

    private static final List<SavableUser> stats = new ArrayList<>();

    public static HashMap<Player, SingleSet<Integer, Player>> teleports = new HashMap<>();
    public static TreeMap<String, HistorySave> chatHistories = new TreeMap<>();

    private static HashMap<SavablePlayer, SingleSet<Integer, Integer>> connections = new HashMap<>();
    private static List<SavableUser> toSave = new ArrayList<>();

    private static Cache<SavablePlayer, String> cachedPrefixes = Caffeine.newBuilder().expireAfterAccess(30L, TimeUnit.MINUTES).build();
    private static Cache<SavablePlayer, String> cachedSuffixes = Caffeine.newBuilder().expireAfterAccess(30L, TimeUnit.MINUTES).build();

    public static SavableConsole applyConsole(){
        if (exists("%")) {
            return applyConsole(new SavableConsole(false));
        } else {
            return applyConsole(new SavableConsole(true));
        }
    }

    public static SavableConsole applyConsole(SavableConsole console){
        addStat(console);

        return console;
    }

    public static List<SavableUser> getStats() {
        return stats;
    }

    /* ----------------------------

    PlayerUtils <-- Utils.

    ---------------------------- */

    public static boolean isNameEqual(SavableUser user, String name){
        if (user.latestName == null) return false;

        return user.latestName.equals(name);
    }

    public static boolean hasStat(String latestName){
        return getSavableUser(latestName) != null;
    }

    public static void removePlayerIf(Predicate<SavableUser> predicate){
        stats.removeIf(predicate);
    }

    public static String createCheck(String thing){
        if (thing.contains("-")){
            return thing;
        } else {
            return Objects.requireNonNull(UUIDUtils.getCachedUUID(thing));
        }
    }

    public static boolean exists(String username){
        if (username.equals("%")) return existsByUUID(username);

        return existsByUUID(UUIDUtils.getCachedUUID(username));
    }

    public static boolean existsByUUID(String uuid){
        if (uuid.equals("%")) return new File(pathToPlayers, "console" + ".properties").exists();

        return new File(StreamLine.getInstance().getPlDir(), uuid + ".properties").exists();
    }

    public static boolean isStats(SavablePlayer stat){
        return stats.contains(stat);
    }

    public static void reloadStats(SavablePlayer stat) {
        stats.remove(getSavableUser(stat.latestName));
        stats.add(stat);
    }

    public static SavableUser addStatByUUID(String uuid){
        if (isInStatsListByUUID(uuid)) return getPlayerStatByUUID(uuid);

        if (uuid.equals("%")) {
            return getConsoleStat();
        } else {
            if (existsByUUID(uuid)) {
                SavablePlayer player = getOrGetPlayerStatByUUID(uuid);
                return addPlayerStat(player);
            } else {
                if (! isInOnlineList(UUIDUtils.getCachedName(uuid))) return null;
                else return addPlayerStat(new SavablePlayer(uuid, true));
            }
        }
    }

    public static SavableUser addStat(SavableUser stat){
        if (isInStatsList(stat)) return getSavableUser(stat.latestName);

        stats.add(stat);

        return stat;
    }

    public static SavablePlayer addPlayerStatByUUID(String uuid){
        SavablePlayer player = getOrGetPlayerStatByUUID(uuid);

        if (player == null) {
            if (isInStatsListByUUID(uuid)) {
                player = getPlayerStatByUUID(uuid);
            } else {
                if (existsByUUID(uuid)) {
                    player = new SavablePlayer(uuid, false);
                } else {
                    player = new SavablePlayer(uuid, true);
                }
            }
        }

        addPlayerStat(player);

        return player;
    }

    public static SavablePlayer addPlayerStat(SavablePlayer stat){
        addStat(stat);

        return stat;
    }

    public static SavablePlayer addPlayerStat(Player pp){
        if (isInStatsListByUUID(pp.getUniqueId().toString())) {
            SavablePlayer player = getPlayerStat(pp);
            if (player != null) return player;
        }

        SavablePlayer player = getOrGetPlayerStatByUUID(pp.getUniqueId().toString());

        if (player == null) {
            if (existsByUUID(pp.getUniqueId().toString())) {
                player = new SavablePlayer(pp, false);
            } else {
                player = new SavablePlayer(pp, true);
            }
        }

        addStat(player);

        return player;
    }

    public static void addStat(SavableConsole stat){
        if (isInStatsList(stat)) return;

        stats.add(stat);
    }

    public static boolean isInStatsList(SavableUser stat) {
        return isInStatsList(stat.latestName);
    }

    public static boolean isInStatsListByIP(String ip) {
        for (SavablePlayer player : getJustPlayers()) {
            for (String IP : player.ipList) {
                if (IP.equals(ip)) return true;
            }
        }

        return false;
    }

    public static boolean isInStatsList(SavableConsole stat) {
        for (SavableConsole player : getJustProxies()) {
            if (stat.equals(player)) return true;
            if (player.latestName == null) {
                stats.remove(player);
                return false;
            }
            if (player.latestName.equals(stat.latestName)) return true;
        }

        return false;
    }

    public static boolean isInStatsList(String username) {
        List<SavableUser> toRemove = new ArrayList<>();

        for (SavableUser user : getStats()) {
            if (user.uuid == null) {
                toRemove.add(user);
                continue;
            }
            if (user.latestName == null) {
                toRemove.add(user);
                continue;
            }

            if (user.latestName.equals(username)) return true;
        }

        for (SavableUser user : toRemove) {
            removeStat(user);
        }

        return false;
    }

    public static boolean isInStatsListByUUID(String uuid) {
        List<SavableUser> toRemove = new ArrayList<>();

        for (SavableUser user : getStats()) {
            if (user.uuid == null) {
                toRemove.add(user);
                continue;
            }

            if (user.uuid.equals(uuid)) return true;
        }

        for (SavableUser user : toRemove) {
            removeStat(user);
        }

        return false;
    }

    public static boolean isOnline(String username){
        if (isInStatsList(username)) {
            SavablePlayer player = getPlayerStat(username);
            if (player != null) {
                return player.online;
            }
        }

        for (Player p : StreamLine.getInstance().getProxy().getAllPlayers()) {
            if (p.getUsername().equals(username)) return true;
        }

        return false;
    }

    public static void removeStat(SavableUser stat){
        for (SavableUser player : new ArrayList<>(stats)) {
            if (player.latestName == null) {
                stats.remove(player);
                continue;
            }

            if (player.uuid == null) {
                stats.remove(player);
            }

            if (player.uuid.equals(stat.uuid)) {
                stats.remove(player);
            }
        }
    }

    public static int saveAll(){
        int push = 0;

        for (SavableUser user : new ArrayList<>(stats)) {
            try {
                addToSave(user);
                pushSaves();
            } catch (Exception e) {
                e.printStackTrace();
            }
            push ++;
        }

        return push;
    }

    public static int removeOfflineStats(){
        int count = 0;
        List<SavablePlayer> players = PlayerUtils.getJustPlayers();
        List<SavablePlayer> toRemove = new ArrayList<>();

        for (SavablePlayer player : players) {
            if (player.uuid == null) toRemove.add(player);

            if (! player.online) toRemove.add(player);
        }

        for (SavablePlayer player : toRemove) {
            try {
                addToSave(player);
                doSave(player);
            } catch (Exception e) {
                e.printStackTrace();
            }
            PlayerUtils.removeStat(player);

            count ++;
        }

        GuildUtils.loadAllMembersInAllGuilds();

        return count;
    }

    public static String getNameFromString(String thing) {
        if (thing.equals("%")) return getConsoleStat().latestName;

        if (thing.contains("-")) return UUIDUtils.getCachedName(thing);
        else return thing;
    }

    public static String getUUIDFromString(String thing) {
        if (thing.equals("%")) return thing;

        return UUIDUtils.getCachedUUID(thing);
    }

    public static String checkIfBanned(String uuid) {
        Configuration bans = StreamLine.bans.getBans();

        if (bans.contains(uuid)) {
            if (! bans.getBoolean(uuid + ".banned")) return null;

            String reason = bans.getString(uuid + ".reason");
            String bannedMillis = bans.getString(uuid + ".till");
            if (bannedMillis == null) bannedMillis = "";
            Date date = new Date();

            if (! bannedMillis.equals("")) {
                date = new Date(Long.parseLong(bannedMillis));

                if (date.before(new Date())) {
                    bans.set(uuid + ".banned", false);
                    StreamLine.bans.saveConfig();
                    return null;
                }
            }


            if (bannedMillis.equals("")) {
                return MessageConfUtils.punBannedPerm()
                        .replace("%reason%", reason)
                ;
            } else {
                return MessageConfUtils.punBannedTemp()
                        .replace("%reason%", reason)
                        .replace("%date%", date.toString())
                ;
            }
        }

        return null;
    }

    public static String checkIfIPBanned(String ip) {
        Configuration bans = StreamLine.bans.getBans();

        String bannedIP = ip.replace(".", "_");

        if (bans.contains(bannedIP)) {
            if (! bans.getBoolean(bannedIP + ".banned")) return null;

            String reason = bans.getString(bannedIP + ".reason");
            String bannedMillis = bans.getString(bannedIP + ".till");
            if (bannedMillis == null) bannedMillis = "";
            Date date = new Date();

            if (! bannedMillis.equals("")) {
                date = new Date(Long.parseLong(bannedMillis));

                if (date.before(new Date())) {
                    bans.set(bannedIP + ".banned", false);
                    StreamLine.bans.saveConfig();
                    return null;
                }
            }


            if (bannedMillis.equals("")) {
                return MessageConfUtils.punIPBannedPerm()
                        .replace("%reason%", reason)
                ;
            } else {
                return MessageConfUtils.punIPBannedTemp()
                        .replace("%reason%", reason)
                        .replace("%date%", date.toString())
                ;
            }
        }

        return null;
    }

    public static boolean checkIfMuted(Player sender, SavablePlayer stat){
        checkAndUpdateIfMuted(stat);

        if (stat.mutedTill != null) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.punMutedTemp().replace("%date%", stat.mutedTill.toString()));
        } else {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.punMutedPerm());
        }
        return true;
    }

    public static void checkAndUpdateIfMuted(SavablePlayer stat){
        if (stat.mutedTill != null) {
            if (stat.mutedTill.before(Date.from(Instant.now()))) {
                stat.setMuted(false);
                stat.removeMutedTill();
            }
        }
    }

    public static boolean hasOfflinePermission(String permission, String uuid){
        if (! StreamLine.lpHolder.enabled) {
            MessagingUtils.logInfo("Tried to do an offline permissions check, but failed due to not having LuckPerms installed!");
            return false;
        }

        LuckPerms api = StreamLine.lpHolder.api;

        User user = api.getUserManager().getUser(UUID.fromString(uuid));

        if (user == null) return false;

        for (PermissionNode node : user.resolveInheritedNodes(NodeType.PERMISSION, QueryOptions.builder(QueryMode.NON_CONTEXTUAL).build())) {
            if (node.getPermission().equals(permission)) return true;
        }

        for (PermissionNode node : user.getNodes(NodeType.PERMISSION)) {
            if (node.getPermission().equals(permission)) return true;
        }

        Group group = api.getGroupManager().getGroup(user.getPrimaryGroup());

        if (group == null) return false;

        for (PermissionNode node : group.getNodes(NodeType.PERMISSION)) {
            if (node.getPermission().equals(permission)) return true;
        }

        return false;
    }

    /* ----------------------------

    PlayerUtils <-- Lists.

    ---------------------------- */

    public static List<SavablePlayer> getJustPlayers(){
        List<SavablePlayer> players = new ArrayList<>();

        for (SavableUser user : stats) {
            if (user instanceof SavablePlayer) {
                players.add((SavablePlayer) user);
            }
        }

        return players;
    }

    public static List<String> getNamesJustPlayers(){
        return getUserNamesFrom(getJustPlayers());
    }

    public static List<String> getNamesJustStaffOnline(){
        return getUserNamesFrom(getJustStaffOnline());
    }

    public static List<String> getNamesJustProxy(){
        return getUserNamesFrom(getJustProxies());
    }

    public static List<String> getNamesFromAllUsers(){
        return getUserNamesFrom(stats);
    }


    public static List<String> getUserNamesFrom(Iterable<? extends SavableUser> users) {
        List<String> names = new ArrayList<>();

        for (SavableUser user : users) {
            names.add(user.getName());
        }

        return names;
    }

    public static List<SavableUser> getJustStaffOnline(){
        List<SavableUser> users = new ArrayList<>();

        for (SavableUser user : getJustPlayersOnline()) {
            if (! user.online) continue;
            if (user.hasPermission(ConfigUtils.staffPerm())) {
                users.add(user);
            }
        }

        users.add(getConsoleStat());

        return users;
    }

    public static List<SavablePlayer> getPermissionedOnline(String permission){
        List<SavablePlayer> users = new ArrayList<>();

        for (SavablePlayer user : getJustPlayersOnline()) {
            if (! user.online) continue;
            if (user.hasPermission(permission)) {
                users.add(user);
            }
        }

        return users;
    }

    public static List<Player> getPermissionedOnlineProxied(String permission){
        List<Player> users = new ArrayList<>();

        for (Player user : getOnlinePPlayers()) {
            if (user.hasPermission(permission)) {
                users.add(user);
            }
        }

        return users;
    }

    public static List<SavablePlayer> getJustPlayersOnline(){
        List<SavablePlayer> players = new ArrayList<>(getJustPlayers());
        List<SavablePlayer> online = new ArrayList<>();

        for (SavablePlayer player : players) {
            if (player.online) online.add(player);
        }

        return online;
    }

    public static List<SavableUser> getStatsOnline(){
        List<SavablePlayer> players = new ArrayList<>(getJustPlayers());
        List<SavableUser> online = new ArrayList<>();

        for (SavablePlayer player : players) {
            if (player.online) online.add(player);
        }

        online.add(getConsoleStat());

        return online;
    }

    public static boolean isStatOnline(SavableUser user) {
        for (SavableUser online : PlayerUtils.getStatsOnline()) {
            if (online.equals(user)) return true;
        }

        return false;
    }

    public static List<SavableConsole> getJustProxies(){
        List<SavableConsole> proxies = new ArrayList<>();

        for (SavableUser user : stats) {
            if (user instanceof SavableConsole) {
                proxies.add((SavableConsole) user);
            }
        }

        return proxies;
    }

    public static List<SavablePlayer> getPlayerStatsByIP(String ip) {
        List<SavablePlayer> players = new ArrayList<>();

        for (SavablePlayer player : getJustPlayers()) {
            for (String IP : player.ipList) {
                if (IP.equals(ip)) players.add(player);
            }
        }

        return players;
    }

    /* ----------------------------

    PlayerUtils <-- Creates.

    ---------------------------- */

    // SavableUsers.

    public static SavableUser createSavableUser(String thing){
        if (thing.equals("%")) return getConsoleStat();

        if (thing.contains("-")) {
            return createPlayerStatByUUID(thing);
        } else {
            return createPlayerStat(thing);
        }
    }

    // SavablePlayer Stats.

    public static SavablePlayer createPlayerStat(Player player) {
        SavablePlayer stat = addPlayerStat(new SavablePlayer(player, true));

        if (ConfigUtils.statsTell()) {
            MessagingUtils.sendStatUserMessage(stat, player, create);
        }

        return stat;
    }

    public static String getSourceName(CommandSource source){
        if (source instanceof Player) return ((Player) source).getUsername();
        else return ConfigUtils.consoleName();
    }

    public static SavablePlayer createPlayerStat(CommandSource sender) {
        return createPlayerStat(getSourceName(sender));
    }

    public static SavablePlayer createPlayerStat(String name) {
        SavablePlayer stat = addPlayerStat(new SavablePlayer(UUIDUtils.getCachedUUID(name), true));

        if (ConfigUtils.statsTell() && stat.online) {
            MessagingUtils.sendStatUserMessage(stat, stat.player, create);
        }

        return stat;
    }

    public static SavablePlayer createPlayerStatByUUID(String uuid) {
        SavablePlayer stat = addPlayerStat(new SavablePlayer(uuid, true));

        if (ConfigUtils.statsTell() && stat.online) {
            MessagingUtils.sendStatUserMessage(stat, stat.player, create);
        }

        return stat;
    }

    /* ----------------------------

    PlayerUtils <-- Single Gets.

    ---------------------------- */

    // SavableUsers.

    public static SavableUser getSavableUser(String name) {
        try {
            for (SavableUser stat : stats) {
                if (isNameEqual(stat, name)) {
                    return stat;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static SavableUser getSavableUserByUUID(String uuid) {
        try {
            for (SavableUser stat : stats) {
                if (stat.uuid.equals(uuid)) {
                    return stat;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static SavableUser getSUFromNameOrUUID(String string) {
        if (string.equals("%")) return getConsoleStat(StreamLine.getInstance().getProxy().getConsoleCommandSource());

        if (string.contains("-")) return getSavableUserByUUID(string);
        else return getSavableUser(string);
    }

    // ConsolePlayers.

    public static SavableConsole getConsoleStat() {
        return getConsoleStat(StreamLine.getInstance().getProxy().getConsoleCommandSource());
    }

    public static SavableConsole getConsoleStat(CommandSource sender) {
        try {
            for (SavableConsole stat : getJustProxies()) {
                if (stat.uuid.equals("%")) return stat;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return applyConsole();
    }

    // SavablePlayer Stats.

    public static SavablePlayer getPlayerStat(CommandSource sender) {
        try {
            for (SavablePlayer stat : getJustPlayers()) {
                if (sender.equals(stat.findSender())) {
                    return stat;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static SavablePlayer getPlayerStat(String name) {
        try {
            for (SavablePlayer stat : getJustPlayers()) {
                if (isNameEqual(stat, name)) {
                    return stat;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static SavablePlayer getPlayerStatByUUID(String uuid) {
        try {
            for (SavablePlayer stat : getJustPlayers()) {
                if (stat.uuid.equals(uuid)) {
                    return stat;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // ----------------------
    // Get or create!
    // ----------------------

    // SavableUsers.

    public static SavableUser getOrCreateSavableUser(String name){
        if (name.equals("%")) {
            return getOrCreateSUByUUID(name);
        }

        SavableUser user = getOrGetSavableUser(name);

        if (user == null) {
            if (isInOnlineList(name)) {
                user = createPlayerStat(getPPlayer(name));
            } else {
                user = createPlayerStat(name);
            }
        } else {
            addStat(user);
        }

        return user;
    }

    public static SavableUser getOrCreateSavableUserByUUID(String uuid){
        if (uuid.equals("%")) {
            return getOrCreateSUByUUID(uuid);
        }

        SavableUser user = getOrGetSavableUser(uuid);

        if (user == null) {
            if (isInOnlineList(uuid)) {
                user = createPlayerStat(getPPlayerByUUID(uuid));
            } else {
                user = createPlayerStatByUUID(uuid);
            }
        } else {
            addStat(user);
        }

        return user;
    }

    public static SavableUser getOrCreateSavableUser(CommandSource sender){
        if (sender.equals(StreamLine.getInstance().getProxy().getConsoleCommandSource())) {
            return getConsoleStat();
        }

        return getOrCreateSavableUser(getSourceName(sender));
    }

    public static SavableUser getOrCreateSavableUser(Player sender){
        return getOrCreateSavableUser(PlayerUtils.getSourceName(sender));
    }

    public static SavableUser getOrCreateSUByUUID(String uuid){
        SavableUser user = getSavableUserByUUID(uuid);

        if (user == null) {
            user = createSavableUser(uuid);
        } else {
            addStat(user);
        }

        return user;
    }

    public static SavableUser getOrCreateSUFromNameOrUUID(String string) {
        if (string.equals("%")) return getConsoleStat(StreamLine.getInstance().getProxy().getConsoleCommandSource());

        if (string.contains("-")) return getOrCreateSUByUUID(string);
        else return getOrCreateSavableUser(string);
    }

    // Players.

    public static SavablePlayer getOrCreatePlayerStat(Player pp){
        SavablePlayer player = getOrGetPlayerStat(pp.getUsername());

        if (player == null) {
            player = createPlayerStat(pp);
        } else {
            addPlayerStat(player);
        }

        return player;
    }

    public static SavablePlayer getOrCreatePlayerStat(CommandSource sender){
        SavablePlayer player = getOrGetPlayerStat(getSourceName(sender));

        if (player == null) {
            player = createPlayerStat(sender);
        } else {
            addPlayerStat(player);
        }

        return player;
    }

    public static SavablePlayer getOrCreatePlayerStat(String name){
        SavablePlayer player = getOrGetPlayerStat(name);

        if (player == null) {
            player = createPlayerStat(name);
        } else {
            addPlayerStat(player);
        }

        return  player;
    }

    public static SavablePlayer getOrCreatePlayerStatByUUID(String uuid) {
        SavablePlayer player = getOrGetPlayerStatByUUID(uuid);

        if (player == null) {
            player = createPlayerStatByUUID(uuid);
        } else {
            addPlayerStat(player);
        }

        return player;
    }

    // ----------------------
    // Get or get!
    // ----------------------

    public static SavableUser getOrGetSavableUser(String thing) {
        try {
            if (thing.equals("%")) return getConsoleStat().getSavableUser();

            if (thing.contains("-")) {
                if (existsByUUID(thing)){
                    if (isInStatsListByUUID(thing)) {
                        return getPlayerStatByUUID(thing);
                    } else {
                        return new SavablePlayer(thing, false);
                    }
                } else return null;
            } else {
                if (exists(thing)){
                    if (isInStatsList(thing)) {
                        return getPlayerStat(thing);
                    } else {
                        return new SavablePlayer(thing, false);
                    }
                } else return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static SavableUser getOrGetSavableUser(CommandSource sender) {
        if (getSourceName(sender).equals(getSourceName(StreamLine.getInstance().getProxy().getConsoleCommandSource()))) return getConsoleStat();

        try {
            if (exists(getSourceName(sender))){
                if (isInStatsList(getSourceName(sender))) {
                    return getPlayerStat(getSourceName(sender));
                } else {
                    return new SavablePlayer(UUIDUtils.getCachedUUID(getSourceName(sender)), false);
                }
            } else return null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static SavablePlayer getOrGetPlayerStat(String name) {
        try {
            if (exists(name)){
                if (isInStatsList(name)) {
                    return getPlayerStat(name);
                } else {
                    return new SavablePlayer(UUIDUtils.getCachedUUID(name), false);
                }
            } else return null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static SavablePlayer getOrGetPlayerStatByUUID(String uuid) {
        try {
            if (existsByUUID(uuid)){
                if (isInStatsListByUUID(uuid)) {
                    SavablePlayer player = getPlayerStatByUUID(uuid);
                    if (player == null) {
                        return getOrGetPlayerStat(UUIDUtils.getCachedName(uuid));
                    } else {
                        return player;
                    }
                } else {
                    return new SavablePlayer(uuid, false);
                }
            } else return null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /* ----------------------------

    PlayerUtils <-- S Functions.

    ---------------------------- */

    public static void info(CommandSource sender, SavableUser of){
        if (! sender.hasPermission(CommandsConfUtils.comBStatsPerm())) {
            MessagingUtils.sendBUserMessage(sender, noPermission);
        }

        if (of instanceof SavableConsole) {
            MessagingUtils.sendStatUserMessage(of, sender, consolePlayerInfo);
        } else if (of instanceof SavablePlayer) {
            MessagingUtils.sendStatUserMessage(of, sender, info);
        }
    }

    public static void remTag(CommandSource sender, SavableUser of, String tag){
        if (! sender.hasPermission(CommandsConfUtils.comBBTagPerm())) {
            MessagingUtils.sendBUserMessage(sender, noPermission);
            return;
        }

        of.tryRemTag(tag);

        MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllPlayerBungee(tagRem, of)
                .replace("%tag%", tag)
        );
    }

    public static void addTag(CommandSource sender, SavableUser of, String tag){
        if (! sender.hasPermission(CommandsConfUtils.comBBTagPerm())) {
            MessagingUtils.sendBUserMessage(sender, noPermission);
            return;
        }

        of.tryAddNewTag(tag);

        MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllPlayerBungee(tagAdd, of)
                .replace("%tag%", tag)
        );
    }

    public static void listTags(CommandSource sender, SavableUser of){
        if (! sender.hasPermission(CommandsConfUtils.comBBTagPerm())) {
            MessagingUtils.sendBUserMessage(sender, noPermission);
            return;
        }

        MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllPlayerBungee(tagListMain, of)
                .replace("%tags%", compileTagList(of))
        );
    }

    public static String compileTagList(SavableUser of) {
        StringBuilder stringBuilder = new StringBuilder();

        int i = 1;
        for (String tag : of.tagList){
            if (i < of.tagList.size()) {
                stringBuilder.append(tagListNotLast
                        .replace("%player_display%", getOffOnDisplayBungee(of))
                        .replace("%tag%", tag)
                );
            } else {
                stringBuilder.append(tagListLast
                        .replace("%player_display%", getOffOnDisplayBungee(of))
                        .replace("%tag%", tag)
                );
            }
            i++;
        }

        return stringBuilder.toString();
    }

    public static String getIgnored(SavableUser stat){
        StringBuilder thing = new StringBuilder();

        int i = 1;

        for (String uuid : stat.ignoredList) {
            if (i < stat.ignoredList.size()) {
                thing.append(TextUtils.replaceAllPlayerBungee(MessageConfUtils.ignoreListNLast(), uuid));
            } else {
                thing.append(TextUtils.replaceAllPlayerBungee(MessageConfUtils.ignoreListLast(), uuid));
            }

            i ++;
        }

        return thing.toString();
    }

    public static String getFriended(SavableUser stat){
        StringBuilder thing = new StringBuilder();

        int i = 1;

        for (String uuid : stat.friendList) {
            if (i < stat.friendList.size()) {
                thing.append(TextUtils.replaceAllPlayerBungee(MessageConfUtils.friendListFNLast(), uuid));
            } else {
                thing.append(TextUtils.replaceAllPlayerBungee(MessageConfUtils.friendListFLast(), uuid));
            }

            i ++;
        }

        return thing.toString();
    }

    public static String getPTFriended(SavableUser stat){
        StringBuilder thing = new StringBuilder();

        int i = 1;

        for (String uuid : stat.pendingToFriendList) {
            if (i < stat.pendingToFriendList.size()) {
                thing.append(TextUtils.replaceAllPlayerBungee(MessageConfUtils.friendListPTNLast(), uuid));
            } else {
                thing.append(TextUtils.replaceAllPlayerBungee(MessageConfUtils.friendListPTLast(), uuid));
            }

            i ++;
        }

        return thing.toString();
    }

    public static String getPFFriended(SavableUser stat){
        StringBuilder thing = new StringBuilder();

        int i = 1;

        for (String uuid : stat.pendingFromFriendList) {
            if (i < stat.pendingFromFriendList.size()) {
                thing.append(TextUtils.replaceAllPlayerBungee(MessageConfUtils.friendListPFNLast(), uuid));
            } else {
                thing.append(TextUtils.replaceAllPlayerBungee(MessageConfUtils.friendListPFLast(), uuid));
            }

            i ++;
        }

        return thing.toString();
    }

    public static void doMessage(SavableUser from, SavableUser to, String message, boolean reply){
        if (to instanceof SavablePlayer) {
            if (! ((SavablePlayer) to).online) {
                MessagingUtils.sendBUserMessage(from.findSender(), MessageConfUtils.noPlayer());
                return;
            }
        }

        from.updateLastTo(to);
        to.updateLastFrom(from);

        switch (ConfigUtils.messReplyTo()) {
            case "sent-to":
                from.updateReplyTo(to);
                break;
            case "sent-from":
                to.updateReplyTo(from);
                break;
            case "very-last":
            default:
                from.updateReplyTo(to);
                to.updateReplyTo(from);
                break;
        }

        from.updateLastToMessage(message);
        to.updateLastFromMessage(message);

        if (reply) {
            MessagingUtils.sendBMessagenging(from.findSender(), from, to, message, MessageConfUtils.replySender());

            MessagingUtils.sendBMessagenging(to.findSender(), from, to, message, MessageConfUtils.replyTo());

            for (Player player : StreamLine.getInstance().getProxy().getAllPlayers()) {
                SavablePlayer p = PlayerUtils.getOrCreatePlayerStat(player);

                if (! player.hasPermission(ConfigUtils.messViewPerm()) || ! p.sspy) continue;
                if (! p.sspyvs) if (from.uuid.equals(p.uuid) || to.uuid.equals(p.uuid)) continue;

                MessagingUtils.sendBMessagenging(player, from, to, message, MessageConfUtils.replySSPY());
            }
        } else {
            MessagingUtils.sendBMessagenging(from.findSender(), from, to, message, MessageConfUtils.messageSender());

            MessagingUtils.sendBMessagenging(to.findSender(), from, to, message, MessageConfUtils.messageTo());

            for (Player player : StreamLine.getInstance().getProxy().getAllPlayers()) {
                SavablePlayer p = PlayerUtils.getOrCreatePlayerStat(player);

                if (! player.hasPermission(ConfigUtils.messViewPerm()) || ! p.sspy) continue;
                if (! p.sspyvs) if (from.uuid.equals(p.uuid) || to.uuid.equals(p.uuid)) continue;

                MessagingUtils.sendBMessagenging(player, from, to, message, MessageConfUtils.messageSSPY());
            }
        }
    }

    public static void doMessageWithIgnoreCheck(SavableUser from, SavableUser to, String message, boolean reply){
        if (to instanceof SavablePlayer) {
            if (! (to).online) {
                MessagingUtils.sendBUserMessage(from.findSender(), MessageConfUtils.noPlayer());
                return;
            }
        }

        if (to.ignoredList.contains(from.uuid)) {
            MessagingUtils.sendBUserMessage(from.findSender(), (reply ? MessageConfUtils.replyIgnored() : MessageConfUtils.messageIgnored()));
            return;
        }

        from.updateLastTo(to);
        to.updateLastFrom(from);

        switch (ConfigUtils.messReplyTo()) {
            case "sent-to":
                from.updateReplyTo(to);
                break;
            case "sent-from":
                to.updateReplyTo(from);
                break;
            case "very-last":
            default:
                from.updateReplyTo(to);
                to.updateReplyTo(from);
                break;
        }

        from.updateLastToMessage(message);
        to.updateLastFromMessage(message);

        if (reply) {
            MessagingUtils.sendBMessagenging(from.findSender(), from, to, message, MessageConfUtils.replySender());

            MessagingUtils.sendBMessagenging(to.findSender(), from, to, message, MessageConfUtils.replyTo());

            for (Player player : StreamLine.getInstance().getProxy().getAllPlayers()) {
                SavablePlayer p = PlayerUtils.getOrCreatePlayerStat(player);

                if (! player.hasPermission(ConfigUtils.messViewPerm()) || ! p.sspy) continue;
                if (! p.sspyvs) if (from.uuid.equals(p.uuid) || to.uuid.equals(p.uuid)) continue;

                MessagingUtils.sendBMessagenging(player, from, to, message, MessageConfUtils.replySSPY());
            }
        } else {
            MessagingUtils.sendBMessagenging(from.findSender(), from, to, message, MessageConfUtils.messageSender());

            MessagingUtils.sendBMessagenging(to.findSender(), from, to, message, MessageConfUtils.messageTo());

            for (Player player : StreamLine.getInstance().getProxy().getAllPlayers()) {
                SavablePlayer p = PlayerUtils.getOrCreatePlayerStat(player);

                if (! player.hasPermission(ConfigUtils.messViewPerm()) || ! p.sspy) continue;
                if (! p.sspyvs) if (from.uuid.equals(p.uuid) || to.uuid.equals(p.uuid)) continue;

                MessagingUtils.sendBMessagenging(player, from, to, message, MessageConfUtils.messageSSPY());
            }
        }
    }

    /* ----------------------------

    PlayerUtils <-- Connections.

    ---------------------------- */

    public static HashMap<SavablePlayer, SingleSet<Integer, Integer>> getConnections(){
        return connections;
    }

    public static void addOneToConn(SavablePlayer player) {
        SingleSet<Integer, Integer> conn = connections.get(player);

        if (conn == null) {
            connections.remove(player);
            return;
        }

        int timer = conn.key;
        int num = conn.value;

        num++;

        connections.remove(player);
        connections.put(player, new SingleSet<>(timer, num));
    }

    public static void removeSecondFromConn(SavablePlayer player, SingleSet<Integer, Integer> conn){
        int timer = conn.key;
        int num = conn.value;

        timer--;

        connections.remove(player);
        connections.put(player, new SingleSet<>(timer, num));
    }

    public static SingleSet<Integer, Integer> getConnection(SavablePlayer player){
        try {
            return connections.get(player);
        } catch (Exception e){
            return null;
        }
    }

    public static void removeConn(SavablePlayer player){
        connections.remove(player);
    }

    public static void addConn(SavablePlayer player){
        connections.put(player, new SingleSet<>(ConfigUtils.lobbyTimeOut(), 0));
    }

    public static void tickConn(){
        if (connections == null) return;

        if (connections.size() <= 0) connections = new HashMap<>();

        List<SavablePlayer> conns = new ArrayList<>(connections.keySet());
        List<SavablePlayer> toRemove = new ArrayList<>();

        for (SavablePlayer player : conns) {
            SingleSet<Integer, Integer> conn = PlayerUtils.getConnection(player);

            if (conn == null) continue;

            PlayerUtils.removeSecondFromConn(player, conn);

            conn = PlayerUtils.getConnection(player);

            if (conn == null) continue;
            if (conn.key <= 0) toRemove.add(player);
        }

        for (SavablePlayer remove : toRemove) {
            PlayerUtils.removeConn(remove);
        }
    }

    /* ----------------------------

    PlayerUtils <-- DisplayNames.

    ---------------------------- */

    public static void updateDisplayName(SavablePlayer player){
        if (! ConfigUtils.updateDisplayNames()) return;
        if (! StreamLine.lpHolder.enabled) return;

        String newDisplayName = getDisplayName(player);

//        if (ConfigUtils.debug()) MessagingUtils.logInfo("Updating " + player.latestName + "'s display name to '" + newDisplayName + "'");

        player.setDisplayName(newDisplayName);
    }

    public static String getDisplayName(SavablePlayer player) {
        return getDisplayName(player.latestName);
    }

    public static String getDisplayName(String username) {
        if (! StreamLine.lpHolder.enabled) {
            MessagingUtils.logSevere("Could not get display name of player " + username + " because LuckPerms is disabled!");
            return username;
        }

        User user = StreamLine.lpHolder.api.getUserManager().getUser(username);
        if (user == null) return username;

        Group group = StreamLine.lpHolder.api.getGroupManager().getGroup(user.getPrimaryGroup());
        if (group == null) return username;

        String prefix = "";
        String suffix = "";

        TreeMap<Integer, String> preWeight = new TreeMap<>();
        TreeMap<Integer, String> sufWeight = new TreeMap<>();

        for (PrefixNode node : group.getNodes(NodeType.PREFIX)) {
            preWeight.put(node.getPriority(), node.getMetaValue());
        }

        for (PrefixNode node : user.getNodes(NodeType.PREFIX)) {
            preWeight.put(node.getPriority(), node.getMetaValue());
        }

        for (SuffixNode node : group.getNodes(NodeType.SUFFIX)) {
            sufWeight.put(node.getPriority(), node.getMetaValue());
        }

        for (SuffixNode node : user.getNodes(NodeType.SUFFIX)) {
            sufWeight.put(node.getPriority(), node.getMetaValue());
        }

        prefix = preWeight.get(PluginUtils.getCeilingInt(preWeight.keySet()));
        suffix = sufWeight.get(PluginUtils.getCeilingInt(sufWeight.keySet()));

        if (prefix == null) prefix = "";
        if (suffix == null) suffix = "";

        return prefix + username + suffix;
    }

    /* ----------------------------

    PlayerUtils <-- Get Off / On.

    ---------------------------- */

    public static String getOffOnDisplayBungee(SavableUser stat){
        if (stat == null) {
            return MessageConfUtils.nullB();
        }

        if (stat instanceof SavableConsole) {
            return ConfigUtils.consoleDisplayName();
        }

        if (stat instanceof SavablePlayer) {
            if (stat.online) {
                return MessageConfUtils.onlineB().replace("%player_formatted%", stat.displayName);
            } else {
                return MessageConfUtils.offlineB().replace("%player_formatted%", stat.displayName);
            }
        }

        return MessageConfUtils.nullB();
    }

    public static String getOffOnRegBungee(SavableUser stat){
        if (stat == null) {
            return MessageConfUtils.nullB();
        }

        if (stat instanceof SavableConsole) {
            return ConfigUtils.consoleName();
        }

        if (stat instanceof SavablePlayer) {
            if (stat.online) {
                return MessageConfUtils.onlineB().replace("%player_formatted%", stat.latestName);
            } else {
                return MessageConfUtils.offlineB().replace("%player_formatted%", stat.latestName);
            }
        }

        return MessageConfUtils.nullB();
    }

    public static String getJustDisplayBungee(SavableUser stat){
        if (stat == null) {
            return MessageConfUtils.nullB();
        }

        if (stat instanceof SavableConsole) {
            return ConfigUtils.consoleDisplayName();
        }

        if (stat instanceof SavablePlayer) {
            return stat.displayName;
        }

        return MessageConfUtils.nullB();
    }

    public static String getAbsoluteBungee(SavableUser stat){
        if (stat == null) {
            return MessageConfUtils.nullB();
        }

        if (stat instanceof SavableConsole) {
            return "%";
        }

        if (stat instanceof SavablePlayer) {
            return stat.latestName;
        }

        return MessageConfUtils.nullB();
    }

    public static String getLuckPermsPrefix(String username, boolean fromCache){
//        if (fromCache) return cachedPrefixes.get(getOrGetPlayerStat(username), (u) -> getLuckPermsPrefix(username, false));

        if (! StreamLine.lpHolder.isPresent()) return "";

        User user = StreamLine.lpHolder.api.getUserManager().getUser(username);
        if (user == null) {
//            MessagingUtils.logWarning("getLuckPermsPrefix -> user == null");
            return "";
        }

        String prefix = "";

        Group group = StreamLine.lpHolder.api.getGroupManager().getGroup(user.getPrimaryGroup());
        if (group == null) {
//            MessagingUtils.logWarning("getLuckPermsPrefix -> group == null");
            TreeMap<Integer, String> preWeight = new TreeMap<>();

            for (PrefixNode node : user.getNodes(NodeType.PREFIX)) {
                preWeight.put(node.getPriority(), node.getMetaValue());
            }

            prefix = preWeight.get(PluginUtils.getCeilingInt(preWeight.keySet()));

            if (prefix == null) {
//            MessagingUtils.logWarning("getLuckPermsPrefix -> prefix == null");
                prefix = "";
            }

//            MessagingUtils.logInfo("LP Pre : group == null | prefix = " + prefix);
            return prefix;
        }


        TreeMap<Integer, String> preWeight = new TreeMap<>();

        for (PrefixNode node : group.getNodes(NodeType.PREFIX)) {
            preWeight.put(node.getPriority(), node.getMetaValue());
//            MessagingUtils.logInfo("getLuckPermsPrefix -> node added: " + node.getPriority() + " , " + node.getMetaValue());
        }

        for (PrefixNode node : user.getNodes(NodeType.PREFIX)) {
            preWeight.put(node.getPriority(), node.getMetaValue());
        }

        prefix = preWeight.get(PluginUtils.getCeilingInt(preWeight.keySet()));

        if (prefix == null) {
//            MessagingUtils.logWarning("getLuckPermsPrefix -> prefix == null");
            prefix = "";
        }

//        cachedPrefixes.put(getOrGetPlayerStat(username), prefix);
        return prefix;
    }

    public static String getLuckPermsSuffix(String username, boolean fromCache){
//        if (fromCache) return cachedSuffixes.get(getOrGetPlayerStat(username), (u) -> getLuckPermsSuffix(username, false));

        if (! StreamLine.lpHolder.isPresent()) return "";

        User user = StreamLine.lpHolder.api.getUserManager().getUser(username);
        if (user == null) return "";

        String suffix = "";

        Group group = StreamLine.lpHolder.api.getGroupManager().getGroup(user.getPrimaryGroup());
        if (group == null){
            TreeMap<Integer, String> preWeight = new TreeMap<>();

            for (PrefixNode node : user.getNodes(NodeType.PREFIX)) {
                preWeight.put(node.getPriority(), node.getMetaValue());
            }

            suffix = preWeight.get(PluginUtils.getCeilingInt(preWeight.keySet()));

            if (suffix == null) {
//            MessagingUtils.logWarning("getLuckPermsPrefix -> prefix == null");
                suffix = "";
            }

//            MessagingUtils.logInfo("LP Pre : group == null | prefix = " + prefix);
            return suffix;
        }


        TreeMap<Integer, String> sufWeight = new TreeMap<>();

        for (SuffixNode node : group.getNodes(NodeType.SUFFIX)) {
            sufWeight.put(node.getPriority(), node.getMetaValue());
        }

        for (SuffixNode node : user.getNodes(NodeType.SUFFIX)) {
            sufWeight.put(node.getPriority(), node.getMetaValue());
        }

        suffix = sufWeight.get(PluginUtils.getCeilingInt(sufWeight.keySet()));

        if (suffix == null) suffix = "";

//        cachedSuffixes.put(getOrGetPlayerStat(username), suffix);
        return suffix;
    }

    public static String getPlayerGuildName(SavableUser user) {
        SavableGuild guild = GuildUtils.getGuild(user);

        if (guild == null) return "";
        return guild.name;
    }

    public static String getPlayerGuildMembers(SavableUser user) {
        SavableGuild guild = GuildUtils.getGuild(user);

        if (guild == null) return "";
        return String.valueOf(guild.totalMembers.size());
    }

    public static String getPlayerGuildLeaderUUID(SavableUser user) {
        SavableGuild guild = GuildUtils.getGuild(user);

        if (guild == null) return "";
        return guild.leaderUUID;
    }

    public static String getPlayerGuildLeaderAbsoluteBungee(SavableUser user) {
        SavableGuild guild = GuildUtils.getGuild(user);

        if (guild == null) return "";
        return getAbsoluteBungee(getOrGetSavableUser(guild.leaderUUID));
    }

    public static String getPlayerGuildLeaderJustDisplayBungee(SavableUser user) {
        SavableGuild guild = GuildUtils.getGuild(user);

        if (guild == null) return "";
        return getJustDisplayBungee(getOrGetSavableUser(guild.leaderUUID));
    }

    public static String getPlayerGuildLeaderOffOnRegBungee(SavableUser user) {
        SavableGuild guild = GuildUtils.getGuild(user);

        if (guild == null) return "";
        return getOffOnRegBungee(getOrGetSavableUser(guild.leaderUUID));
    }

    public static String getPlayerGuildLeaderOffOnDisplayBungee(SavableUser user) {
        SavableGuild guild = GuildUtils.getGuild(user);

        if (guild == null) return "";
        return getOffOnDisplayBungee(getOrGetSavableUser(guild.leaderUUID));
    }

    public static String getPlayerGuildLeaderAbsoluteDiscord(SavableUser user) {
        SavableGuild guild = GuildUtils.getGuild(user);

        if (guild == null) return "";
        return getAbsoluteDiscord(getOrGetSavableUser(guild.leaderUUID));
    }

    public static String getPlayerGuildLeaderJustDisplayDiscord(SavableUser user) {
        SavableGuild guild = GuildUtils.getGuild(user);

        if (guild == null) return "";
        return getJustDisplayDiscord(getOrGetSavableUser(guild.leaderUUID));
    }

    public static String getPlayerGuildLeaderOffOnRegDiscord(SavableUser user) {
        SavableGuild guild = GuildUtils.getGuild(user);

        if (guild == null) return "";
        return getOffOnRegDiscord(getOrGetSavableUser(guild.leaderUUID));
    }

    public static String getPlayerGuildLeaderOffOnDisplayDiscord(SavableUser user) {
        SavableGuild guild = GuildUtils.getGuild(user);

        if (guild == null) return "";
        return getOffOnDisplayDiscord(getOrGetSavableUser(guild.leaderUUID));
    }

    public static String getOffOnDisplayDiscord(SavableUser stat){
        if (stat == null) {
            return MessageConfUtils.nullD();
        }

        if (stat instanceof SavableConsole) {
            return TextUtils.stripColor(ConfigUtils.consoleDisplayName());
        }

        if (stat instanceof SavablePlayer) {
            if (stat.online) {
                return TextUtils.stripColor(MessageConfUtils.onlineD().replace("%player_formatted%", stat.displayName));
            } else {
                return TextUtils.stripColor(MessageConfUtils.offlineD().replace("%player_formatted%", stat.displayName));
            }
        }

        return MessageConfUtils.nullD();
    }

    public static String getOffOnRegDiscord(SavableUser stat){
        if (stat == null) {
            return MessageConfUtils.nullD();
        }

        if (stat instanceof SavableConsole) {
            return ConfigUtils.consoleName();
        }

        if (stat instanceof SavablePlayer) {
            if (stat.online) {
                return TextUtils.stripColor(MessageConfUtils.onlineD().replace("%player_display%", stat.latestName));
            } else {
                return TextUtils.stripColor(MessageConfUtils.offlineD().replace("%player_display%", stat.latestName));
            }
        }

        return MessageConfUtils.nullD();
    }

    public static String getJustDisplayDiscord(SavableUser stat){
        if (stat == null) {
            return MessageConfUtils.nullD();
        }

        if (stat instanceof SavableConsole) {
            return TextUtils.stripColor(ConfigUtils.consoleDisplayName());
        }

        if (stat instanceof SavablePlayer) {
            return TextUtils.stripColor(stat.displayName);
        }

        return MessageConfUtils.nullD();
    }

    public static String getAbsoluteDiscord(SavableUser stat){
        if (stat == null) {
            return MessageConfUtils.nullD();
        }

        if (stat instanceof SavableConsole) {
            return TextUtils.stripColor(ConfigUtils.consoleName());
        }

        if (stat instanceof SavablePlayer) {
            return TextUtils.stripColor(stat.latestName);
        }

        return MessageConfUtils.nullD();
    }

    /* ----------------------------

    PlayerUtils <-- Player.

    ---------------------------- */


    public static int getVotesForPlayer(SavablePlayer player) {
        return getVotesForPlayerByUUID(player.uuid);
    }

    public static int getVotesForPlayer(Player player) {
        return getVotesForPlayerByUUID(player.getUniqueId().toString());
    }

    public static int getVotesForPlayerByUUID(String uuid) {
        return StreamLine.votes.getVotes(UUID.fromString(uuid));
    }

    public static Collection<Player> getOnlinePPlayers(){
        return StreamLine.getInstance().getProxy().getAllPlayers();
    }

    public static List<Player> getServeredPPlayers(String serverName) {
        List<Player> players = new ArrayList<>();

        for (Player player : getOnlinePPlayers()) {
            if (player.getCurrentServer().isEmpty()) continue;
            if (player.getCurrentServer().get().getServerInfo().getName().equals(serverName)) players.add(player);
        }

        return players;
    }

    public static List<SavablePlayer> getRoomedPlayers(Chat room) {
        List<SavablePlayer> players = new ArrayList<>();

        for (SavablePlayer player : getJustPlayers()) {
            if (player.chatChannel.name.equals(room.chatChannel.name) && player.chatIdentifier.equals(room.identifier)) players.add(player);
        }

        return players;
    }

    public static List<String> getPlayerNamesForAllOnline(){
        return getPlayerNamesFrom(getOnlinePPlayers());
    }

    public static List<String> getPlayerNamesByServer(ServerConnection server) {
        return getPlayerNamesFrom(getServeredPPlayers(server.getServerInfo().getName()));
    }

    public static List<String> getPlayerNamesFrom(Iterable<Player> players) {
        List<String> names = new ArrayList<>();

        for (Player player : players) {
            names.add(player.getUsername());
        }

        return names;
    }

    public static Player getPPlayer(UUID uuid) {
        return getPPlayer(uuid.toString());
    }

    public static Player getPPlayer(String string){
        try {
            if (string.contains("-")) {
                return StreamLine.getInstance().getProxy().getPlayer(UUID.fromString(string)).get();
            }

            return StreamLine.getInstance().getProxy().getPlayer(string).get();
        } catch (Exception e) {
            return null;
        }
    }

    public static Player getPPlayerByUUID(String uuid){
        try {
            if (StreamLine.geyserHolder.enabled) {
                if (StreamLine.geyserHolder.file.hasProperty(uuid)) {
                    return StreamLine.geyserHolder.getPPlayerByUUID(uuid);
                }
            }

            return getPPlayer(UUID.fromString(uuid));
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /* ----------------------------

    PlayerUtils <-- ???.

    ---------------------------- */

    public static boolean isInOnlineList(String name){
        for (Player player : getOnlinePPlayers()) {
            if (player.getUsername().equals(name)) return true;
        }

        return false;
    }

    public static List<SavablePlayer> transposeList(List<Player> players){
        List<SavablePlayer> ps = new ArrayList<>();
        for (Player player : players){
            ps.add(PlayerUtils.getOrCreatePlayerStatByUUID(player.getUniqueId().toString()));
        }

        return ps;
    }

    public static void tickTeleport() {
        List<Player> toTp = new ArrayList<>(teleports.keySet());

        for (Player player : toTp) {
            if (teleports.get(player).key <= 0) {
                MessagingUtils.sendTeleportPluginMessageRequest(player, teleports.get(player).value);
                teleports.remove(player);
                continue;
            }

            teleports.replace(player, new SingleSet<>(teleports.get(player).key - 1, teleports.get(player).value));
        }
    }

    public static void addTeleport(Player sender, Player to) {
        teleports.put(sender, new SingleSet<>(ConfigUtils.helperTeleportDelay(), to));
    }

    public static void tickBoosts() {
        TreeList<String> boostUUIDs = new TreeList<>(StreamLine.discordData.getBoostQueue());

        for (String uuid : boostUUIDs) {
            SavableUser user = getOrGetSavableUser(uuid);

            if (user == null) continue;
            if (! user.online) continue;

            Script script = ScriptsHandler.getScript(ConfigUtils.boostsUponBoostRun());
            if (script == null) continue;

            script.execute(StreamLine.getInstance().getProxy().getConsoleCommandSource(), user);
        }
    }

    /* ----------------------------

    PlayerUtils <-- Functionals.

    ---------------------------- */

    public static void kickAll(String message) {
        if (message == null) {
            for (Player player : getOnlinePPlayers()) {
                kick(player);
            }
            return;
        }

        if (message.equals("")) {
            for (Player player : getOnlinePPlayers()) {
                kick(player);
            }
            return;
        }

        for (Player player : getOnlinePPlayers()) {
            kick(player, message);
        }
    }

    public static void kick(Player player) {
        if (player != null) player.disconnect(Component.empty());
    }

    public static void kick(Player player, String message) {
        if (player != null) player.disconnect(TextUtils.codedText(message));
    }

    public static void kick(SavablePlayer player, String message) {
        if (! player.online) return;
        Player pp = PlayerUtils.getPPlayerByUUID(player.uuid);
        if (pp != null) pp.disconnect(TextUtils.codedText(message));
    }

    public static List<SavableUser> getToSave(){
        return toSave;
    }

    public static void addToSave(SavableUser user){
        if (toSave.contains(user)) return;

        toSave.add(user);
    }

    public static void pushSaves(){
        for (SavableUser user : new ArrayList<>(toSave)) {
            doSave(user);
        }
    }

    public static void doSave(SavableUser user){
        try {
            user.saveInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }

        toSave.remove(user);
    }

    public static boolean isInNetworkByName(String name) {
        for (Player player : getOnlinePPlayers()) {
            if (player.getUsername().equals(name)) return true;
        }

        return false;
    }

    public static String getServer(CommandSource sender) {
        for (RegisteredServer server : StreamLine.getInstance().getProxy().getAllServers()) {
            for (Player player : getServeredPPlayers(server.getServerInfo().getName())) {
                if (getSourceName(sender).equals(player.getUsername())) return server.getServerInfo().getName();
            }
        }

        return "";
    }

//    public static void updateServerAll(){
//        for (SavableUser user : PlayerUtils.getStats()) {
//            if (! isInOnlineList(user.latestName)) continue;
//            user.updateServer();
//        }
//    }

    /* ----------------------------

    PlayerUtils <-- Chat History

    ---------------------------- */

    public static HistorySave addChatHistory(String uuid) {
        if (chatHistories.containsKey(uuid)) return getChatHistory(uuid);

        HistorySave save = new HistorySave(uuid);

        chatHistories.put(uuid, save);

        return save;
    }

    public static HistorySave getChatHistory(String uuid) {
        if (! chatHistories.containsKey(uuid)) return addChatHistory(uuid);

        return chatHistories.get(uuid);
    }

    public static TreeList<String> getChatHistoryFilesByUUID() {
        File[] files = StreamLine.getInstance().getChatHistoryDir().listFiles();
        TreeList<String> thing = new TreeList<>();

        if (files == null) return thing;
        if (files.length <= 0) return thing;

        for (File file : files) {
            String trial = file.getName().split("\\.")[0];
            if (! trial.contains("-")) continue;

            thing.add(trial);
        }

        return thing;
    }

    public static void loadAllChatHistories(boolean onlyOnlinePlayers) {
        if (onlyOnlinePlayers) {
            for (Player player : getOnlinePPlayers()) {
                addChatHistory(player.getUniqueId().toString());
            }
        } else {
            for (String uuid : getChatHistoryFilesByUUID()) {
                addChatHistory(uuid);
            }
        }
    }

    public static String addLineToChatHistory(String uuid, String server, String message) {
        return addLineToChatHistory(getChatHistory(uuid), server, message);
    }

    public static String addLineToChatHistory(HistorySave save, String server, String message) {
        return save.addLine(server, message);
    }

    public static List<Player> getPlayersOnlineByGroup(Group group) {
        List<Player> thing = new ArrayList<>();
        if (! StreamLine.lpHolder.enabled) return thing;

        for (Player player : getOnlinePPlayers()) {
            User user = StreamLine.lpHolder.api.getUserManager().getUser(player.getUniqueId());
            if (user == null) continue;

            if (user.getPrimaryGroup().equals(group.getName())) {
                thing.add(player);
            }
        }

        return thing;
    }

    // No stats.
    public static final String noStatsFound = StreamLine.config.getMessString("stats.no-stats");
    // Not high enough permissions.
    public static final String noPermission = StreamLine.config.getMessString("stats.no-permission");
    // Create.
    public static final String create = StreamLine.config.getMessString("stats.create");
    // Info.
    public static final String info = StreamLine.config.getMessString("stats.player");
    public static final String consolePlayerInfo = StreamLine.config.getMessString("stats.console-player");
    public static final String tagsLast = StreamLine.config.getMessString("stats.tags.last");
    public static final String tagsNLast = StreamLine.config.getMessString("stats.tags.not-last");
    public static final String ipsLast = StreamLine.config.getMessString("stats.ips.last");
    public static final String ipsNLast = StreamLine.config.getMessString("stats.ips.not-last");
    public static final String namesLast = StreamLine.config.getMessString("stats.names.last");
    public static final String namesNLast = StreamLine.config.getMessString("stats.names.not-last");
    public static final String sspyT = StreamLine.config.getMessString("stats.sspy.true");
    public static final String sspyF = StreamLine.config.getMessString("stats.sspy.false");
    public static final String gspyT = StreamLine.config.getMessString("stats.gspy.true");
    public static final String gspyF = StreamLine.config.getMessString("stats.gspy.false");
    public static final String pspyT = StreamLine.config.getMessString("stats.pspy.true");
    public static final String pspyF = StreamLine.config.getMessString("stats.pspy.false");
    public static final String onlineT = StreamLine.config.getMessString("stats.online.true");
    public static final String onlineF = StreamLine.config.getMessString("stats.online.false");
    public static final String notSet = StreamLine.config.getMessString("stats.not-set");
    // Tags.
    public static final String tagRem = StreamLine.config.getMessString("btag.remove");
    public static final String tagAdd = StreamLine.config.getMessString("btag.add");
    public static final String tagListMain = StreamLine.config.getMessString("btag.list.main");
    public static final String tagListLast = StreamLine.config.getMessString("btag.list.tags.last");
    public static final String tagListNotLast = StreamLine.config.getMessString("btag.list.tags.not-last");
    // Points.
    public static final String pointsName = StreamLine.config.getMessString("stats.points-name");

    public static String forStats(List<SavableUser> players){
        StringBuilder builder = new StringBuilder("[");

        int i = 1;
        for (SavableUser p : players){
            if (i != players.size()) {
                builder.append(p.toString()).append(", ");
            } else {
                builder.append(p.toString()).append("]");
            }

            i++;
        }

        return builder.toString();
    }
}
