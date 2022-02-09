package net.plasmere.streamline.commands.sql;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
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
import java.util.Optional;

public class MSBQueryCommand extends SLCommand {
    public MSBQueryCommand(String base, String permission, String... aliases) {
        super(base, permission, aliases);
    }

    @Override
    public void run(CommandSource sender, String[] args) {
        StreamLine.msbConfig.reloadQueries();

        if (args.length <= 0) {
            sender.sendMessage(TextUtils.codedText("&cYou haven't supplied a query (and an optional player)!"));
            return;
        }

//        if (args.length >= 3) {
//            sender.sendMessage(TextUtils.codedText("&cYou must only supply a query (and an optional player), nothing else!"));
//            return false;
//        }

        if (! StreamLine.msbConfig.isValidQuery(args[0])) {
            sender.sendMessage(TextUtils.codedText("&cYou haven't supplied a valid query!"));
            return;
        }

        if (args.length == 1) {
            if (sender instanceof Player p) {
                SavableUser player = PlayerUtils.getOrGetSavableUser(p);
                CustomSQLInfo sqlInfo = PluginUtils.getQueryByIdentifier(args[0]);
                if (sqlInfo == null) {
                    sender.sendMessage(TextUtils.codedText("&cThe specified query is either not loaded or does not exist!"));
                    return;
                }

                String queryAnswer = BridgerDataSource.query(sqlInfo, player);

                sender.sendMessage(TextUtils.codedText("&eQuery came back as &7(&c%set%&7)&8: &r%return%"
                        .replace("%set%", args[0])
                        .replace("%return%", queryAnswer)
                ));
            } else {
                sender.sendMessage(TextUtils.codedText("&cMust be a player or supply a player!"));
            }
        } else {
            if (sender instanceof Player p) {
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

                CustomSQLInfo sqlInfo = PluginUtils.getQueryByIdentifier(args[0]);
                if (sqlInfo == null) {
                    sender.sendMessage(TextUtils.codedText("&cThe specified query is either not loaded or does not exist!"));
                    return;
                }

                String queryAnswer = BridgerDataSource.query(sqlInfo, player, extra);

                sender.sendMessage(TextUtils.codedText("&eQuery came back as &7(&c%set%&7)&8: &r%return%"
                        .replace("%set%", args[0])
                        .replace("%return%", queryAnswer)
                ));
            } else {
                sender.sendMessage(TextUtils.codedText("&cMust be a player or supply a player!"));
            }
        }
    }

    @Override
    public Collection<String> onTabComplete(CommandSource sender, String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(PluginUtils.getQueriesAsStrings());
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
