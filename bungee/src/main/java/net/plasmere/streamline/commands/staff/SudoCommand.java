package net.plasmere.streamline.commands.staff;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.PluginUtils;
import net.plasmere.streamline.utils.TextUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SudoCommand extends SLCommand {
    public SudoCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (args.length <= 1) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
        } else {
            ProxiedPlayer sudoOn = StreamLine.getInstance().getProxy().getPlayer(args[0]);

            if (sudoOn.hasPermission(ConfigUtils.noSudoPerm())){
                MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllPlayerBungee(MessageConfUtils.sudoNoSudo(), sudoOn)
                );
                return;
            }

            if (StreamLine.getInstance().getProxy().getPluginManager().dispatchCommand(sudoOn, TextUtils.argsToStringMinus(args, 0))){
                MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllPlayerBungee(MessageConfUtils.sudoWorked(), sender)
                );
            } else {
                MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllPlayerBungee(MessageConfUtils.sudoNoWork(), sender)
                );
            }
        }
    }

    @Override
    public Collection<String> onTabComplete(final CommandSender sender, final String[] args) {
        Collection<ProxiedPlayer> players = PlayerUtils.getOnlinePPlayers();
        List<String> strPlayers = new ArrayList<>();

        for (ProxiedPlayer player : players){
            strPlayers.add(PlayerUtils.getSourceName(player));
        }

        Collection<String> commands = PluginUtils.getCommandAliases();

        List<String> strCommands = new ArrayList<>(commands);

        if (args.length == 1) {
            return TextUtils.getCompletion(strPlayers, args[0]);
        } else if (args.length == 2){
            return TextUtils.getCompletion(strCommands, args[1]);
        }

        return new ArrayList<>();
    }
}
