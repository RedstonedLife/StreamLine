package net.plasmere.streamline.config.from;

import java.util.Arrays;

public class From_1_0_15_0 extends From{
    public From_1_0_15_0(String language) {
        super(language);
    }

    @Override
    public String versionFrom() {
        return "1.0.15.0";
    }

    @Override
    public void getCatchAll_values() {

    }

    @Override
    public void setupConfigFix() {
        addUpdatedConfigEntry("modules.bungee.stats.experience.starting.level", 1);
        addUpdatedConfigEntry("modules.bungee.stats.experience.starting.xp", 0);
        addUpdatedConfigEntry("modules.bungee.stats.experience.equation", "2500 + (2500 * (%player_level% - 1))");

        addUpdatedConfigEntry("modules.bungee.guilds.experience.starting.level", 1);
        addUpdatedConfigEntry("modules.bungee.guilds.experience.starting.xp", 0);
        addUpdatedConfigEntry("modules.bungee.guilds.experience.equation", "10000 + (10000 * (%guild_level% - 1))");
    }

    @Override
    public void setupLocalesFix() {
        addUpdatedLocalesEntry("getstats.save", "&eJust saved all of the current users in the cache!", "en_US");
        addUpdatedLocalesEntry("getstats.reload", "&eJust reloaded all cached users. Make sure to save the users first! &d/getstats save", "en_US");

        addUpdatedLocalesEntry("parties.save", "&eJust saved all of the current parties in the cache!", "en_US");
        addUpdatedLocalesEntry("parties.reload", "&eJust reloaded all cached parties. Make sure to save the parties first! &d/parties save", "en_US");

        addUpdatedLocalesEntry("guilds.save", "&eJust saved all of the current guilds in the cache!", "en_US");
        addUpdatedLocalesEntry("guilds.reload", "&eJust reloaded all cached guilds. Make sure to save the guilds first! &d/guilds save", "en_US");

        addUpdatedLocalesEntry("playtime.console.toggle", "&eToggled console printing to %toggle%&8!", "en_US");
        addUpdatedLocalesEntry("playtime.console.enabled", "&aTRUE", "en_US");
        addUpdatedLocalesEntry("playtime.console.disabled", "&cFALSE", "en_US");
        addUpdatedLocalesEntry("playtime.get", "&eplaytime of &d%player_formatted%&8: &6%player_play_seconds%", "en_US");
        addUpdatedLocalesEntry("playtime.remove", "&eRemoved &6%playtime% &evote(s) from &d%player_formatted%&8! &eCurrent&8: &6%player_play_seconds%", "en_US");
        addUpdatedLocalesEntry("playtime.add", "&eAdded &6%playtime% &evote(s) to &d%player_formatted%&8! &eCurrent&8: &6%player_play_seconds%", "en_US");
        addUpdatedLocalesEntry("playtime.set", "&eSet &6%playtime% &evote(s) for &d%player_formatted%&8! &eCurrent&8: &6%player_play_seconds%", "en_US");
        addUpdatedLocalesEntry("playtime.sync.start", "&eSyncing now...!", "en_US");
        addUpdatedLocalesEntry("playtime.sync.finish", "&eFinished syncing!", "en_US");
        addUpdatedLocalesEntry("playtime.top", "&a#%position% &8: &d%player_formatted% &ewith &b%player_play_hours% &chours&8.", "en_US");

        addUpdatedLocalesEntry("getstats.save", "&eJust saved all of the current users in the cache!", "fr_FR");
        addUpdatedLocalesEntry("getstats.reload", "&eJust reloaded all cached users. Make sure to save the users first! &d/getstats save", "fr_FR");

        addUpdatedLocalesEntry("parties.save", "&eJust saved all of the current parties in the cache!", "fr_FR");
        addUpdatedLocalesEntry("parties.reload", "&eJust reloaded all cached parties. Make sure to save the parties first! &d/parties save", "fr_FR");

        addUpdatedLocalesEntry("guilds.save", "&eJust saved all of the current guilds in the cache!", "fr_FR");
        addUpdatedLocalesEntry("guilds.reload", "&eJust reloaded all cached guilds. Make sure to save the guilds first! &d/guilds save", "fr_FR");

        addUpdatedLocalesEntry("playtime.console.toggle", "&eToggled console printing to %toggle%&8!", "fr_FR");
        addUpdatedLocalesEntry("playtime.console.enabled", "&aTRUE", "fr_FR");
        addUpdatedLocalesEntry("playtime.console.disabled", "&cFALSE", "fr_FR");
        addUpdatedLocalesEntry("playtime.get", "&eplaytime of &d%player_formatted%&8: &6%player_play_seconds%", "fr_FR");
        addUpdatedLocalesEntry("playtime.remove", "&eRemoved &6%playtime% &evote(s) from &d%player_formatted%&8! &eCurrent&8: &6%player_play_seconds%", "fr_FR");
        addUpdatedLocalesEntry("playtime.add", "&eAdded &6%playtime% &evote(s) to &d%player_formatted%&8! &eCurrent&8: &6%player_play_seconds%", "fr_FR");
        addUpdatedLocalesEntry("playtime.set", "&eSet &6%playtime% &evote(s) for &d%player_formatted%&8! &eCurrent&8: &6%player_play_seconds%", "fr_FR");
        addUpdatedLocalesEntry("playtime.sync.start", "&eSyncing now...!", "fr_FR");
        addUpdatedLocalesEntry("playtime.sync.finish", "&eFinished syncing!", "fr_FR");
        addUpdatedLocalesEntry("playtime.top", "&a#%position% &8: &d%player_formatted% &ewith &b%player_play_hours% &chours&8.", "fr_FR");
    }

    @Override
    public void setupServerConfigFix() {

    }

    @Override
    public void setupDiscordBotFix() {
    }

    @Override
    public void setupCommandsFix() {
        addUpdatedCommandsEntry("commands.bungee.configs.playtime.enabled", true);
        addUpdatedCommandsEntry("commands.bungee.configs.playtime.base", "playtime");
        addUpdatedCommandsEntry("commands.bungee.configs.playtime.permission", "streamline.command.playtime");
        addUpdatedCommandsEntry("commands.bungee.configs.playtime.aliases", Arrays.asList("bplaytime", "bpt"));
    }

    @Override
    public void setupChatsFix() {

    }
}
