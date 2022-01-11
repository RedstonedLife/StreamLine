package net.plasmere.streamline.objects.timers;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.objects.savable.groups.SavableGuild;
import net.plasmere.streamline.objects.savable.groups.SavableParty;
import net.plasmere.streamline.objects.enums.CategoryType;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.objects.savable.users.SavableUser;
import net.plasmere.streamline.utils.*;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.TreeMap;

public class OneSecondTimer implements Runnable {
    public int countdown;
    public int reset;
    public int thirty;

    public OneSecondTimer() {
        this.countdown = 0;
        this.reset = 1;
        this.thirty = 0;
    }

    @Override
    public void run() {
        if (countdown == 0) {
            done();

            countdown = reset;
        }

        countdown--;
    }

    public void done() {
//        thirty --;
//        if (thirty == 0) {
//            thirty = 30;
//
//            for (ProxiedPlayer player : PlayerUtils.getOnlinePPlayers()) {
//                PlayerUtils.getLuckPermsPrefix(player.getUsername(), false);
//                PlayerUtils.getLuckPermsSuffix(player.getUsername(), false);
//            }
//        }

        try {
            UUIDUtils.cachedNames = new TreeMap<>();
            UUIDUtils.cachedUUIDs = new TreeMap<>();
            UUIDUtils.cachedPartyFiles = new TreeMap<>();
            UUIDUtils.cachedGuildFiles = new TreeMap<>();
            UUIDUtils.cachedPlayerFiles = new TreeMap<>();
            UUIDUtils.cachedOtherFiles = new TreeMap<>();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (PlayerUtils.getToSave().size() > 0) {
                for (SavableUser user : new ArrayList<>(PlayerUtils.getToSave())) {
                    PlayerUtils.doSave(user);
                }
            }

            PlayerUtils.tickConn();

            if (StreamLine.lpHolder.enabled) {
                for (ProxiedPlayer player : PlayerUtils.getOnlinePPlayers()) {
//                    if (player.latestName == null) continue;
//                    if (player.latestName.equals("")) continue;
                    SavablePlayer p = PlayerUtils.getOrGetPlayerStatByUUID(player.getUniqueId().toString());
                    if (p == null) {
                        if (ConfigUtils.debug())
                            MessagingUtils.logSevere("SavablePlayer for " + player.getName() + " is null!");
                        continue;
                    }
                    PlayerUtils.updateDisplayName(p);
//                    if (player.latestName == null) continue;
//                    if (player.latestName.equals("")) continue;
//                    PlayerUtils.updateDisplayName(player);
                }
            } else {
                MessagingUtils.logSevere("Luckperms not found! Please install luckperms!");
            }

            for (SavablePlayer player : PlayerUtils.getJustPlayers()) {
                player.updateOnline();
                PlayerUtils.checkAndUpdateIfMuted(player);
            }

            PlayerUtils.tickTeleport();

            if (ConfigUtils.moduleDEnabled()) {
                PlayerUtils.tickBoosts();
            }

            if (ConfigUtils.moduleBRanksEnabled()) {
                if (StreamLine.ranksConfig.checkedGroups().size() > 0) {
                    int success = 0;
                    int failed = 0;
                    int other = 0;

                    for (ProxiedPlayer player : PlayerUtils.getOnlinePPlayers()) {
                        try {
                            int result = RanksUtils.checkAndChange(PlayerUtils.getPlayerStat(player));

                            if (result == 1) success++;
                            if (result == 0) other++;
                            if (result == -1) failed++;
                        } catch (Exception e) {
                            failed++;
                            e.printStackTrace();
                        }
                    }

                    if (StreamLine.votes.getConsole()) MessagingUtils.logInfo(
                            "Success: " + success + " Failed: " + failed + " Other: " + other + " Total: (" +
                                    (success + failed + other) + " | " + PlayerUtils.getOnlinePPlayers().size() + ")"
                    );
                }
            }
        } catch (ConcurrentModificationException e) {
            if (ConfigUtils.debug()) e.printStackTrace();
        }

        tickGuilds();
        tickGuildSync();
        tickPartySync();

//        if (! StreamLine.databaseInfo.getHost().equals("")) {
//            for (SavablePlayer player : PlayerUtils.getJustPlayers()) {
//                if (player.onlineCheck()) {
//                    for (String key : new TreeMap<>(player.getInfo()).keySet()) {
//                        Driver.update(SavableType.PLAYER, UUIDUtils.stripUUID(player.uuid), key.replace('-', '_'), player.getInfo().get(key));
//                    }
//                }
//            }
//        }
    }

    public void tickGuilds() {
        if (StreamLine.getJda() == null) return;

        if (ConfigUtils.moduleDPCChangeOnVerifyUnchangeable()) {
            for (long k : StreamLine.discordData.getVerified().keySet()) {
                String uuid = StreamLine.discordData.getUUIDOfVerified(k);
                if (uuid == null) continue;
                if (uuid.equals("null")) continue;
                if (uuid.equals("")) continue;

                SavableUser user = PlayerUtils.getOrGetSavableUser(uuid);
                if (user == null) continue;

                try {
                    for (Guild guild : StreamLine.getJda().getGuilds()) {
                        Member member = guild.getMemberById(k);

                        if (member == null) {
//                            MessagingUtils.logInfo("Cannot find member by uuid: " + k);
                            continue;
                        }

                        if (ConfigUtils.moduleDPCChangeOnVerifyType().equals("discord")) {
                            member.modifyNickname(TextUtils.replaceAllPlayerDiscord(ConfigUtils.moduleDPCChangeOnVerifyTo(), user)).complete();
                        } else if (ConfigUtils.moduleDPCChangeOnVerifyType().equals("bungee")) {
                            member.modifyNickname(TextUtils.replaceAllPlayerBungee(ConfigUtils.moduleDPCChangeOnVerifyTo(), user)).complete();
                        }
                    }
//                } catch (HierarchyException e) {
//                    // do nothing.
//                } catch (ErrorResponseException e) {
//                    // do nothing.
                } catch (Exception e) {
                    // do nothing.
                }
            }
        }
    }

    public void tickGuildSync() {
        if (! ConfigUtils.moduleDEnabled()) return;
        if (! ConfigUtils.guildsSync()) return;

        for (SavableGuild guild : new ArrayList<>(GuildUtils.getGuilds())) {
            if (guild.name == null) return;
            if (guild.name.equals("")) return;

            List<SavablePlayer> players = new ArrayList<>();

            for (SavableUser user : guild.totalMembers) {
                if (user instanceof SavablePlayer) players.add((SavablePlayer) user);
            }

            if (guild.voiceID == 0L) {
                VoiceChannel channel = DiscordUtils.createVoice(guild.name, CategoryType.GUILDS, players.toArray(new SavablePlayer[0]));
                if (channel == null) continue;
                guild.setVoiceID(channel.getIdLong());
            } else {
                DiscordUtils.addToVoice(guild.voiceID, players.toArray(new SavablePlayer[0]));
            }
        }
    }

    public void tickPartySync() {
        if (! ConfigUtils.moduleDEnabled()) return;
        if (! ConfigUtils.partiesSync()) return;

        for (SavableParty party : new ArrayList<>(PartyUtils.getParties())) {
            if (party.uuid == null) return;
            if (party.uuid.equals("")) return;

            List<SavablePlayer> players = new ArrayList<>();

            for (SavableUser user : party.totalMembers) {
                if (user instanceof SavablePlayer) players.add((SavablePlayer) user);
            }

            if (party.voiceID == 0L) {
                SavableUser user = PlayerUtils.getOrGetSavableUser(party.uuid);

                if (user == null) continue;

                VoiceChannel channel = DiscordUtils.createVoice(user.latestName, CategoryType.PARTIES, players.toArray(new SavablePlayer[0]));
                if (channel == null) continue;
                party.setVoiceID(channel.getIdLong());
            } else {
                DiscordUtils.addToVoice(party.voiceID, players.toArray(new SavablePlayer[0]));
            }
        }
    }
}
