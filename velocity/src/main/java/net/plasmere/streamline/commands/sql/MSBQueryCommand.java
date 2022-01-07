package net.plasmere.streamline.commands.sql;

import com.velocitypowered.api.command.CommandSource;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.TextUtils;
import net.plasmere.streamline.utils.sql.BridgerDataSource;

import java.util.ArrayList;
import java.util.Collection;

public class MSBQueryCommand extends SLCommand {
    public MSBQueryCommand(String base, String permission, String... aliases) {
        super(base, permission, aliases);
    }

    @Override
    public void run(CommandSource sender, String[] args) {
        if (args.length <= 0) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.msbQueryNotSupplied());
            return;
        }

        if (! StreamLine.msbConfig.isValidExecution(args[0])) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.msbQueryNotValid());
            return;
        }

        String queryAnswer = BridgerDataSource.doQuery(args[0], TextUtils.argsMinus(args, 0));
        StreamLine.holders.put(StreamLine.msbConfig.getSetAs(args[0]), queryAnswer);

        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.msbQueryComplete()
                .replace("%set%", args[0])
                .replace("%return%", queryAnswer)
        );
    }

    @Override
    public Collection<String> onTabComplete(CommandSource sender, String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(StreamLine.msbConfig.getQueryNames());
        } else {
            return new ArrayList<>();
        }
    }
}
