package net.plasmere.streamline.commands.staff.spy;

import net.md_5.bungee.api.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.PluginUtils;
import net.plasmere.streamline.utils.TextUtils;

import java.util.ArrayList;

public class SSPYCommand extends Command implements TabExecutor {
    public SSPYCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void execute(CommandSource sender, String[] args) {
        if (sender instanceof Player) {
            SavablePlayer player = PlayerUtils.getPlayerStat(sender);
            if (player == null) return;

            if (args.length > 0) {
                if (PluginUtils.checkEqualsStrings(args[0], PluginUtils.stringListToArray(ConfigUtils.viewSelfAliases))) {
                    player.toggleSSPYVS();
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.sspyvsToggle()
                            .replace("%toggle%", (player.sspyvs ? MessageConfUtils.sspyvsOn() : MessageConfUtils.sspyvsOff()))
                    );
                    return;
                }
            }

            player.toggleSSPY();

            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.sspyToggle()
                            .replace("%toggle%", (player.sspy ? MessageConfUtils.sspyOn() : MessageConfUtils.sspyOff()))
            );
        } else {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.onlyPlayers());
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSource sender, String[] args) {
        if (args.length <= 1) {
            return TextUtils.getCompletion(ConfigUtils.viewSelfAliases, args[0]);
        } else {
            return new ArrayList<>();
        }
    }
}
