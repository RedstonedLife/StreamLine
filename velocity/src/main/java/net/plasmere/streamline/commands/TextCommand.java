package net.plasmere.streamline.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.TextUtils;

import java.util.ArrayList;
import java.util.Collection;

public class TextCommand extends SLCommand {

    public TextCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    public void run(CommandSource sender, String[] args) {
        if (args.length <= 1) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
            return;
        }

        Player player = PlayerUtils.getPPlayer(args[0]);
        if (player == null) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPlayer());
            return;
        }

        String toSend = TextUtils.argsToStringMinus(args, 0);

        MessagingUtils.sendBUserMessage(player, toSend);
        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.proxyTextSent()
                .replace("%text%", toSend)
        );
    }

    @Override
    public Collection<String> onTabComplete(CommandSource sender, String[] args) {
        return TextUtils.getCompletion(PlayerUtils.getPlayerNamesForAllOnline(), args[args.length - 1]);
    }
}
