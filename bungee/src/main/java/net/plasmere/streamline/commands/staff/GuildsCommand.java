package net.plasmere.streamline.commands.staff;

import net.md_5.bungee.api.CommandSender;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.savable.groups.SavableGuild;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.utils.GuildUtils;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.TextUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GuildsCommand extends SLCommand {
    public GuildsCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (args.length <= 0) {
            if (GuildUtils.getGuilds().size() <= 0) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.guildsNone());
                return;
            }

            for (SavableGuild guild : GuildUtils.getGuilds()) {
                MessagingUtils.sendBGUserMessage(guild, sender, sender, MessageConfUtils.guildsMessage());
            }
        } else {
            switch (args[0]) {
                case "save" -> {
                    GuildUtils.saveAll();
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.guildsSave());
                }
                case "reload" -> {
                    GuildUtils.reloadAll();
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.guildsReload());
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
