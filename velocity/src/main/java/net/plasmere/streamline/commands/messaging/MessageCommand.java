package net.plasmere.streamline.commands.messaging;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.objects.savable.users.SavableUser;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.TextUtils;
import net.plasmere.streamline.utils.UUIDUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MessageCommand extends SLCommand {
    public MessageCommand(String base, String perm, String[] aliases) {
        super(base, perm, aliases);
    }

    @Override
    public void run(CommandSource sender, String[] args) {
        String thing = "";

        if (PlayerUtils.isInOnlineList(PlayerUtils.getSourceName(sender))) thing = PlayerUtils.getSourceName(sender);
        else thing = "%";

        SavableUser stat = PlayerUtils.getOrGetSavableUser(thing);

        if (stat == null) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorNoYou()
                            .replace("%class%", this.getClass().getName())
                    );
            return;
        }

        if (args.length <= 0) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
        } else {
            SavableUser statTo;

            if (args[0].equals("%")) {
                statTo = PlayerUtils.getSavableUserByUUID("%");
            } else {
                if (! PlayerUtils.exists(args[0])) {
                    MessagingUtils.sendBUserMessage(sender, PlayerUtils.noStatsFound.replace("%class%", this.getClass().getName()));
                    return;
                }

                MessagingUtils.logInfo(args[0]);
                statTo = PlayerUtils.getOrGetSavableUser(args[0]);
//                MessagingUtils.logInfo("1" + statTo.toString());
            }


//            MessagingUtils.logInfo("2" + statTo.toString());

            if (statTo == null) {
//                MessagingUtils.logInfo("3" + statTo.toString());
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPlayer());
                return;
            }

            PlayerUtils.doMessageWithIgnoreCheck(stat, statTo, TextUtils.argsToStringMinus(args, 0), false);
        }
    }

    @Override
    public Collection<String> onTabComplete(CommandSource sender, String[] args) {
        Collection<Player> players = StreamLine.getInstance().getProxy().getAllPlayers();
        List<String> strPlayers = new ArrayList<>();
        List<String> ignored = new ArrayList<>();


        if (sender instanceof Player) {
            Player p = (Player) sender;
            SavablePlayer player = PlayerUtils.getOrGetPlayerStatByUUID(p.getUniqueId().toString());
            for (String uuid : player.ignoredList) {
                ignored.add(UUIDUtils.getCachedName(uuid));
            }
        }

        for (Player pl : players) {
            if (sender instanceof Player) {
                if (pl.equals(sender)) continue;
                if (ignored.contains(PlayerUtils.getSourceName(pl))) continue;
            }
            strPlayers.add(PlayerUtils.getSourceName(pl));
        }

        strPlayers.add("%");

        if (args.length == 1) {
            return TextUtils.getCompletion(strPlayers, args[0]);
        }

        return new ArrayList<>();
    }
}
