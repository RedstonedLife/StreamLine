package net.plasmere.streamline.objects.timers;

import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.utils.PlayerUtils;

public class PlaytimeTimer implements Runnable {
    public int countdown;
    public int reset;

    public PlaytimeTimer(int seconds) {
        this.countdown = 0;
        this.reset = seconds;
    }

    @Override
    public void run() {
        if (countdown == 0) {
            done();
        }

        countdown--;
    }

    public void done(){
        countdown = reset;
        try {
            for (SavablePlayer player : PlayerUtils.getJustPlayersOnline()) {
                player.addPlaySecond(1);
            }

            //if (ConfigUtils.debug()) MessagingUtils.logInfo("Just gave out PlayTimeConf to " + PlayerUtils.getOnlinePPlayers().size() + " online players!");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
