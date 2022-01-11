package net.plasmere.streamline.config.from;

public class From_1_0_14_9 extends From{
    public From_1_0_14_9(String language) {
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
        addUpdatedConfigEntry("modules.database.use", false);

        addUpdatedConfigEntry("modules.discord.proxy-chat.discord-data.non-embedded.message", "%sender_absolute% >> %message%");
    }

    @Override
    public void setupLocalesFix() {

    }

    @Override
    public void setupServerConfigFix() {

    }

    @Override
    public void setupDiscordBotFix() {
        if (! c.getBoolean("modules.discord.enabled")) return;
        addUpdatedDiscordBotEntry("discord.text-channels.reports", Long.valueOf(dis.getString("discord.text-channels.reports")));
        addUpdatedDiscordBotEntry("discord.text-channels.reports", Long.valueOf(dis.getString("discord.text-channels.staffchat")));
        addUpdatedDiscordBotEntry("discord.text-channels.reports", Long.valueOf(dis.getString("discord.text-channels.offline-online")));
        addUpdatedDiscordBotEntry("discord.text-channels.reports", Long.valueOf(dis.getString("discord.text-channels.bungee-joins")));
        addUpdatedDiscordBotEntry("discord.text-channels.reports", Long.valueOf(dis.getString("discord.text-channels.bungee-leaves")));
        addUpdatedDiscordBotEntry("discord.text-channels.reports", Long.valueOf(dis.getString("discord.text-channels.console")));
        addUpdatedDiscordBotEntry("discord.text-channels.reports", Long.valueOf(dis.getString("discord.text-channels.guilds")));
        addUpdatedDiscordBotEntry("discord.text-channels.reports", Long.valueOf(dis.getString("discord.text-channels.parties")));
        addUpdatedDiscordBotEntry("discord.text-channels.reports", Long.valueOf(dis.getString("discord.text-channels.mutes")));
        addUpdatedDiscordBotEntry("discord.text-channels.reports", Long.valueOf(dis.getString("discord.text-channels.kicks")));
        addUpdatedDiscordBotEntry("discord.text-channels.reports", Long.valueOf(dis.getString("discord.text-channels.bans")));
        addUpdatedDiscordBotEntry("discord.text-channels.reports", Long.valueOf(dis.getString("discord.text-channels.ipbans")));
        addUpdatedDiscordBotEntry("discord.text-channels.reports", Long.valueOf(dis.getString("discord.text-channels.proxy-chat")));

        addUpdatedDiscordBotEntry("discord.text-channels.reports", Long.valueOf(dis.getString("discord.roles.reports")));
        addUpdatedDiscordBotEntry("discord.text-channels.reports", Long.valueOf(dis.getString("discord.roles.staff")));
        addUpdatedDiscordBotEntry("discord.text-channels.reports", Long.valueOf(dis.getString("discord.roles.chat")));
    }

    @Override
    public void setupCommandsFix() {
    }

    @Override
    public void setupChatsFix() {

    }
}
