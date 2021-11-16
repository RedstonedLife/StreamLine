package net.plasmere.streamline.commands.staff.events;

import com.velocitypowered.api.command.CommandSource;
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
    public void run(CommandSource sender, String[] args) {
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
    public Collection<String> onTabComplete(CommandSource sender, String[] args) {
        return new ArrayList<>();
    }
}
