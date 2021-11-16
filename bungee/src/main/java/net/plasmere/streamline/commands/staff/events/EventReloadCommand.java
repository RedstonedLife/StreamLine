package net.plasmere.streamline.commands.staff.events;

import net.md_5.bungee.api.CommandSender;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.CommandsConfUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.events.EventsHandler;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.utils.MessagingUtils;

import java.util.ArrayList;
import java.util.Collection;

public class EventReloadCommand extends SLCommand {
    public EventReloadCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (sender.hasPermission(CommandsConfUtils.comBEReloadPerm())) {
            EventsHandler.unloadEvents();
            StreamLine.getInstance().loadEvents();

            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.evReload()
                    .replace("%count%", String.valueOf(EventsHandler.getEvents().size()))
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
