package net.plasmere.streamline.config;

import net.md_5.bungee.config.Configuration;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.objects.chats.ChatChannel;

import java.util.List;

public class ConfigUtils {
    // ConfigHandler //
//    public static String s() {
//        StreamLine.config.reloadConfig();
//        return StreamLine.config.getConfString("");
//    }
    // Important.
//    public static String version() {
//        StreamLine.config.reloadConfig();
//        return StreamLine.config.getConfString("version");
//    }
    // Debug.
    public static boolean debug() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("debug");
    }
    // ... Basics.
    // Links.
    public static String linkPre() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("link-prefix");
    }
    // Custom Chats.
    public static boolean customChats() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.custom-chats");
    }
    // Offline Mode.
    public static boolean offlineMode() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.offline-mode");
    }
//    public static String linkSuff() {
//        StreamLine.config.reloadConfig();
//        return StreamLine.config.getConfString("link.suffix");
//    }
    // ... ... Modules.
    public static String staffPerm() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.staff-permission");
    }
    public static boolean doNotAutoUpdateConfigs() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.automatically-update-configs");
    }
    // ... Discord.
    // Basics.
    public static boolean moduleDMainConsole() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.discord.main-console");
    }
    public static boolean moduleDEnabled() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.discord.enabled");
    }
    // Avatars.
    public static boolean moduleAvatarUse() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.discord.avatar.use");
    }
    public static String moduleAvatarLink() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.discord.avatar.link");
    }
    // Joins / Leaves.
    public static boolean joinsLeavesIcon() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.discord.joins-leaves.use-bot-icon");
    }
    public static boolean joinsLeavesAsConsole() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.discord.joins-leaves.send-as-console");
    }
    // Reports.
    public static boolean moduleReportsDConfirmation() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.discord.reports.send-confirmation");
    }
    public static boolean moduleReportToChannel() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.discord.reports.report-to-channel");
    }
    public static boolean moduleReportsDToMinecraft() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.discord.reports.discord-to-minecraft");
    }
    public static boolean moduleReportChannelPingsRole() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.discord.report-channel-pings-a-role");
    }
    // StaffChat.
    public static boolean moduleStaffChatToMinecraft() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.discord.staffchat-to-minecraft");
    }
    public static boolean moduleSCOnlyStaffRole() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.discord.staffchat-to-minecraft-only-staff-role");
    }
    public static String moduleStaffChatServer() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.discord.staffchat-server");
    }
    // Startup / Shutdowns.
    public static boolean moduleStartups() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.discord.startup-message");
    }
    public static boolean moduleShutdowns() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.discord.shutdown-message");
    }
    // Say if...
    public static String moduleSayNotACommand() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.discord.say-if-not-a-command");
    }
    public static String moduleSayCommandDisabled() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.discord.say-if-command-disabled");
    }
    // SavablePlayer logins / logouts.
    public static String moduleDPlayerJoins() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.discord.player-joins");
    }
    public static String moduleDPlayerLeaves() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.discord.player-leaves");
    }
    // .. Proxy Chat.
    public static boolean moduleDPC() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.discord.proxy-chat.enabled");
    }
    public static boolean moduleDPCConsole() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.discord.proxy-chat.to-console");
    }
    public static boolean moduleDPCOnlyRole() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.discord.proxy-chat.only-role");
    }
    public static boolean moduleDPCChangeOnVerify() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.discord.proxy-chat.display-names.verifying.change.enabled");
    }
    public static String moduleDPCChangeOnVerifyTo() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.discord.proxy-chat.display-names.verifying.change.to");
    }
    public static String moduleDPCChangeOnVerifyType() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.discord.proxy-chat.display-names.verifying.change.type");
    }
    public static boolean moduleDPCChangeOnVerifyUnchangeable() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.discord.proxy-chat.display-names.verifying.change.unchangeable");
    }
    public static List<String> moduleDPCOnVerifyAdd() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfStringList("modules.discord.proxy-chat.display-names.verifying.add-roles");
    }
    public static List<String> moduleDPCOnVerifyRemove() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfStringList("modules.discord.proxy-chat.display-names.verifying.remove-roles");
    }
    public static String moduleDPCDisplayNamesUseThis() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.discord.proxy-chat.display-names.use");
    }
    // Discord Data.
    public static String moduleDPCDDLocalTitle() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.channels.local.title");
    }
    public static String moduleDPCDDLocalMessage() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.channels.local.message");
    }
    public static String moduleDPCDDLocalJoins() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.channels.local.joins");
    }
    public static String moduleDPCDDLocalLeaves() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.channels.local.leaves");
    }
    public static boolean moduleDPCDDLocalUseAvatar() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.discord.proxy-chat.discord-data.channels.local.use-avatar");
    }
    public static String moduleDPCDDGlobalTitle() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.channels.global.title");
    }
    public static String moduleDPCDDGlobalMessage() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.channels.global.message");
    }
    public static String moduleDPCDDGlobalJoins() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.channels.global.joins");
    }
    public static String moduleDPCDDGlobalLeaves() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.channels.global.leaves");
    }
    public static boolean moduleDPCDDGlobalUseAvatar() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.discord.proxy-chat.discord-data.channels.global.use-avatar");
    }
    public static String moduleDPCDDGuildTitle() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.channels.guild.title");
    }
    public static String moduleDPCDDGuildMessage() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.channels.guild.message");
    }
    public static String moduleDPCDDGuildJoins() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.channels.guild.joins");
    }
    public static String moduleDPCDDGuildLeaves() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.channels.guild.leaves");
    }
    public static boolean moduleDPCDDGuildUseAvatar() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.discord.proxy-chat.discord-data.channels.guild.use-avatar");
    }
    public static String moduleDPCDDPartyTitle() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.channels.party.title");
    }
    public static String moduleDPCDDPartyMessage() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.channels.party.message");
    }
    public static String moduleDPCDDPartyJoins() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.channels.party.joins");
    }
    public static String moduleDPCDDPartyLeaves() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.channels.party.leaves");
    }
    public static boolean moduleDPCDDPartyUseAvatar() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.discord.proxy-chat.discord-data.channels.party.use-avatar");
    }
    // Other.
    public static String moduleDPCDDOtherTitle(ChatChannel other) {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.channels." + other.name + ".title");
    }
    public static String moduleDPCDDOtherMessage(ChatChannel other) {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.channels." + other.name + ".message");
    }
    public static String moduleDPCDDOtherJoins(ChatChannel other) {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.channels." + other.name + ".joins");
    }
    public static String moduleDPCDDOtherLeaves(ChatChannel other) {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.channels." + other.name + ".leaves");
    }
    public static boolean moduleDPCDDOtherUseAvatar(ChatChannel other) {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.discord.proxy-chat.discord-data.channels." + other.name + ".use-avatar");
    }
    // Console.
    public static String moduleDPCConsoleTitle() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.discord.proxy-chat.console.title");
    }
    public static String moduleDPCConsoleMessage() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.discord.proxy-chat.console.message");
    }
    public static boolean moduleDPCConsoleUseAvatar() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.discord.proxy-chat.console.use-avatar");
    }
    // Boosts.
    public static boolean boostsEnabled() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.discord.boosts.enabled");
    }
    public static String boostsUponBoostRun() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.discord.boosts.upon-boosts.run");
    }
    // ... Bungee.
    // Reports.
    public static boolean moduleReportsBConfirmation() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.reports.send-confirmation");
    }
    public static boolean moduleReportsMToDiscord() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.reports.minecraft-to-discord");
    }
    public static boolean moduleReportsSendChat() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.reports.send-in-chat");
    }
    // StaffChat.
    public static boolean moduleStaffChat() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.staffchat.enabled");
    }
    public static boolean moduleStaffChatDoPrefix() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.staffchat.enable-prefix");
    }
    public static String moduleStaffChatPrefix() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.bungee.staffchat.prefix");
    }
    public static boolean moduleStaffChatMToDiscord() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.staffchat.minecraft-to-discord");
    }
    // SavablePlayer logins / logouts.
//    public static String moduleBPlayerJoins() {
//        StreamLine.config.reloadConfig();
//        return StreamLine.config.getConfString("modules.bungee.player-joins");
//    }
//    public static String moduleBPlayerJoinsPerm() {
//        StreamLine.config.reloadConfig();
//        return StreamLine.config.getConfString("modules.bungee.joins-permission");
//    }
//    public static String moduleBPlayerLeaves() {
//        StreamLine.config.reloadConfig();
//        return StreamLine.config.getConfString("modules.bungee.player-leaves");
//    }
//    public static String moduleBPlayerLeavesPerm() {
//        StreamLine.config.reloadConfig();
//        return StreamLine.config.getConfString("modules.bungee.leaves-permission");
//    }
    public static String moduleBPlayerJoins() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.bungee.player-joins-order");
    }
    public static String moduleBPlayerJoinsPerm() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.bungee.joins-permission");
    }
    public static String moduleBPlayerLeaves() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.bungee.player-leaves-order");
    }
    public static String moduleBPlayerLeavesPerm() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.bungee.leaves-permission");
    }
    // ... Parties.
    public static boolean partyToDiscord() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.parties.to-discord");
    }
    public static int partyMax() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfInteger("modules.bungee.parties.max-size");
    }
    public static String partyMaxPerm() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.bungee.parties.base-permission");
    }
    public static Configuration getGroupSizeConfig() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfSection("modules.bungee.parties.group-size");
    }

    public static int getGroupedSize(String group) {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfInteger("modules.bungee.parties.group-size." + group);
    }

    public static boolean partyConsoleChats() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.parties.console.chat");
    }
    public static boolean partyConsoleCreates() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.parties.console.creates");
    }
    public static boolean partyConsoleDisbands() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.parties.console.disbands");
    }
    public static boolean partyConsoleOpens() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.parties.console.opens");
    }
    public static boolean partyConsoleCloses() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.parties.console.closes");
    }
    public static boolean partyConsoleJoins() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.parties.console.joins");
    }
    public static boolean partyConsoleLeaves() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.parties.console.leaves");
    }
    public static boolean partyConsoleAccepts() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.parties.console.accepts");
    }
    public static boolean partyConsoleDenies() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.parties.console.denies");
    }
    public static boolean partyConsolePromotes() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.parties.console.promotes");
    }
    public static boolean partyConsoleDemotes() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.parties.console.demotes");
    }
    public static boolean partyConsoleInvites() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.parties.console.invites");
    }
    public static boolean partyConsoleKicks() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.parties.console.kicks");
    }
    public static boolean partyConsoleMutes() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.parties.console.mutes");
    }
    public static boolean partyConsoleWarps() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.parties.console.warps");
    }
    public static String partyView() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.bungee.parties.view-permission");
    }
    public static boolean partySendJoins() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.parties.send.join");
    }
    public static boolean partySendLeaves() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.parties.send.leave");
    }
    public static boolean partyPMEnabled() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.parties.plugin-messaging.enabled");
    }
    // ... Guilds.
    public static boolean guildToDiscord() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.to-discord");
    }
    public static int guildMax() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfInteger("modules.bungee.guilds.max-size");
    }
    public static boolean guildConsoleChats() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.console.chats");
    }
    public static boolean guildConsoleCreates() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.console.creates");
    }
    public static boolean guildConsoleDisbands() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.console.disbands");
    }
    public static boolean guildConsoleOpens() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.console.opens");
    }
    public static boolean guildConsoleCloses() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.console.closes");
    }
    public static boolean guildConsoleJoins() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.console.joins");
    }
    public static boolean guildConsoleLeaves() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.console.leaves");
    }
    public static boolean guildConsoleAccepts() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.console.accepts");
    }
    public static boolean guildConsoleDenies() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.console.denies");
    }
    public static boolean guildConsolePromotes() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.console.promotes");
    }
    public static boolean guildConsoleDemotes() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.console.demotes");
    }
    public static boolean guildConsoleInvites() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.console.invites");
    }
    public static boolean guildConsoleKicks() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.console.kicks");
    }
    public static boolean guildConsoleMutes() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.console.mutes");
    }
    public static boolean guildConsoleWarps() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.console.warps");
    }
    public static boolean guildConsoleRenames() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.console.renames");
    }
    public static int xpPerGiveG() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfInteger("modules.bungee.guilds.xp.amount-per");
    }
    public static int timePerGiveG() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfInteger("modules.bungee.guilds.xp.time-per");
    }
    public static String guildView() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.bungee.guilds.view-permission");
    }
    public static int guildMaxLength() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfInteger("modules.bungee.guilds.name.max-length");
    }
    public static boolean guildIncludeColors() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.name.max-includes-colors");
    }
    public static boolean guildSendJoins() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.send.join");
    }
    public static boolean guildSendLeaves() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.send.leave");
    }
    public static boolean guildPMEnabled() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.plugin-messaging.enabled");
    }
    // ... Sudo.
    public static String noSudoPerm() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.bungee.sudo.no-sudo-permission");
    }
    // ... Stats.
    public static boolean statsTell() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.stats.tell-when-create");
    }
    public static int xpPerGiveP() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfInteger("modules.bungee.stats.xp.amount-per");
    }
    public static int timePerGiveP() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfInteger("modules.bungee.stats.xp.time-per");
    }
    public static int cachedPClear() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfInteger("modules.bungee.stats.cache-clear");
    }
    public static int cachedPSave() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfInteger("modules.bungee.stats.cache-save");
    }
    public static boolean updateDisplayNames() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.stats.update-display-names");
    }
    public static boolean deleteBadStats() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.stats.delete-bad");
    }
    // ... Redirect.
    public static boolean redirectEnabled() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.redirect.enabled");
    }
    public static String redirectPre() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.bungee.redirect.permission-prefix");
    }
    public static String redirectMain() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.bungee.redirect.main");
    }
    // Version Block.
    public static boolean vbEnabled() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.redirect.version-block.enabled");
    }
    public static String vbOverridePerm() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.bungee.redirect.version-block.override-permission");
    }
    public static String vbServerFile() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.bungee.redirect.version-block.server-permission-file");
    }
    // Lobbies.
    public static boolean lobbies() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.redirect.lobbies.enabled");
    }
    public static String lobbiesFile() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.bungee.redirect.lobbies.file");
    }
    public static int lobbyTimeOut() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfInteger("modules.bungee.redirect.lobbies.time-out");
    }
    // Points.
    public static int pointsDefault() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfInteger("modules.bungee.points.default");
    }
    // Tags.
    public static List<String> tagsDefaults() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfStringList("modules.bungee.tags.defaults");
    }
    // Events.
    public static boolean events() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.events.enabled");
    }
    public static String eventsFolder() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.bungee.events.folder");
    }
    public static boolean eventsWhenEmpty() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.events.add-default-when-empty");
    }
    // Errors.
    public static boolean errSendToConsole() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.user-errors.send-to-console");
    }
    // ... Punishments.
    // Mutes.
    public static boolean punMutes() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.punishments.mutes.enabled");
    }
    public static boolean punMutesHard() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.punishments.mutes.hard-mutes");
    }
    public static boolean punMutesReplaceable() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.punishments.mutes.replaceable");
    }
    public static boolean punMutesDiscord() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.punishments.mutes.discord");
    }
    public static String punMutesBypass() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.bungee.punishments.mutes.by-pass");
    }
    // Kicks
    public static boolean punKicksDiscord() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.punishments.kicks.discord");
    }
    public static String punKicksBypass() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.bungee.punishments.kicks.by-pass");
    }
    // Bans.
    public static boolean punBans() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.punishments.bans.enabled");
    }
    public static boolean punBansReplaceable() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.punishments.bans.replaceable");
    }
    public static boolean punBansDiscord() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.punishments.bans.discord");
    }
    public static String punBansBypass() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.bungee.punishments.bans.by-pass");
    }
    // IPBans.
    public static boolean punIPBans() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.punishments.ipbans.enabled");
    }
    public static boolean punIPBansReplaceable() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.punishments.ipbans.replaceable");
    }
    public static boolean punIPBansDiscord() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.punishments.ipbans.discord");
    }
    public static String punIPBansBypass() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.bungee.punishments.ipbans.by-pass");
    }
    // Messaging.
    public static String messViewPerm() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.bungee.messaging.view-permission");
    }
    public static String messReplyTo() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.bungee.messaging.reply-to");
    }
    // Server ConfigHandler.
    public static boolean sc() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.server-config.enabled");
    }
    public static boolean scMakeDefault() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.server-config.make-if-not-exist");
    }
    public static boolean scMOTD() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.server-config.motd");
    }
    public static boolean scVersion() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.server-config.version");
    }
    public static boolean scSample() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.server-config.sample");
    }
    public static boolean scMaxPlayers() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.server-config.max-players");
    }
    public static boolean scOnlinePlayers() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.server-config.online-players");
    }
    // Console.
    public static String consoleName() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.bungee.console.name");
    }
    public static String consoleDisplayName() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.bungee.console.display-name");
    }
    public static List<String> consoleDefaultTags() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfStringList("modules.bungee.console.default-tags");
    }
    public static int consoleDefaultPoints() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfInteger("modules.bungee.console.default-points");
    }
    public static String consoleServer() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.bungee.console.server");
    }
    // On Close.
    public static boolean onCloseSettings() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.on-close.save-settings");
    }
    public static boolean onCloseMain() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.on-close.save-main");
    }
    public static boolean onCloseSafeKick() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.on-close.safe-kick");
    }
    public static boolean onCloseKickMessage() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.on-close.kick-message");
    }
    public static boolean onCloseHackEnd() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.on-close.hack-end-command");
    }
    // Spies.
    public static List<String> viewSelfAliases() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfStringList("modules.bungee.spies.view-self-aliases");
    }
    // // Helper.
    // Teleport.
    public static int helperTeleportDelay() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfInteger("modules.bungee.helper.teleport.delay");
    }
    // Chat History.
    public static boolean chatHistoryEnabled() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.chat-history.enabled");
    }
    public static boolean chatHistoryLoadHistoryStartup() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.chat-history.load-history-on-startup");
    }
    public static int chatHistoryViewDefault() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfInteger("modules.bungee.chat-history.view.default");
    }
    public static int chatHistoryViewMax() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfInteger("modules.bungee.chat-history.view.max");
    }
    // Scripts.
    public static boolean scriptsEnabled() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.scripts.enabled");
    }
    public static boolean scriptsCreateDefault() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.scripts.create-default");
    }
    // bStats.
    public static boolean bstatsMakeDiscoverable() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bstats.make-server-discoverable");
    }
    public static String bstatsDisvoverableAddress() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.bstats.discoverable-ip");
    }
    // Ranks.
    public static boolean moduleBRanksEnabled() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.ranks.enable");
    }
    public static String moduleBRanksUses() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.bungee.ranks.points.uses");
    }
    // Votifier.
    public static boolean moduleBVotifierEnabled() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.votifier.enabled");
    }
    public static String moduleBVotifierRun() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfString("modules.bungee.votifier.on-vote.run");
    }
    // Chat Filters.
    public static boolean moduleBChatFiltersEnabled() {
        StreamLine.config.reloadConfig();
        return StreamLine.config.getConfBoolean("modules.bungee.chat-filters.enabled");
    }
}
