package net.plasmere.streamline.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.TextUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TitleCommand extends SLCommand {

    public TitleCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    public void run(CommandSource sender, String[] args) {
        if (args.length <= 1) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
            return;
        }


        List<Player> toSendTo = null;
        if (! args[0].equals("all")) {
            Player player = PlayerUtils.getPPlayer(args[0]);
            if (player == null) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPlayer());
                return;
            }

            toSendTo = List.of(player);
        } else {
            toSendTo = new ArrayList<>(PlayerUtils.getOnlinePPlayers());
        }

        int fadeIn = -1, stay = -1, fadeOut = -1;

        try {
            fadeIn = Integer.parseInt(args[1]);
            stay = Integer.parseInt(args[2]);
            fadeOut = Integer.parseInt(args[3]);
        } catch (Exception e) {
            if (ConfigUtils.errSendToConsole()) {
                e.printStackTrace();
            }

            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorInt().replace("%class%", getClass().getSimpleName()));

            return;
        }

        String toSend = TextUtils.argsToStringMinus(args, 0, 1, 2, 3);

        for (Player p : toSendTo) {
            MessagingUtils.sendBUserTitle(p, toSend, fadeIn, stay, fadeOut);
        }
        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.proxyTitleSent()
                .replace("%fade_in%", String.valueOf(fadeIn))
                .replace("%stay%", String.valueOf(stay))
                .replace("%fade_out%", String.valueOf(fadeOut))
                .replace("%title%", toSend
                        .replace("%next%", "\n")
                )
        );
    }

    @Override
    public Collection<String> onTabComplete(CommandSource sender, String[] args) {
        if (args.length <= 1) {
            List<String> strings = PlayerUtils.getPlayerNamesForAllOnline();
            strings.add("all");
            return TextUtils.getCompletion(strings, args[0]);
        }
        if (args.length == 2) {
            return List.of("fade-in");
        }
        if (args.length == 3) {
            return List.of("stay");
        }
        if (args.length == 4) {
            return List.of("fade-out");
        }
        return List.of("%next%");
    }
}
