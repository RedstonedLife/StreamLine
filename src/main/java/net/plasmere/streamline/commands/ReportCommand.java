package net.plasmere.streamline.commands;

import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.TextUtils;
import com.velocitypowered.api.command.CommandSource;

import java.util.ArrayList;

public class ReportCommand extends SLCommand {

    public ReportCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void run(CommandSource sender, String[] args) {
        String msg = TextUtils.normalize(args);

        if (msg.length() <= 0){
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.discordNeedsMore());
            return;
        }

        if (ConfigUtils.moduleReportsMToDiscord)
            if (ConfigUtils.moduleDEnabled)
                MessagingUtils.sendDiscordReportMessage(PlayerUtils.getSourceName(sender), true, msg);
        if (ConfigUtils.moduleReportsSendChat)
            MessagingUtils.sendStaffMessageReport(PlayerUtils.getSourceName(sender), true, msg);
        if (ConfigUtils.moduleReportsBConfirmation)
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bConfirmReportMessage()
                    .replace("%reporter%", PlayerUtils.getSourceName(sender))
                    .replace("%report%", TextUtils.normalize(args))
            );
    }

    @Override
    public Iterable<String> onTabComplete(CommandSource sender, String[] args) {
        return new ArrayList<>();
    }
}
