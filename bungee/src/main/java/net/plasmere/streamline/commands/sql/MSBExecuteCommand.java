package net.plasmere.streamline.commands.sql;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.objects.savable.users.SavableUser;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.PluginUtils;
import net.plasmere.streamline.utils.TextUtils;
import net.plasmere.streamline.utils.objects.CustomSQLInfo;
import net.plasmere.streamline.utils.sql.BridgerDataSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MSBExecuteCommand extends SLCommand {
    public MSBExecuteCommand(String base, String permission, String... aliases) {
        super(base, permission, aliases);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        StreamLine.msbConfig.reloadExecutions();

        if (args.length <= 0) {
            sender.sendMessage(TextUtils.codedText("&cYou haven't supplied an execution (and an optional player)!"));
            return;
        }

        if (! StreamLine.msbConfig.isValidExecution(args[0])) {
            sender.sendMessage(TextUtils.codedText("&cYou haven't supplied a valid execution!"));
            return;
        }

        if (args.length == 1) {
            if (sender instanceof ProxiedPlayer p) {
                SavableUser player = PlayerUtils.getOrGetSavableUser(p);
                CustomSQLInfo sqlInfo = PluginUtils.getExecutionByIdentifier(args[0]);
                if (sqlInfo == null) {
                    sender.sendMessage(TextUtils.codedText("&cThe specified execution is either not loaded or does not exist!"));
                    return;
                }

                BridgerDataSource.execute(sqlInfo, player);
            } else {
                sender.sendMessage(TextUtils.codedText("&cMust be a player or supply a player!"));
            }
        } else {
            if (sender instanceof ProxiedPlayer p) {
                SavableUser player = PlayerUtils.getOrGetSavableUser(p);
                String[] extra = TextUtils.argsMinus(args, 0);
                if (args[1].startsWith("-p:")) {
                    player = PlayerUtils.getOrGetSavableUser(args[0].substring("-p:".length()));
                    if (player == null) {
                        sender.sendMessage(TextUtils.codedText("&cThat player is either not online or does not exist!"));
                        return;
                    }
                    extra = TextUtils.argsMinus(args, 0, 1);
                }

                CustomSQLInfo sqlInfo = PluginUtils.getExecutionByIdentifier(args[0]);
                if (sqlInfo == null) {
                    sender.sendMessage(TextUtils.codedText("&cThe specified execution is either not loaded or does not exist!"));
                    return;
                }


                        BridgerDataSource.execute(sqlInfo, player, extra);
            } else {
                sender.sendMessage(TextUtils.codedText("&cMust be a player or supply a player!"));
            }
        }

        sender.sendMessage(TextUtils.codedText("&eExecution &7(&c%set%&7) &efinished&8!"
                .replace("%set%", args[0])
        ));
    }

    @Override
    public Collection<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(PluginUtils.getExecutionsAsStrings());
        } else if (args.length == 2) {
            if (! args[1].startsWith("-p:")) return new ArrayList<>();

            List<String> adjustedPlayers = new ArrayList<>();

            for (String s : PlayerUtils.getOnlinePPlayersAsStrings()) {
                adjustedPlayers.add("-p:" + s);
            }

            return adjustedPlayers;
        } else {
            return new ArrayList<>();
        }
    }
}
