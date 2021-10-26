package net.plasmere.streamline.commands.staff;

import com.velocitypowered.api.command.CommandSource;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.Guild;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.utils.GuildUtils;
import net.plasmere.streamline.utils.MessagingUtils;

import java.util.ArrayList;

public class GuildsCommand extends SLCommand {
    public GuildsCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void run(CommandSource sender, String[] args) {
        if (GuildUtils.getGuilds().size() <= 0) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.guildsNone());
            return;
        }

        for (Guild guild : GuildUtils.getGuilds()){
            MessagingUtils.sendBGUserMessage(guild, sender, sender, MessageConfUtils.guildsMessage());
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSource sender, String[] args) {
        return new ArrayList<>();
    }
}
