package net.plasmere.streamline.objects.timers;

import net.plasmere.streamline.objects.savable.groups.SavableGuild;
import net.plasmere.streamline.objects.savable.groups.SavableParty;
import net.plasmere.streamline.objects.savable.users.SavableUser;
import net.plasmere.streamline.utils.GuildUtils;
import net.plasmere.streamline.utils.PartyUtils;
import net.plasmere.streamline.utils.PlayerUtils;

public class PlayerClearTimer implements Runnable {
    public int countdown;
    public int reset;

    public PlayerClearTimer(int seconds) {
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

        PlayerUtils.saveAll();

        int count = PlayerUtils.removeOfflineStats();

        PlayerUtils.reloadAll();

//        GuildUtils.loadAllGuilds();
//        PartyUtils.loadAllParties();

        for (SavableUser user : PlayerUtils.getStats()) {
            for (SavableGuild g : GuildUtils.getGuilds()) {
                if (g.hasMember(user)) user.setGuild(g.uuid);
            }
            for (SavableParty p : PartyUtils.getParties()) {
                if (p.hasMember(user)) user.setParty(p.uuid);
            }
            user.saveAll();
        }

        //MessagingUtils.logInfo("Just removed " + count + " cached players... Now at " + PlayerUtils.getJustPlayers().size() + " cached players!");
    }
}
