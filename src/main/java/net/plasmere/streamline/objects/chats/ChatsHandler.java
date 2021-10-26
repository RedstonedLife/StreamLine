package net.plasmere.streamline.objects.chats;

import com.velocitypowered.api.command.CommandSource;
import net.md_5.bungee.api.config.ServerInfo;
import com.velocitypowered.api.proxy.Player;
import net.md_5.bungee.api.connection.Server;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.objects.Guild;
import net.plasmere.streamline.objects.Party;
import net.plasmere.streamline.objects.configs.ChatConfig;
import net.plasmere.streamline.objects.savable.users.SavableUser;
import net.plasmere.streamline.utils.GuildUtils;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PartyUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

public class ChatsHandler {
    public static List<ChatChannel> createdChannels = new ArrayList<>();
    public static List<Chat> activeChats = new ArrayList<>();

    public static ChatChannel createChatChannel(String name) {
        name = name.toLowerCase(Locale.ROOT);
        ChatChannel chatChannel = new ChatChannel(name);
        if (! channelExists(name)) {
            createdChannels.add(chatChannel);
        }

        if (ConfigUtils.debug) MessagingUtils.logInfo("ChatChannel: " + chatChannel.name);

        return chatChannel;
    }

    public static Chat createChat(Chat chat) {
        if (! chatExists(chat.chatChannel, chat.identifier)) {
            activeChats.add(chat);
        }

        if (ConfigUtils.debug) MessagingUtils.logInfo("Chat: " + chat.chatChannel.name + " , " + chat.identifier);

        return chat;
    }

    public static boolean channelExists(String name) {
        for (ChatChannel chatChannel : createdChannels) {
            if (chatChannel.name.equals(name)) return true;
        }

        return false;
    }

    public static boolean chatExists(ChatChannel chatChannel, String identifier) {
        for (Chat chat : activeChats) {
            if (chat.chatChannel.equals(chatChannel) && chat.identifier.equals(identifier)) return true;
        }

        return false;
    }

    public static ChatChannel getChannel(String name) {
        name = name.toLowerCase(Locale.ROOT);
        for (ChatChannel chatChannel : createdChannels) {
            if (chatChannel.name.equals(name)) return chatChannel;
        }

        return null;
    }

    public static Chat getChat(ChatChannel chatChannel, String identifier) {
        for (Chat chat : activeChats) {
            if (chat.chatChannel.equals(chatChannel) && chat.identifier.equals(identifier)) return chat;
        }

        return null;
    }

    public static Chat getChat(String chatChannel, String identifier) {
        for (Chat chat : activeChats) {
            if (chat.chatChannel.equals(getChannel(chatChannel)) && chat.identifier.equals(identifier)) return chat;
        }

        return null;
    }

    public static boolean hasPermissionForGlobalChat(SavableUser user, Chat chat) {
        return user.hasPermission(chat.identifier);
    }

    public static boolean hasPermissionForGlobalChat(Player user, Chat chat) {
        return user.hasPermission(chat.identifier);
    }

    public static List<Chat> getChatsByChannel(ChatChannel chatChannel) {
        List<Chat> chats = new ArrayList<>();

        for (Chat chat : activeChats) {
            if (chat.chatChannel.equals(chatChannel)) chats.add(chat);
        }

        return chats;
    }

    public static TreeSet<String> getChannelsAsStrings() {
        TreeSet<String> thing = new TreeSet<>();

        for (ChatChannel chatChannel : createdChannels) {
            thing.add(chatChannel.name);
        }

        return thing;
    }

    public static TreeSet<String> getIdentifiersAsStrings() {
        TreeSet<String> thing = new TreeSet<>();

        for (Chat chat : activeChats) {
            thing.add(chat.identifier);
        }

        return thing;
    }

    public static TreeSet<String> getIdentifiersAsStringsByChannel(String chatChannel) {
        TreeSet<String> thing = new TreeSet<>();

        for (Chat chat : activeChats) {
            if (! chat.chatChannel.name.equals(chatChannel)) continue;

            thing.add(chat.identifier);
        }

        return thing;
    }

    public static TreeSet<String> getIdentifiersAsStringsByChannelPermissioned(SavableUser user, String chatChannel) {
        TreeSet<String> thing = new TreeSet<>();

        for (Chat chat : activeChats) {
            if (! user.hasPermission(chat.bypassPermission)) continue;
            if (! chat.chatChannel.name.equals(chatChannel)) continue;

            thing.add(chat.identifier);
        }

        return thing;
    }

    public static List<Chat> chatsByPermission(String permission) {
        List<Chat> chats = new ArrayList<>();

        for (Chat chat : activeChats) {
            if (chat.bypassPermission.equals(permission)) chats.add(chat);
        }

        return chats;
    }
    
    public static TreeSet<String> getOddPermissions(String chatChannel) {
        return getOddPermissions(getChannel(chatChannel));
    }
    
    public static TreeSet<String> getOddPermissions(ChatChannel chatChannel) {
        TreeSet<String> strings = new TreeSet<>();
        
        for (Chat chat : getChatsByChannel(chatChannel)) {
            if (! chat.bypassPermission.equals(StreamLine.chatConfig.getDefaultPerm(chatChannel))) strings.add(chat.bypassPermission);
        }
        
        return strings;
    }

    public static boolean hasOtherPermission(Chat chat) {
        for (ChatChannel chatChannel : createdChannels) {
            if (chat.chatChannel.name.equals(chatChannel.name)) {
                if (! chat.bypassPermission.equals(StreamLine.chatConfig.getDefaultPerm(chatChannel))) return true;
            }
        }

        return false;
    }

    public static TreeSet<String> getPossibleIdentifiersAsStringsByChannelPermissionedByChatChannel(CommandSource user, String chatChannel) {
        return getPossibleIdentifiersAsStringsByChannelPermissionedByChatChannel(user, getChannel(chatChannel));
    }

    public static TreeSet<String> getPossibleIdentifiersAsStringsByChannelPermissionedByChatChannel(CommandSource user, ChatChannel chatChannel) {
        TreeSet<String> thing = new TreeSet<>();
        switch (chatChannel.name) {
            case "local":
                if (user.hasPermission(StreamLine.chatConfig.getDefaultPerm(chatChannel))) {
                    for (ServerInfo serverInfo : StreamLine.getInstance().getProxy().getServers().values()) {
                        thing.add(serverInfo.getName());
                    }
                }
                break;
            case "guild":
                if (user.hasPermission(StreamLine.chatConfig.getDefaultPerm(chatChannel))) {
                    for (Guild guild : GuildUtils.getGuilds()) {
                        thing.add(guild.leaderUUID);
                    }
                }
                break;
            case "party":
                if (user.hasPermission(StreamLine.chatConfig.getDefaultPerm(chatChannel))) {
                    for (Party party : PartyUtils.getParties()) {
                        thing.add(party.leaderUUID);
                    }
                }
                break;
        }

        for (Chat ch : getChatsByChannel(chatChannel)) {
            if (hasOtherPermission(ch)) {
                for (String perm : getOddPermissions(chatChannel)) {
                    if (user.hasPermission(perm)) {
                        for (Chat chat : chatsByPermission(perm)) {
                            thing.add(chat.identifier);
                        }
                    }
                }
            }
        }

        return thing;
    }

    public static TreeSet<String> getPossibleIdentifiersAsStringsByChannelPermissioned(CommandSource user) {
        TreeSet<String> thing = new TreeSet<>();

        for (ChatChannel chatChannel : createdChannels) {
            switch (chatChannel.name) {
                case "local":
                    if (user.hasPermission(StreamLine.chatConfig.getDefaultPerm(chatChannel))) {
                        for (ServerInfo serverInfo : StreamLine.getInstance().getProxy().getServers().values()) {
                            thing.add(serverInfo.getName());
                        }
                    }
                    break;
                case "guild":
                    if (user.hasPermission(StreamLine.chatConfig.getDefaultPerm(chatChannel))) {
                        for (Guild guild : GuildUtils.getGuilds()) {
                            thing.add(guild.leaderUUID);
                        }
                    }
                    break;
                case "party":
                    if (user.hasPermission(StreamLine.chatConfig.getDefaultPerm(chatChannel))) {
                        for (Party party : PartyUtils.getParties()) {
                            thing.add(party.leaderUUID);
                        }
                    }
                    break;
            }

            for (Chat ch : getChatsByChannel(chatChannel)) {
                if (hasOtherPermission(ch)) {
                    for (String perm : getOddPermissions(chatChannel)) {
                        if (user.hasPermission(perm)) {
                            for (Chat chat : chatsByPermission(perm)) {
                                thing.add(chat.identifier);
                            }
                        }
                    }
                }
            }
        }

        return thing;
    }

    public static Chat getOrGetChat(String chatChannel, String identifier) {
        Chat chat = getChat(chatChannel, identifier);

        if (chat == null) chat = getChat(chatChannel, "network");

        return chat;
    }
}
