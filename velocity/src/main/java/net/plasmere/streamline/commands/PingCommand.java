package net.plasmere.streamline.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.plasmere.streamline.config.CommandsConfUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collection;
import java.util.List;

public class PingCommand extends SLCommand {
    private String perm = "";

    public PingCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);

        this.perm = perm;
    }

    @Override
    public void run(CommandSource sender, String[] args){
        if (sender instanceof Player){
            if (args.length > 1) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
                return;
            }

            if (args.length <= 0) {
                if (sender.hasPermission(perm)) {
                    Player player = (Player) sender;

                    long ping = player.getPing();

                    MessagingUtils.sendBUserMessage(sender, "&ePing&8: &6%ping%&ams".replace("%ping%", String.valueOf(ping)));
                } else {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPerm());
                }
            } else {
                if (sender.hasPermission(CommandsConfUtils.comBPingPermOthers()) && CommandsConfUtils.comBPingOthers()) {
                    if (! PlayerUtils.getPlayerNamesForAllOnline().contains(args[0])) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPlayer());
                        return;
                    }

                    Player player = PlayerUtils.getPPlayer(args[0]);

                    long ping = player.getPing();

                    MessagingUtils.sendBUserMessage(sender, "&ePing&8: &6%ping%&ams".replace("%ping%", String.valueOf(ping)));
                } else {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPerm());
                }
            }
        } else {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.onlyPlayers());
        }
    }

    @Override
    public Collection<String> onTabComplete(CommandSource sender, String[] args) {
        List<String> strings = new ArrayList<>();

        if (sender.hasPermission(CommandsConfUtils.comBPingPermOthers()) && CommandsConfUtils.comBPingOthers()) {
            for (Player player : PlayerUtils.getOnlinePPlayers()) {
                strings.add(player.getUsername());
            }
        }

        return strings;
    }
}
