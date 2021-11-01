package net.plasmere.streamline.listeners;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.DiscordBotConfUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.events.Event;
import net.plasmere.streamline.events.EventsHandler;
import net.plasmere.streamline.events.enums.Condition;
import net.plasmere.streamline.objects.SavableGuild;
import net.plasmere.streamline.objects.chats.Chat;
import net.plasmere.streamline.objects.chats.ChatChannel;
import net.plasmere.streamline.objects.chats.ChatsHandler;
import net.plasmere.streamline.objects.enums.MessageServerType;
import net.plasmere.streamline.objects.filters.ChatFilter;
import net.plasmere.streamline.objects.filters.FilterHandler;
import net.plasmere.streamline.objects.lists.SingleSet;
import net.plasmere.streamline.objects.messaging.DiscordMessage;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.utils.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

public class ChatListener {
    private static String prefix = ConfigUtils.moduleStaffChatPrefix();

    @Subscribe(order = PostOrder.FIRST)
    public void onPlayerChat(PlayerChatEvent e){
        if (! e.getResult().isAllowed()) return;
        boolean isStaffMessage = false;

        Player sender = e.getPlayer();

        String msg = e.getMessage();

        if (ConfigUtils.moduleBChatFiltersEnabled()) {
            FilterHandler.reloadAllFilters();

            for (ChatFilter filter : FilterHandler.filters) {
                if (! filter.enabled) continue;
                msg = filter.applyFilter(msg);
            }
        }

        SavablePlayer stat = PlayerUtils.addPlayerStat(sender);

        stat.updateLastMessage(msg);

        try {
            for (Player pl : StreamLine.getInstance().getProxy().getAllPlayers()){
                SavablePlayer p = PlayerUtils.getOrCreatePlayerStat(pl);

                if (GuildUtils.getGuild(p) == null && ! p.equals(stat)) continue;
                if (GuildUtils.getGuild(p) != null) {
                    if (Objects.requireNonNull(GuildUtils.getGuild(p)).hasMember(stat)) break;
                }

                if (GuildUtils.pHasGuild(stat)) {
                    GuildUtils.addGuild(new SavableGuild(stat.guild, false));
                }
                break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (ConfigUtils.punMutes() && ConfigUtils.punMutesHard() && stat.muted) {
            if (PlayerUtils.checkIfMuted(sender, stat)) {
                e.setResult(PlayerChatEvent.ChatResult.denied());
                return;
            }
        }

        if (TextUtils.isCommand(msg)) return;

        if (ConfigUtils.punMutes() && stat.muted) {
            e.setResult(PlayerChatEvent.ChatResult.denied());
            if (stat.mutedTill != null) {
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

                e.setResult(PlayerChatEvent.ChatResult.denied());
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
                if (msg.startsWith(prefix) && ! prefix.equals("/")) {
                    if (! sender.hasPermission(ConfigUtils.staffPerm())) {
                        return;
                    }

                    if (msg.equals(prefix)) {
                        sender.sendMessage(TextUtils.codedText(MessageConfUtils.staffChatJustPrefix().replace("%newline%", "\n")));
                        e.setResult(PlayerChatEvent.ChatResult.denied());
                        return;
                    }

                    e.setResult(PlayerChatEvent.ChatResult.denied());
                    MessagingUtils.sendStaffMessage(sender, MessageConfUtils.bungeeStaffChatFrom(), msg.substring(prefix.length()));
                    if (ConfigUtils.moduleDEnabled()) {
                        if (ConfigUtils.moduleStaffChatMToDiscord()) {
                            MessagingUtils.sendDiscordEBMessage(new DiscordMessage(sender,
                                    MessageConfUtils.staffChatEmbedTitle(),
                                    TextUtils.replaceAllPlayerDiscord(MessageConfUtils.discordStaffChatMessage(), sender)
                                            .replace("%message%", msg.substring(prefix.length())),
                                    DiscordBotConfUtils.textChannelStaffChat()));
                        }
                    }
                    isStaffMessage = true;
                }
            }
        }


        if (! isStaffMessage) {
            if (StreamLine.serverConfig.getProxyChatEnabled()) {
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
                    if (! allowGlobal && stat.chatChannel.equals(ChatsHandler.getChannel("global"))) {
                        stat.setChatChannel("local");
                        stat.setChatIdentifier("network");
                    }
                    if (! allowLocal && stat.chatChannel.equals(ChatsHandler.getChannel("local"))) {
                        stat.setChatChannel("global");
                        stat.setChatIdentifier("network");
                    }

                    if (stat.chatChannel.equals(ChatsHandler.getChannel("global"))) {
                        Chat chat = ChatsHandler.getChat(ChatsHandler.getChannel("global"), stat.chatIdentifier);
                        if (chat != null) {
                            if (chat.identifier.equals("network")) {
                                String format = StreamLine.chatConfig.getPermissionedChatMessage(stat, chat, chat.chatChannel.name, MessageServerType.BUNGEE);
                                SingleSet<String, List<Player>> msgWithTagged = TextUtils.getMessageWithTags(sender, msg, format);

                                String withEmotes = TextUtils.getMessageWithEmotes(sender, msgWithTagged.key);

                                MessagingUtils.sendGlobalMessageFromUser(sender, sender.getCurrentServer().get(), format, withEmotes);

                                for (Player player : msgWithTagged.value) {
                                    MessagingUtils.sendTagPingPluginMessageRequest(player);
                                }

                                if (StreamLine.serverConfig.getProxyChatConsoleEnabled()) {
                                    MessagingUtils.sendMessageFromUserToConsole(sender, sender.getCurrentServer().get(), format, withEmotes);
                                }

                                if (ConfigUtils.moduleDPC()) {
                                    StreamLine.discordData.sendDiscordChannel(sender, ChatsHandler.getChannel("global"), "", msg);
                                }
                            } else {
                                if (ChatsHandler.hasPermissionForGlobalChat(stat, chat)) {
                                    String format = StreamLine.chatConfig.getPermissionedChatMessage(stat, chat, chat.chatChannel.name, MessageServerType.BUNGEE);
                                    SingleSet<String, List<Player>> msgWithTagged = TextUtils.getMessageWithTags(sender, msg, format);

                                    String withEmotes = TextUtils.getMessageWithEmotes(sender, msgWithTagged.key);

                                    MessagingUtils.sendPermissionedGlobalMessageFromUser(chat.identifier, sender, sender.getCurrentServer().get(), format, withEmotes);

                                    for (Player player : msgWithTagged.value) {
                                        MessagingUtils.sendTagPingPluginMessageRequest(player);
                                    }

                                    if (StreamLine.serverConfig.getProxyChatConsoleEnabled()) {
                                        MessagingUtils.sendMessageFromUserToConsole(sender, sender.getCurrentServer().get(), format, withEmotes);
                                    }

                                    if (ConfigUtils.moduleDPC()) {
                                        StreamLine.discordData.sendDiscordChannel(sender, ChatsHandler.getChannel("global"), chat.identifier, msg);
                                    }
                                }
                            }
                        } else {
                            ChatChannel chatChannel = ChatsHandler.getChannel("global");
                            Chat ch = StreamLine.chatConfig.getDefaultChat(chatChannel);
                            if (ChatsHandler.hasPermissionForGlobalChat(stat, ch)) {
                                String format = StreamLine.chatConfig.getPermissionedChatMessage(stat, ch, chatChannel.name, MessageServerType.BUNGEE);
                                SingleSet<String, List<Player>> msgWithTagged = TextUtils.getMessageWithTags(sender, msg, format);

                                String withEmotes = TextUtils.getMessageWithEmotes(sender, msgWithTagged.key);

                                MessagingUtils.sendPermissionedGlobalMessageFromUser(ch.identifier, sender, sender.getCurrentServer().get(), format, withEmotes);

                                for (Player player : msgWithTagged.value) {
                                    MessagingUtils.sendTagPingPluginMessageRequest(player);
                                }

                                if (StreamLine.serverConfig.getProxyChatConsoleEnabled()) {
                                    MessagingUtils.sendMessageFromUserToConsole(sender, sender.getCurrentServer().get(), format, withEmotes);
                                }

                                if (ConfigUtils.moduleDPC()) {
                                    StreamLine.discordData.sendDiscordChannel(sender, ChatsHandler.getChannel("global"), ch.identifier, msg);
                                }
                            }
                        }

                        e.setResult(PlayerChatEvent.ChatResult.denied());
                    } else if (stat.chatChannel.equals(ChatsHandler.getChannel("local"))) {
                        Chat chat = ChatsHandler.getChat(ChatsHandler.getChannel("local"), stat.chatIdentifier);

                        String format = StreamLine.chatConfig.getPermissionedChatMessage(stat, chat, ChatsHandler.getChannel("local").name, MessageServerType.BUNGEE);
                        SingleSet<String, List<Player>> msgWithTagged = TextUtils.getMessageWithTags(sender, msg, format);

                        String withEmotes = TextUtils.getMessageWithEmotes(sender, msgWithTagged.key);

                        if (stat.chatIdentifier.equals("network")) {
                            MessagingUtils.sendServerMessageFromUser(sender, sender.getCurrentServer().get(), sender.getCurrentServer().get().getServerInfo().getName(), format, withEmotes);
                        } else {
                            MessagingUtils.sendServerMessageFromUser(sender, sender.getCurrentServer().get(), stat.chatIdentifier, format, withEmotes);
                            if (! stat.chatIdentifier.equals(sender.getCurrentServer().get().getServerInfo().getName())) {
                                MessagingUtils.sendServerMessageOtherServerSelf(sender, sender.getCurrentServer().get(), format, withEmotes);
                            }
                        }

                        for (Player player : msgWithTagged.value) {
                            MessagingUtils.sendTagPingPluginMessageRequest(player);
                        }

                        if (StreamLine.serverConfig.getProxyChatConsoleEnabled()) {
                            MessagingUtils.sendMessageFromUserToConsole(sender, sender.getCurrentServer().get(), format, withEmotes);
                        }

                        if (ConfigUtils.moduleDPC()) {
                            StreamLine.discordData.sendDiscordChannel(sender, ChatsHandler.getChannel("local"), sender.getCurrentServer().get().getServerInfo().getName(), msg);
                        }

                        e.setResult(PlayerChatEvent.ChatResult.denied());
                    }
                }

                if (stat.chatChannel.equals(ChatsHandler.getChannel("guild"))) {
                    GuildUtils.sendChat(stat, GuildUtils.getOrGetGuild(stat.chatIdentifier), msg);

                    e.setResult(PlayerChatEvent.ChatResult.denied());
                } else if (stat.chatChannel.equals(ChatsHandler.getChannel("party"))) {
                    PartyUtils.sendChat(stat, PartyUtils.getParty(stat.chatIdentifier), msg);

                    e.setResult(PlayerChatEvent.ChatResult.denied());
                } else if (! stat.chatChannel.equals(ChatsHandler.getChannel("local")) && ! stat.chatChannel.equals(ChatsHandler.getChannel("global"))) {
                    Chat chat = ChatsHandler.getChat(stat.chatChannel, stat.chatIdentifier);

                    if (chat != null) {
                        String format = StreamLine.chatConfig.getPermissionedChatMessage(stat, chat, chat.chatChannel.name, MessageServerType.BUNGEE);
                        SingleSet<String, List<Player>> msgWithTagged = TextUtils.getMessageWithTags(sender, msg, format);

                        String withEmotes = TextUtils.getMessageWithEmotes(sender, msgWithTagged.key);

                        MessagingUtils.sendRoomMessageFromUser(sender, sender.getCurrentServer().get(), chat, format, withEmotes);

                        for (Player player : msgWithTagged.value) {
                            MessagingUtils.sendTagPingPluginMessageRequest(player);
                        }

                        if (StreamLine.serverConfig.getProxyChatConsoleEnabled()) {
                            MessagingUtils.sendMessageFromUserToConsole(sender, sender.getCurrentServer().get(), format, withEmotes);
                        }

                        if (ConfigUtils.moduleDPC()) {
                            StreamLine.discordData.sendDiscordChannel(sender, stat.chatChannel, sender.getCurrentServer().get().getServerInfo().getName(), msg);
                        }

                        e.setResult(PlayerChatEvent.ChatResult.denied());
                    }
                }

            } else {
                if (ConfigUtils.moduleDPC()) {
                    if (stat.chatChannel.equals(ChatsHandler.getChannel("global"))) {
                        if (StreamLine.discordData.ifHasChannels(ChatsHandler.getChannel("global"), "")) {
                            TreeMap<Long, Boolean> ifHas = StreamLine.discordData.ifChannelBypasses(ChatsHandler.getChannel("global"), "");
                            for (Long l : ifHas.keySet()) {
                                if (!ifHas.get(l)) continue;

                                StreamLine.discordData.sendDiscordChannel(sender, ChatsHandler.getChannel("global"), "", msg);
                            }
                        }
                    }

                    if (stat.chatChannel.equals(ChatsHandler.getChannel("local"))) {
                        if (StreamLine.discordData.ifHasChannels(ChatsHandler.getChannel("local"), sender.getCurrentServer().get().getServerInfo().getName())) {
                            TreeMap<Long, Boolean> ifHas = StreamLine.discordData.ifChannelBypasses(ChatsHandler.getChannel("local"), sender.getCurrentServer().get().getServerInfo().getName());
                            for (Long l : ifHas.keySet()) {
                                if (!ifHas.get(l)) continue;

                                StreamLine.discordData.sendDiscordChannel(sender, ChatsHandler.getChannel("local"), sender.getCurrentServer().get().getServerInfo().getName(), msg);
                            }
                        }
                    }

//                    if (stat.chatChannel.equals(ChatsHandler.getChannel("guild"))) {
//                        if (StreamLine.discordData.ifHasChannels(ChatsHandler.getChannel("guild"), )) {
//                            TreeMap<Long, Boolean> ifHas = StreamLine.discordData.ifChannelBypasses(ChatsHandler.getChannel("guild"), sender.getCurrentServer().get().getServerInfo().getName());
//                            for (Long l : ifHas.keySet()) {
//                                if (!ifHas.get(l)) continue;
//
//                                StreamLine.discordData.sendDiscordChannel(sender, ChatsHandler.getChannel("guild"), sender.getCurrentServer().get().getServerInfo().getName(), msg);
//                            }
//                        }
//                    }
//
//                    if (stat.chatChannel.equals(ChatsHandler.getChannel("party"))) {
//                        if (StreamLine.discordData.ifHasChannels(ChatsHandler.getChannel("party"), sender.getCurrentServer().get().getServerInfo().getName())) {
//                            TreeMap<Long, Boolean> ifHas = StreamLine.discordData.ifChannelBypasses(ChatsHandler.getChannel("party"), sender.getCurrentServer().get().getServerInfo().getName());
//                            for (Long l : ifHas.keySet()) {
//                                if (!ifHas.get(l)) continue;
//
//                                StreamLine.discordData.sendDiscordChannel(sender, ChatsHandler.getChannel("party"), sender.getCurrentServer().get().getServerInfo().getName(), msg);
//                            }
//                        }
//                    }
                }
            }
        }

        if (ConfigUtils.chatHistoryEnabled()) {
            PlayerUtils.addLineToChatHistory(stat.uuid, sender.getCurrentServer().get().getServerInfo().getName(), msg);
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
