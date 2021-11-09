package net.plasmere.streamline.utils;

import net.dv8tion.jda.api.entities.Category;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.query.QueryOptions;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.DiscordBotConfUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.SavableParty;
import net.plasmere.streamline.objects.SavableParty;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.plasmere.streamline.objects.chats.ChatsHandler;
import net.plasmere.streamline.objects.enums.CategoryType;
import net.plasmere.streamline.objects.enums.MessageServerType;
import net.plasmere.streamline.objects.savable.users.SavableConsole;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.objects.savable.users.SavableUser;
import net.plasmere.streamline.objects.messaging.DiscordMessage;
import net.plasmere.streamline.objects.savable.users.SavableUser;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PartyUtils {
    private static final List<SavableParty> parties = new ArrayList<>();

    public static List<SavableParty> getParties() {
        List<SavableParty> rem = new ArrayList<>();

        for (SavableParty g : parties) {
            if (g.leaderUUID == null) rem.add(g);
        }

        for (SavableParty g : rem) {
            parties.remove(g);
        }

        return parties;
    }
    // SavableParty , Invites
    public static Map<SavableParty, List<SavableUser>> invites = new HashMap<>();

    public static int allPartiesCount() {
        File[] files = StreamLine.getInstance().getPDir().listFiles();

        if (files == null) return 0;

        int amount = 0;
        for (File file : files) {
            try {
                if (! file.getName().endsWith(".properties")) continue;

                SavableParty party = getOrGetParty(file.getName().replace(".properties", ""));

                if (party == null) continue;
                if (party.leaderUUID == null) continue;

                amount ++;
            } catch (Exception e) {
                // do nothing
            }
        }

        return amount;
    }

    public static void removeInvite(SavableParty party, SavableUser player) {
        invites.get(party).remove(player);
    }

    public static SavableParty getParty(SavableUser stat) {
        try {
            for (SavableParty party : parties) {
                if (party.hasMember(stat)) {
                    return party;
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static SavableParty getOrGetParty(String uuid){
        SavableParty party = getParty(uuid);

        if (party == null) {
            if (existsByUUID(uuid)) {
                try {
                    party = new SavableParty(uuid, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return party;
    }

    public static void loadAllMembersInAllParties(){
        for (SavableParty party : parties) {
            party.loadAllMembers();
        }
    }

    public static boolean hasOnlineMemberAlready(SavableUser stat){
        List<SavableUser> users = new ArrayList<>(PlayerUtils.getStats());

        for (SavableUser user : users) {
            if (user.uuid.equals(stat.uuid)) continue;
            if (user.party.equals(stat.party)) return true;
        }

        return false;
    }

    public static SavableParty getParty(String uuid) {
        try {
            for (SavableParty party : parties) {
                if (party.hasMember(PlayerUtils.getOrGetSavableUser(uuid))) {
                    return party;
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean hasParty(SavableUser player) {
        for (SavableParty party : parties) {
            if (party.hasMember(player)) return true;
        }
        return false;
    }

    public static boolean existsByUUID(String uuid){
        File file = new File(StreamLine.getInstance().getPDir(), uuid + ".properties");

        return file.exists();
    }

    public static boolean exists(String username){
        File file = new File(StreamLine.getInstance().getPDir(), Objects.requireNonNull(PlayerUtils.getOrCreatePlayerStat(username)).party + ".properties");

        return file.exists();
    }

    public static boolean isParty(SavableParty party){
        return parties.contains(party);
    }

    public static boolean pHasParty(SavableUser player) {
        if (!existsByUUID(player.uuid)) return false;

        SavableParty party;
        try {
            party = new SavableParty(player.party, false);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (NullPointerException e) {
            if (ConfigUtils.debug()) {
                MessagingUtils.logWarning("SavablePlayer's party could not be found... Adding now!");
            }

            player.updateKey("party", player.uuid);
            return true;
        }

        if (party.leaderUUID == null) {
            return false;
        }

        return true;
    }

    public static boolean checkPlayer(SavableParty party, SavableUser player, SavableUser sender){
        if (! isParty(party) || party == null) {
            MessagingUtils.sendBUserMessage(sender.findSender(), noPartyFound);
            return false;
        }

        if (! party.hasMember(sender)) {
            MessagingUtils.sendBUserMessage(sender.findSender(), notInParty);
            return false;
        }

        if (hasParty(player)) {
            MessagingUtils.sendBUserMessage(sender.findSender(), alreadyHasOneOthers);
            return false;
        }

        return true;
    }

    public static void openPartySized(SavableUser sender, int size) {
        ProxiedPlayer p = PlayerUtils.getPPlayerByUUID(sender.uuid);

        if (p == null) return;

        try {
            SavableParty party = getParty(sender);

            if (! isParty(party) || party == null) {
                MessagingUtils.sendBUserMessage(p, noPartyFound);
                return;
            }

            if (! party.hasMember(sender)) {
                MessagingUtils.sendBUserMessage(p, notInParty);
                return;
            }

            if (!party.hasModPerms(sender)) {
                MessagingUtils.sendBUserMessage(p, noPermission);
                return;
            }

            if (party.isPublic) {
                MessagingUtils.sendBPUserMessage(party, p, p, openFailure
                );
            } else {
                party.setPublic(true);
                party.setMaxSize(size);

                for (SavableUser pl : party.totalMembers) {
                    if (! pl.online) continue;

                    ProxiedPlayer member = PlayerUtils.getPPlayerByUUID(pl.uuid);

                    if (member == null) continue;

                    if (member.getUniqueId().toString().equals(party.leader.uuid)) {
                        MessagingUtils.sendBPUserMessage(party, p, member, openSender
                        );
                    } else {
                        MessagingUtils.sendBPUserMessage(party, p, member, openMembers
                        );
                    }
                }
            }

            if (ConfigUtils.moduleDEnabled()) {
                if (ConfigUtils.partyToDiscord() && ConfigUtils.partyConsoleOpens()) {
                    MessagingUtils.sendDiscordPEBMessage(party, new DiscordMessage(p, openTitle,
                            openConsole
                            , DiscordBotConfUtils.textChannelParties()));
                }
            }
        }  catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createPartySized(SavableUser player, int size) {
        ProxiedPlayer p = PlayerUtils.getPPlayerByUUID(player.uuid);

        if (p == null) return;

        if (getParty(player) != null) {
            MessagingUtils.sendBUserMessage(p, alreadyMade);
            return;
        }

        try {
            int maxSize = getMaxSize(player);

            if (size > maxSize) {
                MessagingUtils.sendBUserMessage(p, tooBig);
                return;
            }

            SavableParty party = new SavableParty(player.uuid, size);

            parties.add(party);

            MessagingUtils.sendBPUserMessage(party, p, p, create);

            // if (ConfigUtils.debug()) MessagingUtils.logInfo("OPEN : totalMembers --> "  + party.totalMembers.size());

            if (ConfigUtils.moduleDEnabled()) {
                if (ConfigUtils.partyToDiscord() && ConfigUtils.partyConsoleCreates()) {
                    MessagingUtils.sendDiscordPEBMessage(party, new DiscordMessage(p, createTitle,
                            createConsole
                            , DiscordBotConfUtils.textChannelParties()));
                }
            }

            if (ConfigUtils.moduleDEnabled()) {
                if (ConfigUtils.partyToDiscord() && ConfigUtils.partyConsoleOpens()) {
                    MessagingUtils.sendDiscordPEBMessage(party, new DiscordMessage(p, openTitle,
                            openConsole
                            , DiscordBotConfUtils.textChannelParties()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createParty(SavableUser sender) {
        SavableParty g = getParty(sender);

        if (g != null) {
            MessagingUtils.sendBUserMessage(sender.findSender(), alreadyMade);
            return;
        }

        try {
            //MessagingUtils.logInfo("createParty SavablePlayer.uuid > " + sender.uuid);
            SavableParty party = new SavableParty(sender.uuid, getMaxSize(sender));

            addParty(party);

            MessagingUtils.sendBPUserMessage(party, sender.findSender(), sender.findSender(),  create);

            if (ConfigUtils.moduleDEnabled()) {
                if (ConfigUtils.partyToDiscord() && ConfigUtils.partyConsoleCreates()) {
                    MessagingUtils.sendDiscordPEBMessage(party, new DiscordMessage(sender.findSender(), createTitle,
                            createConsole
                            , DiscordBotConfUtils.textChannelParties()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addParty(SavableParty party){
        SavableParty g;

        try {
            g = getParty(party.leaderUUID);
        } catch (Exception e) {
            return;
            // Do nothing.
        }

        if (g != null) return;

        try {
            if (parties.size() > 0) {
                List<SavableParty> rem = new ArrayList<>();

                for (SavableParty gu : parties) {
                    String s = gu.leaderUUID;

                    if (s == null) {
                        rem.add(gu);
                        continue;
                    }

                    if (s.equals(party.leaderUUID)) {
                        rem.add(gu);
                    }
                }

                for (SavableParty gd : rem) {
                    parties.remove(gd);
                }
            }

            parties.add(party);
        } catch (Exception e){
            MessagingUtils.logInfo("Error adding party...");
            e.printStackTrace();
        }
    }

    public static boolean hasLeader(String leader){
        List<SavableParty> toRem = new ArrayList<>();

        boolean hasLeader = false;

        for (SavableParty party : parties){
            if (party.leaderUUID == null) {
                toRem.add(party);
                continue;
            }
            if (party.leaderUUID.equals(leader)) hasLeader = true;
        }

        for (SavableParty party : toRem) {
            try {
                party.dispose();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            removeParty(party);
        }

        return hasLeader;
    }

    public static void removeParty(SavableParty party){
        try {
            party.saveInfo();
        } catch (IOException e) {
            e.printStackTrace();
        }
        parties.remove(party);
    }

    public static void sendInvite(SavableUser to, SavableUser from) {
        try {
            SavableParty party = getParty(from);

            if (! checkPlayer(party, to, from)) return;

            if (to.equals(from)) {
                MessagingUtils.sendBUserMessage(from.findSender(), inviteNonSelf);
                return;
            }

            if (! party.hasModPerms(from.uuid)) {
                MessagingUtils.sendBUserMessage(from.findSender(), noPermission);
                return;
            }

            if (isParty(getParty(to))) {
                MessagingUtils.sendBUserMessage(from.findSender(), alreadyHasOneOthers);
                return;
            }

            if (party.invites.contains(to)) {
                MessagingUtils.sendBUserMessage(from.findSender(), inviteFailure);
                return;
            }

            if (to instanceof SavablePlayer && ((SavablePlayer) to).online) {
                MessagingUtils.sendBPUserMessage(party, from.findSender(), to.findSender(), TextUtils.replaceAllPlayerBungee(inviteUser, to)
                );
            }

            for (SavableUser pl : party.totalMembers) {
                if (pl.uuid.equals(party.leaderUUID)) {
                    MessagingUtils.sendBPUserMessage(party, from.findSender(), pl.findSender(), TextUtils.replaceAllPlayerBungee(inviteLeader, to)
                    );
                } else {
                    MessagingUtils.sendBPUserMessage(party, from.findSender(), pl.findSender(), TextUtils.replaceAllPlayerBungee(inviteMembers, to)
                    );
                }
            }

            party.addInvite(to);
            invites.remove(party);
            invites.put(party, party.invites);

            if (ConfigUtils.moduleDEnabled()) {
                if (ConfigUtils.partyToDiscord() && ConfigUtils.partyConsoleInvites()) {
                    MessagingUtils.sendDiscordPEBMessage(party, new DiscordMessage(from.findSender(), inviteTitle,
                            TextUtils.replaceAllPlayerDiscord(inviteConsole, to)
                            , DiscordBotConfUtils.textChannelParties()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void acceptInvite(SavableUser accepter, SavableUser from) {
        try {
            SavableParty party = getParty(from);

            if (!isParty(party) || party == null) {
                MessagingUtils.sendBUserMessage(accepter.findSender(), acceptFailure);
                return;
            }

            if (! party.hasMember(from)) {
                MessagingUtils.sendBUserMessage(accepter.findSender(), otherNotInParty);
                return;
            }

            if (isParty(getParty(accepter))) {
                MessagingUtils.sendBUserMessage(accepter.findSender(), alreadyHasOneSelf);
                return;
            }

            if (! invites.get(party).contains(accepter)) {
                MessagingUtils.sendBUserMessage(accepter.findSender(), acceptFailure);
                return;
            }

            if (party.invites.contains(accepter)) {
                if (party.getSize() >= party.maxSize) {
                    MessagingUtils.sendBPUserMessage(party, accepter.findSender(), accepter.findSender(), notEnoughSpace);
                    return;
                }

                MessagingUtils.sendBPUserMessage(party, accepter.findSender(), accepter.findSender(), TextUtils.replaceAllPlayerBungee(acceptUser, accepter)
                        .replace("%from_formatted%", PlayerUtils.getJustDisplayBungee(from))
                        .replace("%from_display%", PlayerUtils.getOffOnDisplayBungee(from))
                        .replace("%from_normal%", PlayerUtils.getOffOnRegBungee(from))
                        .replace("%from_absolute%", PlayerUtils.getAbsoluteBungee(from))
                );

                for (SavableUser pl : party.totalMembers){
                    if (pl.uuid.equals(party.leaderUUID)){
                        MessagingUtils.sendBPUserMessage(party, accepter.findSender(), pl.findSender(), TextUtils.replaceAllPlayerBungee(acceptLeader, accepter)
                                .replace("%from_formatted%", PlayerUtils.getJustDisplayBungee(from))
                                .replace("%from_display%", PlayerUtils.getOffOnDisplayBungee(from))
                                .replace("%from_normal%", PlayerUtils.getOffOnRegBungee(from))
                                .replace("%from_absolute%", PlayerUtils.getAbsoluteBungee(from))
                        );
                    } else {
                        MessagingUtils.sendBPUserMessage(party, accepter.findSender(), pl.findSender(), TextUtils.replaceAllPlayerBungee(acceptMembers, accepter)
                                .replace("%from_formatted%", PlayerUtils.getJustDisplayBungee(from))
                                .replace("%from_display%", PlayerUtils.getOffOnDisplayBungee(from))
                                .replace("%from_normal%", PlayerUtils.getOffOnRegBungee(from))
                                .replace("%from_absolute%", PlayerUtils.getAbsoluteBungee(from))
                        );
                    }
                }

                party.addMember(accepter);
                party.remFromInvites(from, accepter);

                if (ConfigUtils.moduleDEnabled()) {
                    if (ConfigUtils.partyToDiscord() && ConfigUtils.partyConsoleJoins()) {
                        MessagingUtils.sendDiscordPEBMessage(party, new DiscordMessage(accepter.findSender(), joinsTitle,
                                TextUtils.replaceAllPlayerDiscord(joinsConsole, accepter)
                                        .replace("%from_formatted%", PlayerUtils.getJustDisplayDiscord(from))
                                        .replace("%from_display%", PlayerUtils.getOffOnDisplayDiscord(from))
                                        .replace("%from_normal%", PlayerUtils.getOffOnRegDiscord(from))
                                        .replace("%from_absolute%", PlayerUtils.getAbsoluteDiscord(from))
                                , DiscordBotConfUtils.textChannelParties()));
                    }

                    if (ConfigUtils.partyToDiscord() && ConfigUtils.partyConsoleAccepts()) {
                        MessagingUtils.sendDiscordPEBMessage(party, new DiscordMessage(accepter.findSender(), acceptTitle,
                                TextUtils.replaceAllPlayerDiscord(acceptConsole, accepter)
                                        .replace("%from_formatted%", PlayerUtils.getJustDisplayDiscord(from))
                                        .replace("%from_display%", PlayerUtils.getOffOnDisplayDiscord(from))
                                        .replace("%from_normal%", PlayerUtils.getOffOnRegDiscord(from))
                                        .replace("%from_absolute%", PlayerUtils.getAbsoluteDiscord(from))
                                , DiscordBotConfUtils.textChannelParties()));
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
            SavableParty party = getParty(from);

            if (!isParty(party) || party == null) {
                MessagingUtils.sendBUserMessage(denier.findSender(), denyFailure);
                return;
            }

            if (! party.hasMember(from)) {
                MessagingUtils.sendBUserMessage(denier.findSender(), otherNotInParty);
                return;
            }

            if (! invites.get(party).contains(denier)) {
                MessagingUtils.sendBUserMessage(denier.findSender(), denyFailure);
                return;
            }

            if (party.invites.contains(denier)) {
                MessagingUtils.sendBPUserMessage(party, denier.findSender(), denier.findSender(), TextUtils.replaceAllPlayerBungee(denyUser, denier)
                        .replace("%from_formatted%", PlayerUtils.getJustDisplayBungee(from))
                        .replace("%from_display%", PlayerUtils.getOffOnDisplayBungee(from))
                        .replace("%from_normal%", PlayerUtils.getOffOnRegBungee(from))
                        .replace("%from_absolute%", PlayerUtils.getAbsoluteBungee(from))
                );

                for (SavableUser pl : party.totalMembers) {
                    if (pl.uuid.equals(party.leaderUUID)) {
                        MessagingUtils.sendBPUserMessage(party, denier.findSender(), pl.findSender(), TextUtils.replaceAllPlayerBungee(denyLeader, denier)
                                .replace("%from_formatted%", PlayerUtils.getJustDisplayBungee(from))
                                .replace("%from_display%", PlayerUtils.getOffOnDisplayBungee(from))
                                .replace("%from_normal%", PlayerUtils.getOffOnRegBungee(from))
                                .replace("%from_absolute%", PlayerUtils.getAbsoluteBungee(from))
                        );
                    } else {
                        MessagingUtils.sendBPUserMessage(party, denier.findSender(), pl.findSender(), TextUtils.replaceAllPlayerBungee(denyMembers, denier)
                                .replace("%from_formatted%", PlayerUtils.getJustDisplayBungee(from))
                                .replace("%from_display%", PlayerUtils.getOffOnDisplayBungee(from))
                                .replace("%from_normal%", PlayerUtils.getOffOnRegBungee(from))
                                .replace("%from_absolute%", PlayerUtils.getAbsoluteBungee(from))
                        );
                    }
                }

                party.remFromInvites(from, denier);

                if (ConfigUtils.moduleDEnabled()) {
                    if (ConfigUtils.partyToDiscord() && ConfigUtils.partyConsoleDenies()) {
                        MessagingUtils.sendDiscordPEBMessage(party, new DiscordMessage(denier.findSender(), denyTitle,
                                TextUtils.replaceAllPlayerDiscord(denyConsole, denier)
                                        .replace("%from_formatted%", PlayerUtils.getJustDisplayDiscord(from))
                                        .replace("%from_display%", PlayerUtils.getOffOnDisplayDiscord(from))
                                        .replace("%from_normal%", PlayerUtils.getOffOnRegDiscord(from))
                                        .replace("%from_absolute%", PlayerUtils.getAbsoluteDiscord(from))
                                , DiscordBotConfUtils.textChannelParties()));
                    }
                }
            } else {
                MessagingUtils.sendBUserMessage(denier.findSender(), denyFailure);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void warpParty(SavableUser sender){
        SavableParty party = getParty(sender);

        if (!isParty(party) || party == null) {
            MessagingUtils.sendBUserMessage(sender.findSender(), noPartyFound);
            return;
        }

        if (sender instanceof SavableConsole) {
            MessagingUtils.sendBUserMessage(sender.findSender(), MessageConfUtils.onlyPlayers());
            return;
        }

        if (! party.hasMember(sender)) {
            MessagingUtils.sendBUserMessage(sender.findSender(), notInParty);
            return;
        }

        if (! party.hasModPerms(sender)) {
            MessagingUtils.sendBUserMessage(sender.findSender(), noPermission);
            return;
        }

        if (sender instanceof SavablePlayer && sender.online) {
            try {
                for (SavableUser player : new ArrayList<>(party.totalMembers)) {
                    if (!player.online) continue;

                    ProxiedPlayer m = PlayerUtils.getPPlayerByUUID(player.uuid);

                    if (m == null) continue;

                    if (player.equals(sender)) {
                        MessagingUtils.sendBPUserMessage(party, player.findSender(), m, warpSender);
                    } else {
                        MessagingUtils.sendBPUserMessage(party, player.findSender(), m, warpMembers);
                    }

                    m.connect(((SavablePlayer) sender).getServer().getInfo());
                }
            } catch (ConcurrentModificationException e) {
                e.printStackTrace();
            }
        }

        if (ConfigUtils.partyToDiscord() && ConfigUtils.partyConsoleWarps()) {
            MessagingUtils.sendDiscordPEBMessage(party, new DiscordMessage(sender.findSender(), warpTitle,
                    warpConsole
                    , DiscordBotConfUtils.textChannelParties()));
        }
    }

    public static void muteParty(SavableUser sender){
        SavableParty party = getParty(sender);

        if (!isParty(party) || party == null) {
            MessagingUtils.sendBUserMessage(sender.findSender(), noPartyFound);
            return;
        }

        if (! party.hasMember(sender)) {
            MessagingUtils.sendBUserMessage(sender.findSender(), notInParty);
            return;
        }

        if (! party.hasModPerms(sender)) {
            MessagingUtils.sendBPUserMessage(party, sender.findSender(), sender.findSender(), noPermission);
            return;
        }

        if (party.isMuted) {
            for (SavableUser pl : party.totalMembers) {
                if (pl.equals(sender)){
                    MessagingUtils.sendBPUserMessage(party, sender.findSender(), pl.findSender(), unmuteSender);
                } else {
                    MessagingUtils.sendBPUserMessage(party, sender.findSender(), pl.findSender(), unmuteMembers);
                }
            }

        } else {
            for (SavableUser pl : party.totalMembers) {
                if (pl.equals(sender)){
                    MessagingUtils.sendBPUserMessage(party, sender.findSender(), pl.findSender(), muteSender);
                } else {
                    MessagingUtils.sendBPUserMessage(party, sender.findSender(), pl.findSender(), muteMembers);
                }
            }

        }
        party.toggleMute();

        if (ConfigUtils.moduleDEnabled()) {
            if (ConfigUtils.partyToDiscord() && ConfigUtils.partyConsoleMutes()) {
                MessagingUtils.sendDiscordPEBMessage(party, new DiscordMessage(sender.findSender(), muteTitle,
                        muteConsole
                        , DiscordBotConfUtils.textChannelParties()));
            }
        }
    }

    public static void kickMember(SavableUser sender, SavableUser player) {
        SavableParty party = getParty(sender);

        if (!isParty(party) || party == null) {
            MessagingUtils.sendBUserMessage(sender.findSender(), kickFailure);
            return;
        }

        if (!party.hasMember(sender)) {
            MessagingUtils.sendBUserMessage(sender.findSender(), notInParty);
            return;
        }

        if (!party.hasMember(player)) {
            MessagingUtils.sendBUserMessage(sender.findSender(), otherNotInParty);
            return;
        }

        if (!party.hasModPerms(sender)) {
            MessagingUtils.sendBPUserMessage(party, sender.findSender(), sender.findSender(),  noPermission);
            return;
        }

        if (party.hasModPerms(player)) {
            MessagingUtils.sendBPUserMessage(party, sender.findSender(), sender.findSender(),  kickMod);
            return;
        }

        try {
            if (sender.equals(player)) {
                MessagingUtils.sendBPUserMessage(party, sender.findSender(), sender.findSender(),  kickSelf);
            } else if (player.equals(PlayerUtils.getOrCreateSUByUUID(party.leaderUUID))) {
                MessagingUtils.sendBPUserMessage(party, sender.findSender(), sender.findSender(),  noPermission);
            } else {
                for (SavableUser pl : party.totalMembers) {
                    if (pl.equals(sender)) {
                        MessagingUtils.sendBPUserMessage(party, sender.findSender(), pl.findSender(), TextUtils.replaceAllPlayerBungee(kickSender, player)
                        );
                    } else if (! pl.uuid.equals(party.leaderUUID)) {
                        MessagingUtils.sendBPUserMessage(party, sender.findSender(), pl.findSender(), TextUtils.replaceAllPlayerBungee(kickUser, player)
                        );
                    } else {
                        MessagingUtils.sendBPUserMessage(party, sender.findSender(), pl.findSender(), TextUtils.replaceAllPlayerBungee(kickMembers, player)
                        );
                    }
                }

                party.removeMemberFromParty(player);
            }

            if (ConfigUtils.moduleDEnabled()) {
                if (ConfigUtils.partyToDiscord() && ConfigUtils.partyConsoleKicks()) {
                    MessagingUtils.sendDiscordPEBMessage(party, new DiscordMessage(sender.findSender(), kickTitle,
                            TextUtils.replaceAllPlayerDiscord(kickConsole, player)
                            , DiscordBotConfUtils.textChannelParties()));
                }
            }
        } catch (Exception e) {
            MessagingUtils.sendBPUserMessage(party, sender.findSender(), sender.findSender(), MessageConfUtils.bungeeCommandErrorUnd());
            e.printStackTrace();
        }
    }

    public static void info(SavableUser sender){
        SavableParty party = getParty(sender);

        if (!isParty(party) || party == null) {
            MessagingUtils.sendBUserMessage(sender.findSender(), noPartyFound);
            return;
        }

        if (! party.hasMember(sender)) {
            MessagingUtils.sendBUserMessage(sender.findSender(), notInParty);
            return;
        }

        MessagingUtils.sendBPUserMessage(party, sender.findSender(), sender.findSender(),  info);
    }

    public static void disband(SavableUser sender) {
        try {
            SavableParty party = getParty(sender);

            if (!isParty(party) || party == null) {
                MessagingUtils.sendBUserMessage(sender.findSender(), noPartyFound);
                return;
            }

            if (! party.hasMember(sender)) {
                MessagingUtils.sendBUserMessage(sender.findSender(), notInParty);
                return;
            }

            if (! party.leaderUUID.equals(sender.uuid)) {
                MessagingUtils.sendBUserMessage(sender.findSender(), noPermission);
                return;
            }

            for (SavableUser pl : party.totalMembers) {
                if (! pl.online) continue;

                if (! pl.uuid.equals(party.leaderUUID)) {
                    MessagingUtils.sendBPUserMessage(party, sender.findSender(), pl.findSender(), disbandMembers
                    );
                } else {
                    MessagingUtils.sendBPUserMessage(party, sender.findSender(), pl.findSender(), disbandLeader
                    );
                }

            }

            if (ConfigUtils.moduleDEnabled()) {
                if (ConfigUtils.partyToDiscord() && ConfigUtils.partyConsoleDisbands()) {
                    MessagingUtils.sendDiscordPEBMessage(party, new DiscordMessage(sender.findSender(), disbandTitle,
                            disbandConsole
                            , DiscordBotConfUtils.textChannelParties()));
                }
            }

            parties.remove(party);
            party.disband();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openParty(SavableUser sender) {
        try {
            SavableParty party = getParty(sender);

            if (!isParty(party) || party == null) {
                MessagingUtils.sendBUserMessage(sender.findSender(), noPartyFound);
                return;
            }

            if (! party.hasMember(sender)) {
                MessagingUtils.sendBUserMessage(sender.findSender(), notInParty);
                return;
            }

            if (! party.hasModPerms(sender)) {
                MessagingUtils.sendBUserMessage(sender.findSender(), noPermission);
                return;
            }

            if (party.isPublic) {
                MessagingUtils.sendBPUserMessage(party, sender.findSender(), sender.findSender(),  openFailure
                );
            } else {
                party.setPublic(true);

                for (SavableUser pl : party.totalMembers) {
                    if (pl.equals(sender)) {
                        MessagingUtils.sendBPUserMessage(party, sender.findSender(), pl.findSender(), openSender
                        );
                    } else {
                        MessagingUtils.sendBPUserMessage(party, sender.findSender(), pl.findSender(), openMembers
                        );
                    }
                }

                if (ConfigUtils.moduleDEnabled()) {
                    if (ConfigUtils.partyToDiscord() && ConfigUtils.partyConsoleOpens()) {
                        MessagingUtils.sendDiscordPEBMessage(party, new DiscordMessage(sender.findSender(), openTitle,
                                openConsole
                                , DiscordBotConfUtils.textChannelParties()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeParty(SavableUser sender) {
        try {
            SavableParty party = getParty(sender);

            if (!isParty(party) || party == null) {
                MessagingUtils.sendBUserMessage(sender.findSender(), noPartyFound);
                return;
            }

            if (! party.hasMember(sender)) {
                MessagingUtils.sendBUserMessage(sender.findSender(), notInParty);
                return;
            }

            if (!party.hasModPerms(sender)) {
                MessagingUtils.sendBUserMessage(sender.findSender(), noPermission);
                return;
            }

            if (!party.isPublic) {
                MessagingUtils.sendBPUserMessage(party, sender.findSender(), sender.findSender(),  closeFailure
                );
            } else {
                party.setPublic(false);

                for (SavableUser pl : party.totalMembers) {
                    if (pl.equals(sender)) {
                        MessagingUtils.sendBPUserMessage(party, sender.findSender(), pl.findSender(), closeSender
                        );
                    } else {
                        MessagingUtils.sendBPUserMessage(party, sender.findSender(), pl.findSender(), closeMembers
                        );
                    }
                }

                if (ConfigUtils.moduleDEnabled()) {
                    if (ConfigUtils.partyToDiscord() && ConfigUtils.partyConsoleCloses()) {
                        MessagingUtils.sendDiscordPEBMessage(party, new DiscordMessage(sender.findSender(), closeTitle,
                                closeConsole
                                , DiscordBotConfUtils.textChannelParties()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void listParty(SavableUser sender) {
        try {
            SavableParty party = getParty(sender);

            if (!isParty(party) || party == null) {
                MessagingUtils.sendBUserMessage(sender.findSender(), noPartyFound);
                return;
            }

            if (! party.hasMember(sender)) {
                MessagingUtils.sendBUserMessage(sender.findSender(), notInParty);
                return;
            }

            String leaderBulk = listLeaderBulk;
            String moderatorBulk = listModBulkMain
                    .replace("%moderators%", Objects.requireNonNull(moderators(party)));
            String memberBulk = listMemberBulkMain
                    .replace("%members%", Objects.requireNonNull(members(party)));

            MessagingUtils.sendBPUserMessage(party, sender.findSender(), sender.findSender(),  listMain
                    .replace("%leaderbulk%", leaderBulk)
                    .replace("%moderatorbulk%", moderatorBulk)
                    .replace("%memberbulk%", memberBulk)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String moderators(SavableParty party) {
        try {
            if (! (party.moderators.size() > 0)) {
                return listModBulkNone;
            }

            StringBuilder mods = new StringBuilder();

            int i = 1;

            for (SavableUser m : party.moderators) {
                if (i < party.moderators.size()) {
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

    private static String members(SavableParty party) {
        try {
            if (! (party.members.size() > 0)) {
                return listMemberBulkNone;
            }

            StringBuilder mems = new StringBuilder();

            int i = 1;

            for (SavableUser m : party.members) {
                if (i <party.moderators.size()) {
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
            SavableParty party = getParty(sender);

            if (!isParty(party) || party == null) {
                MessagingUtils.sendBUserMessage(sender.findSender(), noPartyFound);
                return;
            }

            if (! party.hasMember(sender)) {
                MessagingUtils.sendBUserMessage(sender.findSender(), notInParty);
                return;
            }

            if (! party.hasMember(player)) {
                MessagingUtils.sendBUserMessage(sender.findSender(), otherNotInParty);
                return;
            }

            if (!party.leaderUUID.equals(sender.uuid)) {
                MessagingUtils.sendBUserMessage(sender.findSender(), noPermission);
                return;
            }

            switch (party.getLevel(player)) {
                case LEADER:
                    MessagingUtils.sendBPUserMessage(party, sender.findSender(), sender.findSender(),  TextUtils.replaceAllPlayerBungee(promoteFailure, player)
                            .replace("%level%", TextUtils.replaceAllPlayerBungee(textLeader, player)
                            )
                    );
                    return;
                case MODERATOR:
                    party.replaceLeader(player);

                    for (SavableUser pl : party.totalMembers) {
                        if (pl.uuid.equals(party.leaderUUID)) {
                            MessagingUtils.sendBPUserMessage(party, sender.findSender(), pl.findSender(), TextUtils.replaceAllPlayerBungee(promoteLeader, player)
                                    .replace("%level%", TextUtils.replaceAllPlayerBungee(textLeader, player)
                                    )
                            );
                        } else if (pl.equals(player)) {
                            MessagingUtils.sendBPUserMessage(party, sender.findSender(), pl.findSender(), TextUtils.replaceAllPlayerBungee(promoteUser, player)
                                    .replace("%level%", TextUtils.replaceAllPlayerBungee(textLeader, player)
                                    )
                            );
                        } else {
                            MessagingUtils.sendBPUserMessage(party, sender.findSender(), pl.findSender(), TextUtils.replaceAllPlayerBungee(promoteMembers, player)
                                    .replace("%level%", TextUtils.replaceAllPlayerBungee(textLeader, player)
                                    )
                            );
                        }
                    }
                    return;
                case MEMBER:
                default:
                    party.setModerator(player);

                    for (SavableUser pl : party.totalMembers) {
                        if (pl.uuid.equals(party.leaderUUID)) {
                            MessagingUtils.sendBPUserMessage(party, sender.findSender(), pl.findSender(), TextUtils.replaceAllPlayerBungee(promoteLeader, player)
                                    .replace("%level%", TextUtils.replaceAllPlayerBungee(textModerator, player)
                                    )
                            );
                        } else if (pl.equals(player)) {
                            MessagingUtils.sendBPUserMessage(party, sender.findSender(), pl.findSender(), TextUtils.replaceAllPlayerBungee(promoteUser, player)
                                    .replace("%level%", TextUtils.replaceAllPlayerBungee(textModerator, player)
                                    )
                            );
                        } else {
                            MessagingUtils.sendBPUserMessage(party, sender.findSender(), pl.findSender(), TextUtils.replaceAllPlayerBungee(promoteMembers, player)
                                    .replace("%level%", TextUtils.replaceAllPlayerBungee(textModerator, player)
                                    )
                            );
                        }
                    }
                    break;
            }

            if (ConfigUtils.moduleDEnabled()) {
                if (ConfigUtils.partyToDiscord() && ConfigUtils.partyConsolePromotes()) {
                    MessagingUtils.sendDiscordPEBMessage(party, new DiscordMessage(sender.findSender(), promoteTitle,
                            TextUtils.replaceAllPlayerDiscord(promoteConsole, player)
                            , DiscordBotConfUtils.textChannelParties()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void demotePlayer(SavableUser sender, SavableUser player) {
        try {
            SavableParty party = getParty(sender);

            if (!isParty(party) || party == null) {
                MessagingUtils.sendBUserMessage(sender.findSender(), noPartyFound);
                return;
            }

            if (! party.hasMember(sender)) {
                MessagingUtils.sendBUserMessage(sender.findSender(), notInParty);
                return;
            }

            if (! party.hasMember(player)) {
                MessagingUtils.sendBUserMessage(sender.findSender(), otherNotInParty);
                return;
            }

            if (!party.hasModPerms(sender)) {
                MessagingUtils.sendBUserMessage(sender.findSender(), noPermission);
                return;
            }

            switch (party.getLevel(player)) {
                case LEADER:
                    MessagingUtils.sendBPUserMessage(party, sender.findSender(), sender.findSender(),  TextUtils.replaceAllPlayerBungee(demoteIsLeader, player)
                            .replace("%level%", TextUtils.replaceAllPlayerBungee(textLeader, player)
                            )
                    );
                    return;
                case MODERATOR:
                    party.setMember(player);

                    for (SavableUser pl : party.totalMembers) {
                        if (pl.uuid.equals(party.leaderUUID)) {
                            MessagingUtils.sendBPUserMessage(party, sender.findSender(), pl.findSender(), TextUtils.replaceAllPlayerBungee(demoteLeader, player)
                                    .replace("%level%", TextUtils.replaceAllPlayerBungee(textMember, player)
                                    )
                            );
                        } else if (pl.equals(player)) {
                            MessagingUtils.sendBPUserMessage(party, sender.findSender(), pl.findSender(), TextUtils.replaceAllPlayerBungee(demoteUser, player)
                                    .replace("%level%", TextUtils.replaceAllPlayerBungee(textMember, player)
                                    )
                            );
                        } else {
                            MessagingUtils.sendBPUserMessage(party, sender.findSender(), pl.findSender(), TextUtils.replaceAllPlayerBungee(demoteMembers, player)
                                    .replace("%level%", TextUtils.replaceAllPlayerBungee(textMember, player)
                                    )
                            );
                        }
                    }
                    return;
                case MEMBER:
                default:
                    MessagingUtils.sendBPUserMessage(party, sender.findSender(), sender.findSender(),  TextUtils.replaceAllPlayerBungee(demoteFailure, player)
                            .replace("%level%", TextUtils.replaceAllPlayerBungee(textMember, player)
                            )
                    );
                    break;
            }

            if (ConfigUtils.moduleDEnabled()) {
                if (ConfigUtils.partyToDiscord() && ConfigUtils.partyConsoleDemotes()) {
                    MessagingUtils.sendDiscordPEBMessage(party, new DiscordMessage(sender.findSender(), demoteTitle,
                            TextUtils.replaceAllPlayerDiscord(demoteConsole, player)
                            , DiscordBotConfUtils.textChannelParties()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void joinParty(SavableUser sender, SavableUser from) {
        try {
            SavableParty party = getParty(from);

            if (!isParty(party) || party == null) {
                MessagingUtils.sendBUserMessage(sender.findSender(), noPartyFound);
                return;
            }

            if (! party.hasMember(from)) {
                MessagingUtils.sendBUserMessage(sender.findSender(), otherNotInParty);
                return;
            }

            if (isParty(getParty(sender))) {
                MessagingUtils.sendBUserMessage(sender.findSender(), alreadyHasOneSelf);
                return;
            }

            if (party.getSize() + 1 > party.maxSize) {
                MessagingUtils.sendBPUserMessage(party, sender.findSender(), sender.findSender(),  notEnoughSpace);
                return;
            }

            if (party.isPublic) {
                party.addMember(sender);

                for (SavableUser pl : party.totalMembers) {
                    if (pl.equals(sender)) {
                        MessagingUtils.sendBPUserMessage(party, sender.findSender(), pl.findSender(), joinUser
                        );
                    } else {
                        MessagingUtils.sendBPUserMessage(party, sender.findSender(), pl.findSender(), joinMembers
                        );
                    }
                }

                if (ConfigUtils.moduleDEnabled()) {
                    if (ConfigUtils.partyToDiscord() && ConfigUtils.partyConsoleJoins()) {
                        MessagingUtils.sendDiscordPEBMessage(party, new DiscordMessage(sender.findSender(), joinsTitle,
                                joinsConsole
                                , DiscordBotConfUtils.textChannelParties()));
                    }
                }
            } else {
                MessagingUtils.sendBPUserMessage(party, sender.findSender(), sender.findSender(),  joinFailure);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void leaveParty(SavableUser sender) {
        try {
            SavableParty party = getParty(sender);

            if (!isParty(party) || party == null) {
                MessagingUtils.sendBUserMessage(sender.findSender(), noPartyFound);
                return;
            }

            if (! party.hasMember(sender)) {
                MessagingUtils.sendBUserMessage(sender.findSender(), notInParty);
                return;
            }

            if (PlayerUtils.getOrCreateSUByUUID(party.leaderUUID).equals(sender)) {
                for (SavableUser pl : party.totalMembers) {
                    if (pl.equals(sender)) {
                        MessagingUtils.sendBPUserMessage(party, sender.findSender(), pl.findSender(), leaveUser);
                        MessagingUtils.sendBPUserMessage(party, sender.findSender(), pl.findSender(), disbandLeader);
                    } else {
                        MessagingUtils.sendBPUserMessage(party, sender.findSender(), pl.findSender(), leaveMembers);
                        MessagingUtils.sendBPUserMessage(party, sender.findSender(), pl.findSender(), disbandMembers);
                    }
                }

                parties.remove(party);
                party.dispose();
                return;
            }

            if (party.hasMember(sender)) {
                for (SavableUser pl : party.totalMembers) {
                    if (pl.equals(sender)) {
                        MessagingUtils.sendBPUserMessage(party, sender.findSender(), pl.findSender(), leaveUser
                        );
                    } else {
                        MessagingUtils.sendBPUserMessage(party, sender.findSender(), pl.findSender(), leaveMembers
                        );
                    }
                }

                party.removeMemberFromParty(sender);

                if (ConfigUtils.moduleDEnabled()) {
                    if (ConfigUtils.partyToDiscord() && ConfigUtils.partyConsoleLeaves()) {
                        MessagingUtils.sendDiscordPEBMessage(party, new DiscordMessage(sender.findSender(), leaveTitle,
                                leaveConsole
                                , DiscordBotConfUtils.textChannelParties()));
                    }
                }
            } else {
                MessagingUtils.sendBPUserMessage(party, sender.findSender(), sender.findSender(),  leaveFailure);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void sendChat(SavableUser sender, String msg) {
        try {
            SavableParty party = getParty(sender);

            if (! isParty(party) || party == null) {
                MessagingUtils.sendBUserMessage(sender.findSender(), noPartyFound);
                return;
            }

            if (! party.hasMember(sender)) {
                MessagingUtils.sendBUserMessage(sender.findSender(), notInParty);
                return;
            }

            if (party.isMuted && ! party.hasModPerms(sender)) {
                MessagingUtils.sendBPUserMessage(party, sender.findSender(), sender.findSender(),  chatMuted
                        .replace("%message%", msg)
                );
                return;
            }

            for (SavableUser pl : party.totalMembers) {
                MessagingUtils.sendBPUserMessage(party, sender.findSender(), pl.findSender(),
                        StreamLine.chatConfig.getPermissionedChatMessage(sender, ChatsHandler.getChat("party", party.leaderUUID), "party", MessageServerType.BUNGEE)
                                .replace("%message%", msg)
                );
            }

            if (ConfigUtils.moduleDEnabled()) {
                if (ConfigUtils.partyToDiscord() && ConfigUtils.partyConsoleChats()) {
                    MessagingUtils.sendDiscordPEBMessage(party, new DiscordMessage(sender.findSender(), chatTitle,
                            chatConsole
                                    .replace("%message%", msg)
                            , DiscordBotConfUtils.textChannelParties()));
                }
            }

            if (ConfigUtils.moduleDPC()) {
                StreamLine.discordData.sendDiscordChannel(sender.findSender(), ChatsHandler.getChannel("party"), party.leaderUUID, msg);
            }

            for (ProxiedPlayer pp : StreamLine.getInstance().getProxy().getPlayers()){
                if (! pp.hasPermission(ConfigUtils.partyView())) continue;

                SavablePlayer them = PlayerUtils.getOrCreatePlayerStat(pp);

                if (! them.gspy) continue;

                if (! them.gspyvs) if (them.uuid.equals(sender.uuid)) continue;

                MessagingUtils.sendBPUserMessage(party, sender.findSender(), them.findSender(), spy.replace("%message%", msg));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendChat(SavablePlayer sender, SavableParty party, String msg) {
        try {
            if (! isParty(party) || party == null) {
                MessagingUtils.sendBUserMessage(sender.findSender(), noPartyFound);
                return;
            }

            if (party.isMuted && ! party.hasModPerms(sender)) {
                MessagingUtils.sendBPUserMessage(party, sender.findSender(), sender.findSender(),  chatMuted
                        .replace("%message%", msg)
                );
                return;
            }

            for (SavableUser pl : party.totalMembers) {
                MessagingUtils.sendBPUserMessage(party, sender.findSender(), pl.findSender(),
                        StreamLine.chatConfig.getPermissionedChatMessage(sender, ChatsHandler.getChat("party", party.leaderUUID), "party", MessageServerType.BUNGEE)
                                .replace("%message%", msg)
                );
            }

            if (! sender.chatIdentifier.equals(sender.party) && ! sender.chatIdentifier.equals("network")) {
                MessagingUtils.sendBPUserMessage(party, sender.findSender(), sender.findSender(),
                        StreamLine.chatConfig.getPermissionedChatMessage(sender, ChatsHandler.getChat("party", party.leaderUUID), "party", MessageServerType.BUNGEE)
                                .replace("%message%", msg)
                );
            }

            if (ConfigUtils.moduleDEnabled()) {
                if (ConfigUtils.partyToDiscord() && ConfigUtils.partyConsoleChats()) {
                    MessagingUtils.sendDiscordPEBMessage(party, new DiscordMessage(sender.findSender(), chatTitle,
                            chatConsole
                                    .replace("%message%", msg)
                            , DiscordBotConfUtils.textChannelParties()));
                }
            }

            if (ConfigUtils.moduleDPC()) {
                StreamLine.discordData.sendDiscordChannel(sender.findSender(), ChatsHandler.getChannel("party"), party.leaderUUID, msg);
            }

            for (ProxiedPlayer pp : StreamLine.getInstance().getProxy().getPlayers()){
                if (! pp.hasPermission(ConfigUtils.partyView())) continue;

                SavablePlayer them = PlayerUtils.getOrCreatePlayerStat(pp);

                if (! them.gspy) continue;

                if (! them.gspyvs) if (them.uuid.equals(sender.uuid)) continue;

                MessagingUtils.sendBPUserMessage(party, sender.findSender(), them.findSender(), spy.replace("%message%", msg));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void onSync(SavableUser sender) {
        SavableParty party = getParty(sender);

        if (! isParty(party) || party == null) {
            MessagingUtils.sendBUserMessage(sender.findSender(), noPartyFound);
            return;
        }

        if (! party.hasModPerms(sender)) {
            MessagingUtils.sendBPUserMessage(party, sender.findSender(), sender.findSender(), noPermission);
            return;
        }

        List<SavablePlayer> players = new ArrayList<>();

        for (SavableUser user : party.totalMembers) {
            if (user instanceof SavablePlayer) players.add((SavablePlayer) user);
        }

        VoiceUtils.createVoice(party.leader.latestName, CategoryType.PARTIES, players.toArray(new SavablePlayer[0]));

        Category category = VoiceUtils.getCategory(CategoryType.PARTIES);
        if (category == null) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
            return;
        }

        MessagingUtils.sendBUserMessage(sender, syncSender
                .replace("%category%", category.getName())
        );
    }

    public static void sendChatFromDiscord(String nameUsed, SavableParty party, String format, String msg) {
        if (! ConfigUtils.moduleDEnabled()) return;

        try {
            for (SavableUser pl : party.totalMembers) {
                MessagingUtils.sendBPUserMessageFromDiscord(party, nameUsed, pl.findSender(), format
                        .replace("%message%", msg)
                );
            }

//            if (ConfigUtils.moduleDEnabled()) {
//                if (ConfigUtils.partyToDiscord() && ConfigUtils.partyConsoleChats()) {
//                    MessagingUtils.sendDiscordPEBMessage(party, new DiscordMessage(sender.findSender(), chatTitle,
//                            chatConsole
//                                    .replace("%message%", msg)
//                            , DiscordBotConfUtils.textChannelParties()));
//                }
//            }

            for (ProxiedPlayer pp : StreamLine.getInstance().getProxy().getPlayers()){
                if (! pp.hasPermission(ConfigUtils.partyView())) continue;

                SavablePlayer them = PlayerUtils.getOrCreatePlayerStat(pp);

                if (! them.gspy) continue;

                MessagingUtils.sendBPUserMessageFromDiscord(party, nameUsed, them.findSender(), spy.replace("%message%", msg));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendChatFromDiscord(SavableUser user, SavableParty party, String format, String msg) {
        if (! ConfigUtils.moduleDEnabled()) return;

        try {
            for (SavableUser pl : party.totalMembers) {
                MessagingUtils.sendBPUserMessageFromDiscord(party, user, pl.findSender(), format
                        .replace("%message%", msg)
                );
            }

//            if (ConfigUtils.moduleDEnabled()) {
//                if (ConfigUtils.partyToDiscord() && ConfigUtils.partyConsoleChats()) {
//                    MessagingUtils.sendDiscordPEBMessage(party, new DiscordMessage(sender.findSender(), chatTitle,
//                            chatConsole
//                                    .replace("%message%", msg)
//                            , DiscordBotConfUtils.textChannelParties()));
//                }
//            }

            for (ProxiedPlayer pp : StreamLine.getInstance().getProxy().getPlayers()){
                if (! pp.hasPermission(ConfigUtils.partyView())) continue;

                SavablePlayer them = PlayerUtils.getOrCreatePlayerStat(pp);

                if (! them.gspy) continue;

                MessagingUtils.sendBPUserMessageFromDiscord(party, user, them.findSender(), spy.replace("%message%", msg));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveAll(){
        List<SavableParty> gs = new ArrayList<>(getParties());

        for (SavableParty party : gs) {
            try {
                party.saveInfo();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean hasGroupedSize(String group) {
        for (String key : ConfigUtils.getGroupSizeConfig().getKeys()) {
            if (group.equals(key)) return true;
        }

        return false;
    }

    public static int getMaxSize(SavableUser leader){
        if (! StreamLine.lpHolder.enabled || leader instanceof SavableConsole) return ConfigUtils.partyMax();

        try {
            String group = StreamLine.lpHolder.api.getUserManager().getUser(leader.latestName).getPrimaryGroup();

            if (group.equals("")){
                group = "default";
            }

            int max = 0;

            if (! hasGroupedSize(group) && ! group.equals("default")) {
                group = "default";
            } else if (! hasGroupedSize(group) && group.equals("default")){
                return 1;
            }

            return ConfigUtils.getGroupedSize(group);
        } catch (Exception e) {
            e.printStackTrace();
            return ConfigUtils.partyMax();
        }
    }

    // MESSAGES...
    // Text.
    public static final String textLeader = StreamLine.config.getMessString("party.text.leader");
    public static final String textModerator = StreamLine.config.getMessString("party.text.moderator");
    public static final String textMember = StreamLine.config.getMessString("party.text.member");
    // Discord.
    public static final String discordTitle = StreamLine.config.getMessString("party.discord.title");
    // Spy.
    public static final String spy = StreamLine.config.getMessString("party.spy");
    // No party.
    public static final String noPartyFound = StreamLine.config.getMessString("party.no-party");
    // Already made.
    public static final String alreadyMade = StreamLine.config.getMessString("party.already-made");
    // Already in one, others.
    public static final String alreadyHasOneOthers = StreamLine.config.getMessString("party.already-has-others");
    // Already in one self.
    public static final String alreadyHasOneSelf = StreamLine.config.getMessString("party.already-has-self");
    // Too big.
    public static final String tooBig = StreamLine.config.getMessString("party.too-big");
    // Not high enough permissions.
    public static final String noPermission = StreamLine.config.getMessString("party.no-permission");
    // Not in a party.
    public static final String notInParty = StreamLine.config.getMessString("party.not-in-a-party");
    public static final String otherNotInParty = StreamLine.config.getMessString("party.other-not-in-party");
    // Not enough space in party.
    public static final String notEnoughSpace = StreamLine.config.getMessString("party.not-enough-space");
    // Chat.
    public static final String chat = StreamLine.config.getMessString("party.chat.message");
    public static final String chatMuted = StreamLine.config.getMessString("party.chat.muted");
    public static final String chatConsole = StreamLine.config.getMessString("party.chat.console");;
    public static final String chatTitle = StreamLine.config.getMessString("party.chat.title");
    // Create.
    public static final String create = StreamLine.config.getMessString("party.create.sender");
    public static final String createNonEmpty = StreamLine.config.getMessString("party.create.non-empty");
    public static final String createConsole = StreamLine.config.getMessString("party.create.console");
    public static final String createTitle = StreamLine.config.getMessString("party.create.title");
    // Join.
    public static final String joinMembers = StreamLine.config.getMessString("party.join.members");
    public static final String joinUser = StreamLine.config.getMessString("party.join.user");
    public static final String joinFailure = StreamLine.config.getMessString("party.join.failure");
    public static final String joinsConsole = StreamLine.config.getMessString("party.join.console");
    public static final String joinsTitle = StreamLine.config.getMessString("party.join.title");
    // Leave.
    public static final String leaveMembers = StreamLine.config.getMessString("party.leave.members");
    public static final String leaveUser = StreamLine.config.getMessString("party.leave.user");
    public static final String leaveFailure = StreamLine.config.getMessString("party.leave.failure");
    public static final String leaveConsole = StreamLine.config.getMessString("party.leave.console");
    public static final String leaveTitle = StreamLine.config.getMessString("party.leave.title");
    // Promote.
    public static final String promoteMembers = StreamLine.config.getMessString("party.promote.members");
    public static final String promoteUser = StreamLine.config.getMessString("party.promote.user");
    public static final String promoteLeader = StreamLine.config.getMessString("party.promote.leader");
    public static final String promoteFailure = StreamLine.config.getMessString("party.promote.failure");
    public static final String promoteConsole = StreamLine.config.getMessString("party.promote.console");
    public static final String promoteTitle = StreamLine.config.getMessString("party.promote.title");
    // Demote.
    public static final String demoteMembers = StreamLine.config.getMessString("party.demote.members");
    public static final String demoteUser = StreamLine.config.getMessString("party.demote.user");
    public static final String demoteLeader = StreamLine.config.getMessString("party.demote.leader");
    public static final String demoteFailure = StreamLine.config.getMessString("party.demote.failure");
    public static final String demoteIsLeader = StreamLine.config.getMessString("party.demote.is-leader");
    public static final String demoteConsole = StreamLine.config.getMessString("party.demote.console");
    public static final String demoteTitle = StreamLine.config.getMessString("party.demote.title");
    // List.
    public static final String listMain = StreamLine.config.getMessString("party.list.main");
    public static final String listLeaderBulk = StreamLine.config.getMessString("party.list.leaderbulk");
    public static final String listModBulkMain = StreamLine.config.getMessString("party.list.moderatorbulk.main");
    public static final String listModBulkNotLast = StreamLine.config.getMessString("party.list.moderatorbulk.moderators.not-last");
    public static final String listModBulkLast = StreamLine.config.getMessString("party.list.moderatorbulk.moderators.last");
    public static final String listModBulkNone = StreamLine.config.getMessString("party.list.moderatorbulk.moderators.if-none");
    public static final String listMemberBulkMain = StreamLine.config.getMessString("party.list.memberbulk.main");
    public static final String listMemberBulkNotLast = StreamLine.config.getMessString("party.list.memberbulk.members.not-last");
    public static final String listMemberBulkLast = StreamLine.config.getMessString("party.list.memberbulk.members.last");
    public static final String listMemberBulkNone = StreamLine.config.getMessString("party.list.memberbulk.members.if-none");
    // Open.
    public static final String openMembers = StreamLine.config.getMessString("party.open.members");
    public static final String openSender = StreamLine.config.getMessString("party.open.sender");
    public static final String openFailure = StreamLine.config.getMessString("party.open.failure");
    public static final String openConsole = StreamLine.config.getMessString("party.open.console");
    public static final String openTitle = StreamLine.config.getMessString("party.open.title");
    // Close.
    public static final String closeMembers = StreamLine.config.getMessString("party.close.members");
    public static final String closeSender = StreamLine.config.getMessString("party.close.sender");
    public static final String closeFailure = StreamLine.config.getMessString("party.close.failure");
    public static final String closeConsole = StreamLine.config.getMessString("party.close.console");
    public static final String closeTitle = StreamLine.config.getMessString("party.close.title");
    // Disband.
    public static final String disbandMembers = StreamLine.config.getMessString("party.disband.members");
    public static final String disbandLeader = StreamLine.config.getMessString("party.disband.leader");
    public static final String disbandConsole = StreamLine.config.getMessString("party.disband.console");
    public static final String disbandTitle = StreamLine.config.getMessString("party.disband.title");
    // Accept.
    public static final String acceptUser = StreamLine.config.getMessString("party.accept.user");
    public static final String acceptLeader = StreamLine.config.getMessString("party.accept.leader");
    public static final String acceptMembers = StreamLine.config.getMessString("party.accept.members");
    public static final String acceptFailure = StreamLine.config.getMessString("party.accept.failure");
    public static final String acceptConsole = StreamLine.config.getMessString("party.accept.console");
    public static final String acceptTitle = StreamLine.config.getMessString("party.accept.title");
    // Deny.
    public static final String denyUser = StreamLine.config.getMessString("party.deny.user");
    public static final String denyLeader = StreamLine.config.getMessString("party.deny.leader");
    public static final String denyMembers = StreamLine.config.getMessString("party.deny.members");
    public static final String denyFailure = StreamLine.config.getMessString("party.deny.failure");
    public static final String denyConsole = StreamLine.config.getMessString("party.deny.console");
    public static final String denyTitle = StreamLine.config.getMessString("party.deny.title");
    // Invite.
    public static final String inviteUser = StreamLine.config.getMessString("party.invite.user");
    public static final String inviteLeader = StreamLine.config.getMessString("party.invite.leader");
    public static final String inviteMembers = StreamLine.config.getMessString("party.invite.members");
    public static final String inviteFailure = StreamLine.config.getMessString("party.invite.failure");
    public static final String inviteNonSelf = StreamLine.config.getMessString("party.invite.non-self");
    public static final String inviteConsole = StreamLine.config.getMessString("party.invite.console");
    public static final String inviteTitle = StreamLine.config.getMessString("party.invite.title");
    // Kick.
    public static final String kickUser = StreamLine.config.getMessString("party.kick.user");
    public static final String kickSender = StreamLine.config.getMessString("party.kick.sender");
    public static final String kickMembers = StreamLine.config.getMessString("party.kick.members");
    public static final String kickFailure = StreamLine.config.getMessString("party.kick.failure");
    public static final String kickMod = StreamLine.config.getMessString("party.kick.mod");
    public static final String kickSelf = StreamLine.config.getMessString("party.kick.self");
    public static final String kickConsole = StreamLine.config.getMessString("party.kick.console");
    public static final String kickTitle = StreamLine.config.getMessString("party.kick.title");
    // Mute.
    public static final String muteSender = StreamLine.config.getMessString("party.mute.mute.user");
    public static final String muteMembers = StreamLine.config.getMessString("party.mute.mute.members");
    public static final String unmuteSender = StreamLine.config.getMessString("party.mute.unmute.user");
    public static final String unmuteMembers = StreamLine.config.getMessString("party.mute.unmute.members");
    public static final String muteConsole = StreamLine.config.getMessString("party.mute.console");
    public static final String muteTitle = StreamLine.config.getMessString("party.mute.title");
    public static final String muteToggleMuted = StreamLine.config.getMessString("party.mute.toggle.muted");
    public static final String muteToggleUnMuted = StreamLine.config.getMessString("party.mute.toggle.unmuted");
    // Warp.
    public static final String warpSender = StreamLine.config.getMessString("party.warp.sender");
    public static final String warpMembers = StreamLine.config.getMessString("party.warp.members");
    public static final String warpConsole = StreamLine.config.getMessString("party.warp.console");
    public static final String warpTitle = StreamLine.config.getMessString("party.warp.title");
    // Info.
    public static final String info = StreamLine.config.getMessString("party.info");
    // Sync.
    public static final String syncSender = StreamLine.config.getMessString("party.sync.sender");
}
