package net.plasmere.streamline.utils;

import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.objects.SavableGuild;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;

public class FaceFetcher {
    public static String getFaceAvatarURL(SavablePlayer player){
        return getPlaceholdersApplied(player, ConfigUtils.moduleAvatarLink);
    }

    public static String getFaceAvatarURL(String username){
        return getFaceAvatarURL(PlayerUtils.getPlayerStat(username));
    }

    public static String getPlaceholdersApplied(SavablePlayer player, String string) {
        SavableGuild guild = GuildUtils.getGuild(player);

        return TextUtils.replaceAllPlayerBungee(string, player)
                .replace("%player_uuid%", player.uuid)
                .replace("%guild_uuid%", guild == null ? "" : guild.leaderUUID)
                .replace("%guild_name%", guild == null ? "" : guild.name)
                ;
    }
}
