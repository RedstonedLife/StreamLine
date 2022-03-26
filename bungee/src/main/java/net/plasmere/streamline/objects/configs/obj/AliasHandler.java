package net.plasmere.streamline.objects.configs.obj;

import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PluginUtils;

public class AliasHandler {
    public static void loadAllAliasCommands() {
        StreamLine.aliasConfig.reloadAliasCommands();

        for (AliasCommand command : StreamLine.aliasConfig.loadedAliasCommands) {
            loadAliasCommand(command);
        }

        if (ConfigUtils.debug()) {
            MessagingUtils.logInfo("Loaded " + PluginUtils.aliasesAmount + " custom aliases into memory!");
        }
    }

    public static void unloadAllAliasCommands() {
        StreamLine.aliasConfig.reloadAliasCommands();

        for (AliasCommand command : StreamLine.aliasConfig.loadedAliasCommands) {
            unloadAliasCommand(command);
        }
    }

    public static void loadAliasCommand(AliasCommand command) {
        PluginUtils.registerAlias(new AliasSLCommand(command.scriptName, command.identifier, command.permission));
    }

    public static void unloadAliasCommand(AliasCommand command) {
        PluginUtils.unregisterAlias(command.identifier);
    }
}