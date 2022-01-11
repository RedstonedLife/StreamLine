package net.plasmere.streamline.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.CommandsConfUtils;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.objects.savable.users.SavableUser;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.TextUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StatsCommand extends SLCommand {
    public StatsCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            if (args.length <= 0 || ! CommandsConfUtils.comBStatsOthers()) {
                PlayerUtils.info(sender, PlayerUtils.getOrGetPlayerStat(PlayerUtils.getSourceName(sender)));
            } else {
                SavableUser person = PlayerUtils.getOrGetSavableUser(args[0]);

                if (person == null) {
                    MessagingUtils.sendBUserMessage(sender, PlayerUtils.noStatsFound.replace("%class%", this.getClass().getName()));
                    return;
                }

                PlayerUtils.info(sender, person);
            }
        } else {
            if (args.length <= 0) {
                PlayerUtils.info(sender, PlayerUtils.getConsoleStat());
            } else {
                SavableUser person = PlayerUtils.getOrGetSavableUser(args[0]);

                if (person == null) {
                    MessagingUtils.sendBUserMessage(sender, PlayerUtils.noStatsFound.replace("%class%", this.getClass().getName()));
                    return;
                }

                PlayerUtils.info(sender, person);
            }
        }
    }

    @Override
    public Collection<String> onTabComplete(CommandSender sender, String[] args) {
        Collection<ProxiedPlayer> players = PlayerUtils.getOnlinePPlayers();
        List<String> strPlayers = new ArrayList<>();

        for (ProxiedPlayer player : players){
            strPlayers.add(PlayerUtils.getSourceName(player));
        }

        strPlayers.add("%");

        if (sender.hasPermission(CommandsConfUtils.comBStatsPermOthers())) {
            return TextUtils.getCompletion(strPlayers, args[0]);
        }

        return new ArrayList<>();
    }
}

//                if (args[0].equals("%")) {
//                    SavableConsole person = PlayerUtils.getConsoleStat();
//
//                    if (person == null) {
//                        MessagingUtils.sendBUserMessage(sender, PlayerUtils.noStatsFound.replace("%class%", this.getClass().getName()));
//                        return;
//                    }
//
//                    PlayerUtils.info(sender, person);
//                } else {
//                    SavablePlayer person = PlayerUtils.getOrGetPlayerStat(args[0]);
//
//                    if (person == null) {
//                        MessagingUtils.sendBUserMessage(sender, PlayerUtils.noStatsFound.replace("%class%", this.getClass().getName()));
//                        return;
//                    }
//
//                    PlayerUtils.info(sender, person);
//                }