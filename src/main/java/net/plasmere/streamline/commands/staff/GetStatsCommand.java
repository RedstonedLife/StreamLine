package net.plasmere.streamline.commands.staff;

import com.velocitypowered.api.command.CommandSource;
import net.md_5.bungee.api.plugin.Command;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.savable.users.SavableUser;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.TextUtils;

public class GetStatsCommand extends Command {
    public GetStatsCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void execute(CommandSource sender, String[] args) {
        if (PlayerUtils.getStats().size() <= 0) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.getStatsNone());
            return;
        }

        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.getStatsMessage()
                .replace("%stats%", getStats())
        );
    }

    public static String getStats() {
        StringBuilder stringBuilder = new StringBuilder();

        int i = 1;
        for (SavableUser stat : PlayerUtils.getStats()) {
            if (i >= PlayerUtils.getStats().size()) {
                stringBuilder.append(TextUtils.replaceAllPlayerBungee(MessageConfUtils.getStatsLast(), stat));
            } else {
                stringBuilder.append(TextUtils.replaceAllPlayerBungee(MessageConfUtils.getStatsNLast(), stat));
            }
            i ++;
        }

        return stringBuilder.toString();
    }
}
