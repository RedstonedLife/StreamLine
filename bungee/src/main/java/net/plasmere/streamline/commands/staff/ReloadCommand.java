package net.plasmere.streamline.commands.staff;

import net.md_5.bungee.api.CommandSender;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.events.EventsHandler;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.objects.configs.obj.AliasHandler;
import net.plasmere.streamline.objects.savable.groups.SavableGuild;
import net.plasmere.streamline.objects.savable.groups.SavableParty;
import net.plasmere.streamline.objects.savable.users.SavableUser;
import net.plasmere.streamline.utils.GuildUtils;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PartyUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.sql.DataSource;

import java.util.ArrayList;
import java.util.Collection;

public class ReloadCommand extends SLCommand {
    private String perm = "";

    public ReloadCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);

        this.perm = perm;
    }

    @Override
    public void run(CommandSender sender, String[] strings) {
        if (sender.hasPermission(perm)) {
            try {
                StreamLine.config.reloadConfig();
                StreamLine.config.reloadLocales();
                StreamLine.config.reloadDiscordBot();
                StreamLine.config.reloadCommands();

                if (ConfigUtils.events()) {
                    EventsHandler.reloadEvents();
                }

                if (ConfigUtils.scriptsEnabled()) {
                    StreamLine.getInstance().loadScripts();
                }

                if (ConfigUtils.customAliasesEnabled()) {
                    AliasHandler.unloadAllAliasCommands();
                    AliasHandler.loadAllAliasCommands();
                }

                if (ConfigUtils.moduleDBUse()) {
                    DataSource.verifyTables();
                }


//                PlayerUtils.loadAllPlayers();
//                GuildUtils.loadAllGuilds();
//                PartyUtils.loadAllParties();

                for (SavableUser user : PlayerUtils.getStats()) {
                    for (SavableGuild g : GuildUtils.getGuilds()) {
                        if (g.hasMember(user)) user.setGuild(g.uuid);
                    }
                    for (SavableParty p : PartyUtils.getParties()) {
                        if (p.hasMember(user)) user.setParty(p.uuid);
                    }
                    user.saveAll();
                }
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.prefix() + MessageConfUtils.reload());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.prefix() + MessageConfUtils.noPerm());
        }
    }

    @Override
    public Collection<String> onTabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
