package net.plasmere.streamline.commands.staff.spy;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.PluginUtils;
import net.plasmere.streamline.utils.TextUtils;

import java.util.ArrayList;
import java.util.Collection;

public class PSPYCommand extends SLCommand {
    public PSPYCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            SavablePlayer player = PlayerUtils.getPlayerStat(sender);
            if (player == null) return;

            if (args.length > 0) {
                if (PluginUtils.checkEqualsStrings(args[0], PluginUtils.stringListToArray(ConfigUtils.viewSelfAliases()))) {
                    player.togglePSPYVS();
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.pspyvsToggle()
                            .replace("%toggle%", (player.pspyvs ? MessageConfUtils.pspyvsOn() : MessageConfUtils.pspyvsOff()))
                    );
                    return;
                }
            }

            player.togglePSPY();

            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.pspyToggle()
//                    .replace("%toggle%", (player.pspy ? "&aON" : "&cOFF"))
                    .replace("%toggle%", (player.pspy ? MessageConfUtils.pspyOn() : MessageConfUtils.pspyOff()))
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
