package net.plasmere.streamline.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.DiscordBotConfUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.events.EventsHandler;
import net.plasmere.streamline.objects.SavableGuild;
import net.plasmere.streamline.objects.SavableParty;
import net.plasmere.streamline.objects.chats.Chat;
import net.plasmere.streamline.objects.filters.ChatFilter;
import net.plasmere.streamline.objects.messaging.BungeeMassMessage;
import net.plasmere.streamline.objects.messaging.BungeeMessage;
import net.plasmere.streamline.objects.messaging.DiscordMessage;
import net.plasmere.streamline.objects.savable.users.SavableConsole;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.objects.savable.users.SavableUser;

import java.io.File;
import java.util.*;

public class MessagingUtils {
    public static HashMap<ProxiedPlayer, String> serveredUsernames = new HashMap<>();

    public static void sendStaffMessage(CommandSender sender, String from, String msg){
        List<SavableUser> toExclude = new ArrayList<>();

        for (SavableUser user : PlayerUtils.getJustStaffOnline()) {
            if (! user.scvs || ! user.viewsc) toExclude.add(user);
        }

        sendPermissionedMessageExcludePlayers(toExclude, ConfigUtils.staffPerm(), TextUtils.replaceAllPlayerBungee(
                TextUtils.replaceAllSenderBungee(MessageConfUtils.bungeeStaffChatMessage(), sender), sender)
                .replace("%from_type%", from)
                .replace("%from%", from)
                .replace("%message%", msg)
                .replace("%from_server%", PlayerUtils.getOrGetSavableUser(sender).findServer())
                .replace("%server%", PlayerUtils.getOrGetSavableUser(sender).findServer())
                .replace("%version%", PlayerUtils.getOrGetSavableUser(sender).latestVersion)
        );
    }

    public static void sendStaffMessageExcludeSelf(CommandSender sender, String from, String msg){
        sendPermissionedMessageNonSelf(sender, ConfigUtils.staffPerm(), TextUtils.replaceAllPlayerBungee(
                TextUtils.replaceAllSenderBungee(MessageConfUtils.bungeeStaffChatMessage(), sender), sender)
                .replace("%from_type%", from)
                .replace("%from%", from)
                .replace("%message%", msg)
                .replace("%from_server%", PlayerUtils.getOrGetSavableUser(sender).findServer())
                .replace("%server%", PlayerUtils.getOrGetSavableUser(sender).findServer())
                .replace("%version%", PlayerUtils.getOrGetSavableUser(sender).latestVersion)
        );
    }

    public static void sendPermissionedMessageExcludePlayers(List<SavableUser> toExclude, String toPermission, String message){
        List<SavableUser> toUsers = new ArrayList<>();
        List<String> excludedUUIDs = new ArrayList<>();

        for (SavableUser user : toExclude) {
            excludedUUIDs.add(user.uuid);
        }

        for (SavableUser user : PlayerUtils.getStatsOnline()) {
            if (excludedUUIDs.contains(user.uuid)) continue;
            if (user.hasPermission(toPermission)) toUsers.add(user);
        }

        for (SavableUser player : toUsers) {
            player.sendMessage(TextUtils.codedText(message));
        }
    }

    public static void sendPermissionedMessage(String toPermission, String message){
        Set<ProxiedPlayer> toPlayers = new HashSet<>();

        for (ProxiedPlayer player : PlayerUtils.getOnlinePPlayers()) {
            if (player.hasPermission(toPermission)) toPlayers.add(player);
        }

        for (ProxiedPlayer player : toPlayers) {
            player.sendMessage(TextUtils.codedText(message));
        }
    }

    public static void sendPermissionedMessageNonSelf(CommandSender sender, String toPermission, String message){
        Set<ProxiedPlayer> toPlayers = new HashSet<>();

        for (ProxiedPlayer player : PlayerUtils.getOnlinePPlayers()) {
            if (player.getName().equals(sender.getName())) continue;
            if (player.hasPermission(toPermission)) toPlayers.add(player);
        }

        for (ProxiedPlayer player : toPlayers) {
            player.sendMessage(TextUtils.codedText(TextUtils.replaceAllSenderBungee(message, sender)));
        }
    }

    public static void sendBungeeMessage(BungeeMassMessage message){
        Collection<ProxiedPlayer> staff = StreamLine.getInstance().getProxy().getPlayers();
        Set<ProxiedPlayer> people = new HashSet<>(staff);
        List<ProxiedPlayer> ps = new ArrayList<>(people);

        for (ProxiedPlayer player : people){
            try {
                if (! player.hasPermission(message.permission)) {
                    ps.remove(player);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        for (ProxiedPlayer player : ps) {
            sendBungeeMessage(new BungeeMessage(message.sender, player, message.title, message.transition, message.message));
        }
    }

    public static void sendBungeeMessage(BungeeMessage message){
        SavableUser player = PlayerUtils.getOrGetSavableUser(message.sender);

        message.to.sendMessage(TextUtils.codedText(TextUtils.replaceAllSenderBungee((message.title + message.transition + message.message)
                        .replace("%sender_display%", message.sender.getName()), message.sender)
                        .replace("%version%", player.latestVersion)
                )
        );
    }

    public static void sendServerMessageFromUser(ProxiedPlayer player, Server serverFrom, String serverTo, String format, String message) {
        for (ProxiedPlayer p : PlayerUtils.getServeredPPlayers(serverTo)) {
            p.sendMessage(TextUtils.codedText(TextUtils.replaceAllSenderBungee(format, player)
                    .replace("%sender_servered%", getPlayerDisplayName(player))
                    .replace("%message%", message)
                    .replace("%server%", serverFrom.getInfo().getName())
            ));
        }
    }

    public static void sendRoomMessageFromUser(ProxiedPlayer player, Server serverFrom, Chat room, String format, String message) {
        for (SavablePlayer p : PlayerUtils.getRoomedPlayers(room)) {
            p.sendMessage(TextUtils.codedText(TextUtils.replaceAllSenderBungee(format, player)
                    .replace("%sender_servered%", getPlayerDisplayName(player))
                    .replace("%message%", message)
                    .replace("%server%", serverFrom.getInfo().getName())
                    .replace("%room%", room.identifier)
            ));
        }
    }

    public static void sendRoomMessageFromDiscord(String nameUsed, String userID, Chat room, String format, String message) {
        for (SavablePlayer p : PlayerUtils.getRoomedPlayers(room)) {
            p.sendMessage(TextUtils.codedText(TextUtils.replaceAllSenderBungee(format, userID)
                    .replace("%sender_servered%", nameUsed)
                    .replace("%message%", message)
                    .replace("%server%", ConfigUtils.consoleServer())
                    .replace("%room%", room.identifier)
            ));
        }
    }

    public static void sendRoomMessageFromDiscord(SavableUser user, Chat room, String format, String message) {
        for (SavablePlayer p : PlayerUtils.getRoomedPlayers(room)) {
            p.sendMessage(TextUtils.codedText(TextUtils.replaceAllSenderBungee(format, user)
                    .replace("%sender_servered%", user.latestName)
                    .replace("%message%", message)
                    .replace("%server%", ConfigUtils.consoleServer())
                    .replace("%room%", room.identifier)
            ));
        }
    }

    public static void sendServerMessageOtherServerSelf(ProxiedPlayer player, Server serverFrom, String format, String message) {
        player.sendMessage(TextUtils.codedText(TextUtils.replaceAllSenderBungee(format, player)
                .replace("%sender_servered%", getPlayerDisplayName(player))
                .replace("%message%", message)
                .replace("%server%", serverFrom.getInfo().getName())
        ));
    }

    public static void sendGlobalMessageFromUser(ProxiedPlayer player, Server server, String format, String message) {
        for (ProxiedPlayer p : PlayerUtils.getOnlinePPlayers()) {
            p.sendMessage(TextUtils.codedText(TextUtils.replaceAllSenderBungee(format, player)
                    .replace("%sender_servered%", getPlayerDisplayName(player))
                    .replace("%message%", message)
                    .replace("%server%", server.getInfo().getName())
            ));
        }
    }

    public static void sendPermissionedGlobalMessageFromUser(String permission, ProxiedPlayer player, Server server, String format, String message) {
        for (ProxiedPlayer p : PlayerUtils.getPermissionedOnlineProxied(permission)) {
            p.sendMessage(TextUtils.codedText(TextUtils.replaceAllSenderBungee(format, player)
                    .replace("%sender_servered%", getPlayerDisplayName(player))
                    .replace("%message%", message)
                    .replace("%server%", server.getInfo().getName())
            ));
        }
    }

    public static void sendServerMessageFromDiscord(String nameUsed, ServerInfo server, String format, String message) {
        for (ProxiedPlayer p : PlayerUtils.getServeredPPlayers(server.getName())) {
            p.sendMessage(TextUtils.codedText(format
                    .replace("%sender_servered%", nameUsed)
                    .replace("%sender_display%", nameUsed)
                    .replace("%sender_normal%", nameUsed)
                    .replace("%sender_absolute%", nameUsed)
                    .replace("%sender_formatted%", nameUsed)
                    .replace("%message%", message)
                    .replace("%server%", server.getName())
            ));
        }
    }

    public static void sendGlobalMessageFromDiscord(String nameUsed, String format, String message) {
        for (ProxiedPlayer p : PlayerUtils.getOnlinePPlayers()) {
            p.sendMessage(TextUtils.codedText(format
                    .replace("%sender_servered%", nameUsed)
                    .replace("%sender_display%", nameUsed)
                    .replace("%sender_normal%", nameUsed)
                    .replace("%sender_absolute%", nameUsed)
                    .replace("%sender_formatted%", nameUsed)
                    .replace("%message%", message)
            ));
        }
    }

    public static void sendServerMessageFromDiscord(SavableUser user, ServerInfo server, String format, String message) {
        for (ProxiedPlayer p : PlayerUtils.getServeredPPlayers(server.getName())) {
            p.sendMessage(TextUtils.codedText(TextUtils.replaceAllSenderBungee(format, user)
                    .replace("%message%", message)
                    .replace("%server%", server.getName())
            ));
        }
    }

    public static void sendGlobalMessageFromDiscord(SavableUser user, String format, String message) {
        for (ProxiedPlayer p : PlayerUtils.getOnlinePPlayers()) {
            p.sendMessage(TextUtils.codedText(TextUtils.replaceAllSenderBungee(format, user)
                    .replace("%message%", message)
            ));
        }
    }

    public static void sendMessageFromUserToConsole(ProxiedPlayer player, Server server, String format, String message) {
        PlayerUtils.getConsoleStat().sendMessage(TextUtils.codedText(TextUtils.replaceAllSenderBungee(format, player)
                .replace("%sender_servered%", getPlayerDisplayName(player))
                .replace("%message%", message)
                .replace("%server%", server.getInfo().getName())
        ));
    }

    public static String getPlayerDisplayName(ProxiedPlayer player) {
        sendDisplayPluginMessageRequest(player);
        String string = serveredUsernames.get(player);

        return (string == null) ? PlayerUtils.getOrCreatePlayerStat(player).displayName : string;
    }

    public static void sendDisplayPluginMessageRequest(ProxiedPlayer player) {
        if (PlayerUtils.getServeredPPlayers(player.getServer().getInfo().getName()).size() <= 0) return;

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("request.displayname"); // the channel could be whatever you want

        // we send the data to the server
        // using ServerInfo the packet is being queued if there are no players in the server
        // using only the server to send data the packet will be lost if no players are in it
        player.getServer().getInfo().sendData(StreamLine.customChannel, out.toByteArray());
    }

    public static void sendGuildPluginMessageRequest(ProxiedPlayer to, SavableGuild guild) {
        if (PlayerUtils.getServeredPPlayers(to.getServer().getInfo().getName()).size() <= 0) return;

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("proxy.send.guild"); // the channel could be whatever you want
        try {
            File file = guild.file;
            Scanner reader = new Scanner(file);

            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if (line.startsWith("#")) continue;

                out.writeUTF(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // we send the data to the server
        // using ServerInfo the packet is being queued if there are no players in the server
        // using only the server to send data the packet will be lost if no players are in it
        to.getServer().getInfo().sendData(StreamLine.customChannel, out.toByteArray());
    }

    public static void sendPartyPluginMessageRequest(ProxiedPlayer to, SavableParty party) {
        if (PlayerUtils.getServeredPPlayers(to.getServer().getInfo().getName()).size() <= 0) return;

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("proxy.send.party"); // the channel could be whatever you want
        try {
            File file = party.file;
            Scanner reader = new Scanner(file);

            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if (line.startsWith("#")) continue;

                out.writeUTF(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // we send the data to the server
        // using ServerInfo the packet is being queued if there are no players in the server
        // using only the server to send data the packet will be lost if no players are in it
        to.getServer().getInfo().sendData(StreamLine.customChannel, out.toByteArray());
    }

    public static void sendSavableUserPluginMessageRequest(ProxiedPlayer to, SavableUser user, String type) {
        if (PlayerUtils.getServeredPPlayers(to.getServer().getInfo().getName()).size() <= 0) return;

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("proxy.send.user"); // the channel could be whatever you want
        out.writeUTF(type);

        try {
            File file = user.file;
            Scanner reader = new Scanner(file);

            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if (line.startsWith("#")) continue;

                out.writeUTF(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // we send the data to the server
        // using ServerInfo the packet is being queued if there are no players in the server
        // using only the server to send data the packet will be lost if no players are in it
        to.getServer().getInfo().sendData(StreamLine.customChannel, out.toByteArray());
    }

    public static void sendTeleportPluginMessageRequest(ProxiedPlayer sender, ProxiedPlayer to) {
        if (PlayerUtils.getServeredPPlayers(sender.getServer().getInfo().getName()).size() <= 0) return;

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("teleport"); // the channel could be whatever you want
        out.writeUTF(sender.getUniqueId().toString()); // this data could be whatever you want // THIS IS THE SENDER PLAYER
        out.writeUTF(to.getUniqueId().toString()); // this data could be whatever you want // THIS IS THE TO PLAYER

        // we send the data to the server
        // using ServerInfo the packet is being queued if there are no players in the server
        // using only the server to send data the packet will be lost if no players are in it
        sender.getServer().getInfo().sendData(StreamLine.customChannel, out.toByteArray());
    }

    public static void sendTagPingPluginMessageRequest(ProxiedPlayer toPing) {
        if (PlayerUtils.getServeredPPlayers(toPing.getServer().getInfo().getName()).size() <= 0) return;

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("tag-ping"); // the channel could be whatever you want
        out.writeUTF(toPing.getUniqueId().toString()); // this data could be whatever you want // THIS IS THE SENDER PLAYER

        // we send the data to the server
        // using ServerInfo the packet is being queued if there are no players in the server
        // using only the server to send data the packet will be lost if no players are in it
        toPing.getServer().getInfo().sendData(StreamLine.customChannel, out.toByteArray());
    }

    public static void sendGuildConfigPluginMessage(ProxiedPlayer to, SavableGuild guild) {
        if (PlayerUtils.getServeredPPlayers(to.getServer().getInfo().getName()).size() <= 0) return;

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("config.guild");
        out.writeUTF(guild.leaderUUID);

        try {
            Scanner reader = new Scanner(guild.file);

            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                while (data.startsWith("#")) {
                    data = reader.nextLine();
                }
                out.writeUTF(data);
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        to.getServer().getInfo().sendData(StreamLine.customChannel, out.toByteArray());
    }

    public static void sendStaffMessageFromDiscord(String discordSenderID, String from, String msg){
        Collection<ProxiedPlayer> staff = StreamLine.getInstance().getProxy().getPlayers();
        Set<ProxiedPlayer> staffs = new HashSet<>(staff);

        for (ProxiedPlayer player : staff){
            try {
                if (! player.hasPermission(ConfigUtils.staffPerm())) {
                    staffs.remove(player);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        for (ProxiedPlayer player : staffs) {
            player.sendMessage(TextUtils.codedText(TextUtils.replaceAllPlayerBungeeFromDiscord(
                                    TextUtils.replaceAllSenderBungeeFromDiscord(MessageConfUtils.bungeeStaffChatMessage(), discordSenderID), discordSenderID)
                            .replace("%from_type%", from)
                            .replace("%from%", from)
                            .replace("%message%", msg)
                            .replace("%from_server%", ConfigUtils.moduleStaffChatServer())
                            .replace("%server%", ConfigUtils.moduleStaffChatServer())
                            .replace("%version%", "JDA")
                    )
            );
        }
    }

    public static void sendStaffMessageReport(String sender, boolean fromBungee, String report){
        Collection<ProxiedPlayer> staff = StreamLine.getInstance().getProxy().getPlayers();
        Set<ProxiedPlayer> staffs = new HashSet<>(staff);

        for (ProxiedPlayer player : staff){
            try {
                if (! player.hasPermission("streamline.staff.reports")) {
                    staffs.remove(player);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        for (ProxiedPlayer player : staffs) {
            if (fromBungee)
                player.sendMessage(TextUtils.codedText(MessageConfUtils.bToBReportMessage()
                                .replace("%reporter%", sender)
                                .replace("%report%", report)
                                .replace("%version%", PlayerUtils.getOrCreatePlayerStat(player).latestVersion)
                        )
                );
            else
                player.sendMessage(TextUtils.codedText(MessageConfUtils.dToBReportMessage()
                                .replace("%reporter%", sender)
                                .replace("%report%", report)
                        )
                );
        }
    }

    public static void sendDiscordJoinLeaveMessagePlain(boolean isJoin, SavablePlayer player){
        if (! ConfigUtils.moduleDEnabled()) {
            return;
        }

        JDA jda = StreamLine.getJda();
        EmbedBuilder eb = new EmbedBuilder();

        try {
            if (isJoin) {
                Objects.requireNonNull(jda.getTextChannelById(DiscordBotConfUtils.textChannelBJoins()))
                        .sendMessageEmbeds(
                                eb
                                        .setDescription(TextUtils.replaceAllPlayerBungee(MessageConfUtils.discordOnline(), player))
                                        .setAuthor(MessageConfUtils.discordOnlineEmbed(), jda.getSelfUser().getAvatarUrl(), jda.getSelfUser().getAvatarUrl())
                                        .build()
                        ).queue();
            } else {
                Objects.requireNonNull(jda.getTextChannelById(DiscordBotConfUtils.textChannelBLeaves()))
                        .sendMessageEmbeds(
                                eb
                                        .setDescription(TextUtils.replaceAllPlayerBungee(MessageConfUtils.discordOffline(), player))
                                        .setAuthor(MessageConfUtils.discordOfflineEmbed(), jda.getSelfUser().getAvatarUrl(), jda.getSelfUser().getAvatarUrl())
                                        .build()
                        ).queue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendDiscordJoinLeaveMessageIcon(boolean isJoin, SavablePlayer player){
        if (! ConfigUtils.moduleDEnabled()) {
            return;
        }

        JDA jda = StreamLine.getJda();
        EmbedBuilder eb = new EmbedBuilder();

        try {
            if (isJoin) {
                try {
                    Objects.requireNonNull(jda.getTextChannelById(DiscordBotConfUtils.textChannelBJoins()))
                            .sendMessageEmbeds(
                                    eb
                                            .setDescription(TextUtils.replaceAllPlayerBungee(MessageConfUtils.discordOnline(), player))
                                            .setAuthor(MessageConfUtils.discordOnlineEmbed(), jda.getSelfUser().getAvatarUrl(), FaceFetcher.getFaceAvatarURL(player))
                                            .build()
                            ).queue();
                } catch (NullPointerException e) {
                    MessagingUtils.logSevere("Discord bot is either not in the Discord server, or the bot cannot find " + DiscordBotConfUtils.textChannelBJoins());
                }
            } else {
                try {
                Objects.requireNonNull(jda.getTextChannelById(DiscordBotConfUtils.textChannelBLeaves()))
                        .sendMessageEmbeds(
                                eb
                                        .setDescription(TextUtils.replaceAllPlayerBungee(MessageConfUtils.discordOffline(), player))
                                        .setAuthor(MessageConfUtils.discordOfflineEmbed(), jda.getSelfUser().getAvatarUrl(), FaceFetcher.getFaceAvatarURL(player))
                                        .build()
                        ).queue();

                } catch (NullPointerException e) {
                    MessagingUtils.logSevere("Discord bot is either not in the Discord server, or the bot cannot find " + DiscordBotConfUtils.textChannelBJoins());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendDiscordEBMessage(DiscordMessage message){
        if (! ConfigUtils.moduleDEnabled()) {
            return;
        }

        JDA jda = StreamLine.getJda();
        EmbedBuilder eb = new EmbedBuilder();

        try {
            if (ConfigUtils.moduleAvatarUse()) {
                if (message.sender instanceof ProxiedPlayer) {
                    Objects.requireNonNull(jda.getTextChannelById(message.channel))
                            .sendMessageEmbeds(
                                    eb.setTitle(TextUtils.replaceAllSenderBungee(message.title, message.sender))
                                            .setDescription(TextUtils.replaceAllSenderBungee(message.message, message.sender))
                                            .setAuthor(message.sender.getName(), FaceFetcher.getFaceAvatarURL(((ProxiedPlayer) message.sender).getName()), FaceFetcher.getFaceAvatarURL(((ProxiedPlayer) message.sender).getName()))
                                            .build()
                            ).queue();
                } else {
                    Objects.requireNonNull(jda.getTextChannelById(message.channel))
                            .sendMessageEmbeds(
                                    eb.setTitle(TextUtils.replaceAllSenderBungee(message.title, message.sender))
                                            .setDescription(TextUtils.replaceAllSenderBungee(message.message, message.sender))
                                            .setAuthor("CONSOLE", jda.getSelfUser().getAvatarUrl() , jda.getSelfUser().getAvatarUrl())
                                            .build()
                            ).queue();
                }
            } else {
                Objects.requireNonNull(jda.getTextChannelById(message.channel))
                        .sendMessageEmbeds(
                                eb.setTitle(TextUtils.replaceAllSenderBungee(message.title, message.sender))
                                        .setDescription(TextUtils.replaceAllSenderBungee(message.message, message.sender))
                                        .build()
                        ).queue();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void sendDiscordNonEBMessage(DiscordMessage message) {
        sendDiscordNonEBMessage(StreamLine.getJda(), message);
    }

    public static void sendDiscordNonEBMessage(JDA jda, DiscordMessage message) {
        if (! ConfigUtils.moduleDEnabled()) return;

        try {
            Objects.requireNonNull(jda.getTextChannelById(message.channel))
                    .sendMessage(TextUtils.replaceAllPlayerDiscord(TextUtils.replaceAllSenderDiscord(ConfigUtils.moduleDDisDataNonEmbeddedMessage(), message.sender), message.sender)
                            .replace("%message%", message.message)
                    ).complete();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void sendDiscordEBMessage(DiscordMessage message, boolean useAvatar){
        if (! ConfigUtils.moduleDEnabled()) {
            return;
        }

        JDA jda = StreamLine.getJda();
        EmbedBuilder eb = new EmbedBuilder();

        try {
            if (useAvatar) {
                if (message.sender instanceof ProxiedPlayer) {
                    Objects.requireNonNull(jda.getTextChannelById(message.channel))
                            .sendMessageEmbeds(
                                    eb.setTitle(TextUtils.replaceAllSenderBungee(message.title, message.sender))
                                            .setDescription(TextUtils.replaceAllSenderBungee(message.message, message.sender))
                                            .setAuthor(message.sender.getName(), FaceFetcher.getFaceAvatarURL(((ProxiedPlayer) message.sender).getName()), FaceFetcher.getFaceAvatarURL(((ProxiedPlayer) message.sender).getName()))
                                            .build()
                            ).queue();
                } else {
                    Objects.requireNonNull(jda.getTextChannelById(message.channel))
                            .sendMessageEmbeds(
                                    eb.setTitle(TextUtils.replaceAllSenderBungee(message.title, message.sender))
                                            .setDescription(TextUtils.replaceAllSenderBungee(message.message, message.sender))
                                            .setAuthor("CONSOLE", jda.getSelfUser().getAvatarUrl() , jda.getSelfUser().getAvatarUrl())
                                            .build()
                            ).queue();
                }
            } else {
                Objects.requireNonNull(jda.getTextChannelById(message.channel))
                        .sendMessageEmbeds(
                                eb.setTitle(TextUtils.replaceAllSenderBungee(message.title, message.sender))
                                        .setDescription(TextUtils.replaceAllSenderBungee(message.message, message.sender))
                                        .build()
                        ).queue();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void sendDiscordEBMessage(JDA jda, DiscordMessage message){
        if (! ConfigUtils.moduleDEnabled()) return;

        EmbedBuilder eb = new EmbedBuilder();

        try {
            if (ConfigUtils.moduleAvatarUse()) {
                if (message.sender instanceof ProxiedPlayer) {
                    Objects.requireNonNull(jda.getTextChannelById(message.channel))
                            .sendMessageEmbeds(
                                    eb.setTitle(TextUtils.replaceAllSenderBungee(message.title, message.sender))
                                            .setDescription(TextUtils.replaceAllSenderBungee(message.message, message.sender))
                                            .setAuthor(message.sender.getName(), FaceFetcher.getFaceAvatarURL(((ProxiedPlayer) message.sender).getName()), FaceFetcher.getFaceAvatarURL(((ProxiedPlayer) message.sender).getName()))
                                            .build()
                            ).queue();
                } else {
                    Objects.requireNonNull(jda.getTextChannelById(message.channel))
                            .sendMessageEmbeds(
                                    eb.setTitle(TextUtils.replaceAllSenderBungee(message.title, message.sender))
                                            .setDescription(TextUtils.replaceAllSenderBungee(message.message, message.sender))
                                            .setAuthor("CONSOLE", jda.getSelfUser().getAvatarUrl() , jda.getSelfUser().getAvatarUrl())
                                            .build()
                            ).queue();
                }
            } else {
                Objects.requireNonNull(jda.getTextChannelById(message.channel))
                        .sendMessageEmbeds(
                                eb.setTitle(TextUtils.replaceAllSenderBungee(message.title, message.sender))
                                        .setDescription(TextUtils.replaceAllSenderBungee(message.message, message.sender))
                                        .build()
                        ).queue();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void sendDiscordReportMessage(String sender, boolean fromBungee, String report){
        if (! ConfigUtils.moduleDEnabled()) {
            return;
        }

        JDA jda = StreamLine.getJda();
        EmbedBuilder eb = new EmbedBuilder();

        try {
            String replace = MessageConfUtils.dToDReportMessage()
                    .replace("%reporter%", sender)
                    .replace("%report%", report);

            String replace1 = MessageConfUtils.bToDReportMessage()
                    .replace("%reporter%", sender)
                    .replace("%report%", report);

            if (ConfigUtils.moduleAvatarUse()) {
                if (fromBungee)
                    Objects.requireNonNull(jda.getTextChannelById(DiscordBotConfUtils.textChannelReports())).sendMessageEmbeds(
                            eb.setTitle(MessageConfUtils.reportEmbedTitle())
                                    .setDescription(TextUtils.newLined(
                                            replace1
                                            )
                                    ).setAuthor(sender, FaceFetcher.getFaceAvatarURL(sender), FaceFetcher.getFaceAvatarURL(sender)).build()
                    ).queue();
                else
                    Objects.requireNonNull(jda.getTextChannelById(DiscordBotConfUtils.textChannelReports())).sendMessageEmbeds(
                            eb.setTitle(MessageConfUtils.reportEmbedTitle())
                                    .setDescription(TextUtils.newLined(
                                            replace
                                            )
                                    ).setAuthor(sender, FaceFetcher.getFaceAvatarURL(sender), FaceFetcher.getFaceAvatarURL(sender)).build()
                    ).queue();
            } else {
                if (fromBungee)
                    Objects.requireNonNull(jda.getTextChannelById(DiscordBotConfUtils.textChannelReports())).sendMessageEmbeds(
                            eb.setTitle(MessageConfUtils.reportEmbedTitle())
                                    .setDescription(TextUtils.newLined(
                                            replace1
                                            )
                                    ).build()
                    ).queue();
                else
                    Objects.requireNonNull(jda.getTextChannelById(DiscordBotConfUtils.textChannelReports())).sendMessageEmbeds(
                            eb.setTitle(MessageConfUtils.reportEmbedTitle())
                                    .setDescription(TextUtils.newLined(
                                            replace
                                            )
                                    ).build()
                    ).queue();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void sendDSelfMessage(MessageReceivedEvent context, String title, String description) {
        if (! ConfigUtils.moduleDEnabled()) return;

        EmbedBuilder eb = new EmbedBuilder();

        context.getChannel().sendMessageEmbeds(
                eb.setTitle(title)
                .setDescription(TextUtils.newLined(description))
                        .build()
        ).queue();
    }

    public static void sendBPUserMessage(SavableParty party, CommandSender sender, CommandSender to, String msg){
        to.sendMessage(TextUtils.codedText(TextUtils.replaceAllPlayerBungee(TextUtils.replaceAllSenderBungee(msg, sender), party.leaderUUID)
                .replace("%size%", Integer.toString(party.getSize()))
                .replace("%max%", Integer.toString(party.maxSize))
                .replace("%maxmax%", party.leaderUUID == null ? MessageConfUtils.nullB() : Integer.toString(party.getMaxSize(PlayerUtils.getOrGetSavableUser(party.leaderUUID))))
                .replace("%mods_count%", Integer.toString(party.moderators.size()))
                .replace("%members_count%", Integer.toString(party.members.size()))
                .replace("%total_count%", Integer.toString(party.totalMembers.size()))
                .replace("%invites_count%", Integer.toString(party.invites.size()))
                .replace("%mods%", mods(party))
                .replace("%members%", members(party))
                .replace("%totalmembers%", membersT(party))
                .replace("%invites%", invites(party))
                .replace("%ispublic%", getIsPublic(party))
                .replace("%ismuted%", getIsMuted(party))
                .replace("%version%", PlayerUtils.getOrCreateSavableUser(sender).latestVersion)
                .replace("%leader_display%", party.leaderUUID == null ? MessageConfUtils.nullB() : PlayerUtils.getOffOnDisplayBungee(PlayerUtils.getOrGetSavableUser(party.leaderUUID)))
                .replace("%leader_normal%", party.leaderUUID == null ? MessageConfUtils.nullB() : PlayerUtils.getOffOnRegBungee(PlayerUtils.getOrGetSavableUser(party.leaderUUID)))
                .replace("%leader_absolute%", party.leaderUUID == null ? MessageConfUtils.nullB() : PlayerUtils.getAbsoluteBungee(PlayerUtils.getOrGetSavableUser(party.leaderUUID)))
                .replace("%leader_formatted%", party.leaderUUID == null ? MessageConfUtils.nullB() : PlayerUtils.getJustDisplayBungee(PlayerUtils.getOrGetSavableUser(party.leaderUUID)))
                .replace("%size%", Integer.toString(party.getSize()))
        ));
    }

    public static void sendBPUserMessageFromDiscord(SavableParty party, String nameUsed, CommandSender to, String msg){
        to.sendMessage(TextUtils.codedText(TextUtils.replaceAllPlayerBungee(msg, party.leaderUUID)
                .replace("%size%", Integer.toString(party.getSize()))
                .replace("%max%", Integer.toString(party.maxSize))
                .replace("%maxmax%", party.leaderUUID == null ? MessageConfUtils.nullB() : Integer.toString(party.getMaxSize(PlayerUtils.getOrGetSavableUser(party.leaderUUID))))
                .replace("%mods_count%", Integer.toString(party.moderators.size()))
                .replace("%members_count%", Integer.toString(party.members.size()))
                .replace("%total_count%", Integer.toString(party.totalMembers.size()))
                .replace("%invites_count%", Integer.toString(party.invites.size()))
                .replace("%mods%", mods(party))
                .replace("%members%", members(party))
                .replace("%totalmembers%", membersT(party))
                .replace("%invites%", invites(party))
                .replace("%ispublic%", getIsPublic(party))
                .replace("%ismuted%", getIsMuted(party))
                .replace("%sender_display%", nameUsed)
                .replace("%sender_normal%", nameUsed)
                .replace("%sender_absolute%", nameUsed)
                .replace("%sender_formatted%", nameUsed)
                .replace("%leader_display%", party.leaderUUID == null ? MessageConfUtils.nullB() : PlayerUtils.getOffOnDisplayBungee(PlayerUtils.getOrGetSavableUser(party.leaderUUID)))
                .replace("%leader_normal%", party.leaderUUID == null ? MessageConfUtils.nullB() : PlayerUtils.getOffOnRegBungee(PlayerUtils.getOrGetSavableUser(party.leaderUUID)))
                .replace("%leader_absolute%", party.leaderUUID == null ? MessageConfUtils.nullB() : PlayerUtils.getAbsoluteBungee(PlayerUtils.getOrGetSavableUser(party.leaderUUID)))
                .replace("%leader_formatted%", party.leaderUUID == null ? MessageConfUtils.nullB() : PlayerUtils.getJustDisplayBungee(PlayerUtils.getOrGetSavableUser(party.leaderUUID)))
                .replace("%size%", Integer.toString(party.getSize()))
        ));
    }

    public static void sendBPUserMessageFromDiscord(SavableParty party, SavableUser sender, CommandSender to, String msg){
        to.sendMessage(TextUtils.codedText(TextUtils.replaceAllPlayerBungee(TextUtils.replaceAllSenderBungee(msg, sender), party.leaderUUID)
                .replace("%size%", Integer.toString(party.getSize()))
                .replace("%max%", Integer.toString(party.maxSize))
                .replace("%maxmax%", party.leaderUUID == null ? MessageConfUtils.nullB() : Integer.toString(party.getMaxSize(PlayerUtils.getOrGetSavableUser(party.leaderUUID))))
                .replace("%mods_count%", Integer.toString(party.moderators.size()))
                .replace("%members_count%", Integer.toString(party.members.size()))
                .replace("%total_count%", Integer.toString(party.totalMembers.size()))
                .replace("%invites_count%", Integer.toString(party.invites.size()))
                .replace("%mods%", mods(party))
                .replace("%members%", members(party))
                .replace("%totalmembers%", membersT(party))
                .replace("%invites%", invites(party))
                .replace("%ispublic%", getIsPublic(party))
                .replace("%ismuted%", getIsMuted(party))
                .replace("%version%", sender.latestVersion)
                .replace("%leader_display%", party.leaderUUID == null ? MessageConfUtils.nullB() : PlayerUtils.getOffOnDisplayBungee(PlayerUtils.getOrGetSavableUser(party.leaderUUID)))
                .replace("%leader_normal%", party.leaderUUID == null ? MessageConfUtils.nullB() : PlayerUtils.getOffOnRegBungee(PlayerUtils.getOrGetSavableUser(party.leaderUUID)))
                .replace("%leader_absolute%", party.leaderUUID == null ? MessageConfUtils.nullB() : PlayerUtils.getAbsoluteBungee(PlayerUtils.getOrGetSavableUser(party.leaderUUID)))
                .replace("%leader_formatted%", party.leaderUUID == null ? MessageConfUtils.nullB() : PlayerUtils.getJustDisplayBungee(PlayerUtils.getOrGetSavableUser(party.leaderUUID)))
                .replace("%size%", Integer.toString(party.getSize()))
        ));
    }

    public static void sendDiscordPEBMessage(SavableParty party, DiscordMessage message){
        if (! ConfigUtils.moduleDEnabled()) {
            return;
        }

        JDA jda = StreamLine.getJda();
        EmbedBuilder eb = new EmbedBuilder();

        String msg = TextUtils.replaceAllPlayerDiscord(TextUtils.replaceAllSenderDiscord(message.message, message.sender), party.leaderUUID)
                .replace("%size%", Integer.toString(party.getSize()))
                .replace("%max%", Integer.toString(party.maxSize))
                .replace("%maxmax%", Integer.toString(party.getMaxSize(PlayerUtils.getOrGetSavableUser(party.leaderUUID))))
                .replace("%mods_count%", Integer.toString(party.moderators.size()))
                .replace("%members_count%", Integer.toString(party.members.size()))
                .replace("%total_count%", Integer.toString(party.totalMembers.size()))
                .replace("%invites_count%", Integer.toString(party.invites.size()))
                .replace("%mods%", mods(party))
                .replace("%members%", members(party))
                .replace("%totalmembers%", membersT(party))
                .replace("%invites%", invites(party))
                .replace("%ispublic%", getIsPublic(party))
                .replace("%ismuted%", getIsMuted(party))
                .replace("%version%", PlayerUtils.getOrCreateSavableUser(message.sender).latestVersion)
                .replace("%version%", PlayerUtils.getOrCreatePlayerStat((ProxiedPlayer) message.sender).latestVersion)
                .replace("%leader_display%", PlayerUtils.getOffOnDisplayDiscord(PlayerUtils.getOrGetSavableUser(party.leaderUUID)))
                .replace("%leader_normal%", PlayerUtils.getOffOnRegDiscord(PlayerUtils.getOrGetSavableUser(party.leaderUUID)))
                .replace("%leader_absolute%", PlayerUtils.getAbsoluteDiscord(PlayerUtils.getOrGetSavableUser(party.leaderUUID)))
                .replace("%leader_formatted%", PlayerUtils.getJustDisplayDiscord(PlayerUtils.getOrGetSavableUser(party.leaderUUID)))
                .replace("%size%", Integer.toString(party.getSize()));

        try {
            if (ConfigUtils.moduleAvatarUse()) {
                if (message.sender instanceof ProxiedPlayer) {
                    Objects.requireNonNull(jda.getTextChannelById(message.channel))
                            .sendMessageEmbeds(
                                    eb.setTitle(TextUtils.replaceAllSenderBungee(message.title, message.sender))
                                            .setDescription(TextUtils.replaceAllSenderBungee(msg, message.sender))
                                            .setAuthor(message.sender.getName(), FaceFetcher.getFaceAvatarURL(((ProxiedPlayer) message.sender).getName()), FaceFetcher.getFaceAvatarURL(((ProxiedPlayer) message.sender).getName()))
                                            .build()
                            ).queue();
                } else {
                    Objects.requireNonNull(jda.getTextChannelById(message.channel))
                            .sendMessageEmbeds(
                                    eb.setTitle(TextUtils.replaceAllSenderBungee(message.title, message.sender))
                                            .setDescription(TextUtils.replaceAllSenderBungee(msg, message.sender))
                                            .setAuthor("CONSOLE", jda.getSelfUser().getAvatarUrl() , jda.getSelfUser().getAvatarUrl())
                                            .build()
                            ).queue();
                }
            } else {
                Objects.requireNonNull(jda.getTextChannelById(message.channel))
                        .sendMessageEmbeds(
                                eb.setTitle(TextUtils.replaceAllSenderBungee(message.title, message.sender))
                                        .setDescription(TextUtils.replaceAllSenderBungee(msg, message.sender))
                                        .build()
                        ).queue();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void sendBGUserMessage(SavableGuild guild, CommandSender sender, CommandSender to, String msg){
        to.sendMessage(TextUtils.codedText(TextUtils.replaceAllPlayerBungee(TextUtils.replaceAllSenderBungee(msg, sender), guild.leaderUUID)
                .replace("%size%", Integer.toString(guild.getSize()))
                .replace("%max%", Integer.toString(guild.maxSize))
                .replace("%mods_count%", Integer.toString(guild.modsByUUID.size()))
                .replace("%members_count%", Integer.toString(guild.membersByUUID.size()))
                .replace("%total_count%", Integer.toString(guild.totalMembersByUUID.size()))
                .replace("%invites_count%", Integer.toString(guild.invites.size()))
                .replace("%mods%", modsGuild(guild))
                .replace("%members%", membersGuild(guild))
                .replace("%totalmembers%", membersTGuild(guild))
                .replace("%invites%", invitesGuild(guild))
                .replace("%ispublic%", getIsPublicGuild(guild))
                .replace("%ismuted%", getIsMutedGuild(guild))
                .replace("%total_xp%", Integer.toString(guild.totalXP))
                .replace("%current_xp%", Integer.toString(guild.currentXP))
                .replace("%level%", Integer.toString(guild.lvl))
                .replace("%name%", guild.name)
                .replace("%xpneeded%", Integer.toString(guild.getNeededXp(guild.lvl + 1)))
                .replace("%xplevel%", Integer.toString(guild.xpUntilNextLevel()))
                .replace("%version%", PlayerUtils.getOrCreateSavableUser(sender).latestVersion)
                .replace("%name%", guild.name)
                .replace("%length%", String.valueOf(guild.name.length()))
                .replace("%max_length%", String.valueOf(ConfigUtils.guildMaxLength()))
                .replace("%codes%", (ConfigUtils.guildIncludeColors() ? GuildUtils.withCodes : GuildUtils.withoutCodes))
                .replace("%leader_display%", guild.leaderUUID == null ? MessageConfUtils.nullB() : PlayerUtils.getOffOnDisplayBungee(PlayerUtils.getOrGetSavableUser(guild.leaderUUID)))
                .replace("%leader_normal%", guild.leaderUUID == null ? MessageConfUtils.nullB() : PlayerUtils.getOffOnRegBungee(PlayerUtils.getOrGetSavableUser(guild.leaderUUID)))
                .replace("%leader_absolute%", guild.leaderUUID == null ? MessageConfUtils.nullB() : PlayerUtils.getAbsoluteBungee(PlayerUtils.getOrGetSavableUser(guild.leaderUUID)))
                .replace("%leader_formatted%", guild.leaderUUID == null ? MessageConfUtils.nullB() : PlayerUtils.getJustDisplayBungee(PlayerUtils.getOrGetSavableUser(guild.leaderUUID)))
        ));
    }

    public static void sendBGUserMessageFromDiscord(SavableGuild guild, String nameUsed, CommandSender to, String msg){
        to.sendMessage(TextUtils.codedText(TextUtils.replaceAllPlayerBungee(msg, guild.leaderUUID)
                .replace("%size%", Integer.toString(guild.getSize()))
                .replace("%max%", Integer.toString(guild.maxSize))
                .replace("%mods_count%", Integer.toString(guild.modsByUUID.size()))
                .replace("%members_count%", Integer.toString(guild.membersByUUID.size()))
                .replace("%total_count%", Integer.toString(guild.totalMembersByUUID.size()))
                .replace("%invites_count%", Integer.toString(guild.invites.size()))
                .replace("%mods%", modsGuild(guild))
                .replace("%members%", membersGuild(guild))
                .replace("%totalmembers%", membersTGuild(guild))
                .replace("%invites%", invitesGuild(guild))
                .replace("%ispublic%", getIsPublicGuild(guild))
                .replace("%ismuted%", getIsMutedGuild(guild))
                .replace("%total_xp%", Integer.toString(guild.totalXP))
                .replace("%current_xp%", Integer.toString(guild.currentXP))
                .replace("%level%", Integer.toString(guild.lvl))
                .replace("%name%", guild.name)
                .replace("%xpneeded%", Integer.toString(guild.getNeededXp(guild.lvl + 1)))
                .replace("%xplevel%", Integer.toString(guild.xpUntilNextLevel()))
                .replace("%name%", guild.name)
                .replace("%length%", String.valueOf(guild.name.length()))
                .replace("%max_length%", String.valueOf(ConfigUtils.guildMaxLength()))
                .replace("%codes%", (ConfigUtils.guildIncludeColors() ? GuildUtils.withCodes : GuildUtils.withoutCodes))
                .replace("%sender_display%", nameUsed)
                .replace("%sender_normal%", nameUsed)
                .replace("%sender_absolute%", nameUsed)
                .replace("%sender_formatted%", nameUsed)
                .replace("%leader_display%", guild.leaderUUID == null ? MessageConfUtils.nullB() : PlayerUtils.getOffOnDisplayBungee(PlayerUtils.getOrGetSavableUser(guild.leaderUUID)))
                .replace("%leader_normal%", guild.leaderUUID == null ? MessageConfUtils.nullB() : PlayerUtils.getOffOnRegBungee(PlayerUtils.getOrGetSavableUser(guild.leaderUUID)))
                .replace("%leader_absolute%", guild.leaderUUID == null ? MessageConfUtils.nullB() : PlayerUtils.getAbsoluteBungee(PlayerUtils.getOrGetSavableUser(guild.leaderUUID)))
                .replace("%leader_formatted%", guild.leaderUUID == null ? MessageConfUtils.nullB() : PlayerUtils.getJustDisplayBungee(PlayerUtils.getOrGetSavableUser(guild.leaderUUID)))
        ));
    }

    public static void sendBGUserMessageFromDiscord(SavableGuild guild, SavableUser sender, CommandSender to, String msg){
        to.sendMessage(TextUtils.codedText(TextUtils.replaceAllPlayerBungee(TextUtils.replaceAllSenderBungee(msg, sender), guild.leaderUUID)
                .replace("%size%", Integer.toString(guild.getSize()))
                .replace("%max%", Integer.toString(guild.maxSize))
                .replace("%mods_count%", Integer.toString(guild.modsByUUID.size()))
                .replace("%members_count%", Integer.toString(guild.membersByUUID.size()))
                .replace("%total_count%", Integer.toString(guild.totalMembersByUUID.size()))
                .replace("%invites_count%", Integer.toString(guild.invites.size()))
                .replace("%mods%", modsGuild(guild))
                .replace("%members%", membersGuild(guild))
                .replace("%totalmembers%", membersTGuild(guild))
                .replace("%invites%", invitesGuild(guild))
                .replace("%ispublic%", getIsPublicGuild(guild))
                .replace("%ismuted%", getIsMutedGuild(guild))
                .replace("%total_xp%", Integer.toString(guild.totalXP))
                .replace("%current_xp%", Integer.toString(guild.currentXP))
                .replace("%level%", Integer.toString(guild.lvl))
                .replace("%name%", guild.name)
                .replace("%xpneeded%", Integer.toString(guild.getNeededXp(guild.lvl + 1)))
                .replace("%xplevel%", Integer.toString(guild.xpUntilNextLevel()))
                .replace("%version%", sender.latestVersion)
                .replace("%name%", guild.name)
                .replace("%length%", String.valueOf(guild.name.length()))
                .replace("%max_length%", String.valueOf(ConfigUtils.guildMaxLength()))
                .replace("%codes%", (ConfigUtils.guildIncludeColors() ? GuildUtils.withCodes : GuildUtils.withoutCodes))
                .replace("%leader_display%", guild.leaderUUID == null ? MessageConfUtils.nullB() : PlayerUtils.getOffOnDisplayBungee(PlayerUtils.getOrGetSavableUser(guild.leaderUUID)))
                .replace("%leader_normal%", guild.leaderUUID == null ? MessageConfUtils.nullB() : PlayerUtils.getOffOnRegBungee(PlayerUtils.getOrGetSavableUser(guild.leaderUUID)))
                .replace("%leader_absolute%", guild.leaderUUID == null ? MessageConfUtils.nullB() : PlayerUtils.getAbsoluteBungee(PlayerUtils.getOrGetSavableUser(guild.leaderUUID)))
                .replace("%leader_formatted%", guild.leaderUUID == null ? MessageConfUtils.nullB() : PlayerUtils.getJustDisplayBungee(PlayerUtils.getOrGetSavableUser(guild.leaderUUID)))
        ));
    }

    public static void sendDiscordGEBMessage(SavableGuild guild, DiscordMessage message){
        if (! ConfigUtils.moduleDEnabled()) {
            return;
        }

        JDA jda = StreamLine.getJda();
        EmbedBuilder eb = new EmbedBuilder();

        String msg = TextUtils.replaceAllPlayerDiscord(TextUtils.replaceAllSenderDiscord(message.message, message.sender), guild.leaderUUID)
                .replace("%size%", Integer.toString(guild.getSize()))
                .replace("%max%", Integer.toString(guild.maxSize))
                .replace("%mods_count%", Integer.toString(guild.modsByUUID.size()))
                .replace("%members_count%", Integer.toString(guild.membersByUUID.size()))
                .replace("%total_count%", Integer.toString(guild.totalMembersByUUID.size()))
                .replace("%invites_count%", Integer.toString(guild.invites.size()))
                .replace("%mods%", modsGuild(guild))
                .replace("%members%", membersGuild(guild))
                .replace("%totalmembers%", membersTGuild(guild))
                .replace("%invites%", invitesGuild(guild))
                .replace("%ispublic%", getIsPublicGuild(guild))
                .replace("%ismuted%", getIsMutedGuild(guild))
                .replace("%total_xp%", Integer.toString(guild.totalXP))
                .replace("%current_xp%", Integer.toString(guild.currentXP))
                .replace("%level%", Integer.toString(guild.lvl))
                .replace("%name%", guild.name)
                .replace("%xpneeded%", Integer.toString(guild.getNeededXp(guild.lvl + 1)))
                .replace("%xplevel%", Integer.toString(guild.xpUntilNextLevel()))
                .replace("%version%", PlayerUtils.getOrCreateSavableUser(message.sender).latestVersion)
                .replace("%name%", guild.name)
                .replace("%length%", String.valueOf(guild.name.length()))
                .replace("%max_length%", String.valueOf(ConfigUtils.guildMaxLength()))
                .replace("%codes%", (ConfigUtils.guildIncludeColors() ? GuildUtils.withCodes : GuildUtils.withoutCodes))
                .replace("%leader_display%", guild.leaderUUID == null ? MessageConfUtils.nullB() : PlayerUtils.getOffOnDisplayDiscord(PlayerUtils.getOrGetSavableUser(guild.leaderUUID)))
                .replace("%leader_normal%", guild.leaderUUID == null ? MessageConfUtils.nullB() : PlayerUtils.getOffOnRegDiscord(PlayerUtils.getOrGetSavableUser(guild.leaderUUID)))
                .replace("%leader_absolute%", guild.leaderUUID == null ? MessageConfUtils.nullB() : PlayerUtils.getAbsoluteDiscord(PlayerUtils.getOrGetSavableUser(guild.leaderUUID)))
                .replace("%leader_formatted%", guild.leaderUUID == null ? MessageConfUtils.nullB() : PlayerUtils.getJustDisplayDiscord(PlayerUtils.getOrGetSavableUser(guild.leaderUUID)));

        try {
            if (ConfigUtils.moduleAvatarUse()) {
                if (message.sender instanceof ProxiedPlayer) {
                    Objects.requireNonNull(jda.getTextChannelById(message.channel))
                            .sendMessageEmbeds(
                                    eb.setTitle(TextUtils.replaceAllSenderBungee(message.title, message.sender))
                                            .setDescription(TextUtils.replaceAllSenderBungee(msg, message.sender))
                                            .setAuthor(message.sender.getName(), FaceFetcher.getFaceAvatarURL(((ProxiedPlayer) message.sender).getName()), FaceFetcher.getFaceAvatarURL(((ProxiedPlayer) message.sender).getName()))
                                            .build()
                            ).queue();
                } else {
                    Objects.requireNonNull(jda.getTextChannelById(message.channel))
                            .sendMessageEmbeds(
                                    eb.setTitle(TextUtils.replaceAllSenderBungee(message.title, message.sender))
                                            .setDescription(TextUtils.replaceAllSenderBungee(msg, message.sender))
                                            .setAuthor("CONSOLE", jda.getSelfUser().getAvatarUrl() , jda.getSelfUser().getAvatarUrl())
                                            .build()
                            ).queue();
                }
            } else {
                Objects.requireNonNull(jda.getTextChannelById(message.channel))
                        .sendMessageEmbeds(
                                eb.setTitle(TextUtils.replaceAllSenderBungee(message.title, message.sender))
                                        .setDescription(TextUtils.replaceAllSenderBungee(msg, message.sender))
                                        .build()
                        ).queue();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void sendStatUserMessage(SavableUser user, CommandSender sender, String msg){
        SavableGuild guild = GuildUtils.getGuild(user);

        if (user instanceof SavableConsole) {
            SavableConsole player = PlayerUtils.getConsoleStat();

            if (player == null) {
                sendBUserMessage(sender, MessageConfUtils.noPlayer());
                return;
            }

            sender.sendMessage(TextUtils.codedText(TextUtils.replaceAllSenderBungee(TextUtils.replaceAllPlayerBungee(msg, user), sender)
                    .replace("%version%", player.latestVersion)
                    .replace("%points%", Integer.toString(player.points))
                    .replace("%points_name%", PlayerUtils.pointsName)
                    .replace("%tags%", statTags(player))
                    .replace("%uuid%", player.uuid)
                    .replace("%player_display%", player.displayName)
                    .replace("%guild%", (guild != null ? guild.name : PlayerUtils.notSet))
                    .replace("%guild_members%", (guild != null ? Integer.toString(guild.totalMembers.size()) : PlayerUtils.notSet))
                    .replace("%guild_xp_total%", (guild != null ? Integer.toString(guild.totalXP) : PlayerUtils.notSet))
                    .replace("%guild_xp_current%", (guild != null ? Integer.toString(guild.currentXP) : PlayerUtils.notSet))
                    .replace("%guild_lvl%", (guild != null ? Integer.toString(guild.lvl) : PlayerUtils.notSet))
                    .replace("%guild_leader%", (guild != null ? PlayerUtils.getOrGetSavableUser(guild.leaderUUID).displayName : PlayerUtils.notSet))
                    .replace("%guild_uuid%", (guild != null ? player.guild : PlayerUtils.notSet))
                    .replace("%version%", player.latestVersion)
            ));

            return;
        }

        if (user instanceof SavablePlayer) {
            SavablePlayer player = PlayerUtils.getOrGetPlayerStatByUUID(user.uuid);

            if (player == null) {
                sendBUserMessage(sender, MessageConfUtils.noPlayer());
                return;
            }

            sender.sendMessage(TextUtils.codedText(TextUtils.replaceAllSenderBungee(TextUtils.replaceAllPlayerBungee(msg, user), sender)
                    .replace("%total_xp%", Integer.toString(player.totalXP))
                    .replace("%xp%", Integer.toString(player.getCurrentXP()))
                    .replace("%level%", Integer.toString(player.lvl))
                    .replace("%xpneeded%", Integer.toString(player.getNeededXp(player.lvl + 1)))
                    .replace("%xplevel%", Integer.toString(player.xpUntilNextLevel()))
                    .replace("%playtime%", TextUtils.truncate(Double.toString(player.getPlayHours()), 3))
                    .replace("%version%", player.latestVersion)
                    .replace("%points%", Integer.toString(player.points))
                    .replace("%points_name%", PlayerUtils.pointsName)
                    .replace("%uuid%", player.uuid)
                    .replace("%tags%", statTags(player))
                    .replace("%ip%", player.latestIP)
                    .replace("%ips%", statIPs(player))
                    .replace("%player_display%", player.displayName)
                    .replace("%names%", statNames(player))
                    .replace("%guild%", (guild != null ? guild.name : PlayerUtils.notSet))
                    .replace("%guild_members%", (guild != null ? Integer.toString(guild.totalMembers.size()) : PlayerUtils.notSet))
                    .replace("%guild_xp_total%", (guild != null ? Integer.toString(guild.totalXP) : PlayerUtils.notSet))
                    .replace("%guild_xp_current%", (guild != null ? Integer.toString(guild.currentXP) : PlayerUtils.notSet))
                    .replace("%guild_lvl%", (guild != null ? Integer.toString(guild.lvl) : PlayerUtils.notSet))
                    .replace("%guild_leader%", (guild != null ? PlayerUtils.getOrGetSavableUser(guild.leaderUUID).displayName : PlayerUtils.notSet))
                    .replace("%guild_uuid%", (guild != null ? player.guild : PlayerUtils.notSet))
                    .replace("%sspy%", (player.sspy ? PlayerUtils.sspyT : PlayerUtils.sspyF))
                    .replace("%gspy%", (player.gspy ? PlayerUtils.gspyT : PlayerUtils.gspyF))
                    .replace("%pspy%", (player.pspy ? PlayerUtils.pspyT : PlayerUtils.pspyF))
                    .replace("%online%", (player.online ? PlayerUtils.onlineT : PlayerUtils.onlineF))
            ));
        }
    }

    public static String statTags(SavableUser player){
        StringBuilder stringBuilder = new StringBuilder();

        int i = 1;
        for (String tag : player.tagList) {
            if (i != player.tagList.size()) {
                stringBuilder.append(PlayerUtils.tagsNLast.replace("%value%", tag));
            } else {
                stringBuilder.append(PlayerUtils.tagsLast.replace("%value%", tag));
            }
        }

        return stringBuilder.toString();
    }

    public static String statIPs(SavablePlayer player){
        StringBuilder stringBuilder = new StringBuilder();

        int i = 1;
        for (String ip : player.ipList) {
            if (i != player.ipList.size()) {
                stringBuilder.append(PlayerUtils.ipsNLast.replace("%value%", ip));
            } else {
                stringBuilder.append(PlayerUtils.ipsLast.replace("%value%", ip));
            }
        }

        return stringBuilder.toString();
    }

    public static String statNames(SavablePlayer player){
        StringBuilder stringBuilder = new StringBuilder();

        int i = 1;
        for (String name : player.nameList) {
            if (i != player.nameList.size()) {
                stringBuilder.append(PlayerUtils.namesNLast.replace("%value%", name));
            } else {
                stringBuilder.append(PlayerUtils.namesLast.replace("%value%", name));
            }
        }

        return stringBuilder.toString();
    }

    public static void sendEventUserMessage(SavableUser from, SavableUser to, String msg) {
        to.sendMessage(TextUtils.codedText(msg
                .replace("%from_display%", PlayerUtils.getOffOnDisplayBungee(from))
                .replace("%from_normal%", PlayerUtils.getOffOnRegBungee(from))
                .replace("%from_absolute%", PlayerUtils.getAbsoluteBungee(from))
                .replace("%from_formatted%", PlayerUtils.getJustDisplayBungee(from))
                .replace("%from_server%", from.findServer())
                .replace("%to_display%", PlayerUtils.getOffOnDisplayBungee(to))
                .replace("%to_normal%", PlayerUtils.getOffOnRegBungee(to))
                .replace("%to_absolute%", PlayerUtils.getAbsoluteBungee(to))
                .replace("%to_formatted%", PlayerUtils.getJustDisplayBungee(to))
                .replace("%to_server%", to.findServer())
        ));
    }

    public static void sendBUserMessage(CommandSender sender, String msg){
        if (sender instanceof ProxiedPlayer) {
            sender.sendMessage(TextUtils.codedText(TextUtils.replaceAllSenderBungee(msg, sender)
                    .replace("%version%", PlayerUtils.getOrCreatePlayerStat((ProxiedPlayer) sender).latestVersion)
            ));
        } else {
            sender.sendMessage(TextUtils.codedText(TextUtils.replaceAllSenderBungee(msg, sender)));
        }
    }

    public static void sendBUserMessage(SavableUser sender, String msg){
        if (sender instanceof SavablePlayer) {
            sender.sendMessage(TextUtils.codedText(TextUtils.replaceAllSenderBungee(msg, sender)
                    .replace("%version%", Objects.requireNonNull(sender).latestVersion)
            ));
        }
    }

    public static void sendBMessagenging(CommandSender sendTo, SavableUser from, SavableUser to, String playerMessage, String msg) {
        sendTo.sendMessage(TextUtils.codedText(msg
                .replace("%from_formatted%", PlayerUtils.getJustDisplayBungee(from))
                .replace("%from_display%", PlayerUtils.getOffOnDisplayBungee(from))
                .replace("%from_normal%", PlayerUtils.getOffOnRegBungee(from))
                .replace("%from_absolute%", PlayerUtils.getAbsoluteBungee(from))
                .replace("%from_server%", from.findServer())
                .replace("%to_formatted%", PlayerUtils.getJustDisplayBungee(to))
                .replace("%to_display%", PlayerUtils.getOffOnDisplayBungee(to))
                .replace("%to_normal%", PlayerUtils.getOffOnRegBungee(to))
                .replace("%to_absolute%", PlayerUtils.getAbsoluteBungee(to))
                .replace("%to_server%", to.findServer())
                .replace("%message%", playerMessage)
        ));
    }

    public static void sendBUserAsMessage(CommandSender as, String msg){
        ServerInfo serverInfo = StreamLine.getInstance().getProxy().getPlayer(as.getName()).getServer().getInfo();

        Collection<ProxiedPlayer> players = serverInfo.getPlayers();

        if (as instanceof ProxiedPlayer) {
            for (ProxiedPlayer player : players) {
                player.sendMessage(TextUtils.codedText(TextUtils.replaceAllSenderBungee(msg, as)
                        .replace("%version%", PlayerUtils.getOrCreatePlayerStat((ProxiedPlayer) as).latestVersion)
                ));
            }
        } else {
            for (ProxiedPlayer player : players) {
                player.sendMessage(TextUtils.codedText(TextUtils.replaceAllSenderBungee(msg, as)));
            }
        }
    }

    public static void sendBBroadcast(CommandSender sender, String msg){
        for (SavableUser user : PlayerUtils.getStatsOnline()) {
            sendBUserMessage(user.findSender(), TextUtils.replaceAllSenderBungee(msg, sender));
        }
    }

    public static void sendBCLHBroadcast(CommandSender sender, String msg, String hoverPrefix){
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            player.sendMessage(TextUtils.clhText(msg, hoverPrefix));
        }
    }

    public static boolean compareWithList(String toCompare, List<String> list) {
        for (String item : list) {
            if (toCompare.equals(item))
                return true;
        }
        return false;
    }

    public static void sendDiscordPingRoleMessage(long channelId, long roleId){
        Objects.requireNonNull(StreamLine.getJda().getTextChannelById(channelId)).sendMessage(Objects.requireNonNull(StreamLine.getJda().getRoleById(roleId)).getAsMention()).queue();
    }

    public static void logInfo(String msg){
        if (msg == null) msg = "";
        StreamLine.getInstance().getLogger().info(TextUtils.newLined(msg));
    }

    public static void logWarning(String msg){
        if (msg == null) msg = "";
        StreamLine.getInstance().getLogger().warning(TextUtils.newLined(msg));
    }

    public static void logSevere(String msg){
        if (msg == null) msg = "";
        StreamLine.getInstance().getLogger().severe(TextUtils.newLined(msg));
    }

    public static String mods(SavableParty party){
        StringBuilder msg = new StringBuilder();

        int i = 1;
        for (SavableUser m : party.moderators){
            if (i < party.moderators.size()){
                msg.append(TextUtils.replaceAllPlayerBungee(MessageConfUtils.partiesModsNLast(), m)
                        .replace("%version%", Objects.requireNonNull(m).latestVersion)
                );
            } else {
                msg.append(TextUtils.replaceAllPlayerBungee(MessageConfUtils.partiesModsLast(), m)
                        .replace("%version%", Objects.requireNonNull(m).latestVersion)
                );
            }

            i++;
        }

        return msg.toString();
    }

    public static String members(SavableParty party){
        StringBuilder msg = new StringBuilder();

        int i = 1;
        for (SavableUser m : party.members){
            if (i < party.members.size()){
                msg.append(TextUtils.replaceAllPlayerBungee(MessageConfUtils.partiesMemsNLast(), m)
                        .replace("%version%", Objects.requireNonNull(m).latestVersion)
                );
            } else {
                msg.append(TextUtils.replaceAllPlayerBungee(MessageConfUtils.partiesMemsLast(), m)
                        .replace("%version%", Objects.requireNonNull(m).latestVersion)
                );
            }

            i++;
        }

        return msg.toString();
    }

    public static String membersT(SavableParty party){
        StringBuilder msg = new StringBuilder();

        int i = 1;
        for (SavableUser m : party.totalMembers){
            if (i != party.totalMembers.size()){
                msg.append(TextUtils.replaceAllPlayerBungee(MessageConfUtils.partiesTMemsNLast(), m)
                        .replace("%version%", Objects.requireNonNull(m).latestVersion)
                );
            } else {
                msg.append(TextUtils.replaceAllPlayerBungee(MessageConfUtils.partiesTMemsLast(), m)
                        .replace("%version%", Objects.requireNonNull(m).latestVersion)
                );
            }

            i++;
        }

        return msg.toString();
    }

    public static String invites(SavableParty party){
        StringBuilder msg = new StringBuilder();

        int i = 1;
        for (SavableUser m : party.invites){

            if (i < party.invites.size()){
                msg.append(TextUtils.replaceAllPlayerBungee(MessageConfUtils.partiesInvsNLast(), m)
                        .replace("%version%", Objects.requireNonNull(m).latestVersion)
                );
            } else {
                msg.append(TextUtils.replaceAllPlayerBungee(MessageConfUtils.partiesInvsLast(), m)
                        .replace("%version%", Objects.requireNonNull(m).latestVersion)
                );
            }

            i++;
        }

        return msg.toString();
    }

    public static String getIsPublic(SavableParty party){
        return party.isPublic ? MessageConfUtils.partiesIsPublicTrue() : MessageConfUtils.partiesIsPublicFalse();
    }

    public static String getIsMuted(SavableParty party){
        return party.isMuted ? MessageConfUtils.partiesIsMutedTrue() : MessageConfUtils.partiesIsMutedFalse();
    }

    public static String modsGuild(SavableGuild guild){
        StringBuilder msg = new StringBuilder();

        int i = 1;
        for (String m : guild.modsByUUID){
            SavableUser player;
            try {
                player = PlayerUtils.getOrGetPlayerStatByUUID(m);
            } catch (Exception e) {
                continue;
            }

//            if (player == null) continue;

            if (i < guild.modsByUUID.size()){
                msg.append(TextUtils.replaceAllPlayerBungee(MessageConfUtils.guildsModsNLast(), player)
                        .replace("%version%", player == null ? MessageConfUtils.nullB() : Objects.requireNonNull(player).latestVersion)
                );
            } else {
                msg.append(TextUtils.replaceAllPlayerBungee(MessageConfUtils.guildsModsLast(), player)
                        .replace("%version%", player == null ? MessageConfUtils.nullB() : Objects.requireNonNull(player).latestVersion)
                );
            }

            i++;
        }

        return msg.toString();
    }

    public static String membersGuild(SavableGuild guild){
        StringBuilder msg = new StringBuilder();

        int i = 1;
        for (String m : guild.membersByUUID){
            SavableUser player;
            try {
                player = PlayerUtils.getOrGetPlayerStatByUUID(m);
            } catch (Exception e) {
                continue;
            }

//            if (player == null) continue;

            if (i < guild.membersByUUID.size()){
                msg.append(TextUtils.replaceAllPlayerBungee(MessageConfUtils.guildsMemsNLast(), player)
                        .replace("%version%", player == null ? MessageConfUtils.nullB() : Objects.requireNonNull(player).latestVersion)
                );
            } else {
                msg.append(TextUtils.replaceAllPlayerBungee(MessageConfUtils.guildsMemsLast(), player)
                        .replace("%version%", player == null ? MessageConfUtils.nullB() : Objects.requireNonNull(player).latestVersion)
                );
            }

            i++;
        }

        return msg.toString();
    }

    public static String membersTGuild(SavableGuild guild){
        StringBuilder msg = new StringBuilder();

        int i = 1;
        for (String m : guild.totalMembersByUUID){
            SavableUser player;
            try {
                player = PlayerUtils.getOrGetPlayerStatByUUID(m);
            } catch (Exception e) {
                continue;
            }

//            if (player == null) continue;

            if (i < guild.totalMembersByUUID.size()){
                msg.append(TextUtils.replaceAllPlayerBungee(MessageConfUtils.guildsTMemsNLast(), player)
                        .replace("%version%", player == null ? MessageConfUtils.nullB() : Objects.requireNonNull(player).latestVersion)
                );
            } else {
                msg.append(TextUtils.replaceAllPlayerBungee(MessageConfUtils.guildsTMemsLast(), player)
                        .replace("%version%", player == null ? MessageConfUtils.nullB() : Objects.requireNonNull(player).latestVersion)
                );
            }

            i++;
        }

        return msg.toString();
    }

    public static String invitesGuild(SavableGuild guild){
        StringBuilder msg = new StringBuilder();

        int i = 1;
        for (String m : guild.invitesByUUID){
            SavableUser player;
            try {
                player = PlayerUtils.getOrGetPlayerStatByUUID(m);
            } catch (Exception e) {
                continue;
            }

//            if (player == null) continue;

            if (i < guild.invites.size()){
                msg.append(TextUtils.replaceAllPlayerBungee(MessageConfUtils.guildsInvsNLast(), player)
                        .replace("%version%", player == null ? MessageConfUtils.nullB() : Objects.requireNonNull(player).latestVersion)
                );
            } else {
                msg.append(TextUtils.replaceAllPlayerBungee(MessageConfUtils.guildsInvsLast(), player)
                        .replace("%version%", player == null ? MessageConfUtils.nullB() : Objects.requireNonNull(player).latestVersion)
                );
            }

            i++;
        }

        return msg.toString();
    }

    public static String getIsPublicGuild(SavableGuild guild){
        return guild.isPublic ? MessageConfUtils.guildsIsPublicTrue() : MessageConfUtils.guildsIsPublicFalse();
    }

    public static String getIsMutedGuild(SavableGuild guild){
        return guild.isMuted ? MessageConfUtils.guildsIsMutedTrue() : MessageConfUtils.guildsIsMutedFalse();
    }

    public static void sendInfo(CommandSender sender) {
        sender.sendMessage(TextUtils.codedText(MessageConfUtils.info()
                .replace("%name%", StreamLine.getInstance().getDescription().getName())
                .replace("%version%", StreamLine.getInstance().getDescription().getVersion())
                .replace("%author%", StreamLine.getInstance().getDescription().getAuthor())
                .replace("%num_commands%", String.valueOf(PluginUtils.commandsAmount))
                .replace("%num_listeners%", String.valueOf(PluginUtils.listenerAmount))
                .replace("%num_events%", String.valueOf(EventsHandler.getEvents().size()))
                .replace("%discord%", "https://discord.gg/tny494zXfn")
        ));
    }

    public static void sendChatFilterMessage(CommandSender sender, ChatFilter filter, String message) {
        sender.sendMessage(TextUtils.codedText(message
                .replace("%toggle%", filter.enabled ? MessageConfUtils.filtersEnabled() : MessageConfUtils.filtersDisabled())
                .replace("%regex%", filter.regex)
                .replace("%replacements%", getReplacementsStringFromFilter(filter))
                .replace("%name%", filter.name)
        ));
    }

    public static String getReplacementsStringFromFilter(ChatFilter filter) {
        StringBuilder builder = new StringBuilder();

        int i = 1;
        for (String replacement : filter.replacements) {
            if (i == filter.replacements.size()) {
                builder.append(MessageConfUtils.filtersReplacementsLast().replace("%replacement%", replacement));
            } else {
                builder.append(MessageConfUtils.filtersReplacementsNLast().replace("%replacement%", replacement));
            }
        }

        return builder.toString();
    }
}
