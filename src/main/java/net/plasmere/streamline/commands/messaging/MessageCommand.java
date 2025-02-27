package net.plasmere.streamline.commands.messaging;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.plasmere.streamline.objects.command.SLCommand;
import java.util.Collection;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.MessageConfUtils;
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
    public void run(CommandSender sender, String[] args) {
        String thing = "";

        if (PlayerUtils.isInOnlineList(sender.getName())) thing = sender.getName();
        else thing = "%";

        SavableUser stat = PlayerUtils.getOrGetSavableUser(thing);

        if (stat == null) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorNoYou());
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
                    MessagingUtils.sendBUserMessage(sender, PlayerUtils.noStatsFound);
                    return;
                }

                statTo = PlayerUtils.getOrGetSavableUser(args[0]);
            }

            if (statTo == null) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPlayer());
                return;
            }

            PlayerUtils.doMessageWithIgnoreCheck(stat, statTo, TextUtils.argsToStringMinus(args, 0), false);
        }
    }

    @Override
    public Collection<String> tabComplete(CommandSender sender, String[] args) {
        Collection<ProxiedPlayer> players = StreamLine.getInstance().getProxy().getPlayers();
        List<String> strPlayers = new ArrayList<>();
        List<String> ignored = new ArrayList<>();


        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) sender;
            SavablePlayer player = PlayerUtils.getOrCreatePlayerStatByUUID(p.getUniqueId().toString());
            for (String uuid : player.ignoredList) {
                ignored.add(UUIDUtils.getCachedName(uuid));
            }
        }

        for (ProxiedPlayer pl : players) {
            if (sender instanceof ProxiedPlayer) {
                if (pl.equals(sender)) continue;
                if (ignored.contains(pl.getName())) continue;
            }
            strPlayers.add(pl.getName());
        }

        strPlayers.add("%");

        if (args.length == 1) {
            return TextUtils.getCompletion(strPlayers, args[0]);
        }

        return new ArrayList<>();
    }
}
