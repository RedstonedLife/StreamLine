package net.plasmere.streamline.config.from;

import java.util.ArrayList;
import java.util.Arrays;

public class From_1_0_14_2 extends From{
    public From_1_0_14_2(String language) {
        super(language);
    }

    @Override
    public String versionFrom() {
        return "1.0.14.2";
    }

    @Override
    public void getCatchAll_values() {

    }

    @Override
    public void setupConfigFix() {
        addUpdatedConfigEntry("modules.offline-mode", false);

        addUpdatedConfigEntry("modules.bungee.stats.delete-bad", true);

        addUpdatedConfigEntry("modules.bungee.scripts.enabled", true);
        addUpdatedConfigEntry("modules.bungee.scripts.create-default", true);

        addUpdatedConfigEntry("modules.discord.boosts.enabled", true);
        addUpdatedConfigEntry("modules.discord.boosts.upon-boosts.run", "boost.sl");

        addUpdatedConfigEntry("modules.discord.proxy-chat.discord-data.channels.local.joins", "Joined the server");
        addUpdatedConfigEntry("modules.discord.proxy-chat.discord-data.channels.local.leaves", "Left the server");
        addUpdatedConfigEntry("modules.discord.proxy-chat.discord-data.channels.global.joins", "Joined the server");
        addUpdatedConfigEntry("modules.discord.proxy-chat.discord-data.channels.global.leaves", "Left the server");
        addUpdatedConfigEntry("modules.discord.proxy-chat.discord-data.channels.guild.joins", "Joined the server");
        addUpdatedConfigEntry("modules.discord.proxy-chat.discord-data.channels.guild.leaves", "Left the server");
        addUpdatedConfigEntry("modules.discord.proxy-chat.discord-data.channels.party.joins", "Joined the server");
        addUpdatedConfigEntry("modules.discord.proxy-chat.discord-data.channels.party.leaves", "Left the server");
    }

    @Override
    public void setupLocalesFix() {
        addUpdatedLocalesEntry("script.message", "&eJust ran script &a%script% &eon &d%player_formatted%&e!", "en_US");
        addUpdatedLocalesEntry("script.no-script", "&cNo valid script by this name!", "en_US");
        addUpdatedLocalesEntry("script.reload", "&eReloaded scripts! (&b%count%&e)", "en_US");

        addUpdatedLocalesEntry("script.message", "&eJe viens d'exécuter le script &a%script% &eau &d%player_formatted%&e!", "fr_FR");
        addUpdatedLocalesEntry("script.no-script", "&cAucun script valide sous ce nom!", "fr_FR");
        addUpdatedLocalesEntry("script.reload", "&eScripts rechargés! (&b%count%&e)", "fr_FR");
    }

    @Override
    public void setupServerConfigFix() {

    }

    @Override
    public void setupDiscordBotFix() {

    }

    @Override
    public void setupCommandsFix() {
        addUpdatedCommandsEntry("commands.bungee.staff.script.enabled", true);
        addUpdatedCommandsEntry("commands.bungee.staff.script.base", "slscript");
        addUpdatedCommandsEntry("commands.bungee.staff.script.permission", "streamline.command.script");
        addUpdatedCommandsEntry("commands.bungee.staff.script.aliases", Arrays.asList("sls", "bscript", "slscr", "streamlinescript", "script"));

        addUpdatedCommandsEntry("commands.bungee.staff.script-reload.enabled", true);
        addUpdatedCommandsEntry("commands.bungee.staff.script-reload.base", "slsr");
        addUpdatedCommandsEntry("commands.bungee.staff.script-reload.permission", "streamline.command.script-reload");
        addUpdatedCommandsEntry("commands.bungee.staff.script-reload.aliases", Arrays.asList("reloadslsc", "rslsc"));
    }
}
