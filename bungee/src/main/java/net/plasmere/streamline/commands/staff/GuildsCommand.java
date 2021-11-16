package net.plasmere.streamline.commands.staff;

import net.md_5.bungee.api.CommandSender;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.SavableGuild;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.utils.GuildUtils;
import net.plasmere.streamline.utils.MessagingUtils;

import java.util.ArrayList;
import java.util.Collection;

public class GuildsCommand extends SLCommand {
    public GuildsCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (GuildUtils.getGuilds().size() <= 0) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.guildsNone());
            return;
        }

        for (SavableGuild guild : GuildUtils.getGuilds()){
            MessagingUtils.sendBGUserMessage(guild, sender, sender, MessageConfUtils.guildsMessage());
        }
    }

    @Override
    public Collection<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
