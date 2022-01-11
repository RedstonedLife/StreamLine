package net.plasmere.streamline.discordbot.commands;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
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
                        .replace("%amount%", Integer.toString(StreamLine.getInstance().getProxy().getPlayers().size()))
                        .replace("%servers%", compileServers())
                        .replace("%online%", getOnline())
        );
        if (ConfigUtils.debug()) MessagingUtils.logInfo("Sent message for \"" + command + "\"!");
    }

    private static String compileServers(){
        StringBuilder text = new StringBuilder();
        for (ServerInfo server : StreamLine.getInstance().getProxy().getServers().values()){
            if (server.getPlayers().size() > 0) {
                text.append(server.getName().toUpperCase()).append(": ").append(server.getPlayers().size()).append(" online...").append("\n");
            }
        }

        return text.toString();
    }

    private static String getOnline(){
        StringBuilder text = new StringBuilder();

        int i = 1;
        for (ProxiedPlayer player : PlayerUtils.getOnlinePPlayers()){
            if (!player.hasPermission("streamline.staff.vanish")){
                if (i < PlayerUtils.getOnlinePPlayers().size())
                    text.append(PlayerUtils.getAbsoluteDiscord(PlayerUtils.getOrGetSavableUser(player))).append(", ");
                else
                    text.append(PlayerUtils.getAbsoluteDiscord(PlayerUtils.getOrGetSavableUser(player))).append(".");
            } else {
                if (i < PlayerUtils.getOnlinePPlayers().size())
                    text.append("HIDDEN").append(", ");
                else
                    text.append("HIDDEN").append(".");
            }
        }

        return text.toString();
    }
}
