package net.plasmere.streamline.commands.staff;

import net.md_5.bungee.api.CommandSender;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.savable.groups.SavableParty;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.utils.GuildUtils;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PartyUtils;
import net.plasmere.streamline.utils.TextUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PartiesCommand extends SLCommand {
    public PartiesCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (args.length <= 0) {
            if (PartyUtils.getParties().size() <= 0) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.partiesNone());
                return;
            }
            for (SavableParty party : PartyUtils.getParties()) {
                MessagingUtils.sendBPUserMessage(party, sender, sender, MessageConfUtils.partiesMessage());
            }
        } else {
            switch (args[0]) {
                case "save" -> {
                    PartyUtils.saveAll();
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.partiesSave());
                }
                case "reload" -> {
                    PartyUtils.reloadAll();
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.partiesReload());
                }
            }
        }
    }

    @Override
    public Collection<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return TextUtils.getCompletion(List.of("save", "reload"), args[0]);
        }

        return new ArrayList<>();
    }
}
