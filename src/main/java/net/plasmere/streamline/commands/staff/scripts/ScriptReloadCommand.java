package net.plasmere.streamline.commands.staff.scripts;

import com.velocitypowered.api.command.CommandSource;
import net.md_5.bungee.api.plugin.Command;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.CommandsConfUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.events.EventsHandler;
import net.plasmere.streamline.scripts.ScriptsHandler;
import net.plasmere.streamline.utils.MessagingUtils;

public class ScriptReloadCommand extends Command {
    public ScriptReloadCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void execute(CommandSource sender, String[] args) {
        if (sender.hasPermission(CommandsConfUtils.comBEReloadPerm)) {
            ScriptsHandler.unloadScripts();
            StreamLine.getInstance().loadScripts();

            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.scriptReload()
                    .replace("%count%", String.valueOf(ScriptsHandler.scripts.size()))
            );
        } else {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPerm());
        }
    }
}
