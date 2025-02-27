package net.plasmere.streamline.commands.staff.spy;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.plasmere.streamline.objects.command.SLCommand;
import java.util.Collection;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.PluginUtils;
import net.plasmere.streamline.utils.TextUtils;

import java.util.ArrayList;

public class SCViewCommand extends SLCommand {

    public SCViewCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            SavablePlayer player = PlayerUtils.getPlayerStat(sender);
            if (player == null) return;

            if (args.length > 0) {
                if (PluginUtils.checkEqualsStrings(args[0], PluginUtils.stringListToArray(ConfigUtils.viewSelfAliases()))) {
                    player.toggleSCVS();
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.scvsToggle()
                            .replace("%toggle%", (player.scvs ? MessageConfUtils.scvsOn() : MessageConfUtils.scvsOff()))
                    );
                    return;
                }
            }

            player.toggleSCView();

            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.scViewToggle()
//                    .replace("%toggle%", (player.viewsc ? "&aON" : "&cOFF"))
                    .replace("%toggle%", (player.viewsc ? MessageConfUtils.scViewOn() : MessageConfUtils.scViewOff()))
            );
        } else {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.onlyPlayers());
        }
    }

    @Override
    public Collection<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length <= 1) {
            return TextUtils.getCompletion(ConfigUtils.viewSelfAliases(), args[0]);
        } else {
            return new ArrayList<>();
        }
    }
}
