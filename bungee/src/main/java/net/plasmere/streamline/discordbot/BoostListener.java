package net.plasmere.streamline.discordbot;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.objects.savable.users.SavableUser;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;

public class BoostListener extends ListenerAdapter {
    public BoostListener() {
        if (!ConfigUtils.moduleDEnabled()) return;
        MessagingUtils.logInfo("Boost listener registered!");
    }

    @Override
    public void onGuildMemberUpdateBoostTime(@NotNull GuildMemberUpdateBoostTimeEvent event) {
        if (ConfigUtils.boostsEnabled()) {
            OffsetDateTime old = event.getOldTimeBoosted();
            OffsetDateTime newTime = event.getNewTimeBoosted();
            if (old != null) {
                if (newTime != null) {
                    if (newTime.isBefore(old)) {
                        return;
                    }
                } else {
                    return;
                }
            }

            Member member = event.getMember();

            if (StreamLine.discordData.isVerified(member.getIdLong())) {
                SavableUser user = PlayerUtils.getOrGetSavableUser(StreamLine.discordData.getUUIDOfVerified(member.getIdLong()));
                if (user == null) return;

                StreamLine.discordData.addToBoostQueue(user);
            }
        }
    }
}
