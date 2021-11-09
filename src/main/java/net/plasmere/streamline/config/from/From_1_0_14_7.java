package net.plasmere.streamline.config.from;

public class From_1_0_14_7 extends From{
    public From_1_0_14_7(String language) {
        super(language);
    }

    @Override
    public String versionFrom() {
        return "1.0.14.7";
    }

    @Override
    public void getCatchAll_values() {

    }

    @Override
    public void setupConfigFix() {
        addUpdatedConfigEntry("modules.bungee.votifier.enabled", true);
        addUpdatedConfigEntry("modules.bungee.votifier.on-vote.run", "on-vote.sl");
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
