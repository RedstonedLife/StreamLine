package net.plasmere.streamline.commands.staff;

import net.md_5.bungee.api.CommandSender;
import net.plasmere.streamline.objects.command.SLCommand;

import java.util.ArrayList;
import java.util.Collection;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.utils.GuildUtils;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.TextUtils;

public class EndCommand extends SLCommand {
    public EndCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.gracefulEndSender());

        PlayerUtils.saveAll();

        PlayerUtils.kickAll(MessageConfUtils.gracefulEndKickMessage());

        GuildUtils.saveAll();

        StreamLine.getInstance().getProxy().stop(TextUtils.codedString(MessageConfUtils.kicksStopping()));
    }

    @Override
    public Collection<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
