package net.plasmere.streamline.commands.staff;

import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.TextUtils;
import net.luckperms.api.model.group.Group;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import java.util.*;

public class GlobalOnlineCommand extends SLCommand {

    public GlobalOnlineCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void run(CommandSource sender, String[] args) {
        try {
            compileList(sender);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Use in a try statement:
    private void compileList(CommandSource sendTo){
        if (StreamLine.getInstance().getProxy().getPlayerCount() <= 0){
            sendTo.sendMessage(TextUtils.codedText(MessageConfUtils.onlineMessageNoPlayers()));
            return;
        }

        Set<Group> groups = StreamLine.lpHolder.api.getGroupManager().getLoadedGroups();

        if (groups.size() <= 0){
            sendTo.sendMessage(TextUtils.codedText(MessageConfUtils.onlineMessageNoGroups()));
            return;
        }

        MessagingUtils.sendBUserMessage(sendTo,
                MessageConfUtils.onlineMessageBMain()
                        .replace("%amount%", Integer.toString(StreamLine.getInstance().getProxy().getPlayerCount()))
                        .replace("%servers%", compileServers())
                        .replace("%online%", Objects.requireNonNull(getOnline(groups)))
        );
    }

    private String compileServers(){
        StringBuilder msg = new StringBuilder();

        List<RegisteredServer> servers = new ArrayList<>();

        for (RegisteredServer server : StreamLine.getInstance().getProxy().getAllServers()){
            if (server.getPlayersConnected().size() > 0) {
                servers.add(server);
            }
        }

        int i = 1;
        for (RegisteredServer server : servers){
            if (i != servers.size()) {
                msg.append(TextUtils.newLined(MessageConfUtils.onlineMessageBServers()
                        .replace("%server%", server.getServerInfo().getName().toUpperCase())
                        .replace("%count%", Integer.toString(server.getPlayersConnected().size()))
                )).append("\n");
            } else {
                msg.append(TextUtils.newLined(MessageConfUtils.onlineMessageBServers()
                        .replace("%server%", server.getServerInfo().getName().toUpperCase())
                        .replace("%count%", Integer.toString(server.getPlayersConnected().size()))
                ));
            }
            i++;
        }
        return msg.toString();
    }

    private String getOnline(Set<Group> groups){
        Collection<Player> players = StreamLine.getInstance().getProxy().getAllPlayers();

        HashMap<Player, ServerConnection> playerServers = new HashMap<>();
        HashMap<Player, String> playerGroup = new HashMap<>();

        for (Player player : players){
            playerServers.put(player, player.getCurrentServer().get());
        }

        for (Player player : players){
            try {
                playerGroup.put(player, Objects.requireNonNull(StreamLine.lpHolder.api.getUserManager().getUser(PlayerUtils.getSourceName(player))).getPrimaryGroup().toLowerCase());
            } catch (Exception e){
                e.printStackTrace();
                playerGroup.put(player, "null");
            }
        }

        StringBuilder msg = new StringBuilder();

        int i = 1;
        for (Group group : groups){
            if (i != groups.size())
                msg.append(TextUtils.newLined(getGroupedPlayers(group.getName(), playerGroup, playerServers))).append("\n");
            else
                msg.append(TextUtils.newLined(getGroupedPlayers(group.getName(), playerGroup, playerServers)));
            i++;
        }

        return msg.toString();
    }

    private String getGroupedPlayers(String group, HashMap<Player, String> playerGroup, HashMap<Player, ServerConnection> playerServers){
        List<Player> players = new ArrayList<>();

        for (Player player : playerGroup.keySet()){
            if (group.toLowerCase().equals(playerGroup.get(player).toLowerCase()))
                players.add(player);
        }

        return MessageConfUtils.onlineMessageBPlayersMain()
                .replace("%group%", group.toUpperCase())
                .replace("%count%", Integer.toString(players.size()))
                .replace("%playerbulk%", getPlayerBulk(players, playerServers));
    }

    private String getPlayerBulk(List<Player> players, HashMap<Player, ServerConnection> playerServers){
        StringBuilder text = new StringBuilder();

        int i = 0;

        for (Player player : players){
            ServerConnection server = playerServers.get(player);
            if (! (i == players.size() - 1))
                text.append(TextUtils.replaceAllPlayerBungee(MessageConfUtils.onlineMessageBPlayersBulkNotLast(), player)
                        .replace("%server%", server.getServerInfo().getName())
                );
            else
                text.append(TextUtils.replaceAllPlayerBungee(MessageConfUtils.onlineMessageBPlayersBulkLast(), player)
                        .replace("%server%", server.getServerInfo().getName())
                );
            i++;
        }

        return text.toString();
    }

    @Override
    public Collection<String> onTabComplete(CommandSource sender, String[] args) {
        return new ArrayList<>();
    }
}
