package net.plasmere.streamline.commands.staff.punishments;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.DiscordBotConfUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.objects.messaging.DiscordMessage;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.utils.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collection;
import java.util.List;

public class KickCommand extends SLCommand {
    public KickCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void run(CommandSource sender, String[] args) {
        if (args.length <= 0) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
        } else {
            SavablePlayer other = PlayerUtils.getOrGetPlayerStat(args[0]);

            if (other == null) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPlayer());
                return;
            }

            if (! other.online) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPlayer());
                return;
            }

            if (PlayerUtils.hasOfflinePermission(ConfigUtils.punKicksBypass, other.uuid)) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.kickCannot());
                return;
            }

            String reason = TextUtils.argsToStringMinus(args, 0);

            PlayerUtils.kick(other, MessageConfUtils.kickKicked()
                    .replace("%reason%", reason)
            );

            MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllPlayerBungee(MessageConfUtils.kickSender(), other)
                    .replace("%reason%", reason)
            );

            if (ConfigUtils.moduleDEnabled()) {
                if (ConfigUtils.punKicksDiscord) {
                    MessagingUtils.sendDiscordEBMessage(
                            new DiscordMessage(
                                    sender,
                                    MessageConfUtils.kickEmbed(),
                                    TextUtils.replaceAllPlayerDiscord(MessageConfUtils.kickDiscord(), other)
                                            .replace("%punisher%", PlayerUtils.getSourceName(sender))
                                            .replace("%reason%", reason)
                                    ,
                                    DiscordBotConfUtils.textChannelKicks
                            )
                    );
                }
            }

            MessagingUtils.sendPermissionedMessageNonSelf(sender, ConfigUtils.staffPerm(),
                    TextUtils.replaceAllPlayerBungee(MessageConfUtils.kickStaff(), other)
                    .replace("%punisher%", PlayerUtils.getSourceName(sender))
                    .replace("%reason%", reason)
            );
        }
    }

    @Override
    public Collection<String> onTabComplete(final CommandSource sender, final String[] args) {
        Collection<Player> players = StreamLine.getInstance().getProxy().getAllPlayers();
        List<String> strPlayers = new ArrayList<>();

        for (Player player : players){
            if (sender instanceof Player) if (player.equals(sender)) continue;
            strPlayers.add(PlayerUtils.getSourceName(player));
        }

        if (args.length == 1) {
            return TextUtils.getCompletion(strPlayers, args[0]);
        }

        return new ArrayList<>();
    }
}
