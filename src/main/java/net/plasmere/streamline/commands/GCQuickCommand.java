package net.plasmere.streamline.commands;

import net.md_5.bungee.api.CommandSource;
import net.md_5.bungee.api.plugin.Command;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.utils.TextUtils;

public class GCQuickCommand extends Command {
    public GCQuickCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void execute(CommandSource sender, String[] args) {
        StreamLine.getInstance().getProxy().getPluginManager().dispatchCommand(sender, "guild chat " + TextUtils.normalize(args));
    }
}
