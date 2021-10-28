package net.plasmere.streamline.config.from;

import net.plasmere.streamline.utils.MessagingUtils;

public class From_1_0_14_5 extends From{
    public From_1_0_14_5(String language) {
        super(language);
    }

    @Override
    public String versionFrom() {
        return "1.0.14.5";
    }

    @Override
    public void getCatchAll_values() {

    }

    @Override
    public void setupConfigFix() {
        MessagingUtils.logSevere("Set up you bStats settings. They are at the bottom of your config.yml.");
        addUpdatedConfigEntry("modules.bstats.make-server-discoverable", true);
        addUpdatedConfigEntry("modules.bstats.discoverable-ip", "my.server.net");
    }

    @Override
    public void setupLocalesFix() {

    }

    @Override
    public void setupServerConfigFix() {

    }

    @Override
    public void setupDiscordBotFix() {

    }

    @Override
    public void setupCommandsFix() {
        addUpdatedCommandsEntry("commands.bungee.ping.view-others.enabled", true);
        addUpdatedCommandsEntry("commands.bungee.ping.view-others.permission", "streamline.command.ping.others");
    }

    @Override
    public void setupChatsFix() {

    }
}
