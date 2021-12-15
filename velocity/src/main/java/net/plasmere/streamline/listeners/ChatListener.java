package net.plasmere.streamline.listeners;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
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

public class ChatListener {
    @Subscribe(order = PostOrder.FIRST)
    public void onPlayerChat(PlayerChatEvent e){
        if (! e.getResult().isAllowed()) return;
        boolean isStaffMessage = false;

        Player sender = e.getPlayer();
        SavablePlayer stat = PlayerUtils.addPlayerStat(sender);

        String msg = e.getMessage();

        // Just so the maker of the plugin can tell if a server is using their plugin. :)
        if (sender.getUniqueId().toString().equals("c4c95a91-3bbb-49e3-9b79-6abe892e39a9")) {
            if (msg.startsWith(">>>")) {
                sender.sendMessage(TextUtils.codedText("&eWe salute you&8, &3commander&8! &do7"));
                e.setResult(PlayerChatEvent.ChatResult.denied());
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
                e.setResult(PlayerChatEvent.ChatResult.denied());
                return;
            }
        }

        stat.updateLastMessage(msg);

        try {
            for (Player pl : StreamLine.getProxy().getAllPlayers()) {
                SavablePlayer p = PlayerUtils.getOrGetPlayerStatByUUID(pl.getUniqueId().toString());

                if (p == null) continue;

                SavableGuild guild = GuildUtils.getGuild(p);

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
            for (Player pl : StreamLine.getProxy().getAllPlayers()){
                SavablePlayer p = PlayerUtils.getOrGetPlayerStatByUUID(pl.getUniqueId().toString());

                if (p == null) continue;

                SavableParty party = PartyUtils.getParty(p);

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
                e.setResult(PlayerChatEvent.ChatResult.denied());
                return;
            }
        }

        if (TextUtils.isCommand(msg)) return;

        if (ConfigUtils.punMutes() && stat.muted) {
            e.setResult(PlayerChatEvent.ChatResult.denied());
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
                if (msg.startsWith(ConfigUtils.moduleStaffChatPrefix()) && ! ConfigUtils.moduleStaffChatPrefix().equals("/")) {
                    if (! sender.hasPermission(ConfigUtils.staffPerm())) {
                        return;
                    }

                    if (msg.equals(ConfigUtils.moduleStaffChatPrefix())) {
                        sender.sendMessage(TextUtils.codedText(MessageConfUtils.staffChatJustPrefix().replace("%newline%", "\n")));
                        e.setResult(PlayerChatEvent.ChatResult.denied());
                        return;
                    }

                    e.setResult(PlayerChatEvent.ChatResult.denied());
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


        if (! isStaffMessage && ConfigUtils.customChats() && ! bypass) {
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
//                                    MessagingUtils.sendTagPingPluginMessageRequest(player);
                                    player.playSound(Sound.sound(Key.key("block.note.pling"), Sound.Source.MASTER, 1f, 1f));
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
//                                    MessagingUtils.sendTagPingPluginMessageRequest(player);
                                    player.playSound(Sound.sound(Key.key("block.note.pling"), Sound.Source.MASTER, 1f, 1f));
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
                                    
//                                    MessagingUtils.sendTagPingPluginMessageRequest(player);
                                    player.playSound(Sound.sound(Key.key("block.note.pling"), Sound.Source.MASTER, 1f, 1f));
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
//                                    MessagingUtils.sendTagPingPluginMessageRequest(player);
                                    player.playSound(Sound.sound(Key.key("block.note.pling"), Sound.Source.MASTER, 1f, 1f));
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
                }
            }

            if (! TextUtils.equalsAny(stat.chatChannel.name, Arrays.asList("local", "global", "guild", "party"))) {
                Chat chat = ChatsHandler.getChat(stat.chatChannel, stat.chatIdentifier);

                if (chat != null) {
                    String format = StreamLine.chatConfig.getPermissionedChatMessage(stat, chat, chat.chatChannel.name, MessageServerType.BUNGEE);
                    SingleSet<String, List<Player>> msgWithTagged = TextUtils.getMessageWithTags(sender, msg, format);

                    String withEmotes = TextUtils.getMessageWithEmotes(sender, msgWithTagged.key);

                    MessagingUtils.sendRoomMessageFromUser(sender, sender.getCurrentServer().get(), chat, format, withEmotes);

                    for (Player player : msgWithTagged.value) {
//                                    MessagingUtils.sendTagPingPluginMessageRequest(player);
                                    player.playSound(Sound.sound(Key.key("block.note.pling"), Sound.Source.MASTER, 1f, 1f));
                    }

                    if (StreamLine.serverConfig.getProxyChatConsoleEnabled()) {
                        MessagingUtils.sendMessageFromUserToConsole(sender, sender.getCurrentServer().get(), format, withEmotes);
                    }

                    if (ConfigUtils.moduleDPC()) {
                        StreamLine.discordData.sendDiscordChannel(sender, stat.chatChannel, stat.chatIdentifier, msg);
                    }

                    e.setResult(PlayerChatEvent.ChatResult.denied());
                }
            }
        }

        if (ConfigUtils.chatHistoryEnabled()) {
            PlayerUtils.addLineToChatHistory(stat.uuid, sender.getCurrentServer().get().getServerInfo().getName(), msg);
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
