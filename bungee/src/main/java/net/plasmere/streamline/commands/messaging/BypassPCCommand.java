package net.plasmere.streamline.commands.messaging;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
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

    public void run(CommandSender sender, String[] args) {
        if (TextUtils.isNullOrLessThanEqualTo(args, 0)) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
            return;
        }
        if (args.length > 1) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsLess());
            return;
        }

        if (sender instanceof ProxyServer) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.onlyPlayers());
            return;
        }

        SavablePlayer player = PlayerUtils.getOrGetPlayerStat(sender.getName());
        if (player == null) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorNoYou()
                            .replace("%class%", this.getClass().getName())
                    );
            return;
        }

        try {
            int amount = Integer.parseInt(args[0]);

            if (amount < 0) amount = 0;

            player.setBypassFor(amount);
        } catch (Exception e) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorSTime()
                            .replace("%class%", this.getClass().getName())
                    );
            return;
        }
    }

    @Override
    public Collection<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
