package net.plasmere.streamline.commands;

import com.velocitypowered.api.command.CommandSource;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.utils.TextUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collection;

public class PCQuickCommand extends SLCommand {
    public PCQuickCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void run(CommandSource sender, String[] args) {
        StreamLine.getInstance().getProxy().getCommandManager().executeImmediatelyAsync(sender, "party chat " + TextUtils.normalize(args));
    }

    @Override
    public Collection<String> onTabComplete(CommandSource sender, String[] args) {
        return new ArrayList<>();
    }
}
