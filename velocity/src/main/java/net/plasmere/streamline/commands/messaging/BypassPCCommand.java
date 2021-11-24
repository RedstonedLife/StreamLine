package net.plasmere.streamline.commands.messaging;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.TextUtils;

import java.util.ArrayList;
import java.util.Collection;

public class BypassPCCommand extends SLCommand {

    public BypassPCCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    public void run(CommandSource sender, String[] args) {
        if (TextUtils.isNullOrLessThanEqualTo(args, 0)) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
            return;
        }
        if (args.length > 1) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsLess());
            return;
        }

        if (sender instanceof ConsoleCommandSource) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.onlyPlayers());
            return;
        }

        SavablePlayer player = PlayerUtils.getOrGetPlayerStat(PlayerUtils.getSourceName(sender));
        if (player == null) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorNoYou());
            return;
        }

        try {
            int amount = Integer.parseInt(args[0]);

            if (amount < 0) amount = 0;

            player.setBypassFor(amount);
        } catch (Exception e) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorInt());
            return;
        }
    }

    @Override
    public Collection<String> onTabComplete(CommandSource sender, String[] args) {
        return new ArrayList<>();
    }
}
