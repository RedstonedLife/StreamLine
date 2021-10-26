package net.plasmere.streamline.commands;

import com.velocitypowered.api.command.CommandSource;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.utils.TextUtils;

import java.util.ArrayList;

public class GCQuickCommand extends SLCommand {
    public GCQuickCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void run(CommandSource sender, String[] args) {
        StreamLine.getInstance().getProxy().getCommandManager().executeImmediatelyAsync(sender, "guild chat " + TextUtils.normalize(args));
    }

    @Override
    public Iterable<String> onTabComplete(CommandSource sender, String[] args) {
        return new ArrayList<>();
    }
}
