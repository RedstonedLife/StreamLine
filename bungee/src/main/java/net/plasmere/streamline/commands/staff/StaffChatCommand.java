package net.plasmere.streamline.commands.staff;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.DiscordBotConfUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.objects.messaging.DiscordMessage;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.TextUtils;

import java.util.ArrayList;
import java.util.Collection;

public class StaffChatCommand extends SLCommand {
    public StaffChatCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            if (ConfigUtils.moduleStaffChat()) {
                if (sender.hasPermission(ConfigUtils.staffPerm())) {
                    SavablePlayer player = PlayerUtils.getPlayerStat(sender);

                    if (player == null) return;

                    if (args.length <= 0 || args[0].equals("") || args[0].equals(" ")) {
                        player.toggleSC();

                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.staffChatToggle()
//                                .replace("%toggle%", (player.sc ? "&aON" : "&cOFF"))
                                .replace("%toggle%", (player.sc ? MessageConfUtils.staffChatOn() : MessageConfUtils.staffChatOff()))
                        );
                        return;
                    }

                    MessagingUtils.sendStaffMessage(sender, MessageConfUtils.bungeeStaffChatFrom(), TextUtils.normalize(args));
                    if (ConfigUtils.moduleDEnabled()) {
                        MessagingUtils.sendDiscordEBMessage(new DiscordMessage(sender,
                                MessageConfUtils.staffChatEmbedTitle(),
                                TextUtils.replaceAllPlayerBungee(MessageConfUtils.discordStaffChatMessage(), sender)
                                        .replace("%message%", TextUtils.normalize(args)),
                                DiscordBotConfUtils.textChannelStaffChat()));
                    }
                } else {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.prefix() + MessageConfUtils.noPerm());
                }
            }
        } else {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.onlyPlayers());
        }
    }

    @Override
    public Collection<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
