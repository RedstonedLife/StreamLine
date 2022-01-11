package net.plasmere.streamline.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Plugin;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.utils.MessagingUtils;

import java.util.ArrayList;
import java.util.Collection;

public class PluginsCommand extends SLCommand {

    public PluginsCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    public void run(CommandSender sender, String[] args) {
        if (args.length > 0) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsLess());
        } else {
            MessagingUtils.sendBUserMessage(sender, "&ePlugins&8: " + getPluginList());
        }
    }

    @Override
    public Collection<String> onTabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    private String getPluginList(){
        Collection<Plugin> plugins = StreamLine.getInstance().getProxy().getPluginManager().getPlugins();

        StringBuilder pl = new StringBuilder();
        int i = 0;

        for (Plugin plugin : plugins){
            if (!(i == plugins.size() - 1))
                pl.append(ChatColor.GREEN).append(plugin.getDescription().getName()).append(ChatColor.DARK_GRAY).append(", ");
            else
                pl.append(ChatColor.GREEN).append(plugin.getDescription().getName()).append(ChatColor.DARK_GRAY).append(".");
            i++;
        }

        return pl.toString();
    }
}
