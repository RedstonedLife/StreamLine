package net.plasmere.streamline.commands.staff;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.TextUtils;

import java.util.*;

public class SudoCommand extends Command implements TabExecutor {
    public SudoCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void execute(CommandSource sender, String[] args) {
        if (args.length <= 1) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
        } else {
            Player sudoOn = StreamLine.getInstance().getProxy().getPlayer(args[0]);

            if (sudoOn.hasPermission(ConfigUtils.noSudoPerm)){
                MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllPlayerBungee(MessageConfUtils.sudoNoSudo(), sudoOn)
                );
                return;
            }

            if (StreamLine.getInstance().getProxy().getCommandManager().executeImmediatelyAsync(sudoOn, TextUtils.argsToStringMinus(args, 0))){
                MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllPlayerBungee(MessageConfUtils.sudoWorked(), sender)
                );
            } else {
                MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllPlayerBungee(MessageConfUtils.sudoNoWork(), sender)
                );
            }
        }
    }

    @Override
    public Iterable<String> onTabComplete(final CommandSource sender, final String[] args) {
        Collection<Player> players = StreamLine.getInstance().getProxy().getAllPlayers();
        List<String> strPlayers = new ArrayList<>();

        for (Player player : players){
            strPlayers.add(PlayerUtils.getSourceName(player));
        }

        Collection<Map.Entry<String, Command>> commands = StreamLine.getInstance().getProxy().getPluginManager().getCommands();
        List<String> strCommands = new ArrayList<>();

        for (Map.Entry<String, Command> com : commands){
            strCommands.add(com.getValue().getName());
        }

        if (args.length == 1) {
            return TextUtils.getCompletion(strPlayers, args[0]);
        } else if (args.length == 2){
            return TextUtils.getCompletion(strCommands, args[1]);
        }

        return new ArrayList<>();
    }
}
