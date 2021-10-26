package net.plasmere.streamline.commands;

import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.TextUtils;
import com.velocitypowered.api.command.CommandSource;
import net.md_5.bungee.api.plugin.Command;

public class ReportCommand extends Command {

    public ReportCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void execute(CommandSource sender, String[] args) {
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
}
