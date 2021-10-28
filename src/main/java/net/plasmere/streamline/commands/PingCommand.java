package net.plasmere.streamline.commands;

import net.md_5.bungee.api.plugin.TabExecutor;
import net.plasmere.streamline.StreamLine;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.plasmere.streamline.config.CommandsConfUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PingCommand extends Command implements TabExecutor {
    private String perm = "";

    public PingCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);

        this.perm = perm;
    }

    @Override
    public void execute(CommandSender sender, String[] args){
        if (sender instanceof ProxiedPlayer){
            if (args.length > 1) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
                return;
            }

            if (args.length <= 0) {
                if (sender.hasPermission(perm)) {
                    ProxiedPlayer player = (ProxiedPlayer) sender;

                    long ping = player.getPing();

                    MessagingUtils.sendBUserMessage(sender, "&ePing&8: &6%ping%&ams".replace("%ping%", String.valueOf(ping)));
                } else {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPerm());
                }
            } else {
                if (sender.hasPermission(CommandsConfUtils.comBPingPermOthers) && CommandsConfUtils.comBPingOthers) {
                    if (! PlayerUtils.getPlayerNamesForAllOnline().contains(args[0])) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPlayer());
                        return;
                    }

                    ProxiedPlayer player = PlayerUtils.getPPlayer(args[0]);

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
    public Collection<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> strings = new ArrayList<>();

        if (sender.hasPermission(CommandsConfUtils.comBPingPermOthers) && CommandsConfUtils.comBPingOthers) {
            for (ProxiedPlayer player : PlayerUtils.getOnlinePPlayers()) {
                strings.add(player.getName());
            }
        }

        return strings;
    }
}
