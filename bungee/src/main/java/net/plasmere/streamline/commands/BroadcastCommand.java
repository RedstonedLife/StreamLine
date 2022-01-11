package net.plasmere.streamline.commands;

import net.md_5.bungee.api.CommandSender;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.TextUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BroadcastCommand extends SLCommand {

    public BroadcastCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    public void run(CommandSender sender, String[] args) {
        if (TextUtils.isNullOrLessThanEqualTo(args, 0)) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
            return;
        }

        String message = TextUtils.normalize(args);

        if (message.startsWith("!")) {
            message = message.substring(1);
            MessagingUtils.sendBBroadcast(sender, MessageConfUtils.broadcastMessageWithout()
                    .replace("%message%", message)
            );
        } else {
            MessagingUtils.sendBBroadcast(sender, MessageConfUtils.broadcastMessageWith()
                    .replace("%prefix%", MessageConfUtils.broadcastPrefix())
                    .replace("%message%", message)
            );
        }
    }

    @Override
    public Collection<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return TextUtils.getCompletion(List.of("!"), args[0]);
        }

        return new ArrayList<>();
    }
}
