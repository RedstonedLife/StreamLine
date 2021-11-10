package net.plasmere.streamline.config;

import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.objects.enums.CategoryType;

import java.util.Locale;

public class DiscordBotConfUtils {
    // Bot Stuff.
    public static String botPrefix() {
        StreamLine.config.reloadDiscordBot();
        return StreamLine.config.getDisBotString("bot.prefix");
    }
    public static String botToken() {
        StreamLine.config.reloadDiscordBot();
        return StreamLine.config.getDisBotString("bot.token");
    }
    public static String botStatusMessage() {
        StreamLine.config.reloadDiscordBot();
        return StreamLine.config.getDisBotString("bot.server-ip");
    }
    // Categories.
    public static long categoryGet(CategoryType type) {
        StreamLine.config.reloadDiscordBot();
        return StreamLine.config.getDisBotLong("discord.categories." + type.name().toLowerCase(Locale.ROOT));
    }
    // ... Discord.
    // Text Channels.
    public static String textChannelReports() {
        StreamLine.config.reloadDiscordBot();
        return StreamLine.config.getDisBotString("discord.text-channels.reports");
    }
    public static String textChannelStaffChat() {
        StreamLine.config.reloadDiscordBot();
        return StreamLine.config.getDisBotString("discord.text-channels.staffchat");
    }
    public static String textChannelOfflineOnline() {
        StreamLine.config.reloadDiscordBot();
        return StreamLine.config.getDisBotString("discord.text-channels.offline-online");
    }
    public static String textChannelBJoins() {
        StreamLine.config.reloadDiscordBot();
        return StreamLine.config.getDisBotString("discord.text-channels.bungee-joins");
    }
    public static String textChannelBLeaves() {
        StreamLine.config.reloadDiscordBot();
        return StreamLine.config.getDisBotString("discord.text-channels.bungee-leaves");
    }
    public static String textChannelBConsole() {
        StreamLine.config.reloadDiscordBot();
        return StreamLine.config.getDisBotString("discord.text-channels.console");
    }
    public static String textChannelGuilds() {
        StreamLine.config.reloadDiscordBot();
        return StreamLine.config.getDisBotString("discord.text-channels.guilds");
    }
    public static String textChannelParties() {
        StreamLine.config.reloadDiscordBot();
        return StreamLine.config.getDisBotString("discord.text-channels.parties");
    }
    public static String textChannelMutes() {
        StreamLine.config.reloadDiscordBot();
        return StreamLine.config.getDisBotString("discord.text-channels.mutes");
    }
    public static String textChannelKicks() {
        StreamLine.config.reloadDiscordBot();
        return StreamLine.config.getDisBotString("discord.text-channels.kicks");
    }
    public static String textChannelBans() {
        StreamLine.config.reloadDiscordBot();
        return StreamLine.config.getDisBotString("discord.text-channels.bans");
    }
    public static String textChannelIPBans() {
        StreamLine.config.reloadDiscordBot();
        return StreamLine.config.getDisBotString("discord.text-channels.ipbans");
    }
    public static String textChannelProxyChat() {
        StreamLine.config.reloadDiscordBot();
        return StreamLine.config.getDisBotString("discord.text-channels.proxy-chat");
    }
    // Roles.
    public static String roleReports() {
        StreamLine.config.reloadDiscordBot();
        return StreamLine.config.getDisBotString("discord.roles.reports");
    }
    public static String roleStaff() {
        StreamLine.config.reloadDiscordBot();
        return StreamLine.config.getDisBotString("discord.roles.staff");
    }
    public static String roleChat() {
        StreamLine.config.reloadDiscordBot();
        return StreamLine.config.getDisBotString("discord.roles.chat");
    }
}
