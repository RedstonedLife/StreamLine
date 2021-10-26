package net.plasmere.streamline.commands.staff;

import com.velocitypowered.api.command.CommandSource;
import net.md_5.bungee.api.config.ServerInfo;
import com.velocitypowered.api.proxy.Player;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.TextUtils;

import java.util.ArrayList;

public class TeleportCommand extends Command implements TabExecutor {

    public TeleportCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void execute(CommandSource sender, String[] args) {
        if (sender instanceof Player) {
            Player player = PlayerUtils.getPPlayer(args[0]);
            if (player == null) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPlayer());
                return;
            }

            ServerInfo serverInfo = player.getCurrentServer().get().getServerInfo();

            Player s = (Player) sender;

            s.connect(serverInfo);

//            MessagingUtils.sendTeleportPluginMessageRequest(s, player);
            PlayerUtils.addTeleport(s, player);

            MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllPlayerBungee(MessageConfUtils.bteleport(), sender)
            );
        } else {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.onlyPlayers());
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSource sender, String[] args) {
        if (args.length <= 1) {
            return TextUtils.getCompletion(PlayerUtils.getPlayerNamesForAllOnline(), args[0]);
        } else {
            return new ArrayList<>();
        }
    }
}
