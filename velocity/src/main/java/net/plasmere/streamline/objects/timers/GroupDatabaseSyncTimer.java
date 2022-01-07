package net.plasmere.streamline.objects.timers;

import net.plasmere.streamline.objects.savable.groups.SavableGuild;
import net.plasmere.streamline.objects.savable.groups.SavableParty;
import net.plasmere.streamline.utils.GuildUtils;
import net.plasmere.streamline.utils.PartyUtils;

public class GroupDatabaseSyncTimer implements Runnable {
    public int countdown;
    public int reset;

    public GroupDatabaseSyncTimer(int seconds) {
        this.countdown = 0;
        this.reset = seconds;
    }

    @Override
    public void run() {
        if (this.countdown == 0) {
            this.countdown = this.reset;
            done();
        }

        this.countdown --;
    }

    public void done(){
        for (SavableGuild guild : GuildUtils.getGuilds()) {
            guild.syncWithDatabase();
        }

        for (SavableParty party : PartyUtils.getParties()) {
            party.syncWithDatabase();
        }
    }
}
