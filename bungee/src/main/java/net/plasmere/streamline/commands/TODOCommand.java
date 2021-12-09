package net.plasmere.streamline.commands;

import net.md_5.bungee.api.CommandSender;
import net.plasmere.streamline.objects.command.SLCommand;

import java.util.ArrayList;
import java.util.Collection;

public class TODOCommand extends SLCommand {

    public TODOCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    public void run(CommandSender sender, String[] args) {

    }

    @Override
    public Collection<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
