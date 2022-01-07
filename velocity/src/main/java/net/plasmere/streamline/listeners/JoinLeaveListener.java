package net.plasmere.streamline.listeners;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.DiscordBotConfUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.events.Event;
import net.plasmere.streamline.events.EventsHandler;
import net.plasmere.streamline.events.enums.Condition;
import net.plasmere.streamline.objects.GeyserFile;
import net.plasmere.streamline.objects.savable.groups.SavableGuild;
import net.plasmere.streamline.objects.savable.groups.SavableParty;
import net.plasmere.streamline.objects.chats.ChatChannel;
import net.plasmere.streamline.objects.chats.ChatsHandler;
import net.plasmere.streamline.objects.lists.SingleSet;
import net.plasmere.streamline.objects.messaging.DiscordMessage;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.objects.savable.users.SavableUser;
import net.plasmere.streamline.utils.*;
import net.plasmere.streamline.utils.holders.GeyserHolder;
import net.plasmere.streamline.utils.sql.DataSource;

import java.util.List;
import java.util.TreeMap;

public class JoinLeaveListener {
    private final GeyserFile file = StreamLine.geyserHolder.file;
    private final GeyserHolder holder = StreamLine.geyserHolder;

    @Subscribe(order = PostOrder.FIRST)
    public void preJoin(PreLoginEvent ev) {
        if (! ev.getResult().isAllowed()) return;

        String ip = ev.getConnection().getRemoteAddress().toString().replace("/", "").split(":")[0];

        String uuid = UUIDUtils.getCachedUUID(ev.getUsername());

        if (ConfigUtils.punBans()) {
            String reason = PlayerUtils.checkIfBanned(uuid);
            if (reason != null) {
                ev.setResult(PreLoginEvent.PreLoginComponentResult.denied(TextUtils.codedText(reason)));
            }
        }

        if (ConfigUtils.punIPBans()) {
            String reason = PlayerUtils.checkIfIPBanned(ip);
            if (reason != null) {
                ev.setResult(PreLoginEvent.PreLoginComponentResult.denied(TextUtils.codedText(reason)));
            }
        }
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onJoin(PostLoginEvent ev) {
        Player player = ev.getPlayer();

        boolean firstJoin = PlayerUtils.existsByUUID(player.getUniqueId().toString());

        if (ConfigUtils.offlineMode()) {
            StreamLine.offlineStats.addStat(player.getUniqueId().toString(), PlayerUtils.getSourceName(player));
        }

        if (holder.enabled && holder.isGeyserPlayer(player) && !file.hasProperty(player.getUniqueId().toString())) {
            file.updateKey(holder.getGeyserUUID(PlayerUtils.getSourceName(player)), PlayerUtils.getSourceName(player));
        }

        SavablePlayer stat = PlayerUtils.addPlayerStat(player);

//        if (ConfigUtils.debug()) MessagingUtils.logInfo("SavablePlayer : latestName = " + stat.latestName + " | uuid = " + stat.uuid);

        StreamLine.playTimeConf.setPlayTime(stat.uuid, stat.playSeconds);

        DataSource.updatePlayerData(stat);

//        try {
//            Thread initThread = new Thread(new ProcessDBUpdateRunnable(stat), "Streamline - Database Sync - Join");
//            initThread.setUncaughtExceptionHandler((t, e) -> {
////                e.printStackTrace();
//                MessagingUtils.logSevere("Streamline failed to start thread properly properly: " + e.getMessage() + ".");
//            });
//            initThread.start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        if (ConfigUtils.updateDisplayNames()) {
            for (SavablePlayer p : PlayerUtils.getJustPlayers()) {
                PlayerUtils.updateDisplayName(p);
            }
        }

        stat.addName(PlayerUtils.getSourceName(player));
        stat.addIP(player);
        stat.setLatestIP(player);

        if (StreamLine.chatConfig.getDefaultOnFirstJoin()) {
            if (firstJoin) {
                stat.setChat(StreamLine.chatConfig.getDefaultChannel(), StreamLine.chatConfig.getDefaultIdentifier());
            }
        } else {
            stat.setChat(StreamLine.chatConfig.getDefaultChannel(), StreamLine.chatConfig.getDefaultIdentifier());
        }

        SavableGuild guild = GuildUtils.addGuildIfNotAlreadyLoaded(stat);
        SavableParty party = PartyUtils.addPartyIfNotAlreadyLoaded(stat);

        String joinsOrder = ConfigUtils.moduleBPlayerJoins();

        if (!joinsOrder.equals("")) {
            String[] order = joinsOrder.split(",");
            for (Player p : StreamLine.getProxy().getAllPlayers()) {
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

                            if (guild == null) continue;

                            if (guild.hasMember(stat)) {
                                MessagingUtils.sendBUserMessage(p, TextUtils.replaceAllPlayerBungee(MessageConfUtils.guildConnect(), stat)
                                );
                                break label;
                            }
                            break;
                        case "party":
                            if (!ConfigUtils.partySendJoins()) continue;

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
                        MessagingUtils.sendDiscordEBMessage(new DiscordMessage(StreamLine.getInstance().getProxy().getConsoleCommandSource(),
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
                            MessagingUtils.sendDiscordEBMessage(new DiscordMessage(StreamLine.getInstance().getProxy().getConsoleCommandSource(),
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

        if (party != null) {
            SingleSet<Boolean, ChatChannel> get2 = StreamLine.discordData.ifHasChannelsAsSet(ChatsHandler.getChannel("party"), party.uuid);
            if (get2.key) {
                StreamLine.discordData.sendDiscordJoinChannel(player, ChatsHandler.getChannel("party"), party.uuid);
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

    @Subscribe(order = PostOrder.FIRST)
    public void onServer(ServerPostConnectEvent ev) {
        Player player = ev.getPlayer();

        ServerInfo server = ev.getPlayer().getCurrentServer().get().getServerInfo();

        SavablePlayer stat = PlayerUtils.addPlayerStat(player);

        stat.setLatestName(player.getUsername());
        stat.updateOnline();

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

        guildPM(stat);

        partyPM(stat);

        try {
            if (ConfigUtils.events()) {
                for (Event event : EventsHandler.getEvents()) {
                    if (!EventsHandler.checkTags(event, stat)) continue;

                    if (!(EventsHandler.checkEventConditions(event, stat, Condition.JOIN, server.getName()))) continue;

                    EventsHandler.runEvent(event, stat);
                }
            }
            RegisteredServer previousServer = ev.getPreviousServer();
            if (previousServer == null) return;

            if (ConfigUtils.events()) {
                for (Event event : EventsHandler.getEvents()) {
                    if (! EventsHandler.checkTags(event, stat)) continue;

                    if (! (EventsHandler.checkEventConditions(event, stat, Condition.LEAVE, previousServer.getServerInfo().getName()))) continue;

                    EventsHandler.runEvent(event, stat);
                }
            }
        } catch (Exception e) {
            // do nothing
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
//            if (ConfigUtils.events()) {
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

        StreamLine.playTimeConf.setPlayTime(stat.uuid, stat.playSeconds);

        stat.updateOnline();

        DataSource.updatePlayerData(stat);

//        try {
//            Thread initThread = new Thread(new ProcessDBUpdateRunnable(stat), "Streamline - Database Sync - Leave");
//            initThread.setUncaughtExceptionHandler((t, e) -> {
////                e.printStackTrace();
//                MessagingUtils.logSevere("Streamline failed to start thread properly properly: " + e.getMessage() + ".");
//            });
//            initThread.start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        String leavesOrder = ConfigUtils.moduleBPlayerLeaves();

        if (! leavesOrder.equals("")) {
            String[] order = leavesOrder.split(",");
            for (Player p : StreamLine.getInstance().getProxy().getAllPlayers()) {
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

                            SavableGuild guild = GuildUtils.getOrGetGuild(other);
                            if (guild == null) continue;

                            if (guild.hasMember(stat)) {
                                MessagingUtils.sendBUserMessage(p, TextUtils.replaceAllPlayerBungee(MessageConfUtils.guildDisconnect(), stat)
                                );
                                break label;
                            }
                            break;
                        case "party":
                            if (! ConfigUtils.partySendLeaves()) continue;

                            SavableParty party = PartyUtils.getOrGetParty(other);
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
                        MessagingUtils.sendDiscordEBMessage(new DiscordMessage(StreamLine.getInstance().getProxy().getConsoleCommandSource(),
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
                            MessagingUtils.sendDiscordEBMessage(new DiscordMessage(StreamLine.getInstance().getProxy().getConsoleCommandSource(),
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

        SavableGuild guild = GuildUtils.getOrGetGuild(stat);

        SavableParty party = PartyUtils.getOrGetParty(stat);

        SingleSet<Boolean, ChatChannel> get = StreamLine.discordData.ifHasChannelsAsSet(ChatsHandler.getChannel("guild"), stat.guild);
        if (get.key) {
            StreamLine.discordData.sendDiscordLeaveChannel(player, ChatsHandler.getChannel("guild"), stat.guild);
        }

        if (party != null) {
            SingleSet<Boolean, ChatChannel> get2 = StreamLine.discordData.ifHasChannelsAsSet(ChatsHandler.getChannel("party"), party.uuid);
            if (get2.key) {
                StreamLine.discordData.sendDiscordLeaveChannel(player, ChatsHandler.getChannel("party"), party.uuid);
            }
        }

        if (! TextUtils.equalsAny(stat.chatChannel.name, List.of("global", "party", "guild", "local"))) {
            if (ChatsHandler.chatExists(stat.chatChannel, stat.chatIdentifier)) {
                StreamLine.discordData.sendDiscordLeaveChannel(player, stat.chatChannel, stat.chatIdentifier);
            }
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

    @Subscribe(order = PostOrder.FIRST)
    public void onKick(KickedFromServerEvent ev){
        if (! ev.getResult().isAllowed()) return;

        try {
            if (StreamLine.getProxy().getPlayer(ev.getPlayer().getUniqueId()).isEmpty()) return;
        } catch (Exception e) {
            return;
        }

        Player player = ev.getPlayer();

        SavablePlayer stat = PlayerUtils.addPlayerStat(player);

        if (StreamLine.viaHolder.enabled) {
            if (ConfigUtils.lobbies()) {
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

                ev.setResult(KickedFromServerEvent.RedirectPlayer.create(StreamLine.getProxy().getServer(lobbies[conn.value]).get()));

                PlayerUtils.addOneToConn(stat);
            }
        }
    }
}