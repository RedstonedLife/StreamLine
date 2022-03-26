package net.plasmere.streamline.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.TextUtils;

import java.util.Collection;

public class TextCommand extends SLCommand {
    public TextCommand(String base, String permission, String... aliases) {
        super(base, permission, aliases);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (args.length <= 1) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
            return;
        }

        ProxiedPlayer player = PlayerUtils.getPPlayer(args[0]);
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
    public Collection<String> onTabComplete(CommandSender sender, String[] args) {
        return TextUtils.getCompletion(PlayerUtils.getPlayerNamesForAllOnline(), args[args.length - 1]);
    }
}
