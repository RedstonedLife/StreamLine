package net.plasmere.streamline.commands.staff;

import com.velocitypowered.api.command.CommandSource;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.SavableParty;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PartyUtils;

import java.util.ArrayList;
import java.util.Collection;

public class PartiesCommand extends SLCommand {
    public PartiesCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void run(CommandSource sender, String[] args) {
        if (PartyUtils.getParties().size() <= 0) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.partiesNone());
            return;
        }
        for (SavableParty party : PartyUtils.getParties()){
            MessagingUtils.sendBPUserMessage(party, sender, sender, MessageConfUtils.partiesMessage());
        }
    }

    @Override
    public Collection<String> onTabComplete(CommandSource sender, String[] args) {
        return new ArrayList<>();
    }
}
