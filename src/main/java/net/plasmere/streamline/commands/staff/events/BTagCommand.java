package net.plasmere.streamline.commands.staff.events;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.CommandsConfUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.objects.savable.users.SavableUser;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.TextUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BTagCommand extends SLCommand {
    public BTagCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void run(CommandSource sender, String[] args) {
        if (args.length <= 0) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
        } else {
            if (! PlayerUtils.exists(args[0])) {
                MessagingUtils.sendBUserMessage(sender, PlayerUtils.noStatsFound);
                return;
            }

            SavableUser stat = PlayerUtils.getOrGetSavableUser(args[0]);

            if (stat == null) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPlayer());
                return;
            }

            if (! stat.latestName.equals(PlayerUtils.getSourceName(sender))) {
                if (! sender.hasPermission(CommandsConfUtils.comBBTagOPerm)) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPerm());
                    return;
                }
            }

            switch (args[1]){
                case "remove":
                case "rem":
                case "r":
                case "-":
                    if (! sender.hasPermission(CommandsConfUtils.comBBTagChPerm)) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPerm());
                        return;
                    }

                    if (args.length <= 2) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
                        return;
                    }
                    PlayerUtils.remTag(sender, stat, args[2]);
                    break;
                case "add":
                case "a":
                case "+":
                    if (! sender.hasPermission(CommandsConfUtils.comBBTagChPerm)) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPerm());
                        return;
                    }

                    if (args.length <= 2) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
                        return;
                    }
                    PlayerUtils.addTag(sender, stat, args[2]);
                    break;
                case "list":
                case "l":
                case "?":
                default:
                    PlayerUtils.listTags(sender, stat);
                    break;
            }
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSource sender, String[] args) {
        if (! sender.hasPermission(CommandsConfUtils.comBBTagPerm)) return new ArrayList<>();

        Collection<Player> players = StreamLine.getInstance().getProxy().getAllPlayers();
        List<String> strPlayers = new ArrayList<>();
        List<String> secondTab = new ArrayList<>();

        secondTab.add("add");
        secondTab.add("remove");
        secondTab.add("list");

        if (args.length == 1) {
            for (Player player : players) {
                strPlayers.add(PlayerUtils.getSourceName(player));
            }

            return TextUtils.getCompletion(strPlayers, args[0]);
        } else if (args.length == 2) {
            return TextUtils.getCompletion(secondTab, args[1]);
        } else {
            return new ArrayList<>();
        }
    }
}
