package net.plasmere.streamline.commands;

import com.velocitypowered.api.plugin.PluginContainer;
import net.plasmere.streamline.StreamLine;
import com.velocitypowered.api.command.CommandSource;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.utils.MessagingUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collection;
import java.util.Collection;

public class PluginsCommand extends SLCommand {

    public PluginsCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    public void run(CommandSource sender, String[] args) {
        if (args.length > 0) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsLess());
        } else {
            MessagingUtils.sendBUserMessage(sender, "&ePlugins&8: " + getPluginList());
        }
    }

    @Override
    public Collection<String> onTabComplete(CommandSource sender, String[] args) {
        return new ArrayList<>();
    }

    private String getPluginList(){
        Collection<PluginContainer> plugins = StreamLine.getInstance().getProxy().getPluginManager().getPlugins();

        StringBuilder pl = new StringBuilder();
        int i = 0;

        for (PluginContainer plugin : plugins){
            if (!(i == plugins.size() - 1))
                pl.append("&a").append(plugin.getDescription().getName().get()).append("&8").append(", ");
            else
                pl.append("&a").append(plugin.getDescription().getName().get()).append("&8,").append(".");
            i++;
        }

        return pl.toString();
    }
}
