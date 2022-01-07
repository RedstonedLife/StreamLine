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
import java.util.List;

public class MSBExecuteCommand extends SLCommand {
    public MSBExecuteCommand(String base, String permission, String... aliases) {
        super(base, permission, aliases);
    }

    @Override
    public void run(CommandSource sender, String[] args) {
        if (args.length <= 0) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.msbExecuteNotSupplied());
            return;
        }

        if (! StreamLine.msbConfig.isValidExecution(args[0])) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.msbExecuteNotValid());
            return;
        }

        BridgerDataSource.doExecution(args[0], TextUtils.argsMinus(args, 0));

        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.msbExecuteComplete()
                .replace("%set%", args[0])
        );
    }

    @Override
    public Collection<String> onTabComplete(CommandSource sender, String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(StreamLine.msbConfig.getExecutionNames());
        } else {
            return new ArrayList<>();
        }
    }
}
