package net.plasmere.streamline.commands.staff;

import com.velocitypowered.api.command.CommandSource;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.utils.GuildUtils;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.TextUtils;

import java.util.ArrayList;

public class EndCommand extends SLCommand {
    public EndCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void run(CommandSource sender, String[] args) {
        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.gracefulEndSender());

        PlayerUtils.saveAll();

        PlayerUtils.kickAll(MessageConfUtils.gracefulEndKickMessage());

        GuildUtils.saveAll();

        StreamLine.getInstance().getProxy().shutdown(TextUtils.codedText(MessageConfUtils.kicksStopping()));
    }

    @Override
    public Iterable<String> onTabComplete(CommandSource sender, String[] args) {
        return new ArrayList<>();
    }
}
