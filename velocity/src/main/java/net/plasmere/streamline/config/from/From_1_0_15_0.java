package net.plasmere.streamline.config.from;

public class From_1_0_15_0 extends From{
    public From_1_0_15_0(String language) {
        super(language);
    }

    @Override
    public String versionFrom() {
        return "1.0.14.9";
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

        addUpdatedLocalesEntry("getstats.save", "&eJust saved all of the current users in the cache!", "fr_FR");
        addUpdatedLocalesEntry("getstats.reload", "&eJust reloaded all cached users. Make sure to save the users first! &d/getstats save", "fr_FR");

        addUpdatedLocalesEntry("parties.save", "&eJust saved all of the current parties in the cache!", "fr_FR");
        addUpdatedLocalesEntry("parties.reload", "&eJust reloaded all cached parties. Make sure to save the parties first! &d/parties save", "fr_FR");

        addUpdatedLocalesEntry("guilds.save", "&eJust saved all of the current guilds in the cache!", "fr_FR");
        addUpdatedLocalesEntry("guilds.reload", "&eJust reloaded all cached guilds. Make sure to save the guilds first! &d/guilds save", "fr_FR");
    }

    @Override
    public void setupServerConfigFix() {

    }

    @Override
    public void setupDiscordBotFix() {
    }

    @Override
    public void setupCommandsFix() {
    }

    @Override
    public void setupChatsFix() {

    }
}
