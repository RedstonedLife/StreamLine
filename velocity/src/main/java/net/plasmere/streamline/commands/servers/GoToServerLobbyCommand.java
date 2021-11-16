package net.plasmere.streamline.commands.servers;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.CommandsConfUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.TextUtils;

import java.util.Collection;
import java.util.Locale;
import java.util.TreeSet;

public class GoToServerLobbyCommand extends SLCommand {
    public GoToServerLobbyCommand(String base, String perm, String[] aliases) {
        super(base, perm, aliases);
    }

    @Override
    public void run(CommandSource sender, String[] args){
        Collection<RegisteredServer> servers = StreamLine.getProxy().getAllServers();
        if ( args.length == 0 ) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (player.hasPermission("streamline.server.lobby") || player.hasPermission("streamline.*")) {
                    ProxyServer proxy = StreamLine.getInstance().getProxy();

                    RegisteredServer vanServer = proxy.getServer(CommandsConfUtils.comBLobbyEnd()).get();

                    MessagingUtils.sendBUserMessage(sender, "&aConnecting now...");
                    player.createConnectionRequest(vanServer).connect();
                } else {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPerm());
                }
            } else {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.onlyPlayers());
            }
        } else {
            if (!(sender instanceof Player))
                return;

            Player player = (Player) sender;
            ProxyServer proxy = StreamLine.getInstance().getProxy();

            RegisteredServer server = proxy.getServer(args[0]).get();

            MessagingUtils.sendBUserMessage(sender, "&aConnecting now...");
            player.createConnectionRequest(server).connect();
        }
    }

    @Override
    public Collection<String> onTabComplete(final CommandSource sender, final String[] args)
    {
        TreeSet<String> servers = new TreeSet<>();

        for (RegisteredServer serverInfo : StreamLine.getInstance().getProxy().getAllServers()) {
            servers.add(serverInfo.getServerInfo().getName().toLowerCase(Locale.ROOT));
        }

        return TextUtils.getCompletion(servers, args[0]);
    }
}
