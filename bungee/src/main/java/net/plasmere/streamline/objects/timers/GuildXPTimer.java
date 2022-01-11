package net.plasmere.streamline.objects.timers;

import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.objects.savable.groups.SavableGuild;
import net.plasmere.streamline.objects.savable.users.SavableUser;
import net.plasmere.streamline.utils.GuildUtils;
import net.plasmere.streamline.utils.PlayerUtils;

import java.util.ArrayList;
import java.util.List;

public class GuildXPTimer implements Runnable {
    public int countdown;
    public int reset;

    public GuildXPTimer(int seconds) {
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
            List<SavableUser> users = new ArrayList<>(PlayerUtils.getStats());

            for (SavableUser user : users) {
                if (user.guild == null) user.setGuild("");
                if (user.guild.equals("")) continue;

                SavableGuild guild = GuildUtils.getOrGetGuild(user.guild);
                if (guild == null) continue;

                guild.addTotalXP(ConfigUtils.xpPerGiveG());
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        //MessagingUtils.logInfo("Just gave " + ConfigUtils.xpPerGiveG() + " GEXP to " + GuildUtils.getGuilds().size() + " guilds!");

        //StreamLine.getInstance().getProxy().getScheduler().schedule(StreamLine.getInstance(), new GuildXPTimer(ConfigUtils.timePerGiveG), 1, 1, TimeUnit.SECONDS);
    }
}
