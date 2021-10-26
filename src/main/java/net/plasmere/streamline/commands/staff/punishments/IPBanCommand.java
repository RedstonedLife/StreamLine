package net.plasmere.streamline.commands.staff.punishments;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.plasmere.streamline.config.backend.Configuration;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.DiscordBotConfUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.messaging.DiscordMessage;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.utils.*;
import org.apache.commons.collections4.list.TreeList;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class IPBanCommand extends Command implements TabExecutor {
    private Configuration bans = StreamLine.bans.getBans();

    public IPBanCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void execute(CommandSource sender, String[] args) {
        if (args.length <= 1) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
        } else if (args.length > 2 && ! args[0].equals("add") && ! args[0].equals("temp")) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsLess());
        } else {
            List<String> ipsToBan = new ArrayList<>();
            if (args[1].contains(".")) {
                ipsToBan.add(args[1]);
            } else {
                if (! (args[0].equals("check") && (args[1].equals("*") || args[1].equals("all")))) {
                    SavablePlayer other = PlayerUtils.getOrGetPlayerStat(args[1]);

                    if (other == null) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPlayer());
                        return;
                    }

                    ipsToBan.addAll(other.ipList);
                }
            }

            if (args[0].equals("add")) {
                if (args.length < 3) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
                    return;
                }

                if (! args[1].contains(".")) {
                    SavablePlayer other = PlayerUtils.getPlayerStat(args[1]);

                    if (other == null) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPlayer());
                        return;
                    }

                    String otherUUID = other.uuid;

                    if (PlayerUtils.hasOfflinePermission(ConfigUtils.punIPBansBypass, otherUUID)) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.ipBanCannot());
                        return;
                    }
                }

                if (args.length >= 4 && (args[2].endsWith("y") || args[2].endsWith("mo") || args[2].endsWith("w") || args[2].endsWith("d") || args[2].endsWith("h") || args[2].endsWith("m") || args[2].endsWith("s"))) {
                    for (String ip : ipsToBan) {
                        String ipToBan = ip.replace(".", "_");
                        if (! ConfigUtils.punIPBansReplaceable) {
                            if (bans.contains(ipToBan)) {
                                if (bans.getBoolean(ipToBan + ".banned")) {
                                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.ipBanBTempAlready()
                                            .replace("%ip%", ip)
                                    );
                                    return;
                                }
                            }
                        }

                        double toAdd = 0d;

                        try {
                            toAdd = TimeUtil.convertStringTimeToDouble(args[2]);
                        } catch (Exception e) {
                            e.printStackTrace();
                            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorSTime());
                            return;
                        }

                        String till = String.valueOf((long) (System.currentTimeMillis() + toAdd));

                        String reason = TextUtils.argsToStringMinus(args, 0, 1, 2);

                        bans.set(ipToBan + ".banned", true);
                        bans.set(ipToBan + ".reason", reason);
                        bans.set(ipToBan + ".till", till);
                        bans.set(ipToBan + ".sentenced", Instant.now().toString());
                        StreamLine.bans.saveConfig();

                        for (SavablePlayer player : PlayerUtils.getPlayerStatsByIP(ip)) {
                            if (player.online) {
                                Player pp = PlayerUtils.getPPlayerByUUID(player.uuid);

                                if (pp != null) {
                                    pp.disconnect(TextUtils.codedText(MessageConfUtils.punIPBannedTemp()
                                            .replace("%reason%", reason)
                                            .replace("%date%", new Date(Long.parseLong(till)).toString())
                                    ));
                                }
                            }
                        }

                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.ipBanBTempSender()
                                .replace("%ip%", ip)
                                .replace("%reason%", reason)
                                .replace("%date%", new Date(Long.parseLong(till)).toString())
                        );

                        if (ConfigUtils.moduleDEnabled) {
                            if (ConfigUtils.punIPBansDiscord) {
                                MessagingUtils.sendDiscordEBMessage(
                                        new DiscordMessage(
                                                sender,
                                                MessageConfUtils.ipBanEmbed(),
                                                MessageConfUtils.ipBanBTempDiscord()
                                                        .replace("%punisher%", PlayerUtils.getSourceName(sender))
                                                        .replace("%ip%", ip)
                                                        .replace("%reason%", reason)
                                                        .replace("%date%", new Date(Long.parseLong(till)).toString())
                                                ,
                                                DiscordBotConfUtils.textChannelIPBans
                                        )
                                );
                            }
                        }

                        MessagingUtils.sendPermissionedMessageNonSelf(sender, ConfigUtils.staffPerm, MessageConfUtils.ipBanBTempStaff()
                                .replace("%punisher%", PlayerUtils.getSourceName(sender))
                                .replace("%ip%", ip)
                                .replace("%reason%", reason)
                                .replace("%date%", new Date(Long.parseLong(till)).toString())
                        );
                    }

                    return;
                }

                for (String ip : ipsToBan) {
                    String ipToBan = ip.replace(".", "_");
                    if (! ConfigUtils.punIPBansReplaceable) {
                        if (bans.contains(ipToBan)) {
                            if (bans.getBoolean(ipToBan + ".banned")) {
                                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.ipBanBPermAlready()
                                        .replace("%ip%", ip)
                                );
                                return;
                            }
                        }
                    }

                    String reason = TextUtils.argsToStringMinus(args, 0, 1);

                    bans.set(ipToBan + ".banned", true);
                    bans.set(ipToBan + ".reason", reason);
                    bans.set(ipToBan + ".till", "");
                    bans.set(ipToBan + ".sentenced", Instant.now().toString());
                    StreamLine.bans.saveConfig();

                    for (SavablePlayer player : PlayerUtils.getPlayerStatsByIP(ip)) {
                        if (player.online) {
                            Player pp = PlayerUtils.getPPlayerByUUID(player.uuid);

                            if (pp != null) {
                                pp.disconnect(TextUtils.codedText(MessageConfUtils.punIPBannedPerm()
                                        .replace("%reason%", reason)
                                ));
                            }
                        }
                    }

                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.ipBanBPermSender()
                            .replace("%ip%", ip)
                            .replace("%reason%", reason)
                    );

                    if (ConfigUtils.moduleDEnabled) {
                        if (ConfigUtils.punIPBansDiscord) {
                            MessagingUtils.sendDiscordEBMessage(
                                    new DiscordMessage(
                                            sender,
                                            MessageConfUtils.ipBanEmbed(),
                                            MessageConfUtils.ipBanBPermDiscord()
                                                    .replace("%punisher%", PlayerUtils.getSourceName(sender))
                                                    .replace("%ip%", ip)
                                                    .replace("%reason%", reason)
                                            ,
                                            DiscordBotConfUtils.textChannelIPBans
                                    )
                            );
                        }
                    }

                    MessagingUtils.sendPermissionedMessageNonSelf(sender, ConfigUtils.staffPerm, MessageConfUtils.ipBanBPermStaff()
                            .replace("%punisher%", PlayerUtils.getSourceName(sender))
                            .replace("%ip%", ip)
                            .replace("%reason%", reason)
                    );
                }
            } else if (args[0].equals("temp")) {
                if (args.length < 4) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
                    return;
                }

                if (!args[1].contains(".")) {
                    SavablePlayer other = PlayerUtils.getPlayerStat(args[1]);

                    if (other == null) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                        return;
                    }

                    String otherUUID = other.uuid;

                    if (PlayerUtils.hasOfflinePermission(ConfigUtils.punIPBansBypass, otherUUID)) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.ipBanCannot());
                        return;
                    }
                }
                for (String ip : ipsToBan) {
                    String ipToBan = ip.replace(".", "_");
                    if (!ConfigUtils.punIPBansReplaceable) {
                        if (bans.contains(ipToBan)) {
                            if (bans.getBoolean(ipToBan + ".banned")) {
                                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.ipBanBTempAlready()
                                        .replace("%ip%", ip)
                                );
                                return;
                            }
                        }
                    }

                    double toAdd = 0d;

                    try {
                        toAdd = TimeUtil.convertStringTimeToDouble(args[2]);
                    } catch (Exception e) {
                        e.printStackTrace();
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorSTime());
                        return;
                    }

                    String till = String.valueOf((long) (System.currentTimeMillis() + toAdd));

                    String reason = TextUtils.argsToStringMinus(args, 0, 1, 2);

                    bans.set(ipToBan + ".banned", true);
                    bans.set(ipToBan + ".reason", reason);
                    bans.set(ipToBan + ".till", till);
                    bans.set(ipToBan + ".sentenced", Instant.now().toString());
                    StreamLine.bans.saveConfig();

                    for (SavablePlayer player : PlayerUtils.getPlayerStatsByIP(ip)) {
                        if (player.online) {
                            Player pp = PlayerUtils.getPPlayerByUUID(player.uuid);

                            if (pp != null) {
                                pp.disconnect(TextUtils.codedText(MessageConfUtils.punIPBannedTemp()
                                        .replace("%reason%", reason)
                                        .replace("%date%", new Date(Long.parseLong(till)).toString())
                                ));
                            }
                        }
                    }

                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.ipBanBTempSender()
                            .replace("%ip%", ip)
                            .replace("%reason%", reason)
                            .replace("%date%", new Date(Long.parseLong(till)).toString())
                    );

                    if (ConfigUtils.punIPBansDiscord) {
                        MessagingUtils.sendDiscordEBMessage(
                                new DiscordMessage(
                                        sender,
                                        MessageConfUtils.ipBanEmbed(),
                                        MessageConfUtils.ipBanBTempDiscord()
                                                .replace("%punisher%", PlayerUtils.getSourceName(sender))
                                                .replace("%ip%", ip)
                                                .replace("%reason%", reason)
                                                .replace("%date%", new Date(Long.parseLong(till)).toString())
                                        ,
                                        DiscordBotConfUtils.textChannelIPBans
                                )
                        );
                    }

                    MessagingUtils.sendPermissionedMessageNonSelf(sender, ConfigUtils.staffPerm, MessageConfUtils.ipBanBTempStaff()
                            .replace("%punisher%", PlayerUtils.getSourceName(sender))
                            .replace("%ip%", ip)
                            .replace("%reason%", reason)
                            .replace("%date%", new Date(Long.parseLong(till)).toString())
                    );
                }
            } else if (args[0].equals("remove")) {

                for (String ip : ipsToBan) {
                    String ipToBan = ip.replace(".", "_");
                    if (! bans.contains(ipToBan)) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.ipBanUnAlready()
                                .replace("%ip%", ip)
                        );
                        return;
                    }

                    bans.set(ipToBan + ".banned", false);
                    StreamLine.bans.saveConfig();

                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.ipBanUnSender()
                            .replace("%ip%", ip)
                    );

                    if (ConfigUtils.punIPBansDiscord) {
                        MessagingUtils.sendDiscordEBMessage(
                                new DiscordMessage(
                                        sender,
                                        MessageConfUtils.ipBanEmbed(),
                                        MessageConfUtils.ipBanUnDiscord()
                                                .replace("%punisher%", PlayerUtils.getSourceName(sender))
                                                .replace("%ip%", ip)
                                        ,
                                        DiscordBotConfUtils.textChannelIPBans
                                )
                        );
                    }

                    MessagingUtils.sendPermissionedMessageNonSelf(sender, ConfigUtils.staffPerm, MessageConfUtils.ipBanUnStaff()
                            .replace("%punisher%", PlayerUtils.getSourceName(sender))
                            .replace("%ip%", ip)
                    );
                }
            } else if (args[0].equals("check")) {
                if (args[1].equals("all") || args[1].equals("*")) {
                    Collection<String> banned = bans.getKeys();
                    TreeList<String> bannedIPs = new TreeList<>();

                    for (String ban : banned) {
                        if (ban.contains("_")) bannedIPs.add(ban);
                    }

                    ipsToBan.clear();
                    ipsToBan.addAll(bannedIPs);
                }

                for (String ip : ipsToBan) {
                    String bannedIP = ip.replace(".", "_");
                    String reason = bans.getString(bannedIP + ".reason");
                    String bannedMillis = bans.getString(bannedIP + ".till");
                    if (bannedMillis == null) bannedMillis = "";

                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.ipBanCheckMain()
                            .replace("%ip%", ip)
                            .replace("%check%", bans.getBoolean(ip + ".banned") ? MessageConfUtils.ipBanCheckBanned()
                                    .replace("%date%", (!bannedMillis.equals("") ? new Date(Long.parseLong(bannedMillis)).toString() : MessageConfUtils.ipBanCheckNoDate()))
                                    .replace("%reason%", reason)
                                    : MessageConfUtils.ipBanCheckUnBanned())
                    );
                }
            }
        }
    }

    @Override
    public Iterable<String> onTabComplete(final CommandSource sender, final String[] args) {
        Collection<Player> players = StreamLine.getInstance().getProxy().getAllPlayers();
        List<String> strPlayers = new ArrayList<>();
        List<String> banned = new ArrayList<>();

        for (Player player : players){
            if (sender instanceof Player) if (player.equals(sender)) continue;
            strPlayers.add(PlayerUtils.getSourceName(player));
        }

        for (String ip : bans.getKeys()) {
            if (! ip.contains("_")) continue;
            ip = ip.replace("_", ".");
            if (bans.getBoolean(ip + ".banned")) banned.add(ip);
        }

        List<String> options = new ArrayList<>();

        options.add("add");
        options.add("temp");
        options.add("remove");
        options.add("check");

        List<String> check = new ArrayList<>();

        check.add("*");
        check.add("all");

        if (args.length == 1) {
            return TextUtils.getCompletion(options, args[0]);
        } else if (args.length == 2) {
            if (args[0].equals("remove")) {
                return TextUtils.getCompletion(banned, args[1]);
            } else if (args[0].equals("check")) {
                return TextUtils.getCompletion(check, args[1]);
            } else {
                return TextUtils.getCompletion(strPlayers, args[1]);
            }
        }

        return new ArrayList<>();
    }
}
