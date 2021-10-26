package net.plasmere.streamline.commands.staff;

import com.velocitypowered.api.command.CommandSource;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.objects.savable.users.SavableUser;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.TextUtils;

import java.util.ArrayList;

public class GetStatsCommand extends SLCommand {
    public GetStatsCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void run(CommandSource sender, String[] args) {
        if (PlayerUtils.getStats().size() <= 0) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.getStatsNone());
            return;
        }

        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.getStatsMessage()
                .replace("%stats%", getStats())
        );
    }

    @Override
    public Iterable<String> onTabComplete(CommandSource sender, String[] args) {
        return new ArrayList<>();
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
