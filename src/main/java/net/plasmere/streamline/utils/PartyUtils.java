package net.plasmere.streamline.utils;

import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.query.QueryOptions;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.Config;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.objects.Party;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.plasmere.streamline.objects.Player;
import net.plasmere.streamline.objects.messaging.DiscordMessage;

import java.util.*;

public class PartyUtils {
    private static final List<Party> parties = new ArrayList<>();
    private static final Configuration message = Config.getMess();

    public static List<Party> getParties() {
        return parties;
    }
    // Party , Invites
    public static Map<Party, List<Player>> invites = new HashMap<>();

    public static Party getParty(Player player) {
        try {
            for (Party party : parties) {
                if (party.hasMember(player))
                    return party;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Party getParty(String uuid) {
        try {
            for (Party party : parties) {
                if (party.hasMember(UUIDFetcher.getPlayerByUUID(uuid, true)))
                    return party;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean hasParty(Player player) {
        for (Party party : parties) {
            if (party.hasMember(player)) return true;
        }
        return false;
    }

    public static boolean isParty(Party party){
        return parties.contains(party);
    }

    public static void removeInvite(Party party, Player player) {
        invites.get(party).remove(player);
    }

    public static boolean checkPlayer(Party party, Player player, Player sender){
        if (! isParty(party) || party == null) {
            MessagingUtils.sendBUserMessage(sender, noPartyFound);
            return false;
        }

        if (! party.hasMember(sender)) {
            MessagingUtils.sendBUserMessage(sender, notInParty);
            return false;
        }

        if (hasParty(player)) {
            MessagingUtils.sendBUserMessage(sender, alreadyHasOne);
            return false;
        }

        return true;
    }

    public static void createParty(Player player) {
        ProxiedPlayer p = UUIDFetcher.getPPlayerByUUID(player.uuid);

        if (p == null) return;

        if (getParty(player) != null) {
            MessagingUtils.sendBUserMessage(p, already);
            return;
        }

        try {
            Party party = new Party(player);

            addParty(party);

            MessagingUtils.sendBPUserMessage(party, p, p, create);

            // if (ConfigUtils.debug) StreamLine.getInstance().getLogger().info("CREATE : totalMembers --> "  + party.totalMembers.size());

            if (ConfigUtils.partyToDiscord && ConfigUtils.partyConsoleCreates) {
                MessagingUtils.sendDiscordEBMessage(new DiscordMessage(p, createTitle,
                        createConsole
                                .replace("%sender%", PlayerUtils.getOffOnDisplayDiscord(player))
                                .replace("%leader%", PlayerUtils.getOffOnDisplayDiscord(PlayerUtils.getOrCreate(party.leaderUUID)))
                                .replace("%sender_normal%", PlayerUtils.getOffOnRegDiscord(player))
                                .replace("%leader_normal%", PlayerUtils.getOffOnRegDiscord(PlayerUtils.getOrCreate(party.leaderUUID)))
                                .replace("%size%", String.valueOf(party.maxSize))
                        , ConfigUtils.textChannelParties));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createPartySized(Player player, int size) {
        ProxiedPlayer p = UUIDFetcher.getPPlayerByUUID(player.uuid);

        if (p == null) return;

        if (getParty(player) != null) {
            MessagingUtils.sendBUserMessage(p, already);
            return;
        }

        try {
            int maxSize = getMaxSize(player);

            if (size > maxSize) {
                MessagingUtils.sendBUserMessage(p, tooBig);
                return;
            }

            Party party = new Party(player, size);

            parties.add(party);

            MessagingUtils.sendBPUserMessage(party, p, p, create);

            // if (ConfigUtils.debug) StreamLine.getInstance().getLogger().info("OPEN : totalMembers --> "  + party.totalMembers.size());

            if (ConfigUtils.partyToDiscord && ConfigUtils.partyConsoleCreates) {
                MessagingUtils.sendDiscordEBMessage(new DiscordMessage(p, createTitle,
                        createConsole
                                .replace("%sender%", PlayerUtils.getOffOnDisplayDiscord(player))
                                .replace("%leader%", PlayerUtils.getOffOnDisplayDiscord(PlayerUtils.getOrCreate(party.leaderUUID)))
                                .replace("%sender_normal%", PlayerUtils.getOffOnRegDiscord(player))
                                .replace("%leader_normal%", PlayerUtils.getOffOnRegDiscord(PlayerUtils.getOrCreate(party.leaderUUID)))
                                .replace("%size%", String.valueOf(party.maxSize))
                        , ConfigUtils.textChannelParties));
            }

            if (ConfigUtils.partyToDiscord && ConfigUtils.partyConsoleOpens) {
                MessagingUtils.sendDiscordEBMessage(new DiscordMessage(p, openTitle,
                        openConsole
                                .replace("%sender%", PlayerUtils.getOffOnDisplayDiscord(player))
                                .replace("%leader%", PlayerUtils.getOffOnDisplayDiscord(PlayerUtils.getOrCreate(party.leaderUUID)))
                                .replace("%sender_normal%", PlayerUtils.getOffOnRegDiscord(player))
                                .replace("%leader_normal%", PlayerUtils.getOffOnRegDiscord(PlayerUtils.getOrCreate(party.leaderUUID)))
                                .replace("%size%", String.valueOf(party.maxSize))
                        , ConfigUtils.textChannelParties));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addParty(Party party){
        Party p = getParty(party.leader);

        if (p != null) return;

        parties.add(party);
    }

    public static void removeParty(Party party){ parties.remove(party); }

    public static void sendInvite(Player to, Player from) {
        ProxiedPlayer player = UUIDFetcher.getPPlayerByUUID(from.uuid);

        if (player == null) return;

        try {
            Party party = getParty(from);

            if (party != null) {
                if (party.totalMembers.size() <= 0) {
                    if (ConfigUtils.debug) StreamLine.getInstance().getLogger().info("#1 NO PARTY MEMBERS!");
                }
            }

            if (! checkPlayer(party, to, from)) return;

            if (party != null) {
                if (party.totalMembers.size() <= 0) {
                    if (ConfigUtils.debug) StreamLine.getInstance().getLogger().info("#2 NO PARTY MEMBERS!");
                }
            }

            if (to.equals(from)) {
                MessagingUtils.sendBUserMessage(player, inviteNonSelf);
                return;
            }

            if (! party.hasModPerms(from.uuid)) {
                MessagingUtils.sendBUserMessage(player, noPermission);
                return;
            }

            if (party.invites.contains(to)) {
                MessagingUtils.sendBUserMessage(player, inviteFailure);
                return;
            }

            if (party.totalMembers.size() <= 0) {
                if (ConfigUtils.debug) StreamLine.getInstance().getLogger().info("#3 NO PARTY MEMBERS!");
            }

            if (to.online) {
                MessagingUtils.sendBPUserMessage(party, player, to, inviteUser
                        .replace("%sender%", PlayerUtils.getOffOnDisplayBungee(from))
                        .replace("%user%", PlayerUtils.getOffOnDisplayBungee(to))
                        .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(UUIDFetcher.getPlayerByUUID(party.leaderUUID, true))))
                        .replace("%leaderdefault%", PlayerUtils.getOffOnRegBungee(Objects.requireNonNull(UUIDFetcher.getPlayerByUUID(party.leaderUUID, true))))
                );
            }

            if (party.totalMembers.size() <= 0) {
                if (ConfigUtils.debug) StreamLine.getInstance().getLogger().info("#4 NO PARTY MEMBERS!");
            }

            for (Player pl : party.totalMembers) {
                if (! pl.online) continue;

                ProxiedPlayer member = UUIDFetcher.getPPlayerByUUID(pl.uuid);

                if (member == null) {
                    if (ConfigUtils.debug) StreamLine.getInstance().getLogger().info("member == null");
                    continue;
                }

                if (pl.equals(from)) {
                    MessagingUtils.sendBPUserMessage(party, player, member, inviteLeader
                            .replace("%sender%", PlayerUtils.getOffOnDisplayBungee(from))
                            .replace("%user%", PlayerUtils.getOffOnDisplayBungee(to))
                            .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(UUIDFetcher.getPlayerByUUID(party.leaderUUID, true))))
                            .replace("%leaderdefault%", PlayerUtils.getOffOnRegBungee(Objects.requireNonNull(UUIDFetcher.getPlayerByUUID(party.leaderUUID, true))))
                    );
                } else {
                    MessagingUtils.sendBPUserMessage(party, player, member, inviteMembers
                            .replace("%sender%", PlayerUtils.getOffOnDisplayBungee(from))
                            .replace("%user%", PlayerUtils.getOffOnDisplayBungee(to))
                            .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(UUIDFetcher.getPlayerByUUID(party.leaderUUID, true))))
                            .replace("%leaderdefault%", PlayerUtils.getOffOnRegBungee(Objects.requireNonNull(UUIDFetcher.getPlayerByUUID(party.leaderUUID, true))))
                    );
                }
            }

            party.addInvite(to);
            invites.remove(party);
            invites.put(party, party.invites);

            if (ConfigUtils.partyToDiscord && ConfigUtils.partyConsoleInvites) {
                MessagingUtils.sendDiscordEBMessage(new DiscordMessage(player, inviteTitle,
                        inviteConsole
                                .replace("%sender%", PlayerUtils.getOffOnDisplayDiscord(from))
                                .replace("%leader%", PlayerUtils.getOffOnDisplayDiscord(PlayerUtils.getOrCreate(party.leaderUUID)))
                                .replace("%sender_normal%", PlayerUtils.getOffOnRegDiscord(from))
                                .replace("%leader_normal%", PlayerUtils.getOffOnRegDiscord(PlayerUtils.getOrCreate(party.leaderUUID)))
                                .replace("%user%", PlayerUtils.getOffOnDisplayDiscord(to))
                                .replace("%user_normal%", PlayerUtils.getOffOnRegDiscord(to))
                                .replace("%size%", String.valueOf(party.maxSize))
                        , ConfigUtils.textChannelParties));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void acceptInvite(Player accepter, Player from) {
        ProxiedPlayer p = UUIDFetcher.getPPlayerByUUID(accepter.uuid);

        if (p == null) return;

        try {
            Party party = getParty(PlayerUtils.getStat(from));

            if (!isParty(party) || party == null) {
                MessagingUtils.sendBUserMessage(p, acceptFailure);
                return;
            }

            if (! party.hasMember(from)) {
                MessagingUtils.sendBUserMessage(p, otherNotInParty);
                return;
            }

            if (! party.invites.contains(accepter)) {
                MessagingUtils.sendBUserMessage(p, denyFailure);
                return;
            }

            if (party.invites.contains(accepter)) {
                if (party.getSize() >= party.maxSize) {
                    MessagingUtils.sendBPUserMessage(party, p, p, notEnoughSpace);
                    return;
                }

                MessagingUtils.sendBPUserMessage(party, p, p, acceptUser
                        .replace("%user%", PlayerUtils.getOffOnDisplayBungee(accepter))
                        .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(from))
                );

                for (Player pl : party.totalMembers){
                    if (! pl.online) continue;

                    ProxiedPlayer m = UUIDFetcher.getPPlayerByUUID(pl.uuid);

                    if (m == null) continue;

                    if (m.equals(party.leader)){
                        MessagingUtils.sendBPUserMessage(party, p, m, acceptLeader
                                .replace("%user%", PlayerUtils.getOffOnDisplayBungee(accepter))
                                .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(from))
                        );
                    } else {
                        MessagingUtils.sendBPUserMessage(party, p, m, acceptMembers
                                .replace("%user%", PlayerUtils.getOffOnDisplayBungee(accepter))
                                .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(from))
                        );
                    }
                }

                party.addMember(accepter);
                party.removeInvite(accepter);

                if (ConfigUtils.partyToDiscord && ConfigUtils.partyConsoleJoins) {
                    MessagingUtils.sendDiscordEBMessage(new DiscordMessage(p, joinsTitle,
                            joinsConsole
                                    .replace("%sender%", PlayerUtils.getOffOnDisplayDiscord(accepter))
                                    .replace("%leader%", PlayerUtils.getOffOnDisplayDiscord(PlayerUtils.getOrCreate(party.leaderUUID)))
                                    .replace("%sender_normal%", PlayerUtils.getOffOnRegDiscord(accepter))
                                    .replace("%leader_normal%", PlayerUtils.getOffOnRegDiscord(PlayerUtils.getOrCreate(party.leaderUUID)))
                                    .replace("%size%", String.valueOf(party.maxSize))
                            , ConfigUtils.textChannelParties));
                }

                if (ConfigUtils.partyToDiscord && ConfigUtils.partyConsoleAccepts) {
                    MessagingUtils.sendDiscordEBMessage(new DiscordMessage(p, acceptTitle,
                            acceptConsole
                                    .replace("%sender%", PlayerUtils.getOffOnDisplayDiscord(accepter))
                                    .replace("%leader%", PlayerUtils.getOffOnDisplayDiscord(PlayerUtils.getOrCreate(party.leaderUUID)))
                                    .replace("%sender_normal%", PlayerUtils.getOffOnRegDiscord(accepter))
                                    .replace("%leader_normal%", PlayerUtils.getOffOnRegDiscord(PlayerUtils.getOrCreate(party.leaderUUID)))
                                    .replace("%size%", String.valueOf(party.maxSize))
                            , ConfigUtils.textChannelParties));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void denyInvite(Player denier, Player from) {
        ProxiedPlayer p = UUIDFetcher.getPPlayerByUUID(denier.uuid);

        if (p == null) return;

        try {
            Party party = getParty(PlayerUtils.getStat(from));

            if (!isParty(party) || party == null) {
                MessagingUtils.sendBUserMessage(p, denyFailure);
                return;
            }

            if (! party.hasMember(from)) {
                MessagingUtils.sendBUserMessage(p, otherNotInParty);
                return;
            }

            if (! party.invites.contains(denier)) {
                MessagingUtils.sendBUserMessage(p, denyFailure);
                return;
            }

            party.removeInvite(denier);

            MessagingUtils.sendBPUserMessage(party, p, p, denyUser
                    .replace("%user%", PlayerUtils.getOffOnDisplayBungee(denier))
                    .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(from))
            );

            for (Player pl : party.totalMembers){
                if (! pl.online) continue;

                ProxiedPlayer m = UUIDFetcher.getPPlayerByUUID(pl.uuid);

                if (m == null) continue;

                if (m.equals(party.leader)){
                    MessagingUtils.sendBPUserMessage(party, p, m, denyLeader
                            .replace("%user%", PlayerUtils.getOffOnDisplayBungee(denier))
                            .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(from))
                    );
                } else {
                    MessagingUtils.sendBPUserMessage(party, p, m, denyMembers
                            .replace("%user%", PlayerUtils.getOffOnDisplayBungee(denier))
                            .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(from))
                    );
                }
            }

            if (ConfigUtils.partyToDiscord && ConfigUtils.partyConsoleDenies) {
                MessagingUtils.sendDiscordEBMessage(new DiscordMessage(p, denyTitle,
                        denyConsole
                                .replace("%sender%", PlayerUtils.getOffOnDisplayDiscord(denier))
                                .replace("%leader%", PlayerUtils.getOffOnDisplayDiscord(PlayerUtils.getOrCreate(party.leaderUUID)))
                                .replace("%sender_normal%", PlayerUtils.getOffOnRegDiscord(denier))
                                .replace("%leader_normal%", PlayerUtils.getOffOnRegDiscord(PlayerUtils.getOrCreate(party.leaderUUID)))
                                .replace("%size%", String.valueOf(party.maxSize))
                        , ConfigUtils.textChannelParties));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void warpParty(Player sender) {
        ProxiedPlayer p = UUIDFetcher.getPPlayerByUUID(sender.uuid);

        if (p == null) return;

        Party party = getParty(PlayerUtils.getStat(sender));

        if (!isParty(party) || party == null) {
            MessagingUtils.sendBUserMessage(p, noPartyFound);
            return;
        }

        if (! party.hasMember(sender)) {
            MessagingUtils.sendBUserMessage(p, notInParty);
            return;
        }

        if (! party.hasModPerms(sender)) {
            MessagingUtils.sendBPUserMessage(party, p, p, noPermission);
            return;
        }

        for (Player player : party.totalMembers){
            if (! player.online) continue;

            ProxiedPlayer m = UUIDFetcher.getPPlayerByUUID(player.uuid);

            if (m == null) continue;

            if (player.equals(sender)) {
                MessagingUtils.sendBPUserMessage(party, p, m, warpSender);
            } else {
                MessagingUtils.sendBPUserMessage(party, p, m, warpMembers);
            }

            m.connect(sender.getServer().getInfo());
        }

        if (ConfigUtils.partyToDiscord && ConfigUtils.partyConsoleWarps) {
            MessagingUtils.sendDiscordEBMessage(new DiscordMessage(p, warpTitle,
                    warpConsole
                            .replace("%sender%", PlayerUtils.getOffOnDisplayDiscord(sender))
                            .replace("%leader%", PlayerUtils.getOffOnDisplayDiscord(PlayerUtils.getOrCreate(party.leaderUUID)))
                            .replace("%sender_normal%", PlayerUtils.getOffOnRegDiscord(sender))
                            .replace("%leader_normal%", PlayerUtils.getOffOnRegDiscord(PlayerUtils.getOrCreate(party.leaderUUID)))
                            .replace("%size%", String.valueOf(party.maxSize))
                    , ConfigUtils.textChannelParties));
        }
    }

    public static void muteParty(Player sender) {
        ProxiedPlayer p = UUIDFetcher.getPPlayerByUUID(sender.uuid);

        if (p == null) return;

        Party party = getParty(PlayerUtils.getStat(sender));

        if (!isParty(party) || party == null) {
            MessagingUtils.sendBUserMessage(p, noPartyFound);
            return;
        }

        if (! party.hasMember(sender)) {
            MessagingUtils.sendBUserMessage(p, notInParty);
            return;
        }

        if (! party.hasModPerms(sender)) {
            MessagingUtils.sendBPUserMessage(party, p, p, noPermission);
            return;
        }

        if (party.isMuted) {
            for (Player player : party.totalMembers) {
                if (! player.online) continue;

                ProxiedPlayer m = UUIDFetcher.getPPlayerByUUID(player.uuid);

                if (m == null) continue;

                if (player.equals(sender)){
                    MessagingUtils.sendBPUserMessage(party, p, m, unmuteUser);
                } else {
                    MessagingUtils.sendBPUserMessage(party, p, m, unmuteMembers);
                }
            }

        } else {
            for (Player player : party.totalMembers) {
                if (! player.online) continue;

                ProxiedPlayer m = UUIDFetcher.getPPlayerByUUID(player.uuid);

                if (m == null) continue;

                if (player.equals(sender)){
                    MessagingUtils.sendBPUserMessage(party, p, m, muteUser);
                } else {
                    MessagingUtils.sendBPUserMessage(party, p, m, muteMembers);
                }
            }

        }
        party.toggleMute();

        if (ConfigUtils.partyToDiscord && ConfigUtils.partyConsoleMutes) {
            MessagingUtils.sendDiscordEBMessage(new DiscordMessage(p, muteTitle,
                    muteConsole
                            .replace("%sender%", PlayerUtils.getOffOnDisplayDiscord(sender))
                            .replace("%leader%", PlayerUtils.getOffOnDisplayDiscord(PlayerUtils.getOrCreate(party.leaderUUID)))
                            .replace("%sender_normal%", PlayerUtils.getOffOnRegDiscord(sender))
                            .replace("%leader_normal%", PlayerUtils.getOffOnRegDiscord(PlayerUtils.getOrCreate(party.leaderUUID)))
                            .replace("%size%", String.valueOf(party.maxSize))
                            .replace("%toggle%", party.isMuted ? muteToggleMuted : muteToggleUnMuted)
                    , ConfigUtils.textChannelParties));
        }
    }

    public static void kickMember(Player sender, Player player) {
        ProxiedPlayer p = UUIDFetcher.getPPlayerByUUID(sender.uuid);

        if (p == null) return;

        Party party = getParty(PlayerUtils.getStat(sender));

        if (!isParty(party) || party == null) {
            MessagingUtils.sendBUserMessage(p, kickFailure);
            return;
        }

        if (!party.hasMember(sender)) {
            MessagingUtils.sendBUserMessage(p, notInParty);
            return;
        }

        if (!party.hasMember(player)) {
            MessagingUtils.sendBUserMessage(p, otherNotInParty);
            return;
        }

        if (!party.hasModPerms(sender)) {
            MessagingUtils.sendBPUserMessage(party, p, p, noPermission);
            return;
        }

        if (party.hasModPerms(player)) {
            MessagingUtils.sendBPUserMessage(party, p, p, kickMod);
            return;
        }

        if (sender.equals(player)) {
            MessagingUtils.sendBPUserMessage(party, p, p, kickSelf);
        } else if (! party.hasModPerms(sender)) {
            MessagingUtils.sendBPUserMessage(party, p, p, noPermission);
        } else if (party.hasModPerms(player)) {
            MessagingUtils.sendBPUserMessage(party, p, p, kickMod);
        } else {
            for (Player pl : party.totalMembers) {
                if (! pl.online) continue;

                ProxiedPlayer m = UUIDFetcher.getPPlayerByUUID(pl.uuid);

                if (m == null) continue;

                if (pl.equals(sender)) {
                    MessagingUtils.sendBPUserMessage(party, p, m, kickSender
                            .replace("%user%", PlayerUtils.getOffOnDisplayBungee(player))
                    );
                } else if (pl.equals(player)) {
                    MessagingUtils.sendBPUserMessage(party, p, m, kickUser
                            .replace("%user%", PlayerUtils.getOffOnDisplayBungee(player))
                    );
                } else {
                    MessagingUtils.sendBPUserMessage(party, p, m, kickMembers
                            .replace("%user%", PlayerUtils.getOffOnDisplayBungee(player))
                    );
                }
            }

            party.removeMemberFromParty(player);
        }


        if (ConfigUtils.partyToDiscord && ConfigUtils.partyConsoleKicks) {
            MessagingUtils.sendDiscordEBMessage(new DiscordMessage(player, kickTitle,
                    kickConsole
                            .replace("%sender%", PlayerUtils.getOffOnDisplayDiscord(sender))
                            .replace("%leader%", PlayerUtils.getOffOnDisplayDiscord(PlayerUtils.getOrCreate(party.leaderUUID)))
                            .replace("%sender_normal%", PlayerUtils.getOffOnRegDiscord(sender))
                            .replace("%leader_normal%", PlayerUtils.getOffOnRegDiscord(PlayerUtils.getOrCreate(party.leaderUUID)))
                            .replace("%user%", PlayerUtils.getOffOnDisplayDiscord(player))
                            .replace("%user_normal%", PlayerUtils.getOffOnRegDiscord(player))
                            .replace("%size%", String.valueOf(party.maxSize))
                    , ConfigUtils.textChannelParties));
        }
    }

    public static void disband(Player sender) {
        ProxiedPlayer p = UUIDFetcher.getPPlayerByUUID(sender.uuid);

        if (p == null) return;

        try {
            Party party = getParty(PlayerUtils.getStat(sender));

            if (!isParty(party) || party == null) {
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

            for (Player pl : party.totalMembers) {
                if (! pl.online) continue;

                ProxiedPlayer member = UUIDFetcher.getPPlayerByUUID(pl.uuid);

                if (member == null) continue;

                if (!member.equals(party.leader)) {
                    MessagingUtils.sendBPUserMessage(party, p, member, disbandMembers
                            .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                    );
                } else {
                    MessagingUtils.sendBPUserMessage(party, p, member, disbandLeader
                            .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                    );
                }

            }

            if (ConfigUtils.partyToDiscord && ConfigUtils.partyConsoleDisbands) {
                MessagingUtils.sendDiscordEBMessage(new DiscordMessage(p, disbandTitle,
                        disbandConsole
                                .replace("%sender%", PlayerUtils.getOffOnDisplayDiscord(sender))
                                .replace("%leader%", PlayerUtils.getOffOnDisplayDiscord(PlayerUtils.getOrCreate(party.leaderUUID)))
                                .replace("%sender_normal%", PlayerUtils.getOffOnRegDiscord(sender))
                                .replace("%leader_normal%", PlayerUtils.getOffOnRegDiscord(PlayerUtils.getOrCreate(party.leaderUUID)))
                                .replace("%size%", String.valueOf(party.maxSize))
                        , ConfigUtils.textChannelParties));
            }

            removeParty(party);

            party.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openParty(Player sender) {
        ProxiedPlayer p = UUIDFetcher.getPPlayerByUUID(sender.uuid);

        if (p == null) return;

        try {
            Party party = getParty(PlayerUtils.getStat(sender));

            if (!isParty(party) || party == null) {
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
                        .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                        .replace("%size%", Integer.toString(party.getSize()))
                );
            } else {
                party.setPublic(true);

                for (Player pl : party.totalMembers) {
                    if (! pl.online) continue;

                    ProxiedPlayer member = UUIDFetcher.getPPlayerByUUID(pl.uuid);

                    if (member == null) continue;

                    if (member.equals(party.leader)) {
                        MessagingUtils.sendBPUserMessage(party, p, member, openLeader
                                .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                                .replace("%size%", Integer.toString(party.getSize()))
                        );
                    } else {
                        MessagingUtils.sendBPUserMessage(party, p, member, openMembers
                                .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                                .replace("%size%", Integer.toString(party.getSize()))
                        );
                    }
                }
            }

            if (ConfigUtils.partyToDiscord && ConfigUtils.partyConsoleOpens) {
                MessagingUtils.sendDiscordEBMessage(new DiscordMessage(p, openTitle,
                        openConsole
                                .replace("%sender%", PlayerUtils.getOffOnDisplayDiscord(sender))
                                .replace("%leader%", PlayerUtils.getOffOnDisplayDiscord(PlayerUtils.getOrCreate(party.leaderUUID)))
                                .replace("%sender_normal%", PlayerUtils.getOffOnRegDiscord(sender))
                                .replace("%leader_normal%", PlayerUtils.getOffOnRegDiscord(PlayerUtils.getOrCreate(party.leaderUUID)))
                                .replace("%size%", String.valueOf(party.maxSize))
                        , ConfigUtils.textChannelParties));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openPartySized(Player sender, int size) {
        ProxiedPlayer p = UUIDFetcher.getPPlayerByUUID(sender.uuid);

        if (p == null) return;

        try {
            Party party = getParty(PlayerUtils.getStat(sender));

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
                        .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                        .replace("%max%", Integer.toString(party.getMaxSize()))
                );
            } else {
                party.setPublic(true);
                party.setMaxSize(size);

                for (Player pl : party.totalMembers) {
                    if (! pl.online) continue;

                    ProxiedPlayer member = UUIDFetcher.getPPlayerByUUID(pl.uuid);

                    if (member == null) continue;

                    if (member.equals(party.leader)) {
                        MessagingUtils.sendBPUserMessage(party, p, member, openLeader
                                .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                                .replace("%max%", Integer.toString(party.getMaxSize()))
                        );
                    } else {
                        MessagingUtils.sendBPUserMessage(party, p, member, openMembers
                                .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                                .replace("%max%", Integer.toString(party.getMaxSize()))
                        );
                    }
                }
            }

            if (ConfigUtils.partyToDiscord && ConfigUtils.partyConsoleOpens) {
                MessagingUtils.sendDiscordEBMessage(new DiscordMessage(p, openTitle,
                        openConsole
                                .replace("%sender%", PlayerUtils.getOffOnDisplayDiscord(sender))
                                .replace("%leader%", PlayerUtils.getOffOnDisplayDiscord(PlayerUtils.getOrCreate(party.leaderUUID)))
                                .replace("%sender_normal%", PlayerUtils.getOffOnRegDiscord(sender))
                                .replace("%leader_normal%", PlayerUtils.getOffOnRegDiscord(PlayerUtils.getOrCreate(party.leaderUUID)))
                                .replace("%size%", String.valueOf(party.maxSize))
                        , ConfigUtils.textChannelParties));
            }
        }  catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeParty(Player sender) {
        ProxiedPlayer p = UUIDFetcher.getPPlayerByUUID(sender.uuid);

        if (p == null) return;
        try {
            Party party = getParty(PlayerUtils.getStat(sender));

            if (!isParty(party) || party == null) {
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

            if (!party.isPublic) {
                MessagingUtils.sendBPUserMessage(party, p, p, closeFailure
                        .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                        .replace("%size%", Integer.toString(party.getSize()))
                );
            } else {
                party.setPublic(false);

                for (Player pl : party.totalMembers) {
                    if (! pl.online) continue;

                    ProxiedPlayer member = UUIDFetcher.getPPlayerByUUID(pl.uuid);

                    if (member == null) continue;

                    if (member.equals(pl)) {
                        MessagingUtils.sendBPUserMessage(party, p, member, closeSender
                                .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                                .replace("%size%", Integer.toString(party.getSize()))
                        );
                    } else {
                        MessagingUtils.sendBPUserMessage(party, p, member, closeMembers
                                .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                                .replace("%size%", Integer.toString(party.getSize()))
                        );
                    }
                }
            }

            if (ConfigUtils.partyToDiscord && ConfigUtils.partyConsoleCloses) {
                MessagingUtils.sendDiscordEBMessage(new DiscordMessage(p, closeTitle,
                        closeConsole
                                .replace("%sender%", PlayerUtils.getOffOnDisplayDiscord(sender))
                                .replace("%leader%", PlayerUtils.getOffOnDisplayDiscord(PlayerUtils.getOrCreate(party.leaderUUID)))
                                .replace("%sender_normal%", PlayerUtils.getOffOnRegDiscord(sender))
                                .replace("%leader_normal%", PlayerUtils.getOffOnRegDiscord(PlayerUtils.getOrCreate(party.leaderUUID)))
                                .replace("%size%", String.valueOf(party.maxSize))
                        , ConfigUtils.textChannelParties));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void listParty(Player sender) {
        ProxiedPlayer p = UUIDFetcher.getPPlayerByUUID(sender.uuid);

        if (p == null) return;
        try {
            Party party = getParty(PlayerUtils.getStat(sender));

            if (!isParty(party) || party == null) {
                MessagingUtils.sendBUserMessage(p, noPartyFound);
                return;
            }

            if (! party.hasMember(sender)) {
                MessagingUtils.sendBUserMessage(p, notInParty);
                return;
            }

            String leaderBulk = listLeaderBulk
                    .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                    .replace("%user%", PlayerUtils.getOffOnDisplayBungee(sender))
                    .replace("%size%", Integer.toString(party.getSize()));
            String moderatorBulk = listModBulkMain
                    .replace("%moderators%", moderators(party))
                    .replace("%user%", PlayerUtils.getOffOnDisplayBungee(sender))
                    .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                    .replace("%size%", Integer.toString(party.getSize()));
            String memberBulk = listMemberBulkMain
                    .replace("%members%", members(party))
                    .replace("%user%", PlayerUtils.getOffOnDisplayBungee(sender))
                    .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                    .replace("%size%", Integer.toString(party.getSize()));

            MessagingUtils.sendBPUserMessage(party, p, p, listMain
                    .replace("%leaderbulk%", leaderBulk)
                    .replace("%moderatorbulk%", moderatorBulk)
                    .replace("%memberbulk%", memberBulk)
                    .replace("%user%", PlayerUtils.getOffOnDisplayBungee(sender))
                    .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                    .replace("%size%", Integer.toString(party.getSize()))
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String moderators(Party party) {
        try {
            if (party.moderators.size() <= 0) {
                return listModBulkNone;
            }

            StringBuilder mods = new StringBuilder();

            int i = 1;

            for (Player m : party.moderators) {
                if (i < party.moderators.size()) {
                    mods.append(listModBulkNotLast
                            .replace("%user%", PlayerUtils.getOffOnDisplayBungee(m))
                            .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                            .replace("%size%", Integer.toString(party.getSize()))
                    );
                } else {
                    mods.append(listModBulkLast
                            .replace("%user%", PlayerUtils.getOffOnDisplayBungee(m))
                            .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                            .replace("%size%", Integer.toString(party.getSize()))
                    );
                }
                i++;
            }

            return mods.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String members(Party party) {
        try {
            if (party.members.size() <= 0) {
                return listMemberBulkNone;
            }

            StringBuilder mems = new StringBuilder();

            int i = 1;

            for (Player m : party.members) {
                if (i < party.moderators.size()) {
                    mems.append(listMemberBulkNotLast
                            .replace("%user%", PlayerUtils.getOffOnDisplayBungee(m))
                            .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                            .replace("%size%", Integer.toString(party.getSize()))
                    );
                } else {
                    mems.append(listMemberBulkLast
                            .replace("%user%", PlayerUtils.getOffOnDisplayBungee(m))
                            .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                            .replace("%size%", Integer.toString(party.getSize()))
                    );
                }
                i++;
            }

            return mems.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void promotePlayer(Player sender, Player member) {
        ProxiedPlayer p = UUIDFetcher.getPPlayerByUUID(sender.uuid);

        if (p == null) return;
        try {
            Party party = getParty(PlayerUtils.getStat(sender));

            if (!isParty(party) || party == null) {
                MessagingUtils.sendBUserMessage(p, noPartyFound);
                return;
            }

            if (! party.hasMember(sender)) {
                MessagingUtils.sendBUserMessage(p, notInParty);
                return;
            }

            if (! party.hasMember(member)) {
                MessagingUtils.sendBUserMessage(p, otherNotInParty);
                return;
            }

            if (! party.isLeader(sender)) {
                MessagingUtils.sendBUserMessage(p, noPermission);
                return;
            }

            switch (party.getLevel(member)) {
                case LEADER:
                    MessagingUtils.sendBPUserMessage(party, p, p, promoteFailure
                            .replace("%user%", PlayerUtils.getOffOnDisplayBungee(member))
                            .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                            .replace("%level%", textLeader
                                    .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                                    .replace("%size%", Integer.toString(party.getSize()))
                            )
                    );
                    return;
                case MODERATOR:
                    party.replaceLeader(member);

                    for (Player pl : party.totalMembers) {
                        if (! pl.online) continue;

                        ProxiedPlayer m = UUIDFetcher.getPPlayerByUUID(pl.uuid);

                        if (m == null) continue;

                        if (m.equals(party.leader)) {
                            MessagingUtils.sendBPUserMessage(party, p, m, promoteLeader
                                    .replace("%user%", PlayerUtils.getOffOnDisplayBungee(member))
                                    .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                                    .replace("%level%", textLeader
                                            .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                                            .replace("%size%", Integer.toString(party.getSize()))
                                    )
                            );
                        } else if (m.equals(member)) {
                            MessagingUtils.sendBPUserMessage(party, p, m, promoteUser
                                    .replace("%user%", PlayerUtils.getOffOnDisplayBungee(member))
                                    .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                                    .replace("%level%", textLeader
                                            .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                                            .replace("%size%", Integer.toString(party.getSize()))
                                    )
                            );
                        } else {
                            MessagingUtils.sendBPUserMessage(party, p, m, promoteMembers
                                    .replace("%user%", PlayerUtils.getOffOnDisplayBungee(member))
                                    .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                                    .replace("%level%", textLeader
                                            .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                                            .replace("%size%", Integer.toString(party.getSize()))
                                    )
                            );
                        }
                    }
                    return;
                case MEMBER:
                default:
                    party.setModerator(member);

                    for (Player pl : party.totalMembers) {
                        if (! pl.online) continue;

                        ProxiedPlayer m = UUIDFetcher.getPPlayerByUUID(pl.uuid);

                        if (m == null) continue;

                        if (m.equals(party.leader)) {
                            MessagingUtils.sendBPUserMessage(party, p, m, promoteLeader
                                    .replace("%user%", PlayerUtils.getOffOnDisplayBungee(member))
                                    .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                                    .replace("%level%", textModerator
                                            .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                                            .replace("%size%", Integer.toString(party.getSize()))
                                    )
                            );
                        } else if (m.equals(member)) {
                            MessagingUtils.sendBPUserMessage(party, p, m, promoteUser
                                    .replace("%user%", PlayerUtils.getOffOnDisplayBungee(member))
                                    .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                                    .replace("%level%", textModerator
                                            .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                                            .replace("%size%", Integer.toString(party.getSize()))
                                    )
                            );
                        } else {
                            MessagingUtils.sendBPUserMessage(party, p, m, promoteMembers
                                    .replace("%user%", PlayerUtils.getOffOnDisplayBungee(member))
                                    .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                                    .replace("%level%", textModerator
                                            .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                                            .replace("%size%", Integer.toString(party.getSize()))
                                    )
                            );
                        }
                    }
                    break;
            }

            if (ConfigUtils.partyToDiscord && ConfigUtils.partyConsolePromotes) {
                MessagingUtils.sendDiscordEBMessage(new DiscordMessage(p, promoteTitle,
                        promoteConsole
                                .replace("%sender%", PlayerUtils.getOffOnDisplayDiscord(sender))
                                .replace("%leader%", PlayerUtils.getOffOnDisplayDiscord(PlayerUtils.getOrCreate(party.leaderUUID)))
                                .replace("%sender_normal%", PlayerUtils.getOffOnRegDiscord(sender))
                                .replace("%leader_normal%", PlayerUtils.getOffOnRegDiscord(PlayerUtils.getOrCreate(party.leaderUUID)))
                                .replace("%user%", PlayerUtils.getOffOnDisplayDiscord(member))
                                .replace("%user_normal%", PlayerUtils.getOffOnRegDiscord(member))
                                .replace("%size%", String.valueOf(party.maxSize))
                        , ConfigUtils.textChannelParties));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void demotePlayer(Player sender, Player member) {
        ProxiedPlayer p = UUIDFetcher.getPPlayerByUUID(sender.uuid);

        if (p == null) return;
        try {
            Party party = getParty(PlayerUtils.getStat(sender));

            if (!isParty(party) || party == null) {
                MessagingUtils.sendBUserMessage(p, noPartyFound);
                return;
            }

            if (! party.hasMember(sender)) {
                MessagingUtils.sendBUserMessage(p, notInParty);
                return;
            }

            if (! party.hasMember(member)) {
                MessagingUtils.sendBUserMessage(p, otherNotInParty);
                return;
            }

            if (! party.isLeader(sender)) {
                MessagingUtils.sendBUserMessage(p, noPermission);
                return;
            }

            switch (party.getLevel(member)) {
                case LEADER:
                    MessagingUtils.sendBPUserMessage(party, p, p, demoteIsLeader
                            .replace("%user%", PlayerUtils.getOffOnDisplayBungee(member))
                            .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                            .replace("%level%", textLeader)
                    );
                    return;
                case MODERATOR:
                    party.setMember(member);

                    for (Player pl : party.totalMembers) {
                        if (! pl.online) continue;

                        ProxiedPlayer m = UUIDFetcher.getPPlayerByUUID(pl.uuid);

                        if (m == null) continue;

                        if (m.equals(party.leader)) {
                            MessagingUtils.sendBPUserMessage(party, p, m, demoteLeader
                                    .replace("%user%", PlayerUtils.getOffOnDisplayBungee(member))
                                    .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                                    .replace("%level%", textMember)
                            );
                        } else if (m.equals(member)) {
                            MessagingUtils.sendBPUserMessage(party, p, m, demoteUser
                                    .replace("%user%", PlayerUtils.getOffOnDisplayBungee(member))
                                    .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                                    .replace("%level%", textMember)
                            );
                        } else {
                            MessagingUtils.sendBPUserMessage(party, p, m, demoteMembers
                                    .replace("%user%", PlayerUtils.getOffOnDisplayBungee(member))
                                    .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                                    .replace("%level%", textMember)
                            );
                        }
                    }
                    return;
                case MEMBER:
                default:
                    MessagingUtils.sendBPUserMessage(party, p, p, demoteFailure
                            .replace("%user%", PlayerUtils.getOffOnDisplayBungee(member))
                            .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                            .replace("%level%", textMember)
                    );
                    break;
            }

            if (ConfigUtils.partyToDiscord && ConfigUtils.partyConsoleDemotes) {
                MessagingUtils.sendDiscordEBMessage(new DiscordMessage(p, demoteTitle,
                        demoteConsole
                                .replace("%sender%", PlayerUtils.getOffOnDisplayDiscord(sender))
                                .replace("%leader%", PlayerUtils.getOffOnDisplayDiscord(PlayerUtils.getOrCreate(party.leaderUUID)))
                                .replace("%sender_normal%", PlayerUtils.getOffOnRegDiscord(sender))
                                .replace("%leader_normal%", PlayerUtils.getOffOnRegDiscord(PlayerUtils.getOrCreate(party.leaderUUID)))
                                .replace("%user%", PlayerUtils.getOffOnDisplayDiscord(member))
                                .replace("%user_normal%", PlayerUtils.getOffOnRegDiscord(member))
                                .replace("%size%", String.valueOf(party.maxSize))
                        , ConfigUtils.textChannelParties));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void joinParty(Player sender, Player from) {
        ProxiedPlayer p = UUIDFetcher.getPPlayerByUUID(sender.uuid);

        if (p == null) return;
        try {
            Party party = getParty(PlayerUtils.getStat(from));

            if (!isParty(party) || party == null) {
                MessagingUtils.sendBUserMessage(p, noPartyFound);
                return;
            }

            if (! party.hasMember(from)) {
                MessagingUtils.sendBUserMessage(p, otherNotInParty);
                return;
            }

            if (party.getSize() >= party.maxSize) {
                MessagingUtils.sendBPUserMessage(party, p, p, notEnoughSpace);
                return;
            }

            if (party.isPublic) {
                party.addMember(sender);

                for (Player pl : party.totalMembers) {
                    if (! pl.online) continue;

                    ProxiedPlayer m = UUIDFetcher.getPPlayerByUUID(pl.uuid);

                    if (m == null) continue;

                    if (pl.equals(sender)) {
                        MessagingUtils.sendBPUserMessage(party, p, m, joinUser
                                .replace("%user%", PlayerUtils.getOffOnDisplayBungee(sender))
                                .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                        );
                    } else {
                        MessagingUtils.sendBPUserMessage(party, p, m, joinMembers
                                .replace("%user%", PlayerUtils.getOffOnDisplayBungee(sender))
                                .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                        );
                    }
                }

                if (ConfigUtils.partyToDiscord && ConfigUtils.partyConsoleJoins) {
                    MessagingUtils.sendDiscordEBMessage(new DiscordMessage(p, joinsTitle,
                            joinsConsole
                                    .replace("%sender%", PlayerUtils.getOffOnDisplayDiscord(sender))
                                    .replace("%leader%", PlayerUtils.getOffOnDisplayDiscord(PlayerUtils.getOrCreate(party.leaderUUID)))
                                    .replace("%sender_normal%", PlayerUtils.getOffOnRegDiscord(sender))
                                    .replace("%leader_normal%", PlayerUtils.getOffOnRegDiscord(PlayerUtils.getOrCreate(party.leaderUUID)))
                                    .replace("%size%", String.valueOf(party.maxSize))
                            , ConfigUtils.textChannelParties));
                }
            } else {
                MessagingUtils.sendBPUserMessage(party, p, p, joinFailure);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int getMaxSize(Player leader){
        if (! StreamLine.lpHolder.enabled) return ConfigUtils.partyMax;

        try {
            Collection<PermissionNode> perms =
                    Objects.requireNonNull(StreamLine.lpHolder.api.getGroupManager().getGroup(
                            Objects.requireNonNull(StreamLine.lpHolder.api.getUserManager().getUser(leader.getName())).getPrimaryGroup()
                    )).getNodes(NodeType.PERMISSION);

            for (Group group : Objects.requireNonNull(StreamLine.lpHolder.api.getUserManager().getUser(leader.getName())).getInheritedGroups(QueryOptions.defaultContextualOptions())){
                perms.addAll(group.getNodes(NodeType.PERMISSION));
            }

            boolean isGood = false;

            int highestSize = 1;
            for (PermissionNode perm : perms) {
                try {
                    String p = perm.getPermission();
                    for (int i = 1; i <= ConfigUtils.partyMax; i++) {
                        String pTry = ConfigUtils.partyMaxPerm + i;
                        if (p.equals(pTry)) {
                            isGood = true;

                            if (highestSize < i)
                                highestSize = i;
                        }
                    }
                } catch (Exception e) {
                    // Do nothing.
                }
            }

            if (highestSize == 1)
                return ConfigUtils.partyMax;
            else if (isGood)
                return highestSize;
            else
                return ConfigUtils.partyMax;
        } catch (Exception e) {
            e.printStackTrace();
            return ConfigUtils.partyMax;
        }
    }

    public static void leaveParty(Player sender) {
        ProxiedPlayer p = UUIDFetcher.getPPlayerByUUID(sender.uuid);

        if (p == null) return;
        try {
            Party party = getParty(PlayerUtils.getStat(sender));

            if (!isParty(party) || party == null) {
                MessagingUtils.sendBUserMessage(p, noPartyFound);
                return;
            }

            if (! party.hasMember(sender)) {
                MessagingUtils.sendBUserMessage(p, notInParty);
                return;
            }

            if (party.leader.equals(sender)) {
                for (Player pl : party.totalMembers) {
                    if (! pl.online) continue;

                    ProxiedPlayer m = UUIDFetcher.getPPlayerByUUID(pl.uuid);

                    if (m == null) continue;

                    if (pl.equals(sender)) {
                        MessagingUtils.sendBPUserMessage(party, p, m, leaveUser);
                        MessagingUtils.sendBPUserMessage(party, p, m, disbandLeader);
                    } else {
                        MessagingUtils.sendBPUserMessage(party, p, m, leaveMembers);
                        MessagingUtils.sendBPUserMessage(party, p, m, disbandMembers);
                    }
                }

                parties.remove(party);
                party.dispose();
                return;
            }

            if (party.hasMember(sender)) {
                for (Player pl : party.totalMembers) {
                    if (! pl.online) continue;

                    ProxiedPlayer m = UUIDFetcher.getPPlayerByUUID(pl.uuid);

                    if (m == null) continue;

                    if (pl.equals(sender)) {
                        MessagingUtils.sendBPUserMessage(party, p, m, leaveUser
                                .replace("%user%", PlayerUtils.getOffOnDisplayBungee(sender))
                                .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                        );
                    } else {
                        MessagingUtils.sendBPUserMessage(party, p, m, leaveMembers
                                .replace("%user%", PlayerUtils.getOffOnDisplayBungee(sender))
                                .replace("%leader%", PlayerUtils.getOffOnDisplayBungee(Objects.requireNonNull(PlayerUtils.getStat(party.leader))))
                        );
                    }
                }

                party.removeMemberFromParty(sender);

                if (ConfigUtils.partyToDiscord && ConfigUtils.partyConsoleLeaves) {
                    MessagingUtils.sendDiscordEBMessage(new DiscordMessage(p, leaveTitle,
                            leaveConsole
                                    .replace("%sender%", PlayerUtils.getOffOnDisplayDiscord(sender))
                                    .replace("%leader%", PlayerUtils.getOffOnDisplayDiscord(PlayerUtils.getOrCreate(party.leaderUUID)))
                                    .replace("%sender_normal%", PlayerUtils.getOffOnRegDiscord(sender))
                                    .replace("%leader_normal%", PlayerUtils.getOffOnRegDiscord(PlayerUtils.getOrCreate(party.leaderUUID)))
                                    .replace("%size%", String.valueOf(party.maxSize))
                            , ConfigUtils.textChannelParties));
                }
            } else {
                MessagingUtils.sendBPUserMessage(party, p, p, leaveFailure);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void sendChat(Player sender, String msg) {
        ProxiedPlayer p = UUIDFetcher.getPPlayerByUUID(sender.uuid);

        if (p == null) return;
        try {
            Party party = getParty(PlayerUtils.getStat(sender));

            if (!isParty(party) || party == null) {
                MessagingUtils.sendBUserMessage(p, noPartyFound);
                return;
            }

            if (! party.hasMember(sender)) {
                MessagingUtils.sendBUserMessage(p, notInParty);
                return;
            }

            if (party.isMuted && ! party.hasModPerms(sender)) {
                MessagingUtils.sendBPUserMessage(party, p, p, chatMuted
                        .replace("%sender%", PlayerUtils.getOffOnDisplayBungee(sender))
                        .replace("%message%", msg)
                );
                return;
            }

//            if (ConfigUtils.partyConsoleChats) {
//                MessagingUtils.sendBPUserMessage(party, p, StreamLine.getInstance().getProxy().getConsole(), chatConsole
//                        .replace("%sender%", PlayerUtils.getOffOnDisplayBungee(sender))
//                        .replace("%message%", msg)
//                );
//            }

            for (Player pl : party.totalMembers) {
                if (! pl.online) continue;

                ProxiedPlayer m = UUIDFetcher.getPPlayerByUUID(pl.uuid);

                if (m == null) continue;

                MessagingUtils.sendBPUserMessage(party, p, m, chat
                        .replace("%sender%", PlayerUtils.getOffOnDisplayBungee(sender))
                        .replace("%message%", msg)
                );
            }

            if (ConfigUtils.partyToDiscord && ConfigUtils.partyConsoleChats) {
                MessagingUtils.sendDiscordEBMessage(new DiscordMessage(p, chatTitle,
                        chatConsole
                                .replace("%message%", msg)
                                .replace("%sender%", PlayerUtils.getOffOnDisplayDiscord(sender))
                                .replace("%leader%", PlayerUtils.getOffOnDisplayDiscord(PlayerUtils.getOrCreate(party.leaderUUID)))
                                .replace("%sender_normal%", PlayerUtils.getOffOnRegDiscord(sender))
                                .replace("%leader_normal%", PlayerUtils.getOffOnRegDiscord(PlayerUtils.getOrCreate(party.leaderUUID)))
                                .replace("%size%", String.valueOf(party.maxSize))
                        , ConfigUtils.textChannelParties));
            }

            for (ProxiedPlayer pp : StreamLine.getInstance().getProxy().getPlayers()){
                if (! pp.hasPermission(ConfigUtils.partyView)) continue;

                Player them = PlayerUtils.getStat(pp);

                if (them == null) continue;

                if (! them.pspy) continue;

                MessagingUtils.sendBPUserMessage(party, p, pp, spy.replace("%message%", msg));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // MESSAGES...
    // Text.
    public static final String textLeader = message.getString("party.text.leader");
    public static final String textModerator = message.getString("party.text.moderator");
    public static final String textMember = message.getString("party.text.member");
    // Spy.
    public static final String spy = message.getString("party.spy");
    // No party.
    public static final String noPartyFound = message.getString("party.no-party");
    // Already made.
    public static final String already = message.getString("party.already-made");
    // Already in one.
    public static final String alreadyHasOne = message.getString("party.already-has");
    // Too big.
    public static final String tooBig = message.getString("party.too-big");
    // Not high enough permissions.
    public static final String noPermission = message.getString("party.no-permission");
    // Not in a party.
    public static final String notInParty = message.getString("party.not-in-a-party");
    public static final String otherNotInParty = message.getString("party.other-not-in-party");
    // Not enough space in party.
    public static final String notEnoughSpace = message.getString("party.not-enough-space");
    // Chat.
    public static final String chat = message.getString("party.chat.message");
    public static final String chatMuted = message.getString("party.chat.muted");
    public static final String chatConsole = message.getString("party.chat.console");;
    public static final String chatTitle = message.getString("party.chat.title");
    // Create.
    public static final String create = message.getString("party.create.sender");
    public static final String createConsole = message.getString("party.create.console");
    public static final String createTitle = message.getString("party.create.title");
    // Join.
    public static final String joinMembers = message.getString("party.join.members");
    public static final String joinUser = message.getString("party.join.user");
    public static final String joinFailure = message.getString("party.join.failure");
    public static final String joinsConsole = message.getString("party.join.console");
    public static final String joinsTitle = message.getString("party.join.title");
    // Leave.
    public static final String leaveMembers = message.getString("party.leave.members");
    public static final String leaveUser = message.getString("party.leave.user");
    public static final String leaveFailure = message.getString("party.leave.failure");
    public static final String leaveConsole = message.getString("party.leave.console");
    public static final String leaveTitle = message.getString("party.leave.title");
    // Promote.
    public static final String promoteMembers = message.getString("party.promote.members");
    public static final String promoteUser = message.getString("party.promote.user");
    public static final String promoteLeader = message.getString("party.promote.leader");
    public static final String promoteFailure = message.getString("party.promote.failure");
    public static final String promoteConsole = message.getString("party.promote.console");
    public static final String promoteTitle = message.getString("party.promote.title");
    // Demote.
    public static final String demoteMembers = message.getString("party.demote.members");
    public static final String demoteUser = message.getString("party.demote.user");
    public static final String demoteLeader = message.getString("party.demote.leader");
    public static final String demoteFailure = message.getString("party.demote.failure");
    public static final String demoteIsLeader = message.getString("party.demote.is-leader");
    public static final String demoteConsole = message.getString("party.demote.console");
    public static final String demoteTitle = message.getString("party.demote.title");
    // List.
    public static final String listMain = message.getString("party.list.main");
    public static final String listLeaderBulk = message.getString("party.list.leaderbulk");
    public static final String listModBulkMain = message.getString("party.list.moderatorbulk.main");
    public static final String listModBulkNotLast = message.getString("party.list.moderatorbulk.moderators.not-last");
    public static final String listModBulkLast = message.getString("party.list.moderatorbulk.moderators.last");
    public static final String listModBulkNone = message.getString("party.list.moderatorbulk.moderators.if-none");
    public static final String listMemberBulkMain = message.getString("party.list.memberbulk.main");
    public static final String listMemberBulkNotLast = message.getString("party.list.memberbulk.members.not-last");
    public static final String listMemberBulkLast = message.getString("party.list.memberbulk.members.last");
    public static final String listMemberBulkNone = message.getString("party.list.memberbulk.members.if-none");
    // Open.
    public static final String openMembers = message.getString("party.open.members");
    public static final String openLeader = message.getString("party.open.leader");
    public static final String openFailure = message.getString("party.open.failure");
    public static final String openConsole = message.getString("party.open.console");
    public static final String openTitle = message.getString("party.open.title");
    // Close.
    public static final String closeMembers = message.getString("party.close.members");
    public static final String closeSender = message.getString("party.close.sender");
    public static final String closeFailure = message.getString("party.close.failure");
    public static final String closeConsole = message.getString("party.close.console");
    public static final String closeTitle = message.getString("party.close.title");
    // Disband.
    public static final String disbandMembers = message.getString("party.disband.members");
    public static final String disbandLeader = message.getString("party.disband.leader");
    public static final String disbandConsole = message.getString("party.disband.console");
    public static final String disbandTitle = message.getString("party.disband.title");
    // Accept.
    public static final String acceptUser = message.getString("party.accept.user");
    public static final String acceptLeader = message.getString("party.accept.leader");
    public static final String acceptMembers = message.getString("party.accept.members");
    public static final String acceptFailure = message.getString("party.accept.failure");
    public static final String acceptConsole = message.getString("party.accept.console");
    public static final String acceptTitle = message.getString("party.accept.title");
    // Deny.
    public static final String denyUser = message.getString("party.deny.user");
    public static final String denyLeader = message.getString("party.deny.leader");
    public static final String denyMembers = message.getString("party.deny.members");
    public static final String denyFailure = message.getString("party.deny.failure");
    public static final String denyConsole = message.getString("party.deny.console");
    public static final String denyTitle = message.getString("party.deny.title");
    // Invite.
    public static final String inviteUser = message.getString("party.invite.user");
    public static final String inviteLeader = message.getString("party.invite.leader");
    public static final String inviteMembers = message.getString("party.invite.members");
    public static final String inviteFailure = message.getString("party.invite.failure");
    public static final String inviteNonSelf = message.getString("party.invite.non-self");
    public static final String inviteConsole = message.getString("party.invite.console");
    public static final String inviteTitle = message.getString("party.invite.title");
    // Kick.
    public static final String kickUser = message.getString("party.kick.user");
    public static final String kickSender = message.getString("party.kick.sender");
    public static final String kickMembers = message.getString("party.kick.members");
    public static final String kickFailure = message.getString("party.kick.failure");
    public static final String kickMod = message.getString("party.kick.mod");
    public static final String kickSelf = message.getString("party.kick.self");
    public static final String kickConsole = message.getString("party.kick.console");
    public static final String kickTitle = message.getString("party.kick.title");
    // Mute.
    public static final String muteUser = message.getString("party.mute.mute.user");
    public static final String muteMembers = message.getString("party.mute.mute.members");
    public static final String unmuteUser = message.getString("party.mute.unmute.user");
    public static final String unmuteMembers = message.getString("party.mute.unmute.members");
    public static final String muteConsole = message.getString("party.mute.console");
    public static final String muteTitle = message.getString("party.mute.title");
    public static final String muteToggleMuted = message.getString("party.mute.toggle.muted");
    public static final String muteToggleUnMuted = message.getString("party.mute.toggle.unmuted");
    // Warp.
    public static final String warpSender = message.getString("party.warp.sender");
    public static final String warpMembers = message.getString("party.warp.members");
    public static final String warpConsole = message.getString("party.warp.console");
    public static final String warpTitle = message.getString("party.warp.title");
}
