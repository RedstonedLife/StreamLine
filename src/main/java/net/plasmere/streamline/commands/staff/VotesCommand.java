package net.plasmere.streamline.commands.staff;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.configs.Votes;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.TextUtils;
import net.plasmere.streamline.utils.UUIDUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VotesCommand extends Command implements TabExecutor {

    public VotesCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Votes votes = StreamLine.votes;

        if (args.length <= 0) {
            votes.toggleConsole();
            MessagingUtils.sendBUserMessage(sender, "&eToggled console printing to " + (votes.getConsole() ? "&aTRUE" : "&cFALSE") + "&8!");
        } else {
            UUID uuid = UUID.fromString(UUIDUtils.getCachedUUID(args[0]));

            if (args.length <= 1) {
                MessagingUtils.sendBUserMessage(sender, "&eVotes of &d" + args[0] + "&8: &6" + votes.getVotes(uuid));
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

                        MessagingUtils.sendBUserMessage(sender, "&eRemoved &6" + args[2] + " &evote(s) from &d" + args[0] + "&8! &eCurrent&8: &6" + votes.getVotes(uuid));
                    } catch (Exception e) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
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

                        MessagingUtils.sendBUserMessage(sender, "&eAdded &6" + args[2] + " &evote(s) to &d" + args[0] + "&8! &eCurrent&8: &6" + votes.getVotes(uuid));
                    } catch (Exception e) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
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

                        MessagingUtils.sendBUserMessage(sender, "&eSet &6" + args[2] + " &evotes for &d" + args[0] + "&8! &eCurrent&8: &6" + votes.getVotes(uuid));
                    } catch (Exception e) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                        e.printStackTrace();
                        return;
                    }
                    break;
            }
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
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
