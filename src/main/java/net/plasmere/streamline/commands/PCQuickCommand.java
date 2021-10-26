package net.plasmere.streamline.commands;

import com.velocitypowered.api.command.CommandSource;
import net.md_5.bungee.api.plugin.Command;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.utils.TextUtils;

public class PCQuickCommand extends Command {
    public PCQuickCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void execute(CommandSource sender, String[] args) {
        StreamLine.getInstance().getProxy().getCommandManager().executeImmediatelyAsync(sender, "party chat " + TextUtils.normalize(args));
    }
}
