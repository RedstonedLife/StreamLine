package net.plasmere.streamline.commands.servers;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.CommandsConfUtils;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.utils.MessagingUtils;

import java.util.ArrayList;
import java.util.Collection;

public class GoToServerVanillaCommand extends SLCommand {

    public GoToServerVanillaCommand(String perm) {
        super("fabric", perm,"trampoline", "bungee-trampoline", "tramp");
    }

    @Override
    public void run(CommandSource sender, String[] args){
        if (sender instanceof Player){
            Player player = (Player) sender;

            if (player.hasPermission("streamline.server.fabric") || player.hasPermission("streamline.*")) {
                ProxyServer proxy = StreamLine.getInstance().getProxy();

                RegisteredServer vanServer = proxy.getServer(CommandsConfUtils.comBFabricEnd()).get();

                MessagingUtils.sendBUserMessage(sender, "&aConnecting now...");
                player.createConnectionRequest(vanServer).connect();
            } else {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPerm());
            }
        } else {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.onlyPlayers());
        }
    }

    @Override
    public Collection<String> onTabComplete(CommandSource sender, String[] args) {
        return new ArrayList<>();
    }
}
