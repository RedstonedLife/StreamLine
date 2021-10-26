package net.plasmere.streamline.commands.messaging;

import com.velocitypowered.api.command.CommandSource;
import net.md_5.bungee.api.plugin.Command;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.savable.users.SavableUser;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.TextUtils;

public class ReplyCommand extends Command {
    public ReplyCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void execute(CommandSource sender, String[] args) {
        String thing = "";

        if (PlayerUtils.isInOnlineList(PlayerUtils.getSourceName(sender))) thing = PlayerUtils.getSourceName(sender);
        else thing = "%";

        SavableUser stat = PlayerUtils.getOrGetSavableUser(thing);

        if (stat == null) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorNoYou());
            return;
        }

        if (args.length <= 0) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
        } else {
            SavableUser statTo = PlayerUtils.getOrGetSavableUser(stat.replyToUUID);

            if (statTo == null) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPlayer());
                return;
            }

            PlayerUtils.doMessageWithIgnoreCheck(stat, statTo, TextUtils.normalize(args), true);
        }
    }
}
