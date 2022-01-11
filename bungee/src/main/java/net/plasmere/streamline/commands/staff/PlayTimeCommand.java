package net.plasmere.streamline.commands.staff;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.objects.configs.PlayTimeConf;
import net.plasmere.streamline.objects.lists.SingleSet;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.TextUtils;
import net.plasmere.streamline.utils.UUIDUtils;

import java.util.*;

public class PlayTimeCommand extends SLCommand {
    public PlayTimeCommand(String base, String permission, String... aliases) {
        super(base, permission, aliases);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        PlayTimeConf playTimeConf = StreamLine.playTimeConf;

        if (args.length <= 0) {
            playTimeConf.toggleConsole();
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.playtimeConsoleToggle()
                    .replace("%toggle%",
                            (playTimeConf.getConsole() ? MessageConfUtils.playtimeConsoleEnabled() : MessageConfUtils.playtimeConsoleDisabled())
                    )
            );
        } else {
            if (args.length <= 1) {
            }

            switch (args[0]) {
                case "sync", "==", "?" -> {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.playtimeSyncStart());
                    PlayerUtils.syncPlayTime(false);
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.playtimeSyncFinish());
                }
                case "top", "t", "!" -> {
                    TreeMap<Integer, SingleSet<String, Integer>> map = StreamLine.playTimeConf.getPlayTimeAsMap();

                    int i = 1;
                    for (int playSeconds : map.keySet()) {
                        if (i > 10) break;
                        MessagingUtils.sendBUserMessage(sender,
                                TextUtils.replaceAllPlayerBungee(
                                        MessageConfUtils.playtimeTop().replace("%position%", String.valueOf(playSeconds)),
                                        PlayerUtils.getOrGetSavableUser(map.get(playSeconds).key)
                                )
                        );
                        i++;
                    }
                }
                default -> {
                    if (args.length <= 1) {
                        MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllSenderBungee(
                                TextUtils.replaceAllPlayerBungee(MessageConfUtils.playtimeGet(), PlayerUtils.getOrGetSavableUser(args[0])),
                                PlayerUtils.getOrGetSavableUser(sender)
                        ));
                        return;
                    }

                    switch (args[1]) {
                        case "remove", "rem", "r", "-" -> {
                            UUID uuid = UUID.fromString(UUIDUtils.getCachedUUID(args[0]));
                            if (args.length <= 2) {
                                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
                                return;
                            }
                            try {
                                playTimeConf.remPlayTime(uuid, Integer.parseInt(args[2]));

                                MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllSenderBungee(
                                                TextUtils.replaceAllPlayerBungee(MessageConfUtils.playtimeRemove(), PlayerUtils.getOrGetSavableUser(args[0])),
                                                PlayerUtils.getOrGetSavableUser(sender))
                                        .replace("%playtime%", args[2])
                                );
                            } catch (Exception e) {
                                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd()
                                        .replace("%class%", this.getClass().getName())
                                );
                                e.printStackTrace();
                                return;
                            }
                        }
                        case "add", "a", "+" -> {
                            UUID uuid2 = UUID.fromString(UUIDUtils.getCachedUUID(args[0]));
                            if (args.length <= 2) {
                                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
                                return;
                            }
                            try {
                                playTimeConf.addPlayTime(uuid2, Integer.parseInt(args[2]));

                                MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllSenderBungee(
                                                TextUtils.replaceAllPlayerBungee(MessageConfUtils.playtimeAdd(), PlayerUtils.getOrGetSavableUser(args[0])),
                                                PlayerUtils.getOrGetSavableUser(sender))
                                        .replace("%playtime%", args[2])
                                );
                            } catch (Exception e) {
                                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd()
                                        .replace("%class%", this.getClass().getName())
                                );
                                e.printStackTrace();
                                return;
                            }
                        }
                        /*case "set", "s", "="*/default -> {
                            UUID uuid3 = UUID.fromString(UUIDUtils.getCachedUUID(args[0]));
                            if (args.length <= 2) {
                                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
                                return;
                            }
                            try {
                                playTimeConf.setPlayTime(uuid3, Integer.parseInt(args[2]));

                                MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllSenderBungee(
                                                TextUtils.replaceAllPlayerBungee(MessageConfUtils.playtimeSet(), PlayerUtils.getOrGetSavableUser(args[0])),
                                                PlayerUtils.getOrGetSavableUser(sender))
                                        .replace("%playtime%", args[2])
                                );
                            } catch (Exception e) {
                                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd()
                                        .replace("%class%", this.getClass().getName())
                                );
                                e.printStackTrace();
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public Collection<String> onTabComplete(CommandSender sender, String[] args) {
        List<ProxiedPlayer> players = new ArrayList<>(PlayerUtils.getOnlinePPlayers());
        List<String> strPlayers = new ArrayList<>();
        List<String> secondTab = new ArrayList<>();

        secondTab.add("add");
        secondTab.add("remove");
        secondTab.add("set");
        secondTab.add("sync");

        if (args.length == 1) {
            for (ProxiedPlayer player : players) {
                strPlayers.add(player.getName());
            }

            return TextUtils.getCompletion(strPlayers, args[0]);
        } else if (args.length == 2) {
            return TextUtils.getCompletion(secondTab, args[1]);
        } else {
            return new ArrayList<>();
        }
    }
}
