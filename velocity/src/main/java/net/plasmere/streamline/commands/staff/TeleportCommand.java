package net.plasmere.streamline.commands.staff;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.Player;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.TextUtils;

import java.util.ArrayList;
import java.util.Collection;

public class TeleportCommand extends SLCommand {

    public TeleportCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void run(CommandSource sender, String[] args) {
        if (sender instanceof Player) {
            Player player = PlayerUtils.getPPlayer(args[0]);
            if (player == null) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPlayer());
                return;
            }

            ServerConnection serverInfo = player.getCurrentServer().get();

            Player s = (Player) sender;

            s.createConnectionRequest(serverInfo.getServer()).connect();

//            MessagingUtils.sendTeleportPluginMessageRequest(s, player);
            PlayerUtils.addTeleport(s, player);

            MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllPlayerBungee(MessageConfUtils.bteleport(), sender)
            );
        } else {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.onlyPlayers());
        }
    }

    @Override
    public Collection<String> onTabComplete(CommandSource sender, String[] args) {
        if (args.length <= 1) {
            return TextUtils.getCompletion(PlayerUtils.getPlayerNamesForAllOnline(), args[0]);
        } else {
            return new ArrayList<>();
        }
    }
}
