package net.plasmere.streamline.commands.staff;

import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.TextUtils;
import net.luckperms.api.model.group.Group;
import com.velocitypowered.api.command.CommandSource;
import net.md_5.bungee.api.config.ServerInfo;
import com.velocitypowered.api.proxy.Player;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.plugin.Command;

import java.util.*;

public class GlobalOnlineCommand extends Command {

    public GlobalOnlineCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void execute(CommandSource sender, String[] args) {
        try {
            compileList(sender);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Use in a try statement:
    private void compileList(CommandSource sendTo){
        if (StreamLine.getInstance().getProxy().getOnlineCount() <= 0){
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
                        .replace("%amount%", Integer.toString(StreamLine.getInstance().getProxy().getOnlineCount()))
                        .replace("%servers%", compileServers())
                        .replace("%online%", Objects.requireNonNull(getOnline(groups)))
        );
    }

    private String compileServers(){
        StringBuilder msg = new StringBuilder();

        List<ServerInfo> servers = new ArrayList<>();

        for (ServerInfo server : StreamLine.getInstance().getProxy().getServers().values()){
            if (server.getPlayers().size() > 0) {
                servers.add(server);
            }
        }

        int i = 1;
        for (ServerInfo server : servers){
            if (i != servers.size()) {
                msg.append(TextUtils.newLined(MessageConfUtils.onlineMessageBServers()
                        .replace("%server%", server.getName().toUpperCase())
                        .replace("%count%", Integer.toString(server.getPlayers().size()))
                )).append("\n");
            } else {
                msg.append(TextUtils.newLined(MessageConfUtils.onlineMessageBServers()
                        .replace("%server%", server.getName().toUpperCase())
                        .replace("%count%", Integer.toString(server.getPlayers().size()))
                ));
            }
            i++;
        }
        return msg.toString();
    }

    private String getOnline(Set<Group> groups){
        Collection<Player> players = StreamLine.getInstance().getProxy().getAllPlayers();

        HashMap<Player, Server> playerServers = new HashMap<>();
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

    private String getGroupedPlayers(String group, HashMap<Player, String> playerGroup, HashMap<Player, Server> playerServers){
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

    private String getPlayerBulk(List<Player> players, HashMap<Player, Server> playerServers){
        StringBuilder text = new StringBuilder();

        int i = 0;

        for (Player player : players){
            ServerConnectionserver = playerServers.get(player);
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
}
