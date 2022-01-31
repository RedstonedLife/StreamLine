package net.plasmere.streamline.config.from;

import java.util.Arrays;

public class From_1_0_15_3 extends From{
    public From_1_0_15_3(String language) {
        super(language);
    }

    @Override
    public String versionFrom() {
        return "1.0.15.3";
    }

    @Override
    public void getCatchAll_values() {

    }

    @Override
    public void setupConfigFix() {
        addUpdatedConfigEntry("modules.bungee.custom-tablist.enabled", false);
        addUpdatedConfigEntry("modules.bungee.custom-aliases.enabled", true);
    }

    @Override
    public void setupLocalesFix() {
        addUpdatedLocalesEntry("proxytext.sent", "&eSuccessfully sent text: &r%text%", "en_US");
        addUpdatedLocalesEntry("proxytitle.sent", "&eSuccessfully sent title &7(&a%fade_in%&8, &a%stay%&8, &a%fade_out%&7)&e:\n&r%title%", "en_US");
    }

    @Override
    public void setupServerConfigFix() {

    }

    @Override
    public void setupDiscordBotFix() {
    }

    @Override
    public void setupCommandsFix() {
        addUpdatedCommandsEntry("commands.bungee.messaging.proxytext.enabled", true);
        addUpdatedCommandsEntry("commands.bungee.messaging.proxytext.base", "proxytext");
        addUpdatedCommandsEntry("commands.bungee.messaging.proxytext.permission", "streamline.command.text");
        addUpdatedCommandsEntry("commands.bungee.messaging.proxytext.aliases", Arrays.asList("pt", "ptext"));

        addUpdatedCommandsEntry("commands.bungee.messaging.proxytitle.enabled", true);
        addUpdatedCommandsEntry("commands.bungee.messaging.proxytitle.base", "proxytitle");
        addUpdatedCommandsEntry("commands.bungee.messaging.proxytitle.permission", "streamline.command.title");
        addUpdatedCommandsEntry("commands.bungee.messaging.proxytitle.aliases", Arrays.asList("pti", "ptitle"));
    }

    @Override
    public void setupChatsFix() {

    }
}
