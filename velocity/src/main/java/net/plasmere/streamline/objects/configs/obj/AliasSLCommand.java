package net.plasmere.streamline.objects.configs.obj;

import com.velocitypowered.api.command.CommandSource;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.scripts.Script;
import net.plasmere.streamline.scripts.ScriptsHandler;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;

import java.util.Collection;

public class AliasSLCommand extends SLCommand {
    public String scriptName;

    public AliasSLCommand(String scriptName, String base, String permission, String... aliases) {
        super(base, permission, aliases);
        this.scriptName = scriptName;
    }

    @Override
    public void run(CommandSource sender, String[] args) {
        Script script = ScriptsHandler.getScript(scriptName);
        if (script == null) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd()
                    .replace("%class%", getClass().getSimpleName())
            );
            return;
        }

        script.execute(sender, PlayerUtils.getOrGetSavableUser(sender));
    }

    @Override
    public Collection<String> onTabComplete(CommandSource sender, String[] args) {
        return null;
    }
}
