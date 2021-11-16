package net.plasmere.streamline.config.from;

public class From_1_0_14_3 extends From{
    public From_1_0_14_3(String language) {
        super(language);
    }

    @Override
    public String versionFrom() {
        return "1.0.14.3";
    }

    @Override
    public void getCatchAll_values() {

    }

    @Override
    public void setupConfigFix() {
        addUpdatedConfigEntry("modules.custom-chats", true);
    }

    @Override
    public void setupLocalesFix() {
        setNull(m, "chat-channels.local");
        setNull(m, "chat-channels.global");
        setNull(m, "chat-channels.guild");
        setNull(m, "chat-channels.party");
        setNull(m, "chat-channels.g-officer");
        setNull(m, "chat-channels.p-officer");

        addUpdatedLocalesEntry("chat-channels.switch", "&eJust switched to the &c%new_channel% &a-> &c%new_identifier% &bChat &efrom &c%old_channel% &a-> &c%old_identifier% &bChat&8!", "en_US");
        addUpdatedLocalesEntry("chat-channels.switch", "&eJust switched to the &c%new_channel% &a-> &c%new_identifier% &bChat &efrom &c%old_channel% &a-> &c%old_identifier% &bChat&8!", "fr_FR");
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
