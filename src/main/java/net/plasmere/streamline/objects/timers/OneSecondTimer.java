package net.plasmere.streamline.objects.timers;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.objects.savable.users.SavableUser;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.RanksUtils;

import java.util.*;

public class OneSecondTimer implements Runnable {
    public int countdown;
    public int reset;

    public OneSecondTimer() {
        this.countdown = 0;
        this.reset = 1;
    }

    @Override
    public void run() {
        if (countdown == 0) {
            done();
        }

        countdown--;
    }

    public void done(){
        try {
            countdown = reset;

            if (PlayerUtils.getToSave().size() > 0) {
                for (SavableUser user : new ArrayList<>(PlayerUtils.getToSave())) {
                    PlayerUtils.doSave(user);
                }
            }

            PlayerUtils.tickConn();

            if (StreamLine.lpHolder.enabled) {
                for (SavablePlayer player : PlayerUtils.getJustPlayersOnline()) {
                    if (player.latestName == null) continue;
                    if (player.latestName.equals("")) continue;
                    PlayerUtils.updateDisplayName(player);
                }
            }

            for (SavablePlayer player : PlayerUtils.getJustPlayers()) {
                PlayerUtils.checkAndUpdateIfMuted(player);
            }

            PlayerUtils.tickTeleport();
            PlayerUtils.tickBoosts();

            if (ConfigUtils.moduleBRanksEnabled) {
                int success = 0;
                int failed = 0;
                int other = 0;

                for (ProxiedPlayer player : PlayerUtils.getOnlinePPlayers()) {
                    try {
                        int result = RanksUtils.checkAndChange(PlayerUtils.getPlayerStat(player));

                        if (result == 1) success ++;
                        if (result == 0) other ++;
                        if (result == -1) failed ++;
                    } catch (Exception e) {
                        failed ++;
                        e.printStackTrace();
                    }
                }
            }
        } catch (ConcurrentModificationException e) {
            if (ConfigUtils.debug) e.printStackTrace();
        }
    }
}
