package net.plasmere.streamline.commands.staff;

import com.velocitypowered.api.command.CommandSource;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.events.EventsHandler;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.objects.configs.obj.AliasHandler;
import net.plasmere.streamline.scripts.ScriptsHandler;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PluginUtils;
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
    public void run(CommandSource sender, String[] strings) {
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
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.prefix() + MessageConfUtils.reload());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.prefix() + MessageConfUtils.noPerm());
        }
    }

    @Override
    public Collection<String> onTabComplete(CommandSource sender, String[] args) {
        return new ArrayList<>();
    }
}
