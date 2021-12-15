package net.plasmere.streamline.utils;

import com.velocitypowered.api.proxy.Player;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.DiscordBotConfUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.savable.SavableAdapter;
import net.plasmere.streamline.objects.savable.groups.SavableGuild;
import net.plasmere.streamline.objects.chats.ChatsHandler;
import net.plasmere.streamline.objects.enums.MessageServerType;
import net.plasmere.streamline.objects.messaging.DiscordMessage;
import net.plasmere.streamline.objects.savable.users.SavableConsole;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.objects.savable.users.SavableUser;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GuildUtils {private static final List<SavableGuild> guilds = new ArrayList<>();

    public static List<SavableGuild> getGuilds() {
        List<SavableGuild> rem = new ArrayList<>();

        for (SavableGuild g : guilds) {
            if (g.uuid == null) rem.add(g);
        }

        for (SavableGuild g : rem) {
            guilds.remove(g);
        }

        return guilds;
    }
    // SavableGuild , Invites
    public static Map<SavableGuild, List<SavableUser>> invites = new HashMap<>();

    public static int allGuildsCount() {
        File[] files = StreamLine.getInstance().getGDir().listFiles();

        if (files == null) return 0;

        int amount = 0;
        for (File file : files) {
            try {
                if (! file.getName().endsWith(".properties")) continue;

                SavableGuild guild = getOrGetGuild(file.getName().replace(".properties", ""));

                if (guild == null) continue;
                if (guild.uuid == null) continue;

                amount ++;
            } catch (Exception e) {
                // do nothing
            }
        }

        return amount;
    }

    public static void removeInvite(SavableGuild guild, SavableUser player) {
        invites.get(guild).remove(player);
    }

    public static SavableGuild getGuild(SavableUser stat) {
        try {
            for (SavableGuild guild : guilds) {
                if (guild.hasMember(stat)) {
                    return guild;
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static SavableGuild getOrGetGuild(String uuid){
        SavableGuild guild = getGuild(uuid);

        if (guild == null) {
            if (existsByUUID(uuid)) {
                try {
                    guild = new SavableGuild(uuid);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return guild;
    }

    public static boolean hasOnlineMemberAlready(SavableUser stat){
        List<SavableUser> users = new ArrayList<>(PlayerUtils.getStats());

        for (SavableUser user : users) {
            if (user.uuid.equals(stat.uuid)) continue;
            if (user.guild.equals(stat.guild)) return true;
        }

        return false;
    }

    public static SavableGuild getGuild(String uuid) {
        try {
            for (SavableGuild guild : guilds) {
                if (guild.hasMember(PlayerUtils.getOrGetSavableUser(uuid))) {
                    return guild;
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean hasGuild(SavableUser player) {
        for (SavableGuild guild : guilds) {
            if (guild.hasMember(player)) return true;
        }
        return false;
    }

    public static boolean existsByUUID(String uuid){
        File file = new File(SavableAdapter.Type.GUILD.path, uuid + SavableAdapter.Type.GUILD.suffix);

        return file.exists();
    }

    public static boolean exists(String username){
        return existsByUUID(UUIDUtils.swapToUUID(username));
    }

    public static boolean isGuild(SavableGuild guild){
        return guilds.contains(guild);
    }

    public static boolean pHasGuild(SavableUser player) {
        if (!existsByUUID(player.uuid)) return false;

        SavableGuild guild;
        try {
            guild = new SavableGuild(player.guild);
        } catch (NullPointerException e) {
            if (ConfigUtils.debug()) {
                MessagingUtils.logWarning("SavablePlayer's guild could not be found... Adding now!");
            }

            player.setGuild(player.uuid);
            return true;
        }

        if (guild.uuid == null) {
            return false;
        }

        return true;
    }

    public static boolean checkPlayer(SavableGuild guild, SavableUser player, SavableUser sender){
        if (! isGuild(guild) || guild == null) {
            MessagingUtils.sendBUserMessage(sender.findSender(), noGuildFound);
            return false;
        }

        if (! guild.hasMember(sender)) {
            MessagingUtils.sendBUserMessage(sender.findSender(), notInGuild);
            return false;
        }

        if (hasGuild(player)) {
            MessagingUtils.sendBUserMessage(sender.findSender(), alreadyHasOneOthers);
            return false;
        }

        return true;
    }

    public static void createGuild(SavableUser sender, String name) {
        SavableGuild g = getGuild(sender);

        if (g != null) {
            MessagingUtils.sendBUserMessage(sender.findSender(), alreadyMade);
            return;
        }

        if (name.equals("")) {
            MessagingUtils.sendBUserMessage(sender.findSender(), createNonEmpty);
            return;
        }

        if (ConfigUtils.guildIncludeColors()) {
            if (name.length() > ConfigUtils.guildMaxLength()) {
                MessagingUtils.sendBUserMessage(sender.findSender(), nameTooLong
                        .replace("%length%", String.valueOf(name.length()))
                        .replace("%max_length%", String.valueOf(ConfigUtils.guildMaxLength()))
                        .replace("%codes%", withCodes)
                );
                return;
            }
        } else {
            if (TextUtils.stripColor(name).length() > ConfigUtils.guildMaxLength()) {
                MessagingUtils.sendBUserMessage(sender.findSender(), nameTooLong
                        .replace("%length%", String.valueOf(TextUtils.stripColor(name).length()))
                        .replace("%max_length%", String.valueOf(ConfigUtils.guildMaxLength()))
                        .replace("%codes%", withoutCodes)
                );
                return;
            }
        }

        try {
            //MessagingUtils.logInfo("createGuild SavablePlayer.uuid > " + sender.uuid);
            SavableGuild guild = new SavableGuild(sender.uuid, name);

            addGuild(guild);

            MessagingUtils.sendBGUserMessage(guild, sender.findSender(), sender.findSender(),  create);

            if (ConfigUtils.moduleDEnabled()) {
                if (ConfigUtils.guildToDiscord() && ConfigUtils.guildConsoleCreates()) {
                    MessagingUtils.sendDiscordGEBMessage(guild, new DiscordMessage(sender.findSender(), createTitle,
                            createConsole
                            , DiscordBotConfUtils.textChannelGuilds()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addGuild(SavableGuild guild){
        SavableGuild g;

        try {
            g = getGuild(guild.uuid);
        } catch (Exception e) {
            return;
            // Do nothing.
        }

        if (g != null) return;

        try {
            if (guilds.size() > 0) {
                List<SavableGuild> rem = new ArrayList<>();

                for (SavableGuild gu : guilds) {
                    String s = gu.uuid;

                    if (s == null) {
                        rem.add(gu);
                        continue;
                    }

                    if (s.equals(guild.uuid)) {
                        rem.add(gu);
                    }
                }

                for (SavableGuild gd : rem) {
                    guilds.remove(gd);
                }
            }

            guilds.add(guild);
        } catch (Exception e){
            MessagingUtils.logInfo("Error adding guild...");
            e.printStackTrace();
        }
    }

    public static boolean hasLeader(String leader){
        List<SavableGuild> toRem = new ArrayList<>();

        boolean hasLeader = false;

        for (SavableGuild guild : guilds){
            if (guild.uuid == null) {
                toRem.add(guild);
                continue;
            }
            if (guild.uuid.equals(leader)) hasLeader = true;
        }

        for (SavableGuild guild : toRem) {
            try {
                guild.dispose();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            removeGuild(guild);
        }

        return hasLeader;
    }

    public static void removeGuild(SavableGuild guild){
        guild.saveAll();
        guilds.remove(guild);
    }

    public static void sendInvite(SavableUser to, SavableUser from) {
        try {
            SavableGuild guild = getGuild(from);

            if (! checkPlayer(guild, to, from)) return;

            if (to.equals(from)) {
                MessagingUtils.sendBUserMessage(from.findSender(), inviteNonSelf);
                return;
            }

            if (! guild.hasModPerms(from.uuid)) {
                MessagingUtils.sendBUserMessage(from.findSender(), noPermission);
                return;
            }

            if (isGuild(getGuild(to))) {
                MessagingUtils.sendBUserMessage(from.findSender(), alreadyHasOneOthers);
                return;
            }

            if (guild.invites.contains(to)) {
                MessagingUtils.sendBUserMessage(from.findSender(), inviteFailure);
                return;
            }

            if (to instanceof SavablePlayer && ((SavablePlayer) to).online) {
                MessagingUtils.sendBGUserMessage(guild, from.findSender(), to.findSender(), TextUtils.replaceAllPlayerBungee(inviteUser, to)
                );
            }

            for (SavableUser pl : guild.totalMembers) {
                if (pl.uuid.equals(guild.uuid)) {
                    MessagingUtils.sendBGUserMessage(guild, from.findSender(), pl.findSender(), TextUtils.replaceAllPlayerBungee(inviteLeader, to)
                    );
                } else {
                    MessagingUtils.sendBGUserMessage(guild, from.findSender(), pl.findSender(), TextUtils.replaceAllPlayerBungee(inviteMembers, to)
                    );
                }
            }

            guild.addInvite(to);
            invites.remove(guild);
            invites.put(guild, guild.invites);

            if (ConfigUtils.moduleDEnabled()) {
                if (ConfigUtils.guildToDiscord() && ConfigUtils.guildConsoleInvites()) {
                    MessagingUtils.sendDiscordGEBMessage(guild, new DiscordMessage(from.findSender(), inviteTitle,
                            TextUtils.replaceAllPlayerDiscord(inviteConsole, to)
                            , DiscordBotConfUtils.textChannelGuilds()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void acceptInvite(SavableUser accepter, SavableUser from) {
        try {
            SavableGuild guild = getGuild(from);

            if (!isGuild(guild) || guild == null) {
                MessagingUtils.sendBUserMessage(accepter.findSender(), acceptFailure);
                return;
            }

            if (! guild.hasMember(from)) {
                MessagingUtils.sendBUserMessage(accepter.findSender(), otherNotInGuild);
                return;
            }

            if (isGuild(getGuild(accepter))) {
                MessagingUtils.sendBUserMessage(accepter.findSender(), alreadyHasOneSelf);
                return;
            }

            if (! invites.get(guild).contains(accepter)) {
                MessagingUtils.sendBUserMessage(accepter.findSender(), acceptFailure);
                return;
            }

            if (guild.invites.contains(accepter)) {
                if (guild.getSize() >= guild.maxSize) {
                    MessagingUtils.sendBGUserMessage(guild, accepter.findSender(), accepter.findSender(), notEnoughSpace);
                    return;
                }

                MessagingUtils.sendBGUserMessage(guild, accepter.findSender(), accepter.findSender(), TextUtils.replaceAllPlayerBungee(acceptUser, accepter)
                        .replace("%from_formatted%", PlayerUtils.getJustDisplayBungee(from))
                        .replace("%from_display%", PlayerUtils.getOffOnDisplayBungee(from))
                        .replace("%from_normal%", PlayerUtils.getOffOnRegBungee(from))
                        .replace("%from_absolute%", PlayerUtils.getAbsoluteBungee(from))
                );

                for (SavableUser pl : guild.totalMembers){
                    if (pl.uuid.equals(guild.uuid)){
                        MessagingUtils.sendBGUserMessage(guild, accepter.findSender(), pl.findSender(), TextUtils.replaceAllPlayerBungee(acceptLeader, accepter)
                                .replace("%from_formatted%", PlayerUtils.getJustDisplayBungee(from))
                                .replace("%from_display%", PlayerUtils.getOffOnDisplayBungee(from))
                                .replace("%from_normal%", PlayerUtils.getOffOnRegBungee(from))
                                .replace("%from_absolute%", PlayerUtils.getAbsoluteBungee(from))
                        );
                    } else {
                        MessagingUtils.sendBGUserMessage(guild, accepter.findSender(), pl.findSender(), TextUtils.replaceAllPlayerBungee(acceptMembers, accepter)
                                .replace("%from_formatted%", PlayerUtils.getJustDisplayBungee(from))
                                .replace("%from_display%", PlayerUtils.getOffOnDisplayBungee(from))
                                .replace("%from_normal%", PlayerUtils.getOffOnRegBungee(from))
                                .replace("%from_absolute%", PlayerUtils.getAbsoluteBungee(from))
                        );
                    }
                }

                guild.addMember(accepter);
                guild.remFromInvites(from, accepter);

                if (ConfigUtils.moduleDEnabled()) {
                    if (ConfigUtils.guildToDiscord() && ConfigUtils.guildConsoleJoins()) {
                        MessagingUtils.sendDiscordGEBMessage(guild, new DiscordMessage(accepter.findSender(), joinsTitle,
                                TextUtils.replaceAllPlayerDiscord(joinsConsole, accepter)
                                        .replace("%from_formatted%", PlayerUtils.getJustDisplayDiscord(from))
                                        .replace("%from_display%", PlayerUtils.getOffOnDisplayDiscord(from))
                                        .replace("%from_normal%", PlayerUtils.getOffOnRegDiscord(from))
                                        .replace("%from_absolute%", PlayerUtils.getAbsoluteDiscord(from))
                                , DiscordBotConfUtils.textChannelGuilds()));
                    }

                    if (ConfigUtils.guildToDiscord() && ConfigUtils.guildConsoleAccepts()) {
                        MessagingUtils.sendDiscordGEBMessage(guild, new DiscordMessage(accepter.findSender(), acceptTitle,
                                TextUtils.replaceAllPlayerDiscord(acceptConsole, accepter)
                                        .replace("%from_formatted%", PlayerUtils.getJustDisplayDiscord(from))
                                        .replace("%from_display%", PlayerUtils.getOffOnDisplayDiscord(from))
                                        .replace("%from_normal%", PlayerUtils.getOffOnRegDiscord(from))
                                        .replace("%from_absolute%", PlayerUtils.getAbsoluteDiscord(from))
                                , DiscordBotConfUtils.textChannelGuilds()));
                    }
                }
            } else {
                MessagingUtils.sendBUserMessage(accepter.findSender(), acceptFailure);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void denyInvite(SavableUser denier, SavableUser from) {
        try {
            SavableGuild guild = getGuild(from);

            if (!isGuild(guild) || guild == null) {
                MessagingUtils.sendBUserMessage(denier.findSender(), denyFailure);
                return;
            }

            if (! guild.hasMember(from)) {
                MessagingUtils.sendBUserMessage(denier.findSender(), otherNotInGuild);
                return;
            }

            if (! invites.get(guild).contains(denier)) {
                MessagingUtils.sendBUserMessage(denier.findSender(), denyFailure);
                return;
            }

            if (guild.invites.contains(denier)) {
                MessagingUtils.sendBGUserMessage(guild, denier.findSender(), denier.findSender(), TextUtils.replaceAllPlayerBungee(denyUser, denier)
                        .replace("%from_formatted%", PlayerUtils.getJustDisplayBungee(from))
                        .replace("%from_display%", PlayerUtils.getOffOnDisplayBungee(from))
                        .replace("%from_normal%", PlayerUtils.getOffOnRegBungee(from))
                        .replace("%from_absolute%", PlayerUtils.getAbsoluteBungee(from))
                );

                for (SavableUser pl : guild.totalMembers) {
                    if (pl.uuid.equals(guild.uuid)) {
                        MessagingUtils.sendBGUserMessage(guild, denier.findSender(), pl.findSender(), TextUtils.replaceAllPlayerBungee(denyLeader, denier)
                                .replace("%from_formatted%", PlayerUtils.getJustDisplayBungee(from))
                                .replace("%from_display%", PlayerUtils.getOffOnDisplayBungee(from))
                                .replace("%from_normal%", PlayerUtils.getOffOnRegBungee(from))
                                .replace("%from_absolute%", PlayerUtils.getAbsoluteBungee(from))
                        );
                    } else {
                        MessagingUtils.sendBGUserMessage(guild, denier.findSender(), pl.findSender(), TextUtils.replaceAllPlayerBungee(denyMembers, denier)
                                .replace("%from_formatted%", PlayerUtils.getJustDisplayBungee(from))
                                .replace("%from_display%", PlayerUtils.getOffOnDisplayBungee(from))
                                .replace("%from_normal%", PlayerUtils.getOffOnRegBungee(from))
                                .replace("%from_absolute%", PlayerUtils.getAbsoluteBungee(from))
                        );
                    }
                }

                guild.remFromInvites(from, denier);

                if (ConfigUtils.moduleDEnabled()) {
                    if (ConfigUtils.guildToDiscord() && ConfigUtils.guildConsoleDenies()) {
                        MessagingUtils.sendDiscordGEBMessage(guild, new DiscordMessage(denier.findSender(), denyTitle,
                                TextUtils.replaceAllPlayerDiscord(denyConsole, denier)
                                        .replace("%from_formatted%", PlayerUtils.getJustDisplayDiscord(from))
                                        .replace("%from_display%", PlayerUtils.getOffOnDisplayDiscord(from))
                                        .replace("%from_normal%", PlayerUtils.getOffOnRegDiscord(from))
                                        .replace("%from_absolute%", PlayerUtils.getAbsoluteDiscord(from))
                                , DiscordBotConfUtils.textChannelGuilds()));
                    }
                }
            } else {
                MessagingUtils.sendBUserMessage(denier.findSender(), denyFailure);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void warpGuild(SavableUser sender){
        SavableGuild guild = getGuild(sender);

        if (!isGuild(guild) || guild == null) {
            MessagingUtils.sendBUserMessage(sender.findSender(), noGuildFound);
            return;
        }

        if (sender instanceof SavableConsole) {
            MessagingUtils.sendBUserMessage(sender.findSender(), MessageConfUtils.onlyPlayers());
            return;
        }

        if (! guild.hasMember(sender)) {
            MessagingUtils.sendBUserMessage(sender.findSender(), notInGuild);
            return;
        }

        if (! guild.hasModPerms(sender)) {
            MessagingUtils.sendBUserMessage(sender.findSender(), noPermission);
            return;
        }

        if (sender instanceof SavablePlayer && sender.online) {
            try {
                for (SavableUser player : new ArrayList<>(guild.totalMembers)) {
                    SavablePlayer p = (SavablePlayer) player;
                    if (! p.online) continue;
                    p.player.createConnectionRequest(StreamLine.getProxy().getServer(((SavablePlayer) sender).getServer().getServerInfo().getName()).get()).connect();
                }
            } catch (ConcurrentModificationException e) {
                e.printStackTrace();
            }
        }

        if (ConfigUtils.guildToDiscord() && ConfigUtils.guildConsoleWarps()) {
            MessagingUtils.sendDiscordGEBMessage(guild, new DiscordMessage(sender.findSender(), warpTitle,
                    warpConsole
                    , DiscordBotConfUtils.textChannelGuilds()));
        }
    }

    public static void muteGuild(SavableUser sender){
        SavableGuild guild = getGuild(sender);

        if (!isGuild(guild) || guild == null) {
            MessagingUtils.sendBUserMessage(sender.findSender(), noGuildFound);
            return;
        }

        if (! guild.hasMember(sender)) {
            MessagingUtils.sendBUserMessage(sender.findSender(), notInGuild);
            return;
        }

        if (! guild.hasModPerms(sender)) {
            MessagingUtils.sendBGUserMessage(guild, sender.findSender(), sender.findSender(), noPermission);
            return;
        }

        if (guild.isMuted) {
            for (SavableUser pl : guild.totalMembers) {
                if (pl.equals(sender)){
                    MessagingUtils.sendBGUserMessage(guild, sender.findSender(), pl.findSender(), unmuteSender);
                } else {
                    MessagingUtils.sendBGUserMessage(guild, sender.findSender(), pl.findSender(), unmuteMembers);
                }
            }

        } else {
            for (SavableUser pl : guild.totalMembers) {
                if (pl.equals(sender)){
                    MessagingUtils.sendBGUserMessage(guild, sender.findSender(), pl.findSender(), muteSender);
                } else {
                    MessagingUtils.sendBGUserMessage(guild, sender.findSender(), pl.findSender(), muteMembers);
                }
            }

        }
        guild.toggleMute();

        if (ConfigUtils.moduleDEnabled()) {
            if (ConfigUtils.guildToDiscord() && ConfigUtils.guildConsoleMutes()) {
                MessagingUtils.sendDiscordGEBMessage(guild, new DiscordMessage(sender.findSender(), muteTitle,
                        muteConsole
                        , DiscordBotConfUtils.textChannelGuilds()));
            }
        }
    }

    public static void kickMember(SavableUser sender, SavableUser player) {
        SavableGuild guild = getGuild(sender);

        if (!isGuild(guild) || guild == null) {
            MessagingUtils.sendBUserMessage(sender.findSender(), kickFailure);
            return;
        }

        if (!guild.hasMember(sender)) {
            MessagingUtils.sendBUserMessage(sender.findSender(), notInGuild);
            return;
        }

        if (!guild.hasMember(player)) {
            MessagingUtils.sendBUserMessage(sender.findSender(), otherNotInGuild);
            return;
        }

        if (!guild.hasModPerms(sender)) {
            MessagingUtils.sendBGUserMessage(guild, sender.findSender(), sender.findSender(),  noPermission);
            return;
        }

        if (guild.hasModPerms(player)) {
            MessagingUtils.sendBGUserMessage(guild, sender.findSender(), sender.findSender(),  kickMod);
            return;
        }

        try {
            if (sender.equals(player)) {
                MessagingUtils.sendBGUserMessage(guild, sender.findSender(), sender.findSender(),  kickSelf);
            } else if (player.equals(PlayerUtils.getOrGetSavableUser(guild.uuid))) {
                MessagingUtils.sendBGUserMessage(guild, sender.findSender(), sender.findSender(),  noPermission);
            } else {
                for (SavableUser pl : guild.totalMembers) {
                    if (pl.equals(sender)) {
                        MessagingUtils.sendBGUserMessage(guild, sender.findSender(), pl.findSender(), TextUtils.replaceAllPlayerBungee(kickSender, player)
                        );
                    } else if (! pl.uuid.equals(guild.uuid)) {
                        MessagingUtils.sendBGUserMessage(guild, sender.findSender(), pl.findSender(), TextUtils.replaceAllPlayerBungee(kickUser, player)
                        );
                    } else {
                        MessagingUtils.sendBGUserMessage(guild, sender.findSender(), pl.findSender(), TextUtils.replaceAllPlayerBungee(kickMembers, player)
                        );
                    }
                }

                guild.removeMemberFromGroup(player);
            }

            if (ConfigUtils.moduleDEnabled()) {
                if (ConfigUtils.guildToDiscord() && ConfigUtils.guildConsoleKicks()) {
                    MessagingUtils.sendDiscordGEBMessage(guild, new DiscordMessage(sender.findSender(), kickTitle,
                            TextUtils.replaceAllPlayerDiscord(kickConsole, player)
                            , DiscordBotConfUtils.textChannelGuilds()));
                }
            }
        } catch (Exception e) {
            MessagingUtils.sendBGUserMessage(guild, sender.findSender(), sender.findSender(), MessageConfUtils.bungeeCommandErrorUnd()
                            .replace("%class%", GuildUtils.class.getName())
                    );
            e.printStackTrace();
        }
    }

    public static void info(SavableUser sender){
        SavableGuild guild = getGuild(sender);

        if (!isGuild(guild) || guild == null) {
            MessagingUtils.sendBUserMessage(sender.findSender(), noGuildFound);
            return;
        }

        if (! guild.hasMember(sender)) {
            MessagingUtils.sendBUserMessage(sender.findSender(), notInGuild);
            return;
        }

        MessagingUtils.sendBGUserMessage(guild, sender.findSender(), sender.findSender(),  info);
    }

    public static void disband(SavableUser sender) {
        try {
            SavableGuild guild = getGuild(sender);

            if (!isGuild(guild) || guild == null) {
                MessagingUtils.sendBUserMessage(sender.findSender(), noGuildFound);
                return;
            }

            if (! guild.hasMember(sender)) {
                MessagingUtils.sendBUserMessage(sender.findSender(), notInGuild);
                return;
            }

            if (! guild.uuid.equals(sender.uuid)) {
                MessagingUtils.sendBUserMessage(sender.findSender(), noPermission);
                return;
            }

            for (SavableUser pl : guild.totalMembers) {
                if (! pl.online) continue;

                if (! pl.uuid.equals(guild.uuid)) {
                    MessagingUtils.sendBGUserMessage(guild, sender.findSender(), pl.findSender(), disbandMembers
                    );
                } else {
                    MessagingUtils.sendBGUserMessage(guild, sender.findSender(), pl.findSender(), disbandLeader
                    );
                }

            }

            if (ConfigUtils.moduleDEnabled()) {
                if (ConfigUtils.guildToDiscord() && ConfigUtils.guildConsoleDisbands()) {
                    MessagingUtils.sendDiscordGEBMessage(guild, new DiscordMessage(sender.findSender(), disbandTitle,
                            disbandConsole
                            , DiscordBotConfUtils.textChannelGuilds()));
                }
            }

            guilds.remove(guild);
            guild.disband();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openGuild(SavableUser sender) {
        try {
            SavableGuild guild = getGuild(sender);

            if (!isGuild(guild) || guild == null) {
                MessagingUtils.sendBUserMessage(sender.findSender(), noGuildFound);
                return;
            }

            if (! guild.hasMember(sender)) {
                MessagingUtils.sendBUserMessage(sender.findSender(), notInGuild);
                return;
            }

            if (! guild.hasModPerms(sender)) {
                MessagingUtils.sendBUserMessage(sender.findSender(), noPermission);
                return;
            }

            if (guild.isPublic) {
                MessagingUtils.sendBGUserMessage(guild, sender.findSender(), sender.findSender(),  openFailure
                );
            } else {
                guild.setPublic(true);

                for (SavableUser pl : guild.totalMembers) {
                    if (pl.equals(sender)) {
                        MessagingUtils.sendBGUserMessage(guild, sender.findSender(), pl.findSender(), openSender
                        );
                    } else {
                        MessagingUtils.sendBGUserMessage(guild, sender.findSender(), pl.findSender(), openMembers
                        );
                    }
                }

                if (ConfigUtils.moduleDEnabled()) {
                    if (ConfigUtils.guildToDiscord() && ConfigUtils.guildConsoleOpens()) {
                        MessagingUtils.sendDiscordGEBMessage(guild, new DiscordMessage(sender.findSender(), openTitle,
                                openConsole
                                , DiscordBotConfUtils.textChannelGuilds()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeGuild(SavableUser sender) {
        try {
            SavableGuild guild = getGuild(sender);

            if (!isGuild(guild) || guild == null) {
                MessagingUtils.sendBUserMessage(sender.findSender(), noGuildFound);
                return;
            }

            if (! guild.hasMember(sender)) {
                MessagingUtils.sendBUserMessage(sender.findSender(), notInGuild);
                return;
            }

            if (!guild.hasModPerms(sender)) {
                MessagingUtils.sendBUserMessage(sender.findSender(), noPermission);
                return;
            }

            if (!guild.isPublic) {
                MessagingUtils.sendBGUserMessage(guild, sender.findSender(), sender.findSender(),  closeFailure
                );
            } else {
                guild.setPublic(false);

                for (SavableUser pl : guild.totalMembers) {
                    if (pl.equals(sender)) {
                        MessagingUtils.sendBGUserMessage(guild, sender.findSender(), pl.findSender(), closeSender
                        );
                    } else {
                        MessagingUtils.sendBGUserMessage(guild, sender.findSender(), pl.findSender(), closeMembers
                        );
                    }
                }

                if (ConfigUtils.moduleDEnabled()) {
                    if (ConfigUtils.guildToDiscord() && ConfigUtils.guildConsoleCloses()) {
                        MessagingUtils.sendDiscordGEBMessage(guild, new DiscordMessage(sender.findSender(), closeTitle,
                                closeConsole
                                , DiscordBotConfUtils.textChannelGuilds()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void listGuild(SavableUser sender) {
        try {
            SavableGuild guild = getGuild(sender);

            if (!isGuild(guild) || guild == null) {
                MessagingUtils.sendBUserMessage(sender.findSender(), noGuildFound);
                return;
            }

            if (! guild.hasMember(sender)) {
                MessagingUtils.sendBUserMessage(sender.findSender(), notInGuild);
                return;
            }

            String leaderBulk = listLeaderBulk;
            String moderatorBulk = listModBulkMain
                    .replace("%moderators%", Objects.requireNonNull(moderators(guild)));
            String memberBulk = listMemberBulkMain
                    .replace("%members%", Objects.requireNonNull(members(guild)));

            MessagingUtils.sendBGUserMessage(guild, sender.findSender(), sender.findSender(),  listMain
                    .replace("%leaderbulk%", leaderBulk)
                    .replace("%moderatorbulk%", moderatorBulk)
                    .replace("%memberbulk%", memberBulk)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String moderators(SavableGuild guild) {
        try {
            if (! (guild.moderators.size() > 0)) {
                return listModBulkNone;
            }

            StringBuilder mods = new StringBuilder();

            int i = 1;

            for (SavableUser m : guild.moderators) {
                if (i < guild.moderators.size()) {
                    mods.append(TextUtils.replaceAllPlayerBungee(listModBulkNotLast, m)
                    );
                } else {
                    mods.append(TextUtils.replaceAllPlayerBungee(listModBulkLast, m)
                    );
                }
                i++;
            }

            return mods.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String members(SavableGuild guild) {
        try {
            if (! (guild.members.size() > 0)) {
                return listMemberBulkNone;
            }

            StringBuilder mems = new StringBuilder();

            int i = 1;

            for (SavableUser m : guild.members) {
                if (i <guild.moderators.size()) {
                    mems.append(TextUtils.replaceAllPlayerBungee(listMemberBulkNotLast, m)
                    );
                } else {
                    mems.append(TextUtils.replaceAllPlayerBungee(listMemberBulkLast, m)
                    );
                }
                i++;
            }

            return mems.toString();
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public static void promotePlayer(SavableUser sender, SavableUser player) {
        try {
            SavableGuild guild = getGuild(sender);

            if (!isGuild(guild) || guild == null) {
                MessagingUtils.sendBUserMessage(sender.findSender(), noGuildFound);
                return;
            }

            if (! guild.hasMember(sender)) {
                MessagingUtils.sendBUserMessage(sender.findSender(), notInGuild);
                return;
            }

            if (! guild.hasMember(player)) {
                MessagingUtils.sendBUserMessage(sender.findSender(), otherNotInGuild);
                return;
            }

            if (!guild.uuid.equals(sender.uuid)) {
                MessagingUtils.sendBUserMessage(sender.findSender(), noPermission);
                return;
            }

            switch (guild.getLevel(player)) {
                case LEADER:
                    MessagingUtils.sendBGUserMessage(guild, sender.findSender(), sender.findSender(),  TextUtils.replaceAllPlayerBungee(promoteFailure, player)
                            .replace("%level%", TextUtils.replaceAllPlayerBungee(textLeader, player)
                            )
                    );
                    return;
                case MODERATOR:
                    guild.replaceLeader(player);

                    for (SavableUser pl : guild.totalMembers) {
                        if (pl.uuid.equals(guild.uuid)) {
                            MessagingUtils.sendBGUserMessage(guild, sender.findSender(), pl.findSender(), TextUtils.replaceAllPlayerBungee(promoteLeader, player)
                                    .replace("%level%", TextUtils.replaceAllPlayerBungee(textLeader, player)
                                    )
                            );
                        } else if (pl.equals(player)) {
                            MessagingUtils.sendBGUserMessage(guild, sender.findSender(), pl.findSender(), TextUtils.replaceAllPlayerBungee(promoteUser, player)
                                    .replace("%level%", TextUtils.replaceAllPlayerBungee(textLeader, player)
                                    )
                            );
                        } else {
                            MessagingUtils.sendBGUserMessage(guild, sender.findSender(), pl.findSender(), TextUtils.replaceAllPlayerBungee(promoteMembers, player)
                                    .replace("%level%", TextUtils.replaceAllPlayerBungee(textLeader, player)
                                    )
                            );
                        }
                    }
                    return;
                case MEMBER:
                default:
                    guild.setModerator(player);

                    for (SavableUser pl : guild.totalMembers) {
                        if (pl.uuid.equals(guild.uuid)) {
                            MessagingUtils.sendBGUserMessage(guild, sender.findSender(), pl.findSender(), TextUtils.replaceAllPlayerBungee(promoteLeader, player)
                                    .replace("%level%", TextUtils.replaceAllPlayerBungee(textModerator, player)
                                    )
                            );
                        } else if (pl.equals(player)) {
                            MessagingUtils.sendBGUserMessage(guild, sender.findSender(), pl.findSender(), TextUtils.replaceAllPlayerBungee(promoteUser, player)
                                    .replace("%level%", TextUtils.replaceAllPlayerBungee(textModerator, player)
                                    )
                            );
                        } else {
                            MessagingUtils.sendBGUserMessage(guild, sender.findSender(), pl.findSender(), TextUtils.replaceAllPlayerBungee(promoteMembers, player)
                                    .replace("%level%", TextUtils.replaceAllPlayerBungee(textModerator, player)
                                    )
                            );
                        }
                    }
                    break;
            }

            if (ConfigUtils.moduleDEnabled()) {
                if (ConfigUtils.guildToDiscord() && ConfigUtils.guildConsolePromotes()) {
                    MessagingUtils.sendDiscordGEBMessage(guild, new DiscordMessage(sender.findSender(), promoteTitle,
                            TextUtils.replaceAllPlayerDiscord(promoteConsole, player)
                            , DiscordBotConfUtils.textChannelGuilds()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void demotePlayer(SavableUser sender, SavableUser player) {
        try {
            SavableGuild guild = getGuild(sender);

            if (!isGuild(guild) || guild == null) {
                MessagingUtils.sendBUserMessage(sender.findSender(), noGuildFound);
                return;
            }

            if (! guild.hasMember(sender)) {
                MessagingUtils.sendBUserMessage(sender.findSender(), notInGuild);
                return;
            }

            if (! guild.hasMember(player)) {
                MessagingUtils.sendBUserMessage(sender.findSender(), otherNotInGuild);
                return;
            }

            if (!guild.hasModPerms(sender)) {
                MessagingUtils.sendBUserMessage(sender.findSender(), noPermission);
                return;
            }

            switch (guild.getLevel(player)) {
                case LEADER:
                    MessagingUtils.sendBGUserMessage(guild, sender.findSender(), sender.findSender(),  TextUtils.replaceAllPlayerBungee(demoteIsLeader, player)
                            .replace("%level%", TextUtils.replaceAllPlayerBungee(textLeader, player)
                            )
                    );
                    return;
                case MODERATOR:
                    guild.setMember(player);

                    for (SavableUser pl : guild.totalMembers) {
                        if (pl.uuid.equals(guild.uuid)) {
                            MessagingUtils.sendBGUserMessage(guild, sender.findSender(), pl.findSender(), TextUtils.replaceAllPlayerBungee(demoteLeader, player)
                                    .replace("%level%", TextUtils.replaceAllPlayerBungee(textMember, player)
                                    )
                            );
                        } else if (pl.equals(player)) {
                            MessagingUtils.sendBGUserMessage(guild, sender.findSender(), pl.findSender(), TextUtils.replaceAllPlayerBungee(demoteUser, player)
                                    .replace("%level%", TextUtils.replaceAllPlayerBungee(textMember, player)
                                    )
                            );
                        } else {
                            MessagingUtils.sendBGUserMessage(guild, sender.findSender(), pl.findSender(), TextUtils.replaceAllPlayerBungee(demoteMembers, player)
                                    .replace("%level%", TextUtils.replaceAllPlayerBungee(textMember, player)
                                    )
                            );
                        }
                    }
                    return;
                case MEMBER:
                default:
                    MessagingUtils.sendBGUserMessage(guild, sender.findSender(), sender.findSender(),  TextUtils.replaceAllPlayerBungee(demoteFailure, player)
                            .replace("%level%", TextUtils.replaceAllPlayerBungee(textMember, player)
                            )
                    );
                    break;
            }

            if (ConfigUtils.moduleDEnabled()) {
                if (ConfigUtils.guildToDiscord() && ConfigUtils.guildConsoleDemotes()) {
                    MessagingUtils.sendDiscordGEBMessage(guild, new DiscordMessage(sender.findSender(), demoteTitle,
                            TextUtils.replaceAllPlayerDiscord(demoteConsole, player)
                            , DiscordBotConfUtils.textChannelGuilds()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void joinGuild(SavableUser sender, SavableUser from) {
        try {
            SavableGuild guild = getGuild(from);

            if (!isGuild(guild) || guild == null) {
                MessagingUtils.sendBUserMessage(sender.findSender(), noGuildFound);
                return;
            }

            if (! guild.hasMember(from)) {
                MessagingUtils.sendBUserMessage(sender.findSender(), otherNotInGuild);
                return;
            }

            if (isGuild(getGuild(sender))) {
                MessagingUtils.sendBUserMessage(sender.findSender(), alreadyHasOneSelf);
                return;
            }

            if (guild.getSize() + 1 > guild.maxSize) {
                MessagingUtils.sendBGUserMessage(guild, sender.findSender(), sender.findSender(),  notEnoughSpace);
                return;
            }

            if (guild.isPublic) {
                guild.addMember(sender);

                for (SavableUser pl : guild.totalMembers) {
                    if (pl.equals(sender)) {
                        MessagingUtils.sendBGUserMessage(guild, sender.findSender(), pl.findSender(), joinUser
                        );
                    } else {
                        MessagingUtils.sendBGUserMessage(guild, sender.findSender(), pl.findSender(), joinMembers
                        );
                    }
                }

                if (ConfigUtils.moduleDEnabled()) {
                    if (ConfigUtils.guildToDiscord() && ConfigUtils.guildConsoleJoins()) {
                        MessagingUtils.sendDiscordGEBMessage(guild, new DiscordMessage(sender.findSender(), joinsTitle,
                                joinsConsole
                                , DiscordBotConfUtils.textChannelGuilds()));
                    }
                }
            } else {
                MessagingUtils.sendBGUserMessage(guild, sender.findSender(), sender.findSender(),  joinFailure);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void leaveGuild(SavableUser sender) {
        try {
            SavableGuild guild = getGuild(sender);

            if (!isGuild(guild) || guild == null) {
                MessagingUtils.sendBUserMessage(sender.findSender(), noGuildFound);
                return;
            }

            if (! guild.hasMember(sender)) {
                MessagingUtils.sendBUserMessage(sender.findSender(), notInGuild);
                return;
            }

            if (PlayerUtils.getOrGetSavableUser(guild.uuid).equals(sender)) {
                for (SavableUser pl : guild.totalMembers) {
                    if (pl.equals(sender)) {
                        MessagingUtils.sendBGUserMessage(guild, sender.findSender(), pl.findSender(), leaveUser);
                        MessagingUtils.sendBGUserMessage(guild, sender.findSender(), pl.findSender(), disbandLeader);
                    } else {
                        MessagingUtils.sendBGUserMessage(guild, sender.findSender(), pl.findSender(), leaveMembers);
                        MessagingUtils.sendBGUserMessage(guild, sender.findSender(), pl.findSender(), disbandMembers);
                    }
                }

                guilds.remove(guild);
                guild.dispose();
                return;
            }

            if (guild.hasMember(sender)) {
                for (SavableUser pl : guild.totalMembers) {
                    if (pl.equals(sender)) {
                        MessagingUtils.sendBGUserMessage(guild, sender.findSender(), pl.findSender(), leaveUser
                        );
                    } else {
                        MessagingUtils.sendBGUserMessage(guild, sender.findSender(), pl.findSender(), leaveMembers
                        );
                    }
                }

                guild.removeMemberFromGroup(sender);

                if (ConfigUtils.moduleDEnabled()) {
                    if (ConfigUtils.guildToDiscord() && ConfigUtils.guildConsoleLeaves()) {
                        MessagingUtils.sendDiscordGEBMessage(guild, new DiscordMessage(sender.findSender(), leaveTitle,
                                leaveConsole
                                , DiscordBotConfUtils.textChannelGuilds()));
                    }
                }
            } else {
                MessagingUtils.sendBGUserMessage(guild, sender.findSender(), sender.findSender(),  leaveFailure);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void sendChat(SavableUser sender, String msg) {
        try {
            SavableGuild guild = getGuild(sender);

            if (! isGuild(guild) || guild == null) {
                MessagingUtils.sendBUserMessage(sender.findSender(), noGuildFound);
                return;
            }

            if (! guild.hasMember(sender)) {
                MessagingUtils.sendBUserMessage(sender.findSender(), notInGuild);
                return;
            }

            if (guild.isMuted && ! guild.hasModPerms(sender)) {
                MessagingUtils.sendBGUserMessage(guild, sender.findSender(), sender.findSender(),  chatMuted
                        .replace("%message%", msg)
                );
                return;
            }

            for (SavableUser pl : guild.totalMembers) {
                MessagingUtils.sendBGUserMessage(guild, sender.findSender(), pl.findSender(),
                        StreamLine.chatConfig.getPermissionedChatMessage(sender, ChatsHandler.getChat("guild", guild.uuid), "guild", MessageServerType.BUNGEE)
                                .replace("%message%", msg)
                );
            }

            if (ConfigUtils.moduleDEnabled()) {
                if (ConfigUtils.guildToDiscord() && ConfigUtils.guildConsoleChats()) {
                    MessagingUtils.sendDiscordGEBMessage(guild, new DiscordMessage(sender.findSender(), chatTitle,
                            chatConsole
                                    .replace("%message%", msg)
                            , DiscordBotConfUtils.textChannelGuilds()));
                }
            }

            if (ConfigUtils.moduleDPC()) {
                StreamLine.discordData.sendDiscordChannel(sender.findSender(), ChatsHandler.getChannel("guild"), guild.uuid, msg);
            }

            for (Player pp : StreamLine.getInstance().getProxy().getAllPlayers()){
                if (! pp.hasPermission(ConfigUtils.guildView())) continue;

                SavablePlayer them = PlayerUtils.getOrGetPlayerStatByUUID(pp.getUniqueId().toString());

                if (! them.gspy) continue;

                if (! them.gspyvs) if (them.uuid.equals(sender.uuid)) continue;

                MessagingUtils.sendBGUserMessage(guild, sender.findSender(), them.findSender(), spy.replace("%message%", msg));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendChat(SavablePlayer sender, SavableGuild guild, String msg) {
        try {
            if (! isGuild(guild) || guild == null) {
                MessagingUtils.sendBUserMessage(sender.findSender(), noGuildFound);
                return;
            }

            if (guild.isMuted && ! guild.hasModPerms(sender)) {
                MessagingUtils.sendBGUserMessage(guild, sender.findSender(), sender.findSender(),  chatMuted
                        .replace("%message%", msg)
                );
                return;
            }

            for (SavableUser pl : guild.totalMembers) {
                MessagingUtils.sendBGUserMessage(guild, sender.findSender(), pl.findSender(),
                        StreamLine.chatConfig.getPermissionedChatMessage(sender, ChatsHandler.getChat("guild", guild.uuid), "guild", MessageServerType.BUNGEE)
                                .replace("%message%", msg)
                );
            }

            if (! sender.chatIdentifier.equals(sender.guild) && ! sender.chatIdentifier.equals("network")) {
                MessagingUtils.sendBGUserMessage(guild, sender.findSender(), sender.findSender(),
                        StreamLine.chatConfig.getPermissionedChatMessage(sender, ChatsHandler.getChat("guild", guild.uuid), "guild", MessageServerType.BUNGEE)
                                .replace("%message%", msg)
                );
            }

            if (ConfigUtils.moduleDEnabled()) {
                if (ConfigUtils.guildToDiscord() && ConfigUtils.guildConsoleChats()) {
                    MessagingUtils.sendDiscordGEBMessage(guild, new DiscordMessage(sender.findSender(), chatTitle,
                            chatConsole
                                    .replace("%message%", msg)
                            , DiscordBotConfUtils.textChannelGuilds()));
                }
            }

            if (ConfigUtils.moduleDPC()) {
                StreamLine.discordData.sendDiscordChannel(sender.findSender(), ChatsHandler.getChannel("guild"), guild.uuid, msg);
            }

            for (Player pp : StreamLine.getInstance().getProxy().getAllPlayers()){
                if (! pp.hasPermission(ConfigUtils.guildView())) continue;

                SavablePlayer them = PlayerUtils.getOrGetPlayerStatByUUID(pp.getUniqueId().toString());

                if (! them.gspy) continue;

                if (! them.gspyvs) if (them.uuid.equals(sender.uuid)) continue;

                MessagingUtils.sendBGUserMessage(guild, sender.findSender(), them.findSender(), spy.replace("%message%", msg));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendChatFromDiscord(String nameUsed, SavableGuild guild, String format, String msg) {
        if (! ConfigUtils.moduleDEnabled()) return;

        try {
            for (SavableUser pl : guild.totalMembers) {
                MessagingUtils.sendBGUserMessageFromDiscord(guild, nameUsed, pl.findSender(), format
                        .replace("%message%", msg)
                );
            }

//            if (ConfigUtils.moduleDEnabled()) {
//                if (ConfigUtils.guildToDiscord() && ConfigUtils.guildConsoleChats()) {
//                    MessagingUtils.sendDiscordGEBMessage(guild, new DiscordMessage(sender.findSender(), chatTitle,
//                            chatConsole
//                                    .replace("%message%", msg)
//                            , DiscordBotConfUtils.textChannelGuilds()));
//                }
//            }

            for (Player pp : StreamLine.getInstance().getProxy().getAllPlayers()){
                if (! pp.hasPermission(ConfigUtils.guildView())) continue;

                SavablePlayer them = PlayerUtils.getOrGetPlayerStatByUUID(pp.getUniqueId().toString());

                if (! them.gspy) continue;

                MessagingUtils.sendBGUserMessageFromDiscord(guild, nameUsed, them.findSender(), spy.replace("%message%", msg));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendChatFromDiscord(SavableUser user, SavableGuild guild, String format, String msg) {
        if (! ConfigUtils.moduleDEnabled()) return;

        try {
            for (SavableUser pl : guild.totalMembers) {
                MessagingUtils.sendBGUserMessageFromDiscord(guild, user, pl.findSender(), format
                        .replace("%message%", msg)
                );
            }

//            if (ConfigUtils.moduleDEnabled()) {
//                if (ConfigUtils.guildToDiscord() && ConfigUtils.guildConsoleChats()) {
//                    MessagingUtils.sendDiscordGEBMessage(guild, new DiscordMessage(sender.findSender(), chatTitle,
//                            chatConsole
//                                    .replace("%message%", msg)
//                            , DiscordBotConfUtils.textChannelGuilds()));
//                }
//            }

            for (Player pp : StreamLine.getInstance().getProxy().getAllPlayers()){
                if (! pp.hasPermission(ConfigUtils.guildView())) continue;

                SavablePlayer them = PlayerUtils.getOrGetPlayerStatByUUID(pp.getUniqueId().toString());

                if (! them.gspy) continue;

                MessagingUtils.sendBGUserMessageFromDiscord(guild, user, them.findSender(), spy.replace("%message%", msg));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void rename(SavableUser sender, String newName){
        try {
            SavableGuild guild = getGuild(sender);

            if (! isGuild(guild) || guild == null) {
                MessagingUtils.sendBUserMessage(sender.findSender(), noGuildFound);
                return;
            }

            if (! guild.hasMember(sender)) {
                MessagingUtils.sendBUserMessage(sender.findSender(), notInGuild);
                return;
            }

            if (newName.equals("")) {
                MessagingUtils.sendBUserMessage(sender.findSender(), renameNonEmpty);
                return;
            }

            String oldName = guild.name;

            if (ConfigUtils.guildIncludeColors()) {
                if (newName.length() > ConfigUtils.guildMaxLength()) {
                    MessagingUtils.sendBUserMessage(sender.findSender(), nameTooLong
                            .replace("%length%", String.valueOf(newName.length()))
                            .replace("%max_length%", String.valueOf(ConfigUtils.guildMaxLength()))
                            .replace("%codes%", withCodes)
                    );
                    return;
                }
            } else {
                if (TextUtils.stripColor(newName).length() > ConfigUtils.guildMaxLength()) {
                    MessagingUtils.sendBUserMessage(sender.findSender(), nameTooLong
                            .replace("%length%", String.valueOf(TextUtils.stripColor(newName).length()))
                            .replace("%max_length%", String.valueOf(ConfigUtils.guildMaxLength()))
                            .replace("%codes%", withoutCodes)
                    );
                    return;
                }
            }

            guild.setNameReturnOld(newName);

            for (SavableUser pl : guild.totalMembers) {
                if (pl.equals(sender)) {
                    MessagingUtils.sendBGUserMessage(guild, sender.findSender(), pl.findSender(), renameSender
                            .replace("%old%", oldName)
                            .replace("%new%", newName)
                    );
                } else {
                    MessagingUtils.sendBGUserMessage(guild, sender.findSender(), pl.findSender(), renameMembers
                            .replace("%old%", oldName)
                            .replace("%new%", newName)
                    );
                }
            }

            if (ConfigUtils.moduleDEnabled()) {
                if (ConfigUtils.guildToDiscord() && ConfigUtils.guildConsoleRenames()) {
                    MessagingUtils.sendDiscordGEBMessage(guild, new DiscordMessage(sender.findSender(), renameTitle,
                            renameConsole
                                    .replace("%new%", newName)
                                    .replace("%old_name%", oldName)
                            , DiscordBotConfUtils.textChannelGuilds()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveAll(){
        List<SavableGuild> gs = new ArrayList<>(getGuilds());

        for (SavableGuild guild : gs) {
            try {
                guild.saveAll();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // MESSAGES...
    // Text.
    public static final String textLeader = StreamLine.config.getMessString("guild.text.leader");
    public static final String textModerator = StreamLine.config.getMessString("guild.text.moderator");
    public static final String textMember = StreamLine.config.getMessString("guild.text.member");
    // Discord.
    public static final String discordTitle = StreamLine.config.getMessString("guild.discord.title");
    // Spy.
    public static final String spy = StreamLine.config.getMessString("guild.spy");
    // No guild.
    public static final String noGuildFound = StreamLine.config.getMessString("guild.no-guild");
    // Name too long.
    public static final String nameTooLong = StreamLine.config.getMessString("guild.name-too-long");
    public static final String withCodes = StreamLine.config.getMessString("guild.codes.if-true");
    public static final String withoutCodes = StreamLine.config.getMessString("guild.codes.if-false");
    // Already made.
    public static final String alreadyMade = StreamLine.config.getMessString("guild.already-made");
    // Already in one, others.
    public static final String alreadyHasOneOthers = StreamLine.config.getMessString("guild.already-has-others");
    // Already in one self.
    public static final String alreadyHasOneSelf = StreamLine.config.getMessString("guild.already-has-self");
    // Not high enough permissions.
    public static final String noPermission = StreamLine.config.getMessString("guild.no-permission");
    // Not in a guild.
    public static final String notInGuild = StreamLine.config.getMessString("guild.not-in-a-guild");
    public static final String otherNotInGuild = StreamLine.config.getMessString("guild.other-not-in-guild");
    // Not enough space in guild.
    public static final String notEnoughSpace = StreamLine.config.getMessString("guild.not-enough-space");
    // Chat.
    public static final String chat = StreamLine.config.getMessString("guild.chat.message");
    public static final String chatMuted = StreamLine.config.getMessString("guild.chat.muted");
    public static final String chatConsole = StreamLine.config.getMessString("guild.chat.console");;
    public static final String chatTitle = StreamLine.config.getMessString("guild.chat.title");
    // Create.
    public static final String create = StreamLine.config.getMessString("guild.create.sender");
    public static final String createNonEmpty = StreamLine.config.getMessString("guild.create.non-empty");
    public static final String createConsole = StreamLine.config.getMessString("guild.create.console");
    public static final String createTitle = StreamLine.config.getMessString("guild.create.title");
    // Join.
    public static final String joinMembers = StreamLine.config.getMessString("guild.join.members");
    public static final String joinUser = StreamLine.config.getMessString("guild.join.user");
    public static final String joinFailure = StreamLine.config.getMessString("guild.join.failure");
    public static final String joinsConsole = StreamLine.config.getMessString("guild.join.console");
    public static final String joinsTitle = StreamLine.config.getMessString("guild.join.title");
    // Leave.
    public static final String leaveMembers = StreamLine.config.getMessString("guild.leave.members");
    public static final String leaveUser = StreamLine.config.getMessString("guild.leave.user");
    public static final String leaveFailure = StreamLine.config.getMessString("guild.leave.failure");
    public static final String leaveConsole = StreamLine.config.getMessString("guild.leave.console");
    public static final String leaveTitle = StreamLine.config.getMessString("guild.leave.title");
    // Promote.
    public static final String promoteMembers = StreamLine.config.getMessString("guild.promote.members");
    public static final String promoteUser = StreamLine.config.getMessString("guild.promote.user");
    public static final String promoteLeader = StreamLine.config.getMessString("guild.promote.leader");
    public static final String promoteFailure = StreamLine.config.getMessString("guild.promote.failure");
    public static final String promoteConsole = StreamLine.config.getMessString("guild.promote.console");
    public static final String promoteTitle = StreamLine.config.getMessString("guild.promote.title");
    // Demote.
    public static final String demoteMembers = StreamLine.config.getMessString("guild.demote.members");
    public static final String demoteUser = StreamLine.config.getMessString("guild.demote.user");
    public static final String demoteLeader = StreamLine.config.getMessString("guild.demote.leader");
    public static final String demoteFailure = StreamLine.config.getMessString("guild.demote.failure");
    public static final String demoteIsLeader = StreamLine.config.getMessString("guild.demote.is-leader");
    public static final String demoteConsole = StreamLine.config.getMessString("guild.demote.console");
    public static final String demoteTitle = StreamLine.config.getMessString("guild.demote.title");
    // List.
    public static final String listMain = StreamLine.config.getMessString("guild.list.main");
    public static final String listLeaderBulk = StreamLine.config.getMessString("guild.list.leaderbulk");
    public static final String listModBulkMain = StreamLine.config.getMessString("guild.list.moderatorbulk.main");
    public static final String listModBulkNotLast = StreamLine.config.getMessString("guild.list.moderatorbulk.moderators.not-last");
    public static final String listModBulkLast = StreamLine.config.getMessString("guild.list.moderatorbulk.moderators.last");
    public static final String listModBulkNone = StreamLine.config.getMessString("guild.list.moderatorbulk.moderators.if-none");
    public static final String listMemberBulkMain = StreamLine.config.getMessString("guild.list.memberbulk.main");
    public static final String listMemberBulkNotLast = StreamLine.config.getMessString("guild.list.memberbulk.members.not-last");
    public static final String listMemberBulkLast = StreamLine.config.getMessString("guild.list.memberbulk.members.last");
    public static final String listMemberBulkNone = StreamLine.config.getMessString("guild.list.memberbulk.members.if-none");
    // Open.
    public static final String openMembers = StreamLine.config.getMessString("guild.open.members");
    public static final String openSender = StreamLine.config.getMessString("guild.open.sender");
    public static final String openFailure = StreamLine.config.getMessString("guild.open.failure");
    public static final String openConsole = StreamLine.config.getMessString("guild.open.console");
    public static final String openTitle = StreamLine.config.getMessString("guild.open.title");
    // Close.
    public static final String closeMembers = StreamLine.config.getMessString("guild.close.members");
    public static final String closeSender = StreamLine.config.getMessString("guild.close.sender");
    public static final String closeFailure = StreamLine.config.getMessString("guild.close.failure");
    public static final String closeConsole = StreamLine.config.getMessString("guild.close.console");
    public static final String closeTitle = StreamLine.config.getMessString("guild.close.title");
    // Disband.
    public static final String disbandMembers = StreamLine.config.getMessString("guild.disband.members");
    public static final String disbandLeader = StreamLine.config.getMessString("guild.disband.leader");
    public static final String disbandConsole = StreamLine.config.getMessString("guild.disband.console");
    public static final String disbandTitle = StreamLine.config.getMessString("guild.disband.title");
    // Accept.
    public static final String acceptUser = StreamLine.config.getMessString("guild.accept.user");
    public static final String acceptLeader = StreamLine.config.getMessString("guild.accept.leader");
    public static final String acceptMembers = StreamLine.config.getMessString("guild.accept.members");
    public static final String acceptFailure = StreamLine.config.getMessString("guild.accept.failure");
    public static final String acceptConsole = StreamLine.config.getMessString("guild.accept.console");
    public static final String acceptTitle = StreamLine.config.getMessString("guild.accept.title");
    // Deny.
    public static final String denyUser = StreamLine.config.getMessString("guild.deny.user");
    public static final String denyLeader = StreamLine.config.getMessString("guild.deny.leader");
    public static final String denyMembers = StreamLine.config.getMessString("guild.deny.members");
    public static final String denyFailure = StreamLine.config.getMessString("guild.deny.failure");
    public static final String denyConsole = StreamLine.config.getMessString("guild.deny.console");
    public static final String denyTitle = StreamLine.config.getMessString("guild.deny.title");
    // Invite.
    public static final String inviteUser = StreamLine.config.getMessString("guild.invite.user");
    public static final String inviteLeader = StreamLine.config.getMessString("guild.invite.leader");
    public static final String inviteMembers = StreamLine.config.getMessString("guild.invite.members");
    public static final String inviteFailure = StreamLine.config.getMessString("guild.invite.failure");
    public static final String inviteNonSelf = StreamLine.config.getMessString("guild.invite.non-self");
    public static final String inviteConsole = StreamLine.config.getMessString("guild.invite.console");
    public static final String inviteTitle = StreamLine.config.getMessString("guild.invite.title");
    // Kick.
    public static final String kickUser = StreamLine.config.getMessString("guild.kick.user");
    public static final String kickSender = StreamLine.config.getMessString("guild.kick.sender");
    public static final String kickMembers = StreamLine.config.getMessString("guild.kick.members");
    public static final String kickFailure = StreamLine.config.getMessString("guild.kick.failure");
    public static final String kickMod = StreamLine.config.getMessString("guild.kick.mod");
    public static final String kickSelf = StreamLine.config.getMessString("guild.kick.self");
    public static final String kickConsole = StreamLine.config.getMessString("guild.kick.console");
    public static final String kickTitle = StreamLine.config.getMessString("guild.kick.title");
    // Mute.
    public static final String muteSender = StreamLine.config.getMessString("guild.mute.mute.user");
    public static final String muteMembers = StreamLine.config.getMessString("guild.mute.mute.members");
    public static final String unmuteSender = StreamLine.config.getMessString("guild.mute.unmute.user");
    public static final String unmuteMembers = StreamLine.config.getMessString("guild.mute.unmute.members");
    public static final String muteConsole = StreamLine.config.getMessString("guild.mute.console");
    public static final String muteTitle = StreamLine.config.getMessString("guild.mute.title");
    public static final String muteToggleMuted = StreamLine.config.getMessString("guild.mute.toggle.muted");
    public static final String muteToggleUnMuted = StreamLine.config.getMessString("guild.mute.toggle.unmuted");
    // Warp.
    public static final String warpSender = StreamLine.config.getMessString("guild.warp.sender");
    public static final String warpMembers = StreamLine.config.getMessString("guild.warp.members");
    public static final String warpConsole = StreamLine.config.getMessString("guild.warp.console");
    public static final String warpTitle = StreamLine.config.getMessString("guild.warp.title");
    // Info.
    public static final String info = StreamLine.config.getMessString("guild.info");
    // Rename.
    public static final String renameSender = StreamLine.config.getMessString("guild.rename.sender");
    public static final String renameMembers = StreamLine.config.getMessString("guild.rename.members");
    public static final String renameNonEmpty = StreamLine.config.getMessString("guild.rename.non-empty");
    public static final String renameConsole = StreamLine.config.getMessString("guild.rename.console");
    public static final String renameTitle = StreamLine.config.getMessString("guild.rename.title");
}
