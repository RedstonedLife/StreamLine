package net.plasmere.streamline.discordbot;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.objects.savable.users.SavableUser;
import net.plasmere.streamline.utils.PlayerUtils;

import java.time.OffsetDateTime;

public class BoostListener implements EventListener {
    @Override
    public void onEvent(GenericEvent event) {
        if (ConfigUtils.boostsEnabled()) {
            if (event instanceof GuildMemberUpdateBoostTimeEvent) {
                GuildMemberUpdateBoostTimeEvent e = ((GuildMemberUpdateBoostTimeEvent) event);

                OffsetDateTime old = e.getOldTimeBoosted();
                OffsetDateTime newTime = e.getNewTimeBoosted();
                if (old != null) {
                    if (newTime != null) {
                        if (newTime.isBefore(old)) {
                            return;
                        }
                    } else {
                        return;
                    }
                }

                Member member = e.getMember();

                if (StreamLine.discordData.isVerified(member.getIdLong())) {
                    SavableUser user = PlayerUtils.getOrGetSavableUser(StreamLine.discordData.getUUIDOfVerified(member.getIdLong()));
                    if (user == null) return;

                    StreamLine.discordData.addToBoostQueue(user);
                }
            }
        }
    }
}
