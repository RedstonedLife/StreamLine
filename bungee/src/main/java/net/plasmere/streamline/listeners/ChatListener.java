package net.plasmere.streamline.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.md_5.bungee.api.event.ChatEvent;
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
import net.plasmere.streamline.objects.savable.groups.SavableGuild;
import net.plasmere.streamline.objects.chats.Chat;
import net.plasmere.streamline.objects.chats.ChatChannel;
import net.plasmere.streamline.objects.chats.ChatsHandler;
import net.plasmere.streamline.objects.enums.MessageServerType;
import net.plasmere.streamline.objects.filters.ChatFilter;
import net.plasmere.streamline.objects.filters.FilterHandler;
import net.plasmere.streamline.objects.lists.SingleSet;
import net.plasmere.streamline.objects.messaging.DiscordMessage;
import net.plasmere.streamline.objects.savable.groups.SavableParty;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.utils.*;

import java.util.*;

public class ChatListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(ChatEvent e){
        if (! e.isCancelled()) return;
        boolean isStaffMessage = false;

        ProxiedPlayer sender = (ProxiedPlayer) e.getSender();
        SavablePlayer stat = PlayerUtils.addPlayerStat(sender);

        String msg = e.getMessage();

        // Just so the maker of the plugin can tell if a server is using their plugin. :)
        if (sender.getUniqueId().toString().equals("c4c95a91-3bbb-49e3-9b79-6abe892e39a9")) {
            if (msg.startsWith(">>>")) {
                sender.sendMessage(TextUtils.codedText("&eWe salute you&8, &3commander&8! &do7"));
                e.setCancelled(true);
                return;
            }
        }

        boolean bypass = (stat.bypassFor > 0) || (stat.chatChannel.name.equals("off"));

        if (ConfigUtils.moduleBChatFiltersEnabled() && ! bypass) {
            FilterHandler.reloadAllFilters();

            boolean needsBlocking = false;

            for (ChatFilter filter : FilterHandler.filters) {
                if (! filter.enabled) continue;

                SingleSet<Boolean, String> filtered = filter.applyFilter(msg, sender);

                if (! needsBlocking) needsBlocking = filtered.key;
                msg = filtered.value;
            }

            if (needsBlocking) {
                e.setCancelled(true);
                return;
            }
        }

        stat.updateLastMessage(msg);

        try {
            for (ProxiedPlayer pl : PlayerUtils.getOnlinePPlayers()) {
                SavablePlayer p = PlayerUtils.getOrGetPlayerStatByUUID(pl.getUniqueId().toString());

                if (p == null) continue;

                SavableGuild guild = GuildUtils.getOrGetGuild(p);

                if (guild == null && ! p.equals(stat)) continue;
                if (guild != null) {
                    if (guild.hasMember(stat)) break;
                }


                if (GuildUtils.pHasGuild(stat)) {
                    GuildUtils.addGuild(new SavableGuild(stat.guild));
                }
                break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            for (ProxiedPlayer pl : PlayerUtils.getOnlinePPlayers()){
                SavablePlayer p = PlayerUtils.getOrGetPlayerStatByUUID(pl.getUniqueId().toString());

                if (p == null) continue;

                SavableParty party = PartyUtils.getOrGetParty(p);

                if (party == null && ! p.equals(stat)) continue;
                if (party != null) {
                    if (party.hasMember(stat)) break;
                }

                if (PartyUtils.pHasParty(stat)) {
                    PartyUtils.addParty(new SavableParty(stat.party));
                }
                break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (ConfigUtils.punMutes() && ConfigUtils.punMutesHard() && stat.muted) {
            if (PlayerUtils.checkIfMuted(sender, stat)) {
                e.setCancelled(true);
                return;
            }
        }

        if (TextUtils.isCommand(msg)) return;

        if (ConfigUtils.punMutes() && stat.muted) {
            e.setCancelled(true);
            if (! Objects.equals(stat.mutedTill, new Date(0L))) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.punMutedTemp().replace("%date%", stat.mutedTill.toString()));
            } else {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.punMutedPerm());
            }
            return;
        }

        if (ConfigUtils.moduleStaffChat()) {
            if (stat.sc) {
                if (! sender.hasPermission(ConfigUtils.staffPerm())) {
                    return;
                }

                e.setCancelled(true);
                MessagingUtils.sendStaffMessage(sender, MessageConfUtils.bungeeStaffChatFrom(), msg);
                if (ConfigUtils.moduleDEnabled()) {
                    if (ConfigUtils.moduleStaffChatMToDiscord()) {
                        MessagingUtils.sendDiscordEBMessage(new DiscordMessage(sender,
                                MessageConfUtils.staffChatEmbedTitle(),
                                TextUtils.replaceAllPlayerDiscord(MessageConfUtils.discordStaffChatMessage(), sender)
                                        .replace("%message%", msg),
                                DiscordBotConfUtils.textChannelStaffChat()));
                    }
                }
                isStaffMessage = true;
            } else if (ConfigUtils.moduleStaffChatDoPrefix()) {
                if (msg.startsWith(ConfigUtils.moduleStaffChatPrefix()) && ! ConfigUtils.moduleStaffChatPrefix().equals("/")) {
                    if (! sender.hasPermission(ConfigUtils.staffPerm())) {
                        return;
                    }

                    if (msg.equals(ConfigUtils.moduleStaffChatPrefix())) {
                        sender.sendMessage(TextUtils.codedText(MessageConfUtils.staffChatJustPrefix().replace("%newline%", "\n")));
                        e.setCancelled(true);
                        return;
                    }

                    e.setCancelled(true);
                    MessagingUtils.sendStaffMessage(sender, MessageConfUtils.bungeeStaffChatFrom(), msg.substring(ConfigUtils.moduleStaffChatPrefix().length()));
                    if (ConfigUtils.moduleDEnabled()) {
                        if (ConfigUtils.moduleStaffChatMToDiscord()) {
                            MessagingUtils.sendDiscordEBMessage(new DiscordMessage(sender,
                                    MessageConfUtils.staffChatEmbedTitle(),
                                    TextUtils.replaceAllPlayerDiscord(MessageConfUtils.discordStaffChatMessage(), sender)
                                            .replace("%message%", msg.substring(ConfigUtils.moduleStaffChatPrefix().length())),
                                    DiscordBotConfUtils.textChannelStaffChat()));
                        }
                    }
                    isStaffMessage = true;
                }
            }
        }

        if (! isStaffMessage && ConfigUtils.customChats()) {
            if (StreamLine.serverConfig.getProxyChatEnabled() && ! bypass) {
                if (ConfigUtils.moduleDEnabled()) {
                    if (ConfigUtils.moduleDPC()) if (ConfigUtils.moduleDPCConsole()) {
                        MessagingUtils.sendDiscordEBMessage(new DiscordMessage(sender,
                                        ConfigUtils.moduleDPCConsoleTitle(),
                                        ConfigUtils.moduleDPCConsoleMessage()
                                                .replace("%message%", msg),
                                        DiscordBotConfUtils.textChannelProxyChat()
                                ),
                                ConfigUtils.moduleDPCConsoleUseAvatar()
                        );
                    }
                }

                boolean allowGlobal = StreamLine.serverConfig.getAllowGlobal();
                boolean allowLocal = StreamLine.serverConfig.getAllowLocal();

                if (allowGlobal || allowLocal) {
                    if (!allowGlobal && stat.chatChannel.equals(ChatsHandler.getChannel("global"))) {
                        stat.setChatChannel("local");
                        stat.setChatIdentifier("network");
                    }
                    if (!allowLocal && stat.chatChannel.equals(ChatsHandler.getChannel("local"))) {
                        stat.setChatChannel("global");
                        stat.setChatIdentifier("network");
                    }

                    if (stat.chatChannel.equals(ChatsHandler.getChannel("global"))) {
                        Chat chat = ChatsHandler.getChat(ChatsHandler.getChannel("global"), stat.chatIdentifier);
                        if (chat != null) {
                            if (chat.identifier.equals("network")) {
                                String format = StreamLine.chatConfig.getPermissionedChatMessage(stat, chat, chat.chatChannel.name, MessageServerType.BUNGEE);
                                SingleSet<String, List<ProxiedPlayer>> msgWithTagged = TextUtils.getMessageWithTags(sender, msg, format);

                                String withEmotes = TextUtils.getMessageWithEmotes(sender, msgWithTagged.key);

                                MessagingUtils.sendGlobalMessageFromUser(sender, sender.getServer().getInfo(), format, withEmotes);

                                for (ProxiedPlayer player : msgWithTagged.value) {
                                    MessagingUtils.sendTagPingPluginMessageRequest(player);
//                                    player.playSound(PlayerUtils.getDefaultPlingSound(), Sound.Emitter.self());
                                }

                                if (StreamLine.serverConfig.getProxyChatConsoleEnabled()) {
                                    MessagingUtils.sendMessageFromUserToConsole(sender, sender.getServer().getInfo(), format, withEmotes);
                                }
                            } else {
                                if (ChatsHandler.hasPermissionForGlobalChat(stat, chat)) {
                                    String format = StreamLine.chatConfig.getPermissionedChatMessage(stat, chat, chat.chatChannel.name, MessageServerType.BUNGEE);
                                    SingleSet<String, List<ProxiedPlayer>> msgWithTagged = TextUtils.getMessageWithTags(sender, msg, format);

                                    String withEmotes = TextUtils.getMessageWithEmotes(sender, msgWithTagged.key);

                                    MessagingUtils.sendPermissionedGlobalMessageFromUser(chat.identifier, sender, sender.getServer().getInfo(), format, withEmotes);

                                    for (ProxiedPlayer player : msgWithTagged.value) {
                                    MessagingUtils.sendTagPingPluginMessageRequest(player);
//                                        player.playSound(PlayerUtils.getDefaultPlingSound(), Sound.Emitter.self());
                                    }

                                    if (StreamLine.serverConfig.getProxyChatConsoleEnabled()) {
                                        MessagingUtils.sendMessageFromUserToConsole(sender, sender.getServer().getInfo(), format, withEmotes);
                                    }
                                }
                            }
                        } else {
                            ChatChannel chatChannel = ChatsHandler.getChannel("global");
                            Chat ch = StreamLine.chatConfig.getDefaultChat(chatChannel);
                            if (ChatsHandler.hasPermissionForGlobalChat(stat, ch)) {
                                String format = StreamLine.chatConfig.getPermissionedChatMessage(stat, ch, chatChannel.name, MessageServerType.BUNGEE);
                                SingleSet<String, List<ProxiedPlayer>> msgWithTagged = TextUtils.getMessageWithTags(sender, msg, format);

                                String withEmotes = TextUtils.getMessageWithEmotes(sender, msgWithTagged.key);

                                MessagingUtils.sendPermissionedGlobalMessageFromUser(ch.identifier, sender, sender.getServer().getInfo(), format, withEmotes);

                                for (ProxiedPlayer player : msgWithTagged.value) {
                                    MessagingUtils.sendTagPingPluginMessageRequest(player);
//                                    player.playSound(PlayerUtils.getDefaultPlingSound(), Sound.Emitter.self());
                                }

                                if (StreamLine.serverConfig.getProxyChatConsoleEnabled()) {
                                    MessagingUtils.sendMessageFromUserToConsole(sender, sender.getServer().getInfo(), format, withEmotes);
                                }
                            }
                        }

                        e.setCancelled(true);
                    } else if (stat.chatChannel.equals(ChatsHandler.getChannel("local"))) {
                        Chat chat = ChatsHandler.getChat(ChatsHandler.getChannel("local"), stat.chatIdentifier);

                        String format = StreamLine.chatConfig.getPermissionedChatMessage(stat, chat, ChatsHandler.getChannel("local").name, MessageServerType.BUNGEE);
                        SingleSet<String, List<ProxiedPlayer>> msgWithTagged = TextUtils.getMessageWithTags(sender, msg, format);

                        String withEmotes = TextUtils.getMessageWithEmotes(sender, msgWithTagged.key);

                        if (stat.chatIdentifier.equals("network")) {
                            MessagingUtils.sendServerMessageFromUser(sender, sender.getServer().getInfo(), sender.getServer().getInfo().getName(), format, withEmotes);
                        } else {
                            MessagingUtils.sendServerMessageFromUser(sender, sender.getServer().getInfo(), stat.chatIdentifier, format, withEmotes);
                            if (!stat.chatIdentifier.equals(sender.getServer().getInfo().getName())) {
                                MessagingUtils.sendServerMessageOtherServerSelf(sender, sender.getServer().getInfo(), format, withEmotes);
                            }
                        }

                        for (ProxiedPlayer player : msgWithTagged.value) {
                                    MessagingUtils.sendTagPingPluginMessageRequest(player);
//                            player.playSound(PlayerUtils.getDefaultPlingSound(), Sound.Emitter.self());
                        }

                        if (StreamLine.serverConfig.getProxyChatConsoleEnabled()) {
                            MessagingUtils.sendMessageFromUserToConsole(sender, sender.getServer().getInfo(), format, withEmotes);
                        }

                        e.setCancelled(true);
                    }
                }

                if (stat.chatChannel.equals(ChatsHandler.getChannel("guild"))) {
                    GuildUtils.sendChat(stat, GuildUtils.getOrGetGuild(stat.chatIdentifier), msg);

                    e.setCancelled(true);
                } else if (stat.chatChannel.equals(ChatsHandler.getChannel("party"))) {
                    PartyUtils.sendChat(stat, PartyUtils.getOrGetParty(stat.chatIdentifier), msg);

                    e.setCancelled(true);
                }
            } else {
                String withEmotes = TextUtils.getMessageWithEmotes(sender, msg);
                msg = withEmotes;
                e.setMessage(withEmotes);
            }

            if (! TextUtils.equalsAny(stat.chatChannel.name, Arrays.asList("local", "global", "guild", "party"))) {
                Chat chat = ChatsHandler.getChat(stat.chatChannel, stat.chatIdentifier);

                if (chat != null) {
                    String format = StreamLine.chatConfig.getPermissionedChatMessage(stat, chat, chat.chatChannel.name, MessageServerType.BUNGEE);
                    SingleSet<String, List<ProxiedPlayer>> msgWithTagged = TextUtils.getMessageWithTags(sender, msg, format);

                    String withEmotes = TextUtils.getMessageWithEmotes(sender, msgWithTagged.key);

                    MessagingUtils.sendRoomMessageFromUser(sender, sender.getServer().getInfo(), chat, format, withEmotes);

                    for (ProxiedPlayer player : msgWithTagged.value) {
                                    MessagingUtils.sendTagPingPluginMessageRequest(player);
//                        player.playSound(PlayerUtils.getDefaultPlingSound(), Sound.Emitter.self());
                    }

                    if (StreamLine.serverConfig.getProxyChatConsoleEnabled()) {
                        MessagingUtils.sendMessageFromUserToConsole(sender, sender.getServer().getInfo(), format, withEmotes);
                    }

                    e.setCancelled(true);
                }
            }
        }

        if (ConfigUtils.moduleDPC()) {
            ChatChannel channel = ChatsHandler.getChannel("local");;
            String identifier = stat.findServer();

            if (stat.chatChannel.equals(ChatsHandler.getChannel("local"))) {
                identifier = (stat.chatIdentifier.equals("network") ? stat.findServer() : stat.chatIdentifier);
            }
            if (stat.chatChannel.equals(ChatsHandler.getChannel("global"))) {
                channel = ChatsHandler.getChannel("global");
                identifier = stat.chatIdentifier;
            }
            if (stat.chatChannel.equals(ChatsHandler.getChannel("guild"))) {
                channel = ChatsHandler.getChannel("guild");
                identifier = (stat.chatIdentifier.equals("network") ? stat.guild : stat.chatIdentifier);
            }
            if (stat.chatChannel.equals(ChatsHandler.getChannel("party"))) {
                channel = ChatsHandler.getChannel("party");
                identifier = (stat.chatIdentifier.equals("network") ? stat.party : stat.chatIdentifier);
            }

            StreamLine.discordData.sendDiscordChannel(sender, channel, identifier, msg, bypass);
        }

        if (ConfigUtils.chatHistoryEnabled()) {
            PlayerUtils.addLineToChatHistory(stat.uuid, sender.getServer().getInfo().getName(), msg);
        }

        if (bypass) {
            stat.tickBypassFor();
        }

        if (ConfigUtils.events()) {
            if (!msg.startsWith("/")) {
                for (Event event : EventsHandler.getEvents()) {
                    if (!EventsHandler.checkTags(event, stat)) continue;

                    if (!EventsHandler.checkEventConditions(event, stat, Condition.MESSAGE_EXACT, Arrays.asList(msg, "", "null")))
                        continue;

                    EventsHandler.runEvent(event, stat, msg);
                }
            } else {
                for (Event event : EventsHandler.getEvents()) {
                    if (!EventsHandler.checkTags(event, stat)) continue;

                    if (!EventsHandler.checkEventConditions(event, stat, Condition.COMMAND, msg) || !EventsHandler.checkEventConditions(event, stat, Condition.MESSAGE_EXACT, Arrays.asList(msg.substring(1), "", "null")))
                        continue;

                    EventsHandler.runEvent(event, stat, msg);
                }
            }

            for (Event event : EventsHandler.getEvents()) {
                if (!EventsHandler.checkTags(event, stat)) continue;

                if (!EventsHandler.checkEventConditions(event, stat, Condition.MESSAGE_CONTAINS, msg)) continue;

                EventsHandler.runEvent(event, stat, msg);
            }

            for (Event event : EventsHandler.getEvents()) {
                if (!EventsHandler.checkTags(event, stat)) continue;

                if (!EventsHandler.checkEventConditions(event, stat, Condition.MESSAGE_STARTS_WITH, msg)) continue;

                EventsHandler.runEvent(event, stat, msg);
            }

            for (Event event : EventsHandler.getEvents()) {
                if (!EventsHandler.checkTags(event, stat)) continue;

                if (!EventsHandler.checkEventConditions(event, stat, Condition.MESSAGE_ENDS_WITH, msg)) continue;

                EventsHandler.runEvent(event, stat, msg);
            }
        }
    }
}
