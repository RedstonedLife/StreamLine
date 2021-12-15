package net.plasmere.streamline.listeners;

import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.Node;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.DiscordBotConfUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.events.Event;
import net.plasmere.streamline.events.EventsHandler;
import net.plasmere.streamline.events.enums.Condition;
import net.plasmere.streamline.objects.GeyserFile;
import net.plasmere.streamline.objects.SavableGuild;
import net.plasmere.streamline.objects.SavableParty;
import net.plasmere.streamline.objects.chats.ChatChannel;
import net.plasmere.streamline.objects.chats.ChatsHandler;
import net.plasmere.streamline.objects.enums.SavableType;
import net.plasmere.streamline.objects.lists.SingleSet;
import net.plasmere.streamline.objects.messaging.DiscordMessage;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.objects.savable.users.SavableUser;
import net.plasmere.streamline.objects.timers.ProcessDBUpdateRunnable;
import net.plasmere.streamline.utils.*;
import net.plasmere.streamline.utils.holders.GeyserHolder;
import net.plasmere.streamline.utils.sql.Driver;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

public class JoinLeaveListener implements Listener {
    private final GeyserFile file = StreamLine.geyserHolder.file;
    private final GeyserHolder holder = StreamLine.geyserHolder;

    public boolean updatePlayerOnDB(SavablePlayer player) {
        if (! StreamLine.databaseInfo.getHost().equals("")) {
            if (player.onlineCheck()) {
                Driver.update(SavableType.PLAYER, UUIDUtils.stripUUID(player.uuid));
                return true;
            }
        }

        return false;
    }

    public CompletableFuture<Boolean> completeUpdatePlayerOnDB(SavablePlayer player) {
        return CompletableFuture.supplyAsync(() -> updatePlayerOnDB(player));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void preJoin(PreLoginEvent ev) {
        if (ev.isCancelled()) return;

        String ip = ev.getConnection().getSocketAddress().toString().replace("/", "").split(":")[0];

        String uuid = UUIDUtils.getCachedUUID(ev.getConnection().getName());

        if (ConfigUtils.punBans()) {
            String reason = PlayerUtils.checkIfBanned(uuid);
            if (reason != null) {
                ev.setCancelReason(TextUtils.codedText(reason));
                ev.setCancelled(true);
            }
        }

        if (ConfigUtils.punIPBans()) {
            String reason = PlayerUtils.checkIfIPBanned(ip);
            if (reason != null) {
                ev.setCancelReason(TextUtils.codedText(reason));
                ev.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PostLoginEvent ev) {
        ProxiedPlayer player = ev.getPlayer();

        if (ConfigUtils.offlineMode()) {
            StreamLine.offlineStats.addStat(player.getUniqueId().toString(), player.getName());
        }

        if (holder.enabled && holder.isGeyserPlayer(player) && !file.hasProperty(player.getUniqueId().toString())) {
            file.updateKey(holder.getGeyserUUID(player.getName()), player.getName());
        }

        SavablePlayer stat = PlayerUtils.addPlayerStat(player);


        try {
            Thread initThread = new Thread(new ProcessDBUpdateRunnable(stat), "Streamline - Database Sync - Join");
            initThread.setUncaughtExceptionHandler((t, e) -> {
//                e.printStackTrace();
                MessagingUtils.logSevere("Streamline failed to start thread properly properly: " + e.getMessage() + ".");
            });
            initThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        stat.tryAddNewName(player.getName());
        stat.tryAddNewIP(player);

        try {
            if (stat.guild != null) {
                if (! stat.guild.equals("")) {
                    if (! GuildUtils.existsByUUID(stat.guild)) {
                        stat.updateKey("guild", "");
                    } else {
                        if (! GuildUtils.hasOnlineMemberAlready(stat)) {
                            GuildUtils.addGuild(new SavableGuild(stat.guild, false));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String joinsOrder = ConfigUtils.moduleBPlayerJoins();

        if (!joinsOrder.equals("")) {
            String[] order = joinsOrder.split(",");
            for (ProxiedPlayer p : StreamLine.getInstance().getProxy().getPlayers()) {
                if (!p.hasPermission(ConfigUtils.moduleBPlayerJoinsPerm())) continue;

                SavablePlayer other = PlayerUtils.getOrGetPlayerStatByUUID(p.getUniqueId().toString());

                label:
                for (String s : order) {
                    switch (s) {
                        case "staff":
                            if (player.hasPermission(ConfigUtils.staffPerm())) {
                                if (p.hasPermission(ConfigUtils.staffPerm())) {
                                    MessagingUtils.sendBUserMessage(p, TextUtils.replaceAllPlayerBungee(MessageConfUtils.bungeeOnline(), stat)
                                    );
                                    break label;
                                }
                            }
                            break;
                        case "guild":
                            if (!ConfigUtils.guildSendJoins()) continue;

                            SavableGuild guild = GuildUtils.getGuild(other);
                            if (guild == null) continue;

                            if (guild.hasMember(stat)) {
                                MessagingUtils.sendBUserMessage(p, TextUtils.replaceAllPlayerBungee(MessageConfUtils.guildConnect(), stat)
                                );
                                break label;
                            }
                            break;
                        case "party":
                            if (!ConfigUtils.partySendJoins()) continue;

                            SavableParty party = PartyUtils.getParty(other);
                            if (party == null) continue;

                            if (party.hasMember(stat)) {
                                MessagingUtils.sendBUserMessage(p, TextUtils.replaceAllPlayerBungee(MessageConfUtils.partyConnect(), stat)
                                );
                                break label;
                            }
                            break;
                        case "friend":
                            if (stat.friendList.contains(other.uuid)) {
                                MessagingUtils.sendBUserMessage(p, TextUtils.replaceAllPlayerBungee(MessageConfUtils.friendConnect(), stat)
                                );
                                break label;
                            }
                            break;
                    }
                }
            }
        }

        if (ConfigUtils.moduleDEnabled()) {
            switch (ConfigUtils.moduleDPlayerJoins()) {
                case "yes":
                    if (ConfigUtils.joinsLeavesAsConsole()) {
                        MessagingUtils.sendDiscordEBMessage(new DiscordMessage(StreamLine.getInstance().getProxy().getConsole(),
                                MessageConfUtils.discordOnlineEmbed(),
                                TextUtils.replaceAllPlayerDiscord(MessageConfUtils.discordOnline(), stat),
                                DiscordBotConfUtils.textChannelBJoins()));
                    } else {
                        if (ConfigUtils.joinsLeavesIcon()) {
                            MessagingUtils.sendDiscordJoinLeaveMessageIcon(true, stat);
                        } else {
                            MessagingUtils.sendDiscordJoinLeaveMessagePlain(true, stat);
                        }
                    }
                    break;
                case "staff":
                    if (player.hasPermission(ConfigUtils.staffPerm())) {
                        if (ConfigUtils.joinsLeavesAsConsole()) {
                            MessagingUtils.sendDiscordEBMessage(new DiscordMessage(StreamLine.getInstance().getProxy().getConsole(),
                                    MessageConfUtils.discordOnlineEmbed(),
                                    TextUtils.replaceAllPlayerDiscord(MessageConfUtils.discordOnline(), stat),
                                    DiscordBotConfUtils.textChannelBJoins()));
                        } else {
                            if (ConfigUtils.joinsLeavesIcon()) {
                                MessagingUtils.sendDiscordJoinLeaveMessageIcon(true, stat);
                            } else {
                                MessagingUtils.sendDiscordJoinLeaveMessagePlain(true, stat);
                            }
                        }
                    }
                    break;
                case "no":
                default:
                    break;
            }
        }

        if (StreamLine.discordData.ifHasChannels(ChatsHandler.getChannel("guild"), stat.guild)) {
            StreamLine.discordData.sendDiscordJoinChannel(player, ChatsHandler.getChannel("guild"), stat.guild);
        }

        SavableParty party = PartyUtils.getParty(stat.uuid);

        if (party != null) {
            SingleSet<Boolean, ChatChannel> get2 = StreamLine.discordData.ifHasChannelsAsSet(ChatsHandler.getChannel("party"), party.leaderUUID);
            if (get2.key) {
                StreamLine.discordData.sendDiscordJoinChannel(player, ChatsHandler.getChannel("party"), party.leaderUUID);
            }
        }

        SingleSet<Boolean, ChatChannel> get3 = StreamLine.discordData.ifHasChannelsAsSet(ChatsHandler.getChannel("global"), "");
        if (get3.key) {
            StreamLine.discordData.sendDiscordJoinChannel(player, ChatsHandler.getChannel("global"), "");
        }

        if (! TextUtils.equalsAny(stat.chatChannel.name, List.of("global", "party", "guild", "local"))) {
            if (ChatsHandler.chatExists(stat.chatChannel, stat.chatIdentifier)) {
                StreamLine.discordData.sendDiscordJoinChannel(player, stat.chatChannel, stat.chatIdentifier);
            }
        }

        if (ConfigUtils.events()) {
            for (Event event : EventsHandler.getEvents()) {
                if (! EventsHandler.checkTags(event, stat)) continue;

                if (! (EventsHandler.checkEventConditions(event, stat, Condition.JOIN, "network"))) continue;

                EventsHandler.runEvent(event, stat);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServer(ServerConnectEvent ev){
        ProxiedPlayer player = ev.getPlayer();

        boolean hasServer = false;
        ServerInfo server = ev.getTarget();

        SavablePlayer stat = PlayerUtils.addPlayerStat(player);

        if (ev.getReason().equals(ServerConnectEvent.Reason.JOIN_PROXY) && ConfigUtils.redirectEnabled() && StreamLine.lpHolder.enabled) {
            for (ServerInfo s : StreamLine.getInstance().getProxy().getServers().values()) {
                String sv = s.getName();
                if (player.hasPermission(ConfigUtils.redirectPre() + sv)) {
                    Group group = StreamLine.lpHolder.api.getGroupManager().getGroup(Objects.requireNonNull(StreamLine.lpHolder.api.getUserManager().getUser(player.getName())).getPrimaryGroup());

                    if (group == null) {
                        hasServer = true;
                        server = s;
                        break;
                    }

                    Collection<Node> nodes = group.getNodes();

                    for (Node node : nodes) {
                        if (node.getKey().equals(ConfigUtils.redirectPre() + sv)) {
                            hasServer = true;
                            server = s;
                            break;
                        }
                    }

                    Collection<Node> nods = Objects.requireNonNull(StreamLine.lpHolder.api.getUserManager().getUser(player.getName())).getNodes();

                    for (Node node : nods) {
                        if (node.getKey().equals(ConfigUtils.redirectPre() + sv)) {
                            hasServer = true;
                            server = s;
                            break;
                        }
                    }

                    if (hasServer){
                        break;
                    }
                }
            }

            if (! hasServer) {
                server = StreamLine.getInstance().getProxy().getServerInfo(ConfigUtils.redirectMain());
            }
        }

        if (StreamLine.viaHolder.enabled && ConfigUtils.redirectEnabled()) {
            if (! hasServer && ConfigUtils.lobbies() && ev.getReason().equals(ServerConnectEvent.Reason.JOIN_PROXY)){
                for (SingleSet<String, String> set : StreamLine.lobbies.getInfo().values()) {
                    String sName = set.key;

                    int version = StreamLine.viaHolder.via.getPlayerVersion(player.getUniqueId());

                    if (! StreamLine.lobbies.isAllowed(version, sName)) continue;

                    server = StreamLine.getInstance().getProxy().getServerInfo(sName);

                    ev.setTarget(server);

                    return;
                }
            }

            if (! ev.getReason().equals(ServerConnectEvent.Reason.JOIN_PROXY)) {
                if (ConfigUtils.vbEnabled()) {
                    if (!player.hasPermission(ConfigUtils.vbOverridePerm())) {
                        int version = StreamLine.viaHolder.via.getPlayerVersion(player.getUniqueId());

                        if (!StreamLine.serverPermissions.isAllowed(version, server.getName())) {
                            MessagingUtils.sendBUserMessage(ev.getPlayer(), MessageConfUtils.vbBlocked());
                            ev.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }

        if (server == null) return;

        ev.setTarget(server);

        try {
            SingleSet<Boolean, ChatChannel> get1 = StreamLine.discordData.ifHasChannelsAsSet(ChatsHandler.getChannel("local"), stat.getServer().getInfo().getName());
            if (get1.key) {
                StreamLine.discordData.sendDiscordLeaveChannel(player, ChatsHandler.getChannel("local"), stat.getServer().getInfo().getName());
            }
        } catch (Exception e) {
            // do nothing.
        }

        stat.setLatestServer(server.getName());

        SingleSet<Boolean, ChatChannel> get = StreamLine.discordData.ifHasChannelsAsSet(ChatsHandler.getChannel("local"), server.getName());
        if (get.key) {
            StreamLine.discordData.sendDiscordJoinChannel(player, ChatsHandler.getChannel("local"), server.getName());
        }





        if (ConfigUtils.events()) {
            for (Event event : EventsHandler.getEvents()) {
                if (!EventsHandler.checkTags(event, stat)) continue;

                if (!(EventsHandler.checkEventConditions(event, stat, Condition.JOIN, server.getName()))) continue;

                EventsHandler.runEvent(event, stat);
            }
        }
    }

    public void guildPM(SavablePlayer stat) {
        if (ConfigUtils.guildPMEnabled()) {
            if (GuildUtils.hasGuild(stat)) {
                SavableGuild guild = GuildUtils.getOrGetGuild(stat.guild);

                if (guild == null) return;

                MessagingUtils.sendGuildPluginMessageRequest(stat.player, guild);

                for (SavableUser user : guild.totalMembers) {
                    MessagingUtils.sendSavableUserPluginMessageRequest(stat.player, user, (user instanceof SavablePlayer ? "player" : "console"));
                }
            }
        }
    }

    public void partyPM(SavablePlayer stat) {
        if (ConfigUtils.partyPMEnabled()) {
            if (PartyUtils.hasParty(stat)) {
                SavableParty party = PartyUtils.getOrGetParty(stat.party);

                if (party == null) return;

                MessagingUtils.sendPartyPluginMessageRequest(stat.player, party);

                for (SavableUser user : party.totalMembers) {
                    MessagingUtils.sendSavableUserPluginMessageRequest(stat.player, user, (user instanceof SavablePlayer ? "player" : "console"));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerDiscon(ServerDisconnectEvent ev) {
        ProxiedPlayer player = ev.getPlayer();

        ServerInfo server = ev.getTarget();

        if (player.getServer() == null) return;

        if (PluginUtils.isLocked()) return;

        SavablePlayer stat = PlayerUtils.addPlayerStat(player);

        try {
            if (ConfigUtils.events()) {
                for (Event event : EventsHandler.getEvents()) {
                    if (!EventsHandler.checkTags(event, stat)) continue;

                    if (!(EventsHandler.checkEventConditions(event, stat, Condition.LEAVE, server.getName()))) continue;

                    EventsHandler.runEvent(event, stat);
                }
            }
        } catch (Exception e) {
            // do nothing
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeave(PlayerDisconnectEvent ev) {
        ProxiedPlayer player = ev.getPlayer();

        SavablePlayer stat = PlayerUtils.addPlayerStat(player);

        try {
            Thread initThread = new Thread(new ProcessDBUpdateRunnable(stat), "Streamline - Database Sync - Leave");
            initThread.setUncaughtExceptionHandler((t, e) -> {
//                e.printStackTrace();
                MessagingUtils.logSevere("Streamline failed to start thread properly properly: " + e.getMessage() + ".");
            });
            initThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        switch (ConfigUtils.moduleBPlayerLeaves()) {
//            case "yes":
//                MessagingUtils.sendBungeeMessage(new BungeeMassMessage(StreamLine.getInstance().getProxy().getConsole(),
//                        MessageConfUtils.bungeeOffline.replace("%player_absolute%", player.getName())
//                                .replace("%player_display%", PlayerUtils.getOffOnDisplayBungee(PlayerUtils.getOrCreatePlayerStat(player))),
//                        ConfigUtils.moduleBPlayerLeavesPerm()));
//                break;
//            case "staff":
//                if (player.hasPermission(ConfigUtils.staffPerm())) {
//                    MessagingUtils.sendBungeeMessage(new BungeeMassMessage(StreamLine.getInstance().getProxy().getConsole(),
//                            MessageConfUtils.bungeeOffline.replace("%player_absolute%", player.getName())
//                                    .replace("%player_display%", PlayerUtils.getOffOnDisplayBungee(PlayerUtils.getOrCreatePlayerStat(player))),
//                            ConfigUtils.moduleBPlayerLeavesPerm()));
//                }
//                break;
//            case "no":
//            default:
//                break;
//        }

        String leavesOrder = ConfigUtils.moduleBPlayerLeaves();

        if (! leavesOrder.equals("")) {
            String[] order = leavesOrder.split(",");
            for (ProxiedPlayer p : StreamLine.getInstance().getProxy().getPlayers()) {
                if (! p.hasPermission(ConfigUtils.moduleBPlayerLeavesPerm())) continue;

                SavablePlayer other = PlayerUtils.getOrGetPlayerStatByUUID(p.getUniqueId().toString());

                label:
                for (String s : order) {
                    switch (s) {
                        case "staff":
                            if (player.hasPermission(ConfigUtils.staffPerm())) {
                                if (p.hasPermission(ConfigUtils.staffPerm())) {
                                    MessagingUtils.sendBUserMessage(p, TextUtils.replaceAllPlayerBungee(MessageConfUtils.bungeeOffline(), stat)
                                    );
                                    break label;
                                }
                            }
                            break;
                        case "guild":
                            if (! ConfigUtils.guildSendLeaves()) continue;

                            SavableGuild guild = GuildUtils.getGuild(other);
                            if (guild == null) continue;

                            if (guild.hasMember(stat)) {
                                MessagingUtils.sendBUserMessage(p, TextUtils.replaceAllPlayerBungee(MessageConfUtils.guildDisconnect(), stat)
                                );
                                break label;
                            }
                            break;
                        case "party":
                            if (! ConfigUtils.partySendLeaves()) continue;

                            SavableParty party = PartyUtils.getParty(other);
                            if (party == null) continue;

                            if (party.hasMember(stat)) {
                                MessagingUtils.sendBUserMessage(p, TextUtils.replaceAllPlayerBungee(MessageConfUtils.partyDisconnect(), stat)
                                );
                                break label;
                            }
                            break;
                        case "friend":
                            if (stat.friendList.contains(other.uuid)) {
                                MessagingUtils.sendBUserMessage(p, TextUtils.replaceAllPlayerBungee(MessageConfUtils.friendDisconnect(), stat)
                                );
                                break label;
                            }
                            break;
                    }
                }
            }
        }

        if (ConfigUtils.moduleDEnabled()) {
            switch (ConfigUtils.moduleDPlayerLeaves()) {
                case "yes":
                    if (ConfigUtils.joinsLeavesAsConsole()) {
                        MessagingUtils.sendDiscordEBMessage(new DiscordMessage(StreamLine.getInstance().getProxy().getConsole(),
                                MessageConfUtils.discordOfflineEmbed(),
                                TextUtils.replaceAllPlayerDiscord(MessageConfUtils.discordOffline(), stat),
                                DiscordBotConfUtils.textChannelBLeaves()));
                    } else {
                        if (ConfigUtils.joinsLeavesIcon()) {
                            MessagingUtils.sendDiscordJoinLeaveMessageIcon(false, stat);
                        } else {
                            MessagingUtils.sendDiscordJoinLeaveMessagePlain(false, stat);
                        }
                    }
                    break;
                case "staff":
                    if (player.hasPermission(ConfigUtils.staffPerm())) {
                        if (ConfigUtils.joinsLeavesAsConsole()) {
                            MessagingUtils.sendDiscordEBMessage(new DiscordMessage(StreamLine.getInstance().getProxy().getConsole(),
                                    MessageConfUtils.discordOfflineEmbed(),
                                    TextUtils.replaceAllPlayerDiscord(MessageConfUtils.discordOffline(), stat),
                                    DiscordBotConfUtils.textChannelBLeaves()));
                        } else {
                            if (ConfigUtils.joinsLeavesIcon()) {
                                MessagingUtils.sendDiscordJoinLeaveMessageIcon(false, stat);
                            } else {
                                MessagingUtils.sendDiscordJoinLeaveMessagePlain(false, stat);
                            }
                        }
                    }
                    break;
                case "no":
                default:
                    break;
            }
        }

        SingleSet<Boolean, ChatChannel> get = StreamLine.discordData.ifHasChannelsAsSet(ChatsHandler.getChannel("guild"), stat.guild);
        if (get.key) {
            StreamLine.discordData.sendDiscordLeaveChannel(player, ChatsHandler.getChannel("guild"), stat.guild);
        }

        SavableParty party = PartyUtils.getParty(stat.uuid);

        if (party != null) {
            SingleSet<Boolean, ChatChannel> get2 = StreamLine.discordData.ifHasChannelsAsSet(ChatsHandler.getChannel("party"), party.leaderUUID);
            if (get2.key) {
                StreamLine.discordData.sendDiscordLeaveChannel(player, ChatsHandler.getChannel("party"), party.leaderUUID);
            }
        }

        if (! TextUtils.equalsAny(stat.chatChannel.name, List.of("global", "party", "guild", "local"))) {
            if (ChatsHandler.chatExists(stat.chatChannel, stat.chatIdentifier)) {
                StreamLine.discordData.sendDiscordLeaveChannel(player, stat.chatChannel, stat.chatIdentifier);
            }
        }

        try {
            for (ProxiedPlayer pl : StreamLine.getInstance().getProxy().getPlayers()){
                SavablePlayer p = PlayerUtils.getOrGetPlayerStatByUUID(pl.getUniqueId().toString());

                if (GuildUtils.pHasGuild(stat)) {
                    SavableGuild guild = GuildUtils.getGuild(stat);

                    if (guild == null || p.equals(stat)) continue;
                    if (guild.hasMember(p)) break;
                    if (stat.guild.equals(p.guild)) break;

                    GuildUtils.removeGuild(guild);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        SingleSet<Boolean, ChatChannel> get3 = StreamLine.discordData.ifHasChannelsAsSet(ChatsHandler.getChannel("global"), "");
        if (get3.key) {
            StreamLine.discordData.sendDiscordLeaveChannel(player, ChatsHandler.getChannel("global"), "");
        }

        if (ConfigUtils.events()) {
            for (Event event : EventsHandler.getEvents()) {
                if (!EventsHandler.checkTags(event, stat)) continue;

                if (!(EventsHandler.checkEventConditions(event, stat, Condition.LEAVE, "network"))) continue;

                EventsHandler.runEvent(event, stat);
            }
        }

        try {
            PlayerUtils.addToSave(stat);
            PlayerUtils.doSave(stat);
            PlayerUtils.removeStat(stat);
//            PlayerUtils.removeOfflineStats();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onKick(ServerKickEvent ev){
        if (ev.isCancelled()) return;

        try {
            if (StreamLine.getInstance().getProxy().getPlayer(ev.getPlayer().getUniqueId()) == null) return;
        } catch (Exception e) {
            return;
        }

        ProxiedPlayer player = ev.getPlayer();

        SavablePlayer stat = PlayerUtils.addPlayerStat(player);

        if (StreamLine.viaHolder.enabled) {
            if (ConfigUtils.lobbies()) {
                Server server = PlayerUtils.getPPlayer(stat.uuid).getServer();

                if (server == null) {
                    MessagingUtils.logSevere("Server for " + player.getName() + " returned null during kick!");
                    return;
                }

                TreeMap<Integer, SingleSet<String, String>> servers = StreamLine.lobbies.getInfo();

                String[] lobbies = new String[servers.size()];

                int i = 0;
                for (SingleSet<String, String> s : servers.values()) {
                    lobbies[i] = s.key;
                    i++;
                }

                PlayerUtils.addConn(stat);
                SingleSet<Integer, Integer> conn = PlayerUtils.getConnection(stat);

                if (conn == null) return;

                String kickTo = lobbies[conn.value];

                while (server.getInfo().getName().equals(kickTo)) {
                    PlayerUtils.addOneToConn(stat);
                    conn = PlayerUtils.getConnection(stat);
                    if (conn == null) return;
                }

                ev.setCancelServer(StreamLine.getInstance().getProxy().getServerInfo(lobbies[conn.value]));
                ev.setCancelled(true);

                PlayerUtils.addOneToConn(stat);
            }
        }
    }
}