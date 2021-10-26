package net.plasmere.streamline.discordbot.commands;

import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.utils.MessagingUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.velocitypowered.api.proxy.Player;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.TextUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class StaffOnlineCommand {
    public static void sendMessage(String command, MessageReceivedEvent event){
        Collection<Player> staffs = StreamLine.getInstance().getProxy().getAllPlayers();
        Set<Player> lstaffs = new HashSet<>(staffs);

        for (Player player : staffs){
            try {
                if (! player.hasPermission(ConfigUtils.staffPerm)) {
                    lstaffs.remove(player);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        MessagingUtils.sendDSelfMessage(event,
                MessageConfUtils.sOnlineMessageEmbedTitle(),
                MessageConfUtils.sOnlineDiscordMain()
                        .replace("%amount%", Integer.toString(lstaffs.size()))
                        .replace("%staffbulk%", getStaffList(lstaffs))
        );

        if (ConfigUtils.debug) MessagingUtils.logInfo("Sent message for \"" + command + "\"!");
    }

    private static String getStaffList(Set<Player> lstaffs){
        StringBuilder staff = new StringBuilder();
        int i = 1;

        for (Player player : lstaffs){
            if (i < lstaffs.size())
                staff.append(TextUtils.replaceAllPlayerBungee(MessageConfUtils.sOnlineDiscordBulkNotLast(), player)
                        .replace("%server%", player.getCurrentServer().get().getServerInfo().getName().toLowerCase())
                );
            else
                staff.append(TextUtils.replaceAllPlayerBungee(MessageConfUtils.sOnlineDiscordBulkLast(), player)
                        .replace("%server%", player.getCurrentServer().get().getServerInfo().getName().toLowerCase())
                );
            i++;
        }

        return staff.toString();
    }
}
