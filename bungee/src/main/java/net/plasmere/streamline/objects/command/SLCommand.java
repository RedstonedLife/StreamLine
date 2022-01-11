package net.plasmere.streamline.objects.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class SLCommand extends Command implements TabExecutor {
    public String base;
    public String permission;
    public String[] aliases;

    public SLCommand(String base, String permission, String... aliases) {
        super(base, permission, aliases);
        this.base = base;
        this.permission = permission;
        this.aliases = aliases;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        run(sender, args);
    }

    abstract public void run(CommandSender sender, String[] args);

    abstract public Collection<String> onTabComplete(CommandSender sender, String[] args);

    public Collection<String> preTabComplete(CommandSender sender, String[] args){
        if (args == null) args = new String[]{ "" };
        if (args.length <= 0) args = new String[]{ "" };

        return onTabComplete(sender, args);
    }

    @Override
    public boolean hasPermission(CommandSender invocation) {
        return invocation.hasPermission(permission);
    }
}
