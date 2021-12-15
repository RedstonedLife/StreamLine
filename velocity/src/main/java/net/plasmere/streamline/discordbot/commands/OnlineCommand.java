package net.plasmere.streamline.discordbot.commands;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;

public class OnlineCommand {
    public static void sendMessage(String command, MessageReceivedEvent event){
        MessagingUtils.sendDSelfMessage(event,
                MessageConfUtils.onlineMessageEmbedTitle(),
                MessageConfUtils.onlineMessageDiscord()
                        .replace("%amount%", Integer.toString(StreamLine.getInstance().getProxy().getPlayerCount()))
                        .replace("%servers%", compileServers())
                        .replace("%online%", getOnline())
        );
        if (ConfigUtils.debug()) MessagingUtils.logInfo("Sent message for \"" + command + "\"!");
    }

    private static String compileServers(){
        StringBuilder text = new StringBuilder();
        for (RegisteredServer server : StreamLine.getInstance().getProxy().getAllServers()){
            if (server.getPlayersConnected().size() > 0) {
                text.append(server.getServerInfo().getName().toUpperCase()).append(": ").append(server.getPlayersConnected().size()).append(" online...").append("\n");
            }
        }

        return text.toString();
    }

    private static String getOnline(){
        StringBuilder text = new StringBuilder();

        int i = 1;
        for (Player player : StreamLine.getInstance().getProxy().getAllPlayers()){
            if (!player.hasPermission("streamline.staff.vanish")){
                if (i < StreamLine.getInstance().getProxy().getAllPlayers().size())
                    text.append(PlayerUtils.getAbsoluteDiscord(PlayerUtils.getOrGetSavableUser(player))).append(", ");
                else
                    text.append(PlayerUtils.getAbsoluteDiscord(PlayerUtils.getOrGetSavableUser(player))).append(".");
            } else {
                if (i < StreamLine.getInstance().getProxy().getAllPlayers().size())
                    text.append("HIDDEN").append(", ");
                else
                    text.append("HIDDEN").append(".");
            }
        }

        return text.toString();
    }
}
