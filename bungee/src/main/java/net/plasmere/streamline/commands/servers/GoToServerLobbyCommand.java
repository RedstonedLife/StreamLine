package net.plasmere.streamline.commands.servers;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.ProxyServer;
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
    public void run(CommandSender sender, String[] args){
        Collection<ServerInfo> servers = StreamLine.getInstance().getProxy().getServers().values();
        if ( args.length == 0 ) {
            if (sender instanceof ProxiedPlayer) {
                ProxiedPlayer player = (ProxiedPlayer) sender;

                if (player.hasPermission("streamline.server.lobby") || player.hasPermission("streamline.*")) {
                    ProxyServer proxy = StreamLine.getInstance().getProxy();

                    ServerInfo vanServer = proxy.getServerInfo(CommandsConfUtils.comBLobbyEnd());

                    MessagingUtils.sendBUserMessage(sender, "&aConnecting now...");
                    player.connect(vanServer);
                } else {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPerm());
                }
            } else {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.onlyPlayers());
            }
        } else {
            if (!(sender instanceof ProxiedPlayer))
                return;

            ProxiedPlayer player = (ProxiedPlayer) sender;
            ProxyServer proxy = StreamLine.getInstance().getProxy();

            ServerInfo server = proxy.getServerInfo(args[0]);

            MessagingUtils.sendBUserMessage(sender, "&aConnecting now...");
            player.connect(server);
        }
    }

    @Override
    public Collection<String> onTabComplete(final CommandSender sender, final String[] args)
    {
        TreeSet<String> servers = new TreeSet<>();

        for (ServerInfo serverInfo : StreamLine.getInstance().getProxy().getServers().values()) {
            servers.add(serverInfo.getName().toLowerCase(Locale.ROOT));
        }

        return TextUtils.getCompletion(servers, args[0]);
    }
}
