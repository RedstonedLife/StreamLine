package net.plasmere.streamline.discordbot.commands;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.TextUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class StaffOnlineCommand {
    public static void sendMessage(String command, MessageReceivedEvent event){
        Collection<ProxiedPlayer> staffs = PlayerUtils.getOnlinePPlayers();
        Set<ProxiedPlayer> lstaffs = new HashSet<>(staffs);

        for (ProxiedPlayer player : staffs){
            try {
                if (! player.hasPermission(ConfigUtils.staffPerm())) {
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

        if (ConfigUtils.debug()) MessagingUtils.logInfo("Sent message for \"" + command + "\"!");
    }

    private static String getStaffList(Set<ProxiedPlayer> lstaffs){
        StringBuilder staff = new StringBuilder();
        int i = 1;

        for (ProxiedPlayer player : lstaffs){
            if (i < lstaffs.size())
                staff.append(TextUtils.replaceAllPlayerBungee(MessageConfUtils.sOnlineDiscordBulkNotLast(), player)
                        .replace("%server%", player.getServer().getInfo().getName().toLowerCase())
                );
            else
                staff.append(TextUtils.replaceAllPlayerBungee(MessageConfUtils.sOnlineDiscordBulkLast(), player)
                        .replace("%server%", player.getServer().getInfo().getName().toLowerCase())
                );
            i++;
        }

        return staff.toString();
    }
}
