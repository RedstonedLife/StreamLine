package net.plasmere.streamline.commands.sql;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.objects.savable.users.SavableUser;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.PluginUtils;
import net.plasmere.streamline.utils.TextUtils;
import net.plasmere.streamline.utils.objects.CustomSQLInfo;
import net.plasmere.streamline.utils.objects.Syncable;
import net.plasmere.streamline.utils.sql.BridgerDataSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class MSBSyncCommand extends SLCommand {
    public MSBSyncCommand(String base, String permission, String... aliases) {
        super(base, permission, aliases);
    }

    @Override
    public void run(CommandSource sender, String[] args) {
        StreamLine.msbConfig.reloadHosts();
        StreamLine.msbConfig.reloadSyncables();

        if (args.length <= 0) {
            sender.sendMessage(TextUtils.codedText("&cYou haven't supplied a player (and an optional syncable)!"));
            return;
        }

        if (args.length >= 3) {
            sender.sendMessage(TextUtils.codedText("&cYou must only supply a player (and an optional syncable), nothing else!"));
            return;
        }

        if (args.length == 1) {
            SavableUser player = PlayerUtils.getSavableUser(args[0].substring("-p:".length()));
            if (player == null) {
                sender.sendMessage(TextUtils.codedText("&cThat player is either not online or does not exist!"));
                return;
            }

            for (Syncable syncable : StreamLine.msbConfig.loadedSyncables) {
                String queryAnswer = BridgerDataSource.sync(syncable, player);

                sender.sendMessage(TextUtils.codedText("&eSync came back as &7(&c%set%&7)&8: &r%return%"
                        .replace("%set%", args[0])
                        .replace("%return%", queryAnswer)
                ));
            }
        } else {
            SavableUser player = PlayerUtils.getSavableUser(args[0].substring("-p:".length()));
            if (player == null) {
                sender.sendMessage(TextUtils.codedText("&cThat player is either not online or does not exist!"));
                return;
            }

            if (! StreamLine.msbConfig.isValidSyncable(args[1])) {
                sender.sendMessage(TextUtils.codedText("&cYou haven't supplied a valid syncable!"));
                return;
            }

            Syncable syncable = PluginUtils.getSyncableByIdentifier(args[1]);
            if (syncable == null) {
                sender.sendMessage(TextUtils.codedText("&cThe specified syncable is either not loaded or does not exist!"));
                return;
            }

            String queryAnswer = BridgerDataSource.sync(syncable, player);

            sender.sendMessage(TextUtils.codedText("&eSync came back as &7(&c%set%&7)&8: &r%return%"
                    .replace("%set%", args[0])
                    .replace("%return%", queryAnswer)
            ));
        }
    }

    @Override
    public Collection<String> onTabComplete(CommandSource sender, String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(PluginUtils.getSyncablesAsStrings());
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
