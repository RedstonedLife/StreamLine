package net.plasmere.streamline.commands;

import net.md_5.bungee.api.CommandSender;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.utils.TextUtils;

import java.util.ArrayList;
import java.util.Collection;

public class GCQuickCommand extends SLCommand {
    public GCQuickCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        StreamLine.getInstance().getProxy().getPluginManager().dispatchCommand(sender, "guild chat " + TextUtils.normalize(args));
    }

    @Override
    public Collection<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
