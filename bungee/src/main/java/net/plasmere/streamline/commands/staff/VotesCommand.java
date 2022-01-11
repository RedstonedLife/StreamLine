package net.plasmere.streamline.commands.staff;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.objects.configs.Votes;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.TextUtils;
import net.plasmere.streamline.utils.UUIDUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class VotesCommand extends SLCommand {
    public VotesCommand(String base, String permission, String... aliases) {
        super(base, permission, aliases);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        Votes votes = StreamLine.votes;

        if (args.length <= 0) {
            votes.toggleConsole();
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.votesConsoleToggle()
                    .replace("%toggle%",
                            (votes.getConsole() ? MessageConfUtils.votesConsoleEnabled() : MessageConfUtils.votesConsoleDisabled())
                    )
            );
        } else {
            UUID uuid = UUID.fromString(UUIDUtils.getCachedUUID(args[0]));

            if (args.length <= 1) {
                MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllSenderBungee(
                        TextUtils.replaceAllPlayerBungee(MessageConfUtils.votesGet(), PlayerUtils.getOrGetSavableUser(args[0])),
                        PlayerUtils.getOrGetSavableUser(sender)
                ));
                return;
            }

            switch (args[1]){
                case "remove":
                case "rem":
                case "r":
                case "-":
                    if (args.length <= 2) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
                        return;
                    }

                    try {
                        votes.remVotes(uuid, Integer.parseInt(args[2]));

                        MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllSenderBungee(
                                        TextUtils.replaceAllPlayerBungee(MessageConfUtils.votesRemove(), PlayerUtils.getOrGetSavableUser(args[0])),
                                        PlayerUtils.getOrGetSavableUser(sender))
                                .replace("%votes%", args[2])
                        );
                    } catch (Exception e) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd()
                            .replace("%class%", this.getClass().getName())
                    );
                        e.printStackTrace();
                        return;
                    }
                    break;
                case "add":
                case "a":
                case "+":
                    if (args.length <= 2) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
                        return;
                    }

                    try {
                        votes.addVotes(uuid, Integer.parseInt(args[2]));

                        MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllSenderBungee(
                                        TextUtils.replaceAllPlayerBungee(MessageConfUtils.votesAdd(), PlayerUtils.getOrGetSavableUser(args[0])),
                                        PlayerUtils.getOrGetSavableUser(sender))
                                .replace("%votes%", args[2])
                        );
                    } catch (Exception e) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd()
                            .replace("%class%", this.getClass().getName())
                    );
                        e.printStackTrace();
                        return;
                    }
                    break;
                case "set":
                case "s":
                case "=":
                default:
                    if (args.length <= 2) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
                        return;
                    }

                    try {
                        votes.setVotes(uuid, Integer.parseInt(args[2]));

                        MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllSenderBungee(
                                        TextUtils.replaceAllPlayerBungee(MessageConfUtils.votesSet(), PlayerUtils.getOrGetSavableUser(args[0])),
                                        PlayerUtils.getOrGetSavableUser(sender))
                                .replace("%votes%", args[2])
                        );
                    } catch (Exception e) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd()
                            .replace("%class%", this.getClass().getName())
                    );
                        e.printStackTrace();
                        return;
                    }
                    break;
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
