package net.plasmere.streamline.config.from;

public class From_1_0_14_6 extends From{
    public From_1_0_14_6(String language) {
        super(language);
    }

    @Override
    public String versionFrom() {
        return "1.0.14.6";
    }

    @Override
    public void getCatchAll_values() {

    }

    @Override
    public void setupConfigFix() {
        addUpdatedConfigEntry("modules.discord.proxy-chat.display-names.verifying.change.unchangeable", true);
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
