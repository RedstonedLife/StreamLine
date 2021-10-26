package net.plasmere.streamline.commands.staff;

import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.utils.*;
import com.velocitypowered.api.command.CommandSource;

import java.util.ArrayList;
import java.util.Collection;

public class ReloadCommand extends SLCommand {
    private String perm = "";

    public ReloadCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);

        this.perm = perm;
    }

    @Override
    public void run(CommandSource sender, String[] strings) {
        if (sender.hasPermission(perm)) {
            try {
                StreamLine.config.reloadConfig();
                StreamLine.config.reloadLocales();
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.prefix() + MessageConfUtils.reload());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.prefix() + MessageConfUtils.noPerm());
        }
    }

    @Override
    public Collection<String> onTabComplete(CommandSource sender, String[] args) {
        return new ArrayList<>();
    }
}
