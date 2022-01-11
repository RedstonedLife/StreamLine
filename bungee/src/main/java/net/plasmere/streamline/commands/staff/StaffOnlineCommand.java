package net.plasmere.streamline.commands.staff;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.TextUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class StaffOnlineCommand extends SLCommand {

    public StaffOnlineCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    public void run(CommandSender sender, String[] args){
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

        MessagingUtils.sendBUserMessage(sender,
                MessageConfUtils.sOnlineBungeeMain()
                        .replace("%amount%", Integer.toString(lstaffs.size()))
                        .replace("%staffbulk%", getStaffList(lstaffs))
        );
    }

    private static String getStaffList(Set<ProxiedPlayer> lstaffs){
        StringBuilder staff = new StringBuilder();
        int i = 1;

        for (ProxiedPlayer player : lstaffs){
            if (i < lstaffs.size())
                staff.append(TextUtils.replaceAllPlayerBungee(MessageConfUtils.sOnlineBungeeBulkNotLast(), player)
                        .replace("%server%", player.getServer().getInfo().getName().toLowerCase())
                );
            else
                staff.append(TextUtils.replaceAllPlayerBungee(MessageConfUtils.sOnlineBungeeBulkLast(), player)
                        .replace("%server%", player.getServer().getInfo().getName().toLowerCase())
                );
            i++;
        }

        return staff.toString();
    }

    @Override
    public Collection<String> onTabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
