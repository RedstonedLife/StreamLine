package net.plasmere.streamline.commands.staff;

import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.utils.MessagingUtils;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.md_5.bungee.api.plugin.Command;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.TextUtils;

import java.util.*;

public class StaffOnlineCommand extends Command {

    public StaffOnlineCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    public void execute(CommandSource sender, String[] args){
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

        MessagingUtils.sendBUserMessage(sender,
                MessageConfUtils.sOnlineBungeeMain()
                        .replace("%amount%", Integer.toString(lstaffs.size()))
                        .replace("%staffbulk%", getStaffList(lstaffs))
        );
    }

    private static String getStaffList(Set<Player> lstaffs){
        StringBuilder staff = new StringBuilder();
        int i = 1;

        for (Player player : lstaffs){
            if (i < lstaffs.size())
                staff.append(TextUtils.replaceAllPlayerBungee(MessageConfUtils.sOnlineBungeeBulkNotLast(), player)
                        .replace("%server%", player.getCurrentServer().get().getServerInfo().getName().toLowerCase())
                );
            else
                staff.append(TextUtils.replaceAllPlayerBungee(MessageConfUtils.sOnlineBungeeBulkLast(), player)
                        .replace("%server%", player.getCurrentServer().get().getServerInfo().getName().toLowerCase())
                );
            i++;
        }

        return staff.toString();
    }
}
