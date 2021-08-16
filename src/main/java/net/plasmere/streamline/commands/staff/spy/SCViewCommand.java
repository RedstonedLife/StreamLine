package net.plasmere.streamline.commands.staff.spy;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.users.Player;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.PluginUtils;
import net.plasmere.streamline.utils.TextUtils;

import java.util.ArrayList;

public class SCViewCommand extends Command implements TabExecutor {

    public SCViewCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            Player player = PlayerUtils.getPlayerStat(sender);
            if (player == null) return;

            if (args.length > 0) {
                if (PluginUtils.checkEqualsStrings(args[0], PluginUtils.stringListToArray(ConfigUtils.viewSelfAliases))) {
                    player.toggleSCVS();
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.scvsToggle
                            .replace("%toggle%", (player.scvs ? MessageConfUtils.scvsOn : MessageConfUtils.scvsOff))
                    );
                    return;
                }
            }

            player.toggleSCView();

            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.scViewToggle
//                    .replace("%toggle%", (player.viewsc ? "&aON" : "&cOFF"))
                    .replace("%toggle%", (player.viewsc ? MessageConfUtils.scViewOn : MessageConfUtils.scViewOff))
            );
        } else {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.onlyPlayers);
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length <= 1) {
            return TextUtils.getCompletion(ConfigUtils.viewSelfAliases, args[0]);
        } else {
            return new ArrayList<>();
        }
    }
}
