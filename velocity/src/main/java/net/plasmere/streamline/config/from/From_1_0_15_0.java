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
