package net.plasmere.streamline.commands.staff.scripts;

import net.md_5.bungee.api.CommandSender;
import net.plasmere.streamline.objects.command.SLCommand;

import java.util.ArrayList;
import java.util.Collection;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.CommandsConfUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.events.EventsHandler;
import net.plasmere.streamline.scripts.ScriptsHandler;
import net.plasmere.streamline.utils.MessagingUtils;

public class ScriptReloadCommand extends SLCommand {
    public ScriptReloadCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (sender.hasPermission(CommandsConfUtils.comBScriptRePerm())) {
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
    public Collection<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
