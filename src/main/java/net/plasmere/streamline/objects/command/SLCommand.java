package net.plasmere.streamline.objects.command;

import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.plasmere.streamline.StreamLine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class SLCommand implements SimpleCommand {
    public String base;
    public String permission;
    public String[] aliases;

    public SLCommand(String base, String permission, String... aliases) {
        this.base = base;
        this.permission = permission;
        this.aliases = aliases;
    }

    @Override
    public void execute(Invocation invocation) {
        run(invocation.source(), invocation.arguments());
    }

    abstract public void run(CommandSource sender, String[] args);

    abstract public Iterable<String> onTabComplete(CommandSource sender, String[] args);

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        return CompletableFuture.completedFuture(new ArrayList<>((Collection<? extends String>) onTabComplete(invocation.source(), invocation.arguments())));
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission(permission);
    }
}
