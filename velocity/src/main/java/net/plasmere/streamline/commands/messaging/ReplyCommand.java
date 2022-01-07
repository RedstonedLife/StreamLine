package net.plasmere.streamline.commands.messaging;

import com.velocitypowered.api.command.CommandSource;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.objects.savable.users.SavableUser;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.TextUtils;

import java.util.ArrayList;
import java.util.Collection;

public class ReplyCommand extends SLCommand {
    public ReplyCommand(String base, String perm, String[] aliases){
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
            SavableUser statTo = PlayerUtils.getOrGetSavableUser(stat.replyToUUID);

            if (statTo == null) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPlayer());
                return;
            }

            PlayerUtils.doMessageWithIgnoreCheck(stat, statTo, TextUtils.normalize(args), true);
        }
    }

    @Override
    public Collection<String> onTabComplete(CommandSource sender, String[] args) {
        return new ArrayList<>();
    }
}
