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
    // Guild.
    public static long guildID() {
        StreamLine.config.reloadDiscordBot();
        return StreamLine.config.getDisBotLong("discord.guild-id");
    }
    // Text Channels.
    public static long textChannelReports() {
        StreamLine.config.reloadDiscordBot();
        return StreamLine.config.getDisBotLong("discord.text-channels.reports");
    }
    public static long textChannelStaffChat() {
        StreamLine.config.reloadDiscordBot();
        return StreamLine.config.getDisBotLong("discord.text-channels.staffchat");
    }
    public static long textChannelOfflineOnline() {
        StreamLine.config.reloadDiscordBot();
        return StreamLine.config.getDisBotLong("discord.text-channels.offline-online");
    }
    public static long textChannelBJoins() {
        StreamLine.config.reloadDiscordBot();
        return StreamLine.config.getDisBotLong("discord.text-channels.bungee-joins");
    }
    public static long textChannelBLeaves() {
        StreamLine.config.reloadDiscordBot();
        return StreamLine.config.getDisBotLong("discord.text-channels.bungee-leaves");
    }
    public static long textChannelBConsole() {
        StreamLine.config.reloadDiscordBot();
        return StreamLine.config.getDisBotLong("discord.text-channels.console");
    }
    public static long textChannelGuilds() {
        StreamLine.config.reloadDiscordBot();
        return StreamLine.config.getDisBotLong("discord.text-channels.guilds");
    }
    public static long textChannelParties() {
        StreamLine.config.reloadDiscordBot();
        return StreamLine.config.getDisBotLong("discord.text-channels.parties");
    }
    public static long textChannelMutes() {
        StreamLine.config.reloadDiscordBot();
        return StreamLine.config.getDisBotLong("discord.text-channels.mutes");
    }
    public static long textChannelKicks() {
        StreamLine.config.reloadDiscordBot();
        return StreamLine.config.getDisBotLong("discord.text-channels.kicks");
    }
    public static long textChannelBans() {
        StreamLine.config.reloadDiscordBot();
        return StreamLine.config.getDisBotLong("discord.text-channels.bans");
    }
    public static long textChannelIPBans() {
        StreamLine.config.reloadDiscordBot();
        return StreamLine.config.getDisBotLong("discord.text-channels.ipbans");
    }
    public static long textChannelProxyChat() {
        StreamLine.config.reloadDiscordBot();
        return StreamLine.config.getDisBotLong("discord.text-channels.proxy-chat");
    }
    // Roles.
    public static long roleReports() {
        StreamLine.config.reloadDiscordBot();
        return StreamLine.config.getDisBotLong("discord.roles.reports");
    }
    public static long roleStaff() {
        StreamLine.config.reloadDiscordBot();
        return StreamLine.config.getDisBotLong("discord.roles.staff");
    }
    public static long roleChat() {
        StreamLine.config.reloadDiscordBot();
        return StreamLine.config.getDisBotLong("discord.roles.chat");
    }
}
