package net.plasmere.streamline.commands.staff;

import net.md_5.bungee.api.CommandSender;
import net.plasmere.streamline.objects.command.SLCommand;

import java.util.ArrayList;
import java.util.Collection;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.Party;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PartyUtils;

public class PartiesCommand extends SLCommand {
    public PartiesCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (PartyUtils.getParties().size() <= 0) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.partiesNone());
            return;
        }
        for (Party party : PartyUtils.getParties()){
            MessagingUtils.sendBPUserMessage(party, sender, sender, MessageConfUtils.partiesMessage());
        }
    }

    @Override
    public Collection<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
