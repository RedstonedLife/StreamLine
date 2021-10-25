package net.plasmere.streamline.discordbot.commands;

import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.utils.MessagingUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.md_5.bungee.api.config.ServerInfo;
import com.velocitypowered.api.proxy.Player;
import net.plasmere.streamline.utils.PlayerUtils;

public class OnlineCommand {
    public static void sendMessage(String command, MessageReceivedEvent event){
        MessagingUtils.sendDSelfMessage(event,
                MessageConfUtils.onlineMessageEmbedTitle(),
                MessageConfUtils.onlineMessageDiscord()
                        .replace("%amount%", Integer.toString(StreamLine.getInstance().getProxy().getOnlineCount()))
                        .replace("%servers%", compileServers())
                        .replace("%online%", getOnline())
        );
        if (ConfigUtils.debug) MessagingUtils.logInfo("Sent message for \"" + command + "\"!");
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
        for (Player player : StreamLine.getInstance().getProxy().getPlayers()){
            if (!player.hasPermission("streamline.staff.vanish")){
                if (i < StreamLine.getInstance().getProxy().getPlayers().size())
                    text.append(PlayerUtils.getAbsoluteDiscord(PlayerUtils.getOrCreatePlayerStat(player))).append(", ");
                else
                    text.append(PlayerUtils.getAbsoluteDiscord(PlayerUtils.getOrCreatePlayerStat(player))).append(".");
            } else {
                if (i < StreamLine.getInstance().getProxy().getPlayers().size())
                    text.append("HIDDEN").append(", ");
                else
                    text.append("HIDDEN").append(".");
            }
        }

        return text.toString();
    }
}
