package net.plasmere.streamline.objects.timers;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.objects.SavableGuild;
import net.plasmere.streamline.objects.SavableParty;
import net.plasmere.streamline.objects.enums.CategoryType;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.objects.savable.users.SavableUser;
import net.plasmere.streamline.utils.*;

import java.util.*;

public class OneSecondTimer implements Runnable {
    public int countdown;
    public int reset;

    public OneSecondTimer() {
        this.countdown = 0;
        this.reset = 1;
    }

    @Override
    public void run() {
        if (countdown == 0) {
            done();
        }

        countdown--;
    }

    public void done(){
        try {
            countdown = reset;

            if (PlayerUtils.getToSave().size() > 0) {
                for (SavableUser user : new ArrayList<>(PlayerUtils.getToSave())) {
                    PlayerUtils.doSave(user);
                }
            }

            PlayerUtils.tickConn();

            if (StreamLine.lpHolder.enabled) {
                for (SavablePlayer player : PlayerUtils.getJustPlayersOnline()) {
                    if (player.latestName == null) continue;
                    if (player.latestName.equals("")) continue;
                    PlayerUtils.updateDisplayName(player);
                }
            }

            for (SavablePlayer player : PlayerUtils.getJustPlayers()) {
                PlayerUtils.checkAndUpdateIfMuted(player);
            }

            PlayerUtils.tickTeleport();

            if (ConfigUtils.moduleDEnabled()) {
                if (ConfigUtils.boostsEnabled()) {
                    PlayerUtils.tickBoosts();
                }
            }

            if (ConfigUtils.moduleBRanksEnabled()) {
                int success = 0;
                int failed = 0;
                int other = 0;

                for (ProxiedPlayer player : PlayerUtils.getOnlinePPlayers()) {
                    try {
                        int result = RanksUtils.checkAndChange(PlayerUtils.getPlayerStat(player));

                        if (result == 1) success ++;
                        if (result == 0) other ++;
                        if (result == -1) failed ++;
                    } catch (Exception e) {
                        failed ++;
                        e.printStackTrace();
                    }
                }

                if (StreamLine.votes.getConsole()) MessagingUtils.logInfo(
                        "Success: " + success + " Failed: " + failed + " Other: " + other + " Total: (" +
                                (success + failed + other) + " | " + PlayerUtils.getOnlinePPlayers().size() + ")"
                );
            }
        } catch (ConcurrentModificationException e) {
            if (ConfigUtils.debug()) e.printStackTrace();
        }

        tickGuilds();
        tickGuildSync();
        tickPartySync();
    }

    public void tickGuilds() {
        if (StreamLine.getJda() == null) return;

        if (ConfigUtils.moduleDPCChangeOnVerifyUnchangeable()) {
            for (long k : StreamLine.discordData.getVerified().keySet()) {
                String uuid = StreamLine.discordData.getUUIDOfVerified(k);
                if (uuid == null) continue;
                if (uuid.equals("")) continue;

                SavableUser user = PlayerUtils.getOrGetSavableUser(uuid);

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
            List<SavablePlayer> players = new ArrayList<>();

            for (SavableUser user : guild.totalMembers) {
                if (user instanceof SavablePlayer) players.add((SavablePlayer) user);
            }

            if (guild.voiceID == 0L) {
                List<VoiceChannel> channel = DiscordUtils.createVoice(guild.name, CategoryType.GUILDS, players.toArray(new SavablePlayer[0]));
                if (channel.size() <= 0) continue;
                guild.updateKey("voice", channel.get(0).getIdLong());
            } else {
                DiscordUtils.addToVoice(guild.voiceID, players.toArray(new SavablePlayer[0]));
            }
        }
    }

    public void tickPartySync() {
        if (! ConfigUtils.moduleDEnabled()) return;
        if (! ConfigUtils.partiesSync()) return;

        for (SavableParty party : new ArrayList<>(PartyUtils.getParties())) {
            List<SavablePlayer> players = new ArrayList<>();

            for (SavableUser user : party.totalMembers) {
                if (user instanceof SavablePlayer) players.add((SavablePlayer) user);
            }

            if (party.voiceID == 0L) {
                List<VoiceChannel> channel = DiscordUtils.createVoice(party.leader.latestName, CategoryType.PARTIES, players.toArray(new SavablePlayer[0]));
                if (channel.size() <= 0) continue;
                party.updateKey("voice", channel.get(0).getIdLong());
            } else {
                DiscordUtils.addToVoice(party.voiceID, players.toArray(new SavablePlayer[0]));
            }
        }
    }
}
