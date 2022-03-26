package net.plasmere.streamline.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.config.ServerInfo;
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
import net.plasmere.streamline.objects.configs.obj.TablistHandler;
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

public class JoinLeaveListener implements Listener {
    private final GeyserFile file = StreamLine.geyserHolder.file;
    private final GeyserHolder holder = StreamLine.geyserHolder;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void preJoin(PreLoginEvent ev) {
        if (! ev.isCancelled()) return;

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
        int debug = 1;
        MessagingUtils.logInfo("Log #" + debug);
        debug ++;
        ProxiedPlayer player = ev.getPlayer();

        boolean firstJoin = PlayerUtils.existsByUUID(player.getUniqueId().toString());

        MessagingUtils.logInfo("Log #" + debug);
        debug ++;

        if (ConfigUtils.offlineMode()) {
            StreamLine.offlineStats.addStat(player.getUniqueId().toString(), PlayerUtils.getSourceName(player));
        }

        MessagingUtils.logInfo("Log #" + debug);
        debug ++;

        if (holder.enabled && holder.isGeyserPlayer(player) && !file.hasProperty(player.getUniqueId().toString())) {
            file.updateKey(holder.getGeyserUUID(PlayerUtils.getSourceName(player)), PlayerUtils.getSourceName(player));
        }

        MessagingUtils.logInfo("Log #" + debug);
        debug ++;

        SavablePlayer stat = PlayerUtils.addPlayerStat(player);

        MessagingUtils.logInfo("Log #" + debug);
        debug ++;
//        if (ConfigUtils.debug()) MessagingUtils.logInfo("SavablePlayer : latestName = " + stat.latestName + " | uuid = " + stat.uuid);

        StreamLine.playTimeConf.setPlayTime(stat.uuid, stat.playSeconds);

        MessagingUtils.logInfo("Log #" + debug);
        debug ++;

        if (ConfigUtils.moduleDBUse()) {
            DataSource.updatePlayerData(stat);
        }

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


        MessagingUtils.logInfo("Log #" + debug);
        debug ++;

        if (ConfigUtils.updateDisplayNames()) {
            PlayerUtils.updateDisplayName(stat);
        }

        MessagingUtils.logInfo("Log #" + debug);
        debug ++;

        stat.addName(PlayerUtils.getSourceName(player));
        stat.addIP(player);
        stat.setLatestIP(player);

        MessagingUtils.logInfo("Log #" + debug);
        debug ++;

        if (StreamLine.chatConfig.getDefaultOnFirstJoin()) {
            if (firstJoin) {
                stat.setChat(StreamLine.chatConfig.getDefaultChannel(), StreamLine.chatConfig.getDefaultIdentifier());
            }
        } else {
            stat.setChat(StreamLine.chatConfig.getDefaultChannel(), StreamLine.chatConfig.getDefaultIdentifier());
        }

        MessagingUtils.logInfo("Log #" + debug);
        debug ++;

        SavableGuild guild = GuildUtils.addGuildIfNotAlreadyLoaded(stat);
        SavableParty party = PartyUtils.addPartyIfNotAlreadyLoaded(stat);

        if (ConfigUtils.customTablistEnabled()) {
            if (StreamLine.tablistConfig.isGlobal()) {
                TablistHandler.tickPlayers();
            }
        }

        MessagingUtils.logInfo("Log #" + debug);
        debug ++;

        String joinsOrder = ConfigUtils.moduleBPlayerJoins();

        if (!joinsOrder.equals("")) {
            String[] order = joinsOrder.split(",");
            for (ProxiedPlayer p : StreamLine.getInstance().getProxy().getPlayers()) {
                if (!p.hasPermission(ConfigUtils.moduleBPlayerJoinsPerm())) continue;

                SavablePlayer other = PlayerUtils.getOrGetPlayerStatByUUID(p.getUniqueId().toString());
                if (other == null) continue;

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

                            if (guild.hasMember(other)) {
                                MessagingUtils.sendBUserMessage(p, TextUtils.replaceAllPlayerBungee(MessageConfUtils.guildConnect(), stat)
                                );
                                break label;
                            }
                            break;
                        case "party":
                            if (!ConfigUtils.partySendJoins()) continue;

                            if (party == null) continue;

                            if (party.hasMember(other)) {
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


        MessagingUtils.logInfo("Log #" + debug);
        debug ++;

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

        MessagingUtils.logInfo("Log #" + debug);
        debug ++;

        if (StreamLine.discordData.ifHasChannels(ChatsHandler.getChannel("guild"), stat.guild)) {
            StreamLine.discordData.sendDiscordJoinChannel(player, ChatsHandler.getChannel("guild"), stat.guild);
        }

        MessagingUtils.logInfo("Log #" + debug);
        debug ++;

        if (party != null) {
            SingleSet<Boolean, ChatChannel> get2 = StreamLine.discordData.ifHasChannelsAsSet(ChatsHandler.getChannel("party"), party.uuid);
            if (get2.key) {
                StreamLine.discordData.sendDiscordJoinChannel(player, ChatsHandler.getChannel("party"), party.uuid);
            }
        }

        MessagingUtils.logInfo("Log #" + debug);
        debug ++;

        if (guild != null) {
            SingleSet<Boolean, ChatChannel> get3 = StreamLine.discordData.ifHasChannelsAsSet(ChatsHandler.getChannel("global"), "");
            if (get3.key) {
                StreamLine.discordData.sendDiscordJoinChannel(player, ChatsHandler.getChannel("global"), "");
            }
        }

        MessagingUtils.logInfo("Log #" + debug);
        debug ++;

        if (! TextUtils.equalsAny(stat.chatChannel.name, List.of("global", "party", "guild", "local"))) {
            if (ChatsHandler.chatExists(stat.chatChannel, stat.chatIdentifier)) {
                StreamLine.discordData.sendDiscordJoinChannel(player, stat.chatChannel, stat.chatIdentifier);
            }
        }

        MessagingUtils.logInfo("Log #" + debug);
        debug ++;

        if (ConfigUtils.events()) {
            for (Event event : EventsHandler.getEvents()) {
                if (! EventsHandler.checkTags(event, stat)) continue;

                if (! ( (EventsHandler.checkEventConditions(event, stat, Condition.JOIN, "network")) || (EventsHandler.checkEventConditions(event,stat, Condition.JOIN, "*")))) continue;

                EventsHandler.runEvent(event, stat);
            }
        }

        MessagingUtils.logInfo("Log #" + debug);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServer(ServerConnectedEvent ev) {
        ProxiedPlayer player = ev.getPlayer();

        Server s = ev.getPlayer().getServer();
        ServerInfo server = s != null ? s.getInfo() : null;

        SavablePlayer stat = PlayerUtils.addPlayerStat(player);

        stat.setLatestName(player.getName());
        stat.updateOnline();

        try {
            SingleSet<Boolean, ChatChannel> get1 = StreamLine.discordData.ifHasChannelsAsSet(ChatsHandler.getChannel("local"), stat.getServer().getName());
            if (get1.key) {
                StreamLine.discordData.sendDiscordLeaveChannel(player, ChatsHandler.getChannel("local"), stat.getServer().getName());
            }
        } catch (Exception e) {
            // do nothing.
        }

        guildPM(stat);

        partyPM(stat);

        if (ConfigUtils.customTablistEnabled()) {
            if (StreamLine.tablistConfig.isGlobal()) {
                TablistHandler.tickPlayers();
            } else {
                for (ProxiedPlayer p : PlayerUtils.getServeredPPlayers(server.getName())) {
                    TablistHandler.tickPlayer(p);
                }
            }
        }

        if (server != null) {
            stat.setLatestServer(server.getName());

            SingleSet<Boolean, ChatChannel> get = StreamLine.discordData.ifHasChannelsAsSet(ChatsHandler.getChannel("local"), server.getName());
            if (get.key) {
                StreamLine.discordData.sendDiscordJoinChannel(player, ChatsHandler.getChannel("local"), server.getName());
            }

            try {
                if (ConfigUtils.events()) {
                    for (Event event : EventsHandler.getEvents()) {
                        if (!EventsHandler.checkTags(event, stat)) continue;

                        if (!(EventsHandler.checkEventConditions(event, stat, Condition.JOIN, server.getName())))
                            continue;

                        EventsHandler.runEvent(event, stat);
                    }
                }
                ServerInfo previousServer = ev.getServer().getInfo();
                if (previousServer == null) return;

                if (ConfigUtils.events()) {
                    for (Event event : EventsHandler.getEvents()) {
                        if (!EventsHandler.checkTags(event, stat)) continue;

                        if (! ( (EventsHandler.checkEventConditions(event, stat, Condition.LEAVE, previousServer.getName())) || (EventsHandler.checkEventConditions(event,stat, Condition.LEAVE, "*")))) continue;

                        EventsHandler.runEvent(event, stat);
                    }
                }
            } catch (Exception e) {
                // do nothing
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

        if (player.getServer().getInfo() == null) return;

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

        StreamLine.playTimeConf.setPlayTime(stat.uuid, stat.playSeconds);

        stat.updateOnline();

        if (ConfigUtils.moduleDBUse()) {
            DataSource.updatePlayerData(stat);
        }

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
            for (ProxiedPlayer p : PlayerUtils.getOnlinePPlayers()) {
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

        SavableGuild guild = GuildUtils.getOrGetGuild(stat);

        SavableParty party = PartyUtils.getOrGetParty(stat);

        if (ConfigUtils.customTablistEnabled()) {
            if (StreamLine.tablistConfig.isGlobal()) {
                TablistHandler.tickPlayers();
            } else {
                try {
                    for (ProxiedPlayer p : PlayerUtils.getServeredPPlayers(player.getServer().getInfo().getName())) {
                        TablistHandler.tickPlayer(p);
                    }
                } catch (Exception e) {
                    TablistHandler.tickPlayers();
                }
            }
        }

        if (guild != null) {
            SingleSet<Boolean, ChatChannel> get = StreamLine.discordData.ifHasChannelsAsSet(ChatsHandler.getChannel("guild"), stat.guild);
            if (get.key) {
                StreamLine.discordData.sendDiscordLeaveChannel(player, ChatsHandler.getChannel("guild"), stat.guild);
            }
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

                if (! ( (EventsHandler.checkEventConditions(event, stat, Condition.LEAVE, "network")) || (EventsHandler.checkEventConditions(event,stat, Condition.LEAVE, "*")))) continue;

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
        if (! ev.isCancelled()) return;

        try {
            if (StreamLine.getInstance().getProxy().getPlayer(ev.getPlayer().getUniqueId()) == null) return;
        } catch (Exception e) {
            return;
        }

        ProxiedPlayer player = ev.getPlayer();

        SavablePlayer stat = PlayerUtils.addPlayerStat(player);

        if (StreamLine.viaHolder.enabled) {
            if (ConfigUtils.lobbies()) {
                ServerInfo server = PlayerUtils.getPPlayer(stat.uuid).getServer().getInfo();

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

                while (server.getName().equals(kickTo)) {
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