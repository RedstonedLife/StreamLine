package net.plasmere.streamline.listeners;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.Node;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.DiscordBotConfUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.events.Event;
import net.plasmere.streamline.events.EventsHandler;
import net.plasmere.streamline.events.enums.Condition;
import net.plasmere.streamline.objects.GeyserFile;
import net.plasmere.streamline.objects.Party;
import net.plasmere.streamline.objects.chats.ChatChannel;
import net.plasmere.streamline.objects.chats.ChatsHandler;
import net.plasmere.streamline.objects.lists.SingleSet;
import net.plasmere.streamline.objects.messaging.DiscordMessage;
import net.plasmere.streamline.objects.Guild;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.utils.*;
import com.velocitypowered.api.proxy.Player;
import net.plasmere.streamline.utils.holders.GeyserHolder;

import java.util.*;

public class JoinLeaveListener {
    private final GeyserFile file = StreamLine.geyserHolder.file;
    private final GeyserHolder holder = StreamLine.geyserHolder;

    @Subscribe(order = PostOrder.FIRST)
    public void preJoin(PreLoginEvent ev) {
        if (! ev.getResult().isAllowed()) return;

        String ip = ev.getConnection().getRemoteAddress().toString().replace("/", "").split(":")[0];

        String uuid = UUIDUtils.fetch(ev.getUsername());

        if (ConfigUtils.punBans) {
            String reason = PlayerUtils.checkIfBanned(uuid);
            if (reason != null) {
                ev.setResult(PreLoginEvent.PreLoginComponentResult.denied(TextUtils.codedText(reason)));
            }
        }

        if (ConfigUtils.punIPBans) {
            String reason = PlayerUtils.checkIfIPBanned(ip);
            if (reason != null) {
                ev.setResult(PreLoginEvent.PreLoginComponentResult.denied(TextUtils.codedText(reason)));
            }
        }
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onJoin(PostLoginEvent ev) {
        Player player = ev.getPlayer();

        if (ConfigUtils.offlineMode) {
            StreamLine.offlineStats.addStat(player.getUniqueId().toString(), PlayerUtils.getSourceName(player));
        }

        if (holder.enabled && holder.isGeyserPlayer(player) && !file.hasProperty(player.getUniqueId().toString())) {
            file.updateKey(holder.getGeyserUUID(PlayerUtils.getSourceName(player)), PlayerUtils.getSourceName(player));
        }

        SavablePlayer stat = PlayerUtils.addPlayerStat(player);

        stat.tryAddNewName(PlayerUtils.getSourceName(player));
        stat.tryAddNewIP(player);

        try {
            if (stat.guild != null) {
                if (! stat.guild.equals("")) {
                    if (! GuildUtils.existsByUUID(stat.guild)) {
                        stat.updateKey("guild", "");
                    } else {
                        if (! GuildUtils.hasOnlineMemberAlready(stat)) {
                            GuildUtils.addGuild(new Guild(stat.guild, false));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String joinsOrder = ConfigUtils.moduleBPlayerJoins;

        if (!joinsOrder.equals("")) {
            String[] order = joinsOrder.split(",");
            for (Player p : StreamLine.getInstance().getProxy().getAllPlayers()) {
                if (!p.hasPermission(ConfigUtils.moduleBPlayerJoinsPerm)) continue;

                SavablePlayer other = PlayerUtils.getOrCreatePlayerStat(p);

                label:
                for (String s : order) {
                    switch (s) {
                        case "staff":
                            if (player.hasPermission(ConfigUtils.staffPerm)) {
                                if (p.hasPermission(ConfigUtils.staffPerm)) {
                                    MessagingUtils.sendBUserMessage(p, TextUtils.replaceAllPlayerBungee(MessageConfUtils.bungeeOnline(), stat)
                                    );
                                    break label;
                                }
                            }
                            break;
                        case "guild":
                            if (!ConfigUtils.guildSendJoins) continue;

                            Guild guild = GuildUtils.getGuild(other);
                            if (guild == null) continue;

                            if (guild.hasMember(stat)) {
                                MessagingUtils.sendBUserMessage(p, TextUtils.replaceAllPlayerBungee(MessageConfUtils.guildConnect(), stat)
                                );
                                break label;
                            }
                            break;
                        case "party":
                            if (!ConfigUtils.partySendJoins) continue;

                            Party party = PartyUtils.getParty(other);
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

        if (ConfigUtils.moduleDEnabled) {
            switch (ConfigUtils.moduleDPlayerJoins) {
                case "yes":
                    if (ConfigUtils.joinsLeavesAsConsole) {
                        MessagingUtils.sendDiscordEBMessage(new DiscordMessage(StreamLine.getInstance().getProxy().getConsoleCommandSource(),
                                MessageConfUtils.discordOnlineEmbed(),
                                TextUtils.replaceAllPlayerDiscord(MessageConfUtils.discordOnline(), stat),
                                DiscordBotConfUtils.textChannelBJoins));
                    } else {
                        if (ConfigUtils.joinsLeavesIcon) {
                            MessagingUtils.sendDiscordJoinLeaveMessageIcon(true, stat);
                        } else {
                            MessagingUtils.sendDiscordJoinLeaveMessagePlain(true, stat);
                        }
                    }
                    break;
                case "staff":
                    if (player.hasPermission(ConfigUtils.staffPerm)) {
                        if (ConfigUtils.joinsLeavesAsConsole) {
                            MessagingUtils.sendDiscordEBMessage(new DiscordMessage(StreamLine.getInstance().getProxy().getConsoleCommandSource(),
                                    MessageConfUtils.discordOnlineEmbed(),
                                    TextUtils.replaceAllPlayerDiscord(MessageConfUtils.discordOnline(), stat),
                                    DiscordBotConfUtils.textChannelBJoins));
                        } else {
                            if (ConfigUtils.joinsLeavesIcon) {
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

        Party party = PartyUtils.getParty(stat.uuid);

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

        if (ConfigUtils.events) {
            for (Event event : EventsHandler.getEvents()) {
                if (! EventsHandler.checkTags(event, stat)) continue;

                if (! (EventsHandler.checkEventConditions(event, stat, Condition.JOIN, "network"))) continue;

                EventsHandler.runEvent(event, stat);
            }
        }
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onServer(ServerPreConnectEvent ev){
        Player player = ev.getPlayer();

        boolean hasServer = false;
        ServerInfo server = ev.getResult().getServer().get().getServerInfo();

        SavablePlayer stat = PlayerUtils.addPlayerStat(player);

//        if (ev.get().equals(ConnectionRequestBuilder.JOIN_PROXY) && ConfigUtils.redirectEnabled && StreamLine.lpHolder.enabled) {
//            for (RegisteredServer s : StreamLine.getInstance().getProxy().getAllServers()) {
//                String sv = s.getName();
//                if (player.hasPermission(ConfigUtils.redirectPre + sv)) {
//                    Group group = StreamLine.lpHolder.api.getGroupManager().getGroup(Objects.requireNonNull(StreamLine.lpHolder.api.getUserManager().getUser(PlayerUtils.getSourceName(player))).getPrimaryGroup());
//
//                    if (group == null) {
//                        hasServer = true;
//                        server = s;
//                        break;
//                    }
//
//                    Collection<Node> nodes = group.getNodes();
//
//                    for (Node node : nodes) {
//                        if (node.getKey().equals(ConfigUtils.redirectPre + sv)) {
//                            hasServer = true;
//                            server = s;
//                            break;
//                        }
//                    }
//
//                    Collection<Node> nods = Objects.requireNonNull(StreamLine.lpHolder.api.getUserManager().getUser(PlayerUtils.getSourceName(player))).getNodes();
//
//                    for (Node node : nods) {
//                        if (node.getKey().equals(ConfigUtils.redirectPre + sv)) {
//                            hasServer = true;
//                            server = s;
//                            break;
//                        }
//                    }
//
//                    if (hasServer){
//                        break;
//                    }
//                }
//            }
//
//            if (! hasServer) {
//                server = StreamLine.getInstance().getProxy().getServerInfo(ConfigUtils.redirectMain);
//            }
//        }
//
//        if (StreamLine.viaHolder.enabled && ConfigUtils.redirectEnabled) {
//            if (! hasServer && ConfigUtils.lobbies && ev.getReason().equals(ServerConnectEvent.Reason.JOIN_PROXY)){
//                for (SingleSet<String, String> set : StreamLine.lobbies.getServerInfo().values()) {
//                    String sName = set.key;
//
//                    int version = StreamLine.viaHolder.via.getPlayerVersion(player.getUniqueId());
//
//                    if (! StreamLine.lobbies.isAllowed(version, sName)) continue;
//
//                    server = StreamLine.getInstance().getProxy().getServerInfo(sName);
//
//                    ev.setTarget(server);
//
//                    return;
//                }
//            }
//
//            if (! ev.getReason().equals(ServerConnectEvent.Reason.JOIN_PROXY)) {
//                if (ConfigUtils.vbEnabled) {
//                    if (!player.hasPermission(ConfigUtils.vbOverridePerm)) {
//                        int version = StreamLine.viaHolder.via.getPlayerVersion(player.getUniqueId());
//
//                        if (!StreamLine.serverPermissions.isAllowed(version, server.getName())) {
//                            MessagingUtils.sendBUserMessage(ev.getPlayer(), MessageConfUtils.vbBlocked());
//                            ev.setResult(PlayerChatEvent.ChatResult.denied());
//                            return;
//                        }
//                    }
//                }
//            }
//        }
//
//        if (server == null) return;
//
//        ev.setTarget(server);

        try {
            SingleSet<Boolean, ChatChannel> get1 = StreamLine.discordData.ifHasChannelsAsSet(ChatsHandler.getChannel("local"), stat.getServer().getServerInfo().getName());
            if (get1.key) {
                StreamLine.discordData.sendDiscordLeaveChannel(player, ChatsHandler.getChannel("local"), stat.getServer().getServerInfo().getName());
            }
        } catch (Exception e) {
            // do nothing.
        }

        stat.setLatestServer(server.getName());

        SingleSet<Boolean, ChatChannel> get = StreamLine.discordData.ifHasChannelsAsSet(ChatsHandler.getChannel("local"), server.getName());
        if (get.key) {
            StreamLine.discordData.sendDiscordJoinChannel(player, ChatsHandler.getChannel("local"), server.getName());
        }

        if (ConfigUtils.events) {
            for (Event event : EventsHandler.getEvents()) {
                if (!EventsHandler.checkTags(event, stat)) continue;

                if (!(EventsHandler.checkEventConditions(event, stat, Condition.JOIN, server.getName()))) continue;

                EventsHandler.runEvent(event, stat);
            }
        }
    }

//    @Subscribe(order = PostOrder.FIRST)
//    public void onServerDiscon( ev) {
//        Player player = ev.getPlayer();
//
//        ServerInfo server = ev.getTarget();
//
//        if (player.getCurrentServer().get() == null) return;
//
//        if (PluginUtils.isLocked()) return;
//
//        SavablePlayer stat = PlayerUtils.addPlayerStat(player);
//
//        try {
//            if (ConfigUtils.events) {
//                for (Event event : EventsHandler.getEvents()) {
//                    if (!EventsHandler.checkTags(event, stat)) continue;
//
//                    if (!(EventsHandler.checkEventConditions(event, stat, Condition.LEAVE, server.getName()))) continue;
//
//                    EventsHandler.runEvent(event, stat);
//                }
//            }
//        } catch (Exception e) {
//            // do nothing
//        }
//    }

    @Subscribe(order = PostOrder.FIRST)
    public void onLeave(DisconnectEvent ev) {
        Player player = ev.getPlayer();

        SavablePlayer stat = PlayerUtils.addPlayerStat(player);

//        switch (ConfigUtils.moduleBPlayerLeaves) {
//            case "yes":
//                MessagingUtils.sendBungeeMessage(new BungeeMassMessage(StreamLine.getInstance().getProxy().getConsoleCommandSource(),
//                        MessageConfUtils.bungeeOffline.replace("%player_absolute%", PlayerUtils.getSourceName(player))
//                                .replace("%player_display%", PlayerUtils.getOffOnDisplayBungee(PlayerUtils.getOrCreatePlayerStat(player))),
//                        ConfigUtils.moduleBPlayerLeavesPerm));
//                break;
//            case "staff":
//                if (player.hasPermission(ConfigUtils.staffPerm)) {
//                    MessagingUtils.sendBungeeMessage(new BungeeMassMessage(StreamLine.getInstance().getProxy().getConsoleCommandSource(),
//                            MessageConfUtils.bungeeOffline.replace("%player_absolute%", PlayerUtils.getSourceName(player))
//                                    .replace("%player_display%", PlayerUtils.getOffOnDisplayBungee(PlayerUtils.getOrCreatePlayerStat(player))),
//                            ConfigUtils.moduleBPlayerLeavesPerm));
//                }
//                break;
//            case "no":
//            default:
//                break;
//        }

        String leavesOrder = ConfigUtils.moduleBPlayerLeaves;

        if (! leavesOrder.equals("")) {
            String[] order = leavesOrder.split(",");
            for (Player p : StreamLine.getInstance().getProxy().getAllPlayers()) {
                if (! p.hasPermission(ConfigUtils.moduleBPlayerLeavesPerm)) continue;

                SavablePlayer other = PlayerUtils.getOrCreatePlayerStat(p);

                label:
                for (String s : order) {
                    switch (s) {
                        case "staff":
                            if (player.hasPermission(ConfigUtils.staffPerm)) {
                                if (p.hasPermission(ConfigUtils.staffPerm)) {
                                    MessagingUtils.sendBUserMessage(p, TextUtils.replaceAllPlayerBungee(MessageConfUtils.bungeeOffline(), stat)
                                    );
                                    break label;
                                }
                            }
                            break;
                        case "guild":
                            if (! ConfigUtils.guildSendLeaves) continue;

                            Guild guild = GuildUtils.getGuild(other);
                            if (guild == null) continue;

                            if (guild.hasMember(stat)) {
                                MessagingUtils.sendBUserMessage(p, TextUtils.replaceAllPlayerBungee(MessageConfUtils.guildDisconnect(), stat)
                                );
                                break label;
                            }
                            break;
                        case "party":
                            if (! ConfigUtils.partySendLeaves) continue;

                            Party party = PartyUtils.getParty(other);
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

        if (ConfigUtils.moduleDEnabled) {
            switch (ConfigUtils.moduleDPlayerLeaves) {
                case "yes":
                    if (ConfigUtils.joinsLeavesAsConsole) {
                        MessagingUtils.sendDiscordEBMessage(new DiscordMessage(StreamLine.getInstance().getProxy().getConsoleCommandSource(),
                                MessageConfUtils.discordOfflineEmbed(),
                                TextUtils.replaceAllPlayerDiscord(MessageConfUtils.discordOffline(), stat),
                                DiscordBotConfUtils.textChannelBLeaves));
                    } else {
                        if (ConfigUtils.joinsLeavesIcon) {
                            MessagingUtils.sendDiscordJoinLeaveMessageIcon(false, stat);
                        } else {
                            MessagingUtils.sendDiscordJoinLeaveMessagePlain(false, stat);
                        }
                    }
                    break;
                case "staff":
                    if (player.hasPermission(ConfigUtils.staffPerm)) {
                        if (ConfigUtils.joinsLeavesAsConsole) {
                            MessagingUtils.sendDiscordEBMessage(new DiscordMessage(StreamLine.getInstance().getProxy().getConsoleCommandSource(),
                                    MessageConfUtils.discordOfflineEmbed(),
                                    TextUtils.replaceAllPlayerDiscord(MessageConfUtils.discordOffline(), stat),
                                    DiscordBotConfUtils.textChannelBLeaves));
                        } else {
                            if (ConfigUtils.joinsLeavesIcon) {
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

        Party party = PartyUtils.getParty(stat.uuid);

        if (party != null) {
            SingleSet<Boolean, ChatChannel> get2 = StreamLine.discordData.ifHasChannelsAsSet(ChatsHandler.getChannel("party"), party.leaderUUID);
            if (get2.key) {
                StreamLine.discordData.sendDiscordLeaveChannel(player, ChatsHandler.getChannel("party"), party.leaderUUID);
            }
        }

        try {
            for (Player pl : StreamLine.getInstance().getProxy().getAllPlayers()){
                SavablePlayer p = PlayerUtils.getOrCreatePlayerStat(pl);

                if (GuildUtils.pHasGuild(stat)) {
                    Guild guild = GuildUtils.getGuild(stat);

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

        if (ConfigUtils.events) {
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

    @Subscribe(order = PostOrder.FIRST)
    public void onKick(KickedFromServerEvent ev){
        if (! ev.getResult().isAllowed()) return;

        try {
            if (StreamLine.getInstance().getProxy().getPlayer(ev.getPlayer().getUniqueId()) == null) return;
        } catch (Exception e) {
            return;
        }

        Player player = ev.getPlayer();

        SavablePlayer stat = PlayerUtils.addPlayerStat(player);

        if (StreamLine.viaHolder.enabled) {
            if (ConfigUtils.lobbies) {
                ServerConnection server = PlayerUtils.getPPlayer(stat.uuid).getCurrentServer().get();

                if (server == null) {
                    MessagingUtils.logSevere("Server for " + PlayerUtils.getSourceName(player) + " returned null during kick!");
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

                while (server.getServerInfo().getName().equals(kickTo)) {
                    PlayerUtils.addOneToConn(stat);
                    conn = PlayerUtils.getConnection(stat);
                    if (conn == null) return;
                }

                ev.setResult(KickedFromServerEvent.RedirectPlayer.create(StreamLine.getInstance().getProxy().getServer(lobbies[conn.value]).get()));

                PlayerUtils.addOneToConn(stat);
            }
        }
    }
}