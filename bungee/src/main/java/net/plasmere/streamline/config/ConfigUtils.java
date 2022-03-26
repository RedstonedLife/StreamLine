package net.plasmere.streamline.config;

import de.leonhard.storage.sections.FlatFileSection;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.objects.chats.ChatChannel;
import net.plasmere.streamline.objects.configs.obj.ConfigSection;

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
        return StreamLine.config.getConfBoolean("debug");
    }
    // ... Basics.
    // Links.
    public static String linkPre() {
        return StreamLine.config.getConfString("link-prefix");
    }
    // Custom Chats.
    public static boolean customChats() {
        return StreamLine.config.getConfBoolean("modules.custom-chats");
    }
    // Offline Mode.
    public static boolean offlineMode() {
        return true;
//        StreamLine.config.reloadConfig();
//        return StreamLine.config.getConfBoolean("modules.offline-mode");
    }
    //    public static String linkSuff() {
//        StreamLine.config.reloadConfig();
//        return StreamLine.config.getConfString("link.suffix");
//    }
    // ... ... Modules.
    public static String staffPerm() {
        return StreamLine.config.getConfString("modules.staff-permission");
    }
    public static boolean doNotAutoUpdateConfigs() {
        return StreamLine.config.getConfBoolean("modules.automatically-update-configs");
    }
    // ... Discord.
    // Basics.
    public static boolean moduleDMainConsole() {
        return StreamLine.config.getConfBoolean("modules.discord.main-console");
    }
    public static boolean moduleDEnabled() {
        return StreamLine.config.getConfBoolean("modules.discord.enabled");
    }
    // Avatars.
    public static boolean moduleAvatarUse() {
        return StreamLine.config.getConfBoolean("modules.discord.avatar.use");
    }
    public static String moduleAvatarLink() {
        return StreamLine.config.getConfString("modules.discord.avatar.link");
    }
    // Joins / Leaves.
    public static boolean joinsLeavesIcon() {
        return StreamLine.config.getConfBoolean("modules.discord.joins-leaves.use-bot-icon");
    }
    public static boolean joinsLeavesAsConsole() {
        return StreamLine.config.getConfBoolean("modules.discord.joins-leaves.send-as-console");
    }
    // Reports.
    public static boolean moduleReportsDConfirmation() {
        return StreamLine.config.getConfBoolean("modules.discord.reports.send-confirmation");
    }
    public static boolean moduleReportToChannel() {
        return StreamLine.config.getConfBoolean("modules.discord.reports.report-to-channel");
    }
    public static boolean moduleReportsDToMinecraft() {
        return StreamLine.config.getConfBoolean("modules.discord.reports.discord-to-minecraft");
    }
    public static boolean moduleReportChannelPingsRole() {
        return StreamLine.config.getConfBoolean("modules.discord.report-channel-pings-a-role");
    }
    // StaffChat.
    public static boolean moduleStaffChatToMinecraft() {
        return StreamLine.config.getConfBoolean("modules.discord.staffchat-to-minecraft");
    }
    public static boolean moduleSCOnlyStaffRole() {
        return StreamLine.config.getConfBoolean("modules.discord.staffchat-to-minecraft-only-staff-role");
    }
    public static String moduleStaffChatServer() {
        return StreamLine.config.getConfString("modules.discord.staffchat-server");
    }
    // Startup / Shutdowns.
    public static boolean moduleStartups() {
        return StreamLine.config.getConfBoolean("modules.discord.startup-message");
    }
    public static boolean moduleShutdowns() {
        return StreamLine.config.getConfBoolean("modules.discord.shutdown-message");
    }
    // Say if...
    public static String moduleSayNotACommand() {
        return StreamLine.config.getConfString("modules.discord.say-if-not-a-command");
    }
    public static String moduleSayCommandDisabled() {
        return StreamLine.config.getConfString("modules.discord.say-if-command-disabled");
    }
    // SavablePlayer logins / logouts.
    public static String moduleDPlayerJoins() {
        return StreamLine.config.getConfString("modules.discord.player-joins");
    }
    public static String moduleDPlayerLeaves() {
        return StreamLine.config.getConfString("modules.discord.player-leaves");
    }
    // .. Proxy Chat.
    public static boolean moduleDPC() {
        return StreamLine.config.getConfBoolean("modules.discord.proxy-chat.enabled");
    }
    public static boolean moduleDPCConsole() {
        return StreamLine.config.getConfBoolean("modules.discord.proxy-chat.to-console");
    }
    public static boolean moduleDPCOnlyRole() {
        return StreamLine.config.getConfBoolean("modules.discord.proxy-chat.only-role");
    }
    public static boolean moduleDPCChangeOnVerify() {
        return StreamLine.config.getConfBoolean("modules.discord.proxy-chat.display-names.verifying.change.enabled");
    }
    public static String moduleDPCChangeOnVerifyTo() {
        return StreamLine.config.getConfString("modules.discord.proxy-chat.display-names.verifying.change.to");
    }
    public static String moduleDPCChangeOnVerifyType() {
        return StreamLine.config.getConfString("modules.discord.proxy-chat.display-names.verifying.change.type");
    }
    public static boolean moduleDPCChangeOnVerifyUnchangeable() {
        return StreamLine.config.getConfBoolean("modules.discord.proxy-chat.display-names.verifying.change.unchangeable");
    }
    public static List<String> moduleDPCOnVerifyAdd() {
        return StreamLine.config.getConfStringList("modules.discord.proxy-chat.display-names.verifying.add-roles");
    }
    public static List<String> moduleDPCOnVerifyRemove() {
        return StreamLine.config.getConfStringList("modules.discord.proxy-chat.display-names.verifying.remove-roles");
    }
    public static String moduleDPCDisplayNamesUseThis() {
        return StreamLine.config.getConfString("modules.discord.proxy-chat.display-names.use");
    }
    // Discord Data.
    public static String moduleDPCDDLocalTitle() {
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.channels.local.title");
    }
    public static String moduleDPCDDLocalMessage() {
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.channels.local.message");
    }
    public static String moduleDPCDDLocalJoins() {
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.channels.local.joins");
    }
    public static String moduleDPCDDLocalLeaves() {
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.channels.local.leaves");
    }
    public static boolean moduleDPCDDLocalUseAvatar() {
        return StreamLine.config.getConfBoolean("modules.discord.proxy-chat.discord-data.channels.local.use-avatar");
    }
    public static String moduleDPCDDGlobalTitle() {
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.channels.global.title");
    }
    public static String moduleDPCDDGlobalMessage() {
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.channels.global.message");
    }
    public static String moduleDPCDDGlobalJoins() {
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.channels.global.joins");
    }
    public static String moduleDPCDDGlobalLeaves() {
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.channels.global.leaves");
    }
    public static boolean moduleDPCDDGlobalUseAvatar() {
        return StreamLine.config.getConfBoolean("modules.discord.proxy-chat.discord-data.channels.global.use-avatar");
    }
    public static String moduleDPCDDGuildTitle() {
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.channels.guild.title");
    }
    public static String moduleDPCDDGuildMessage() {
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.channels.guild.message");
    }
    public static String moduleDPCDDGuildJoins() {
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.channels.guild.joins");
    }
    public static String moduleDPCDDGuildLeaves() {
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.channels.guild.leaves");
    }
    public static boolean moduleDPCDDGuildUseAvatar() {
        return StreamLine.config.getConfBoolean("modules.discord.proxy-chat.discord-data.channels.guild.use-avatar");
    }
    public static String moduleDPCDDPartyTitle() {
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.channels.party.title");
    }
    public static String moduleDPCDDPartyMessage() {
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.channels.party.message");
    }
    public static String moduleDPCDDPartyJoins() {
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.channels.party.joins");
    }
    public static String moduleDPCDDPartyLeaves() {
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.channels.party.leaves");
    }
    public static boolean moduleDPCDDPartyUseAvatar() {
        return StreamLine.config.getConfBoolean("modules.discord.proxy-chat.discord-data.channels.party.use-avatar");
    }
    // Other.
    public static String moduleDPCDDOtherTitle(ChatChannel other) {
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.channels." + other.name + ".title");
    }
    public static String moduleDPCDDOtherMessage(ChatChannel other) {
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.channels." + other.name + ".message");
    }
    public static String moduleDPCDDOtherJoins(ChatChannel other) {
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.channels." + other.name + ".joins");
    }
    public static String moduleDPCDDOtherLeaves(ChatChannel other) {
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.channels." + other.name + ".leaves");
    }
    public static boolean moduleDPCDDOtherUseAvatar(ChatChannel other) {
        return StreamLine.config.getConfBoolean("modules.discord.proxy-chat.discord-data.channels." + other.name + ".use-avatar");
    }
    // Console.
    public static String moduleDPCConsoleTitle() {
        return StreamLine.config.getConfString("modules.discord.proxy-chat.console.title");
    }
    public static String moduleDPCConsoleMessage() {
        return StreamLine.config.getConfString("modules.discord.proxy-chat.console.message");
    }
    public static boolean moduleDPCConsoleUseAvatar() {
        return StreamLine.config.getConfBoolean("modules.discord.proxy-chat.console.use-avatar");
    }
    // Boosts.
    public static boolean boostsEnabled() {
        return StreamLine.config.getConfBoolean("modules.discord.boosts.enabled");
    }
    public static String boostsUponBoostRun() {
        return StreamLine.config.getConfString("modules.discord.boosts.upon-boosts.run");
    }
    // Guilds.
    public static boolean guildsSync() {
        return StreamLine.config.getConfBoolean("modules.discord.guilds.sync");
    }
    // Parties.
    public static boolean partiesSync() {
        return StreamLine.config.getConfBoolean("modules.discord.parties.sync");
    }
    // ... Bungee.
    // Reports.
    public static boolean moduleReportsBConfirmation() {
        return StreamLine.config.getConfBoolean("modules.bungee.reports.send-confirmation");
    }
    public static boolean moduleReportsMToDiscord() {
        return StreamLine.config.getConfBoolean("modules.bungee.reports.minecraft-to-discord");
    }
    public static boolean moduleReportsSendChat() {
        return StreamLine.config.getConfBoolean("modules.bungee.reports.send-in-chat");
    }
    // StaffChat.
    public static boolean moduleStaffChat() {
        return StreamLine.config.getConfBoolean("modules.bungee.staffchat.enabled");
    }
    public static boolean moduleStaffChatDoPrefix() {
        return StreamLine.config.getConfBoolean("modules.bungee.staffchat.enable-prefix");
    }
    public static String moduleStaffChatPrefix() {
        return StreamLine.config.getConfString("modules.bungee.staffchat.prefix");
    }
    public static boolean moduleStaffChatMToDiscord() {
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
        return StreamLine.config.getConfString("modules.bungee.player-joins-order");
    }
    public static String moduleBPlayerJoinsPerm() {
        return StreamLine.config.getConfString("modules.bungee.joins-permission");
    }
    public static String moduleBPlayerLeaves() {
        return StreamLine.config.getConfString("modules.bungee.player-leaves-order");
    }
    public static String moduleBPlayerLeavesPerm() {
        return StreamLine.config.getConfString("modules.bungee.leaves-permission");
    }
    // ... Parties.
    public static boolean partyToDiscord() {
        return StreamLine.config.getConfBoolean("modules.bungee.parties.to-discord");
    }
    public static int partyMax() {
        return StreamLine.config.getConfInteger("modules.bungee.parties.max-size");
    }
    public static String partyMaxPerm() {
        return StreamLine.config.getConfString("modules.bungee.parties.base-permission");
    }
    public static ConfigSection getGroupSizeConfig() {
        return StreamLine.config.getConfSection("modules.bungee.parties.group-size");
    }

    public static int getGroupedSize(String group) {
        return StreamLine.config.getConfInteger("modules.bungee.parties.group-size." + group);
    }

    public static boolean partyConsoleChats() {
        return StreamLine.config.getConfBoolean("modules.bungee.parties.console.chat");
    }
    public static boolean partyConsoleCreates() {
        return StreamLine.config.getConfBoolean("modules.bungee.parties.console.creates");
    }
    public static boolean partyConsoleDisbands() {
        return StreamLine.config.getConfBoolean("modules.bungee.parties.console.disbands");
    }
    public static boolean partyConsoleOpens() {
        return StreamLine.config.getConfBoolean("modules.bungee.parties.console.opens");
    }
    public static boolean partyConsoleCloses() {
        return StreamLine.config.getConfBoolean("modules.bungee.parties.console.closes");
    }
    public static boolean partyConsoleJoins() {
        return StreamLine.config.getConfBoolean("modules.bungee.parties.console.joins");
    }
    public static boolean partyConsoleLeaves() {
        return StreamLine.config.getConfBoolean("modules.bungee.parties.console.leaves");
    }
    public static boolean partyConsoleAccepts() {
        return StreamLine.config.getConfBoolean("modules.bungee.parties.console.accepts");
    }
    public static boolean partyConsoleDenies() {
        return StreamLine.config.getConfBoolean("modules.bungee.parties.console.denies");
    }
    public static boolean partyConsolePromotes() {
        return StreamLine.config.getConfBoolean("modules.bungee.parties.console.promotes");
    }
    public static boolean partyConsoleDemotes() {
        return StreamLine.config.getConfBoolean("modules.bungee.parties.console.demotes");
    }
    public static boolean partyConsoleInvites() {
        return StreamLine.config.getConfBoolean("modules.bungee.parties.console.invites");
    }
    public static boolean partyConsoleKicks() {
        return StreamLine.config.getConfBoolean("modules.bungee.parties.console.kicks");
    }
    public static boolean partyConsoleMutes() {
        return StreamLine.config.getConfBoolean("modules.bungee.parties.console.mutes");
    }
    public static boolean partyConsoleWarps() {
        return StreamLine.config.getConfBoolean("modules.bungee.parties.console.warps");
    }
    public static String partyView() {
        return StreamLine.config.getConfString("modules.bungee.parties.view-permission");
    }
    public static boolean partySendJoins() {
        return StreamLine.config.getConfBoolean("modules.bungee.parties.send.join");
    }
    public static boolean partySendLeaves() {
        return StreamLine.config.getConfBoolean("modules.bungee.parties.send.leave");
    }
    public static boolean partyPMEnabled() {
        return StreamLine.config.getConfBoolean("modules.bungee.parties.plugin-messaging.enabled");
    }
    // ... Guilds.
    public static boolean guildToDiscord() {
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.to-discord");
    }
    public static int guildMax() {
        return StreamLine.config.getConfInteger("modules.bungee.guilds.max-size");
    }
    public static boolean guildConsoleChats() {
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.console.chats");
    }
    public static boolean guildConsoleCreates() {
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.console.creates");
    }
    public static boolean guildConsoleDisbands() {
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.console.disbands");
    }
    public static boolean guildConsoleOpens() {
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.console.opens");
    }
    public static boolean guildConsoleCloses() {
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.console.closes");
    }
    public static boolean guildConsoleJoins() {
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.console.joins");
    }
    public static boolean guildConsoleLeaves() {
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.console.leaves");
    }
    public static boolean guildConsoleAccepts() {
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.console.accepts");
    }
    public static boolean guildConsoleDenies() {
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.console.denies");
    }
    public static boolean guildConsolePromotes() {
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.console.promotes");
    }
    public static boolean guildConsoleDemotes() {
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.console.demotes");
    }
    public static boolean guildConsoleInvites() {
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.console.invites");
    }
    public static boolean guildConsoleKicks() {
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.console.kicks");
    }
    public static boolean guildConsoleMutes() {
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.console.mutes");
    }
    public static boolean guildConsoleWarps() {
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.console.warps");
    }
    public static boolean guildConsoleRenames() {
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.console.renames");
    }
    public static int xpPerGiveG() {
        return StreamLine.config.getConfInteger("modules.bungee.guilds.xp.amount-per");
    }
    public static int timePerGiveG() {
        return StreamLine.config.getConfInteger("modules.bungee.guilds.xp.time-per");
    }
    public static String guildView() {
        return StreamLine.config.getConfString("modules.bungee.guilds.view-permission");
    }
    public static int guildMaxLength() {
        return StreamLine.config.getConfInteger("modules.bungee.guilds.name.max-length");
    }
    public static boolean guildIncludeColors() {
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.name.max-includes-colors");
    }
    public static boolean guildSendJoins() {
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.send.join");
    }
    public static boolean guildSendLeaves() {
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.send.leave");
    }
    public static boolean guildPMEnabled() {
        return StreamLine.config.getConfBoolean("modules.bungee.guilds.plugin-messaging.enabled");
    }
    public static int guildExperienceStartingLevel() {
        return StreamLine.config.getConfInteger("modules.bungee.guilds.experience.starting.level");
    }
    public static int guildExperienceStartingXP() {
        return StreamLine.config.getConfInteger("modules.bungee.guilds.experience.starting.xp");
    }
    public static String guildExperienceEquation() {
        return StreamLine.config.getConfString("modules.bungee.guilds.experience.equation");
    }
    // ... Sudo.
    public static String noSudoPerm() {
        return StreamLine.config.getConfString("modules.bungee.sudo.no-sudo-permission");
    }
    // ... Stats.
    public static boolean statsTell() {
        return StreamLine.config.getConfBoolean("modules.bungee.stats.tell-when-create");
    }
    public static int xpPerGiveP() {
        return StreamLine.config.getConfInteger("modules.bungee.stats.xp.amount-per");
    }
    public static int timePerGiveP() {
        return StreamLine.config.getConfInteger("modules.bungee.stats.xp.time-per");
    }
    public static int cachedPClear() {
        return StreamLine.config.getConfInteger("modules.bungee.stats.cache-clear");
    }
    public static int cachedPSave() {
        return StreamLine.config.getConfInteger("modules.bungee.stats.cache-save");
    }
    public static boolean updateDisplayNames() {
        return StreamLine.config.getConfBoolean("modules.bungee.stats.update-display-names");
    }
    public static boolean deleteBadStats() {
        return StreamLine.config.getConfBoolean("modules.bungee.stats.delete-bad");
    }
    public static int statsExperienceStartingLevel() {
        return StreamLine.config.getConfInteger("modules.bungee.stats.experience.starting.level");
    }
    public static int statsExperienceStartingXP() {
        return StreamLine.config.getConfInteger("modules.bungee.stats.experience.starting.xp");
    }
    public static String statsExperienceEquation() {
        return StreamLine.config.getConfString("modules.bungee.stats.experience.equation");
    }
    // ... Redirect.
    public static boolean redirectEnabled() {
        return StreamLine.config.getConfBoolean("modules.bungee.redirect.enabled");
    }
    public static String redirectPre() {
        return StreamLine.config.getConfString("modules.bungee.redirect.permission-prefix");
    }
    public static String redirectMain() {
        return StreamLine.config.getConfString("modules.bungee.redirect.main");
    }
    // Version Block.
    public static boolean vbEnabled() {
        return StreamLine.config.getConfBoolean("modules.bungee.redirect.version-block.enabled");
    }
    public static String vbOverridePerm() {
        return StreamLine.config.getConfString("modules.bungee.redirect.version-block.override-permission");
    }
    public static String vbServerFile() {
        return StreamLine.config.getConfString("modules.bungee.redirect.version-block.server-permission-file");
    }
    // Lobbies.
    public static boolean lobbies() {
        return StreamLine.config.getConfBoolean("modules.bungee.redirect.lobbies.enabled");
    }
    public static String lobbiesFile() {
        return StreamLine.config.getConfString("modules.bungee.redirect.lobbies.file");
    }
    public static int lobbyTimeOut() {
        return StreamLine.config.getConfInteger("modules.bungee.redirect.lobbies.time-out");
    }
    // Points.
    public static int pointsDefault() {
        return StreamLine.config.getConfInteger("modules.bungee.points.default");
    }
    // Tags.
    public static List<String> tagsDefaults() {
        return StreamLine.config.getConfStringList("modules.bungee.tags.defaults");
    }
    // Events.
    public static boolean events() {
        return StreamLine.config.getConfBoolean("modules.bungee.events.enabled");
    }
    public static String eventsFolder() {
        return StreamLine.config.getConfString("modules.bungee.events.folder");
    }
    public static boolean eventsWhenEmpty() {
        return StreamLine.config.getConfBoolean("modules.bungee.events.add-default-when-empty");
    }
    // Errors.
    public static boolean errSendToConsole() {
        return StreamLine.config.getConfBoolean("modules.bungee.user-errors.send-to-console");
    }
    // ... Punishments.
    // Mutes.
    public static boolean punMutes() {
        return StreamLine.config.getConfBoolean("modules.bungee.punishments.mutes.enabled");
    }
    public static boolean punMutesHard() {
        return StreamLine.config.getConfBoolean("modules.bungee.punishments.mutes.hard-mutes");
    }
    public static boolean punMutesReplaceable() {
        return StreamLine.config.getConfBoolean("modules.bungee.punishments.mutes.replaceable");
    }
    public static boolean punMutesDiscord() {
        return StreamLine.config.getConfBoolean("modules.bungee.punishments.mutes.discord");
    }
    public static String punMutesBypass() {
        return StreamLine.config.getConfString("modules.bungee.punishments.mutes.by-pass");
    }
    // Kicks
    public static boolean punKicksDiscord() {
        return StreamLine.config.getConfBoolean("modules.bungee.punishments.kicks.discord");
    }
    public static String punKicksBypass() {
        return StreamLine.config.getConfString("modules.bungee.punishments.kicks.by-pass");
    }
    // Bans.
    public static boolean punBans() {
        return StreamLine.config.getConfBoolean("modules.bungee.punishments.bans.enabled");
    }
    public static boolean punBansReplaceable() {
        return StreamLine.config.getConfBoolean("modules.bungee.punishments.bans.replaceable");
    }
    public static boolean punBansDiscord() {
        return StreamLine.config.getConfBoolean("modules.bungee.punishments.bans.discord");
    }
    public static String punBansBypass() {
        return StreamLine.config.getConfString("modules.bungee.punishments.bans.by-pass");
    }
    // IPBans.
    public static boolean punIPBans() {
        return StreamLine.config.getConfBoolean("modules.bungee.punishments.ipbans.enabled");
    }
    public static boolean punIPBansReplaceable() {
        return StreamLine.config.getConfBoolean("modules.bungee.punishments.ipbans.replaceable");
    }
    public static boolean punIPBansDiscord() {
        return StreamLine.config.getConfBoolean("modules.bungee.punishments.ipbans.discord");
    }
    public static String punIPBansBypass() {
        return StreamLine.config.getConfString("modules.bungee.punishments.ipbans.by-pass");
    }
    // Messaging.
    public static String messViewPerm() {
        return StreamLine.config.getConfString("modules.bungee.messaging.view-permission");
    }
    public static String messReplyTo() {
        return StreamLine.config.getConfString("modules.bungee.messaging.reply-to");
    }
    // Server ConfigHandler.
    public static boolean sc() {
        return StreamLine.config.getConfBoolean("modules.bungee.server-config.enabled");
    }
    public static boolean scMakeDefault() {
        return StreamLine.config.getConfBoolean("modules.bungee.server-config.make-if-not-exist");
    }
    public static boolean scMOTD() {
        return StreamLine.config.getConfBoolean("modules.bungee.server-config.motd");
    }
    public static boolean scVersion() {
        return StreamLine.config.getConfBoolean("modules.bungee.server-config.version");
    }
    public static boolean scSample() {
        return StreamLine.config.getConfBoolean("modules.bungee.server-config.sample");
    }
    public static boolean scMaxPlayers() {
        return StreamLine.config.getConfBoolean("modules.bungee.server-config.max-players");
    }
    public static boolean scOnlinePlayers() {
        return StreamLine.config.getConfBoolean("modules.bungee.server-config.online-players");
    }
    // Console.
    public static String consoleName() {
        return StreamLine.config.getConfString("modules.bungee.console.name");
    }
    public static String consoleDisplayName() {
        return StreamLine.config.getConfString("modules.bungee.console.display-name");
    }
    public static List<String> consoleDefaultTags() {
        return StreamLine.config.getConfStringList("modules.bungee.console.default-tags");
    }
    public static int consoleDefaultPoints() {
        return StreamLine.config.getConfInteger("modules.bungee.console.default-points");
    }
    public static String consoleServer() {
        return StreamLine.config.getConfString("modules.bungee.console.server");
    }
    // On Close.
    public static boolean onCloseSettings() {
        return StreamLine.config.getConfBoolean("modules.bungee.on-close.save-settings");
    }
    public static boolean onCloseMain() {
        return StreamLine.config.getConfBoolean("modules.bungee.on-close.save-main");
    }
    public static boolean onCloseSafeKick() {
        return StreamLine.config.getConfBoolean("modules.bungee.on-close.safe-kick");
    }
    public static boolean onCloseKickMessage() {
        return StreamLine.config.getConfBoolean("modules.bungee.on-close.kick-message");
    }
    public static boolean onCloseHackEnd() {
        return StreamLine.config.getConfBoolean("modules.bungee.on-close.hack-end-command");
    }
    // Spies.
    public static List<String> viewSelfAliases() {
        return StreamLine.config.getConfStringList("modules.bungee.spies.view-self-aliases");
    }
    // // Helper.
    // Teleport.
    public static int helperTeleportDelay() {
        return StreamLine.config.getConfInteger("modules.bungee.helper.teleport.delay");
    }
    // Chat History.
    public static boolean chatHistoryEnabled() {
        return StreamLine.config.getConfBoolean("modules.bungee.chat-history.enabled");
    }
    public static boolean chatHistoryLoadHistoryStartup() {
        return StreamLine.config.getConfBoolean("modules.bungee.chat-history.load-history-on-startup");
    }
    public static int chatHistoryViewDefault() {
        return StreamLine.config.getConfInteger("modules.bungee.chat-history.view.default");
    }
    public static int chatHistoryViewMax() {
        return StreamLine.config.getConfInteger("modules.bungee.chat-history.view.max");
    }
    // Scripts.
    public static boolean scriptsEnabled() {
        return StreamLine.config.getConfBoolean("modules.bungee.scripts.enabled");
    }
    public static boolean scriptsCreateDefault() {
        return StreamLine.config.getConfBoolean("modules.bungee.scripts.create-default");
    }
    // bStats.
    public static boolean bstatsMakeDiscoverable() {
        return StreamLine.config.getConfBoolean("modules.bstats.make-server-discoverable");
    }
    public static String bstatsDisvoverableAddress() {
        return StreamLine.config.getConfString("modules.bstats.discoverable-ip");
    }
    // Ranks.
    public static boolean moduleBRanksEnabled() {
        return StreamLine.config.getConfBoolean("modules.bungee.ranks.enable");
    }
    public static String moduleBRanksUses() {
        return StreamLine.config.getConfString("modules.bungee.ranks.points.uses");
    }
    // Votifier.
    public static boolean moduleBVotifierEnabled() {
        return StreamLine.config.getConfBoolean("modules.bungee.votifier.enabled");
    }
    public static String moduleBVotifierRun() {
        return StreamLine.config.getConfString("modules.bungee.votifier.on-vote.run");
    }
    // Chat Filters.
    public static boolean moduleBChatFiltersEnabled() {
        return StreamLine.config.getConfBoolean("modules.bungee.chat-filters.enabled");
    }
    // Voice.
    public static int moduleBVoiceMaxDefault() {
        return StreamLine.config.getConfInteger("modules.bungee.voice.max.default");
    }
    public static String moduleBVoiceMaxBasePerm() {
        return StreamLine.config.getConfString("modules.bungee.voice.max.base-permission");
    }

    public static boolean moduleDBUse() {
        return StreamLine.config.getConfBoolean("modules.database.use");
    }

    public static String moduleDDisDataNonEmbeddedMessage() {
        return StreamLine.config.getConfString("modules.discord.proxy-chat.discord-data.non-embedded.message");
    }
    
    public static boolean mysqlbridgerEnabled() {
        return StreamLine.config.getConfBoolean("modules.bungee.mysqlbridger.enabled");
    }

    public static boolean customTablistEnabled() {
        return StreamLine.config.getConfBoolean("modules.bungee.custom-tablist.enabled");
    }

    public static boolean customAliasesEnabled() {
        return StreamLine.config.getConfBoolean("modules.bungee.custom-aliases.enabled");
    }
}
