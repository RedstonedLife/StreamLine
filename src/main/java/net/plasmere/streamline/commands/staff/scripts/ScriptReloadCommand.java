package net.plasmere.streamline.commands.staff.scripts;

import com.velocitypowered.api.command.CommandSource;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.CommandsConfUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.events.EventsHandler;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.scripts.ScriptsHandler;
import net.plasmere.streamline.utils.MessagingUtils;

import java.util.ArrayList;
import java.util.Collection;

public class ScriptReloadCommand extends SLCommand {
    public ScriptReloadCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void run(CommandSource sender, String[] args) {
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

    @Override
    public Collection<String> onTabComplete(CommandSource sender, String[] args) {
        return new ArrayList<>();
    }
}
