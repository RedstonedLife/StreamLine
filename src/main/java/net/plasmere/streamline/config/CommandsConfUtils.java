package net.plasmere.streamline.config;

import net.plasmere.streamline.StreamLine;

import java.util.List;

public class CommandsConfUtils {
    // ... ... ... Commands.
    // ... ... Discord Stuff.
    // Commands.
    public static boolean comDCommands() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.discord.help.enabled");
     }
    public static List<String> comDCommandsAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.discord.help.aliases");
     }
    public static String comDCommandsPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.discord.help.permission");
     }
    // Online.
    public static boolean comDOnline() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.discord.online.enabled");
     }
    public static List<String> comDOnlineAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.discord.online.aliases");
     }
    public static String comDOnlinePerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.discord.online.permission");
     }
    // Report.
    public static boolean comDReport() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.discord.report.enabled");
     }
    public static List<String> comDReportAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.discord.report.aliases");
     }
    public static String comDReportPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.discord.report.permission");
     }
    // StaffChat.
    public static boolean comDStaffChat() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.discord.staffchat.enabled");
     }
    public static List<String> comDStaffChatAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.discord.staffchat.aliases");
     }
    public static String comDStaffChatPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.discord.staffchat.permission");
     }
    // StaffOnline.
    public static boolean comDStaffOnline() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.discord.staffonline.enabled");
     }
    public static List<String> comDStaffOnlineAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.discord.staffonline.aliases");
     }
    public static String comDStaffOnlinePerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.discord.staffonline.permission");
     }
    // Channel.
    public static boolean comDChannel() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.discord.channel.enabled");
     }
    public static List<String> comDChannelAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.discord.channel.aliases");
     }
    public static String comDChannelPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.discord.channel.permission");
     }
    // BVerify.
    public static boolean comDVerify() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.discord.verify.enabled");
     }
    public static List<String> comDVerifyAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.discord.verify.aliases");
     }
    public static String comDVerifyPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.discord.verify.permission");
     }
    // ... ... Bungee Stuff.
    // Ping.
    public static boolean comBPing() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.ping.enabled");
     }
    public static String comBPingBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.ping.base");
     }
    public static List<String> comBPingAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.ping.aliases");
     }
    public static String comBPingPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.ping.permission");
     }
    public static boolean comBPingOthers() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.ping.view-others.enabled");
     }
    public static String comBPingPermOthers() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.ping.view-others.permission");
     }
    // Plugins.
    public static boolean comBPlugins() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.plugins.enabled");
     }
    public static String comBPluginsBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.plugins.base");
     }
    public static List<String> comBPluginsAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.plugins.aliases");
     }
    public static String comBPluginsPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.plugins.permission");
     }
    // Stream.
    public static boolean comBStream() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.stream.enabled");
     }
    public static String comBStreamBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.stream.base");
     }
    public static List<String> comBStreamAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.stream.aliases");
     }
    public static String comBStreamPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.stream.permission");
     }
    // Report.
    public static boolean comBReport() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.report.enabled");
     }
    public static String comBReportBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.report.base");
     }
    public static List<String> comBReportAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.report.aliases");
     }
    public static String comBReportPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.report.permission");
     }
    // StatsCommand
    public static boolean comBStats() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.stats.enabled");
     }
    public static String comBStatsBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.stats.base");
     }
    public static List<String> comBStatsAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.stats.aliases");
     }
    public static String comBStatsPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.stats.permission");
     }
    public static boolean comBStatsOthers() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.stats.view-others.enabled");
     }
    public static String comBStatsPermOthers() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.stats.view-others.permission");
     }
    // ... SavableParty.
    //
    public static boolean comBParty() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.party.enabled");
     }
    public static String comBPartyBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.party.base");
     }
    public static boolean comBParQuick() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.party.quick-chat");
     }
    public static List<String> comBParMainAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.party.aliases.main");
     }
    public static String comBParPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.party.permission");
     }
    // Join.
    public static List<String> comBParJoinAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.party.aliases.join");
    }
    public static String comBParJoinPermission() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.party.permissions.join");
    }
    // Leave.
    public static List<String> comBParLeaveAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.party.aliases.leave");
     }
    public static String comBParLeavePermission() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.party.permissions.leave");
    }
    // Create.
    public static List<String> comBParCreateAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.party.aliases.create");
     }
    public static String comBParCreatePermission() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.party.permissions.create");
    }
    // Promote.
    public static List<String> comBParPromoteAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.party.aliases.promote");
     }
    public static String comBParPromotePermission() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.party.permissions.promote");
    }
    // Demote.
    public static List<String> comBParDemoteAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.party.aliases.demote");
     }
    public static String comBParDemotePermission() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.party.permissions.demote");
    }
    // Chat.
    public static List<String> comBParChatAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.party.aliases.chat");
     }
    public static String comBParChatPermission() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.party.permissions.chat");
    }
    // List.
    public static List<String> comBParListAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.party.aliases.list");
     }
    public static String comBParListPermission() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.party.permissions.list");
    }
    // Open.
    public static List<String> comBParOpenAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.party.aliases.open");
     }
    public static String comBParOpenPermission() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.party.permissions.open");
    }
    // Close.
    public static List<String> comBParCloseAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.party.aliases.close");
     }
    public static String comBParClosePermission() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.party.permissions.close");
    }
    // Disband.
    public static List<String> comBParDisbandAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.party.aliases.disband");
     }
    public static String comBParDisbandPermission() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.party.permissions.disband");
    }
    // Accept.
    public static List<String> comBParAcceptAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.party.aliases.accept");
     }
    public static String comBParAcceptPermission() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.party.permissions.accept");
    }
    // Deny.
    public static List<String> comBParDenyAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.party.aliases.deny");
     }
    public static String comBParDenyPermission() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.party.permissions.deny");
    }
    // Invite.
    public static List<String> comBParInvAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.party.aliases.invite");
     }
    public static String comBParInvitePermission() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.party.permissions.invite");
    }
    // Kick.
    public static List<String> comBParKickAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.party.aliases.kick");
     }
    public static String comBParKickPermission() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.party.permissions.kick");
    }
    // Mute.
    public static List<String> comBParMuteAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.party.aliases.mute");
     }
    public static String comBParMutePermission() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.party.permissions.mute");
    }
    // Warp.
    public static List<String> comBParWarpAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.party.aliases.warp");
     }
    public static String comBParWarpPermission() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.party.permissions.warp");
    }
    // ... SavableGuild.
    //
    public static boolean comBGuild() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.guild.enabled");
     }
    public static String comBGuildBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.guild.base");
     }
    public static boolean comBGuildQuick() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.guild.quick-chat");
     }
    public static String comBGuildPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.guild.permission");
     }
    public static List<String> comBGuildMainAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.guild.aliases.main");
     }
    // Join.
    public static List<String> comBGuildJoinAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.guild.aliases.join");
    }
    public static String comBGuildJoinPermission() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.guild.permissions.join");
    }
    // Leave.
    public static List<String> comBGuildLeaveAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.guild.aliases.leave");
    }
    public static String comBGuildLeavePermission() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.guild.permissions.leave");
    }
    // Create.
    public static List<String> comBGuildCreateAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.guild.aliases.create");
    }
    public static String comBGuildCreatePermission() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.guild.permissions.create");
    }
    // Promote.
    public static List<String> comBGuildPromoteAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.guild.aliases.promote");
    }
    public static String comBGuildPromotePermission() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.guild.permissions.promote");
    }
    // Demote.
    public static List<String> comBGuildDemoteAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.guild.aliases.demote");
    }
    public static String comBGuildDemotePermission() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.guild.permissions.demote");
    }
    // Chat.
    public static List<String> comBGuildChatAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.guild.aliases.chat");
    }
    public static String comBGuildChatPermission() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.guild.permissions.chat");
    }
    // List.
    public static List<String> comBGuildListAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.guild.aliases.list");
    }
    public static String comBGuildListPermission() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.guild.permissions.list");
    }
    // Open.
    public static List<String> comBGuildOpenAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.guild.aliases.open");
    }
    public static String comBGuildOpenPermission() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.guild.permissions.open");
    }
    // Close.
    public static List<String> comBGuildCloseAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.guild.aliases.close");
    }
    public static String comBGuildClosePermission() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.guild.permissions.close");
    }
    // Disband.
    public static List<String> comBGuildDisbandAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.guild.aliases.disband");
    }
    public static String comBGuildDisbandPermission() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.guild.permissions.disband");
    }
    // Accept.
    public static List<String> comBGuildAcceptAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.guild.aliases.accept");
    }
    public static String comBGuildAcceptPermission() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.guild.permissions.accept");
    }
    // Deny.
    public static List<String> comBGuildDenyAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.guild.aliases.deny");
    }
    public static String comBGuildDenyPermission() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.guild.permissions.deny");
    }
    // Invite.
    public static List<String> comBGuildInvAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.guild.aliases.invite");
    }
    public static String comBGuildInvitePermission() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.guild.permissions.invite");
    }
    // Kick.
    public static List<String> comBGuildKickAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.guild.aliases.kick");
    }
    public static String comBGuildKickPermission() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.guild.permissions.kick");
    }
    // Mute.
    public static List<String> comBGuildMuteAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.guild.aliases.mute");
    }
    public static String comBGuildMutePermission() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.guild.permissions.mute");
    }
    // Warp.
    public static List<String> comBGuildWarpAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.guild.aliases.warp");
    }
    public static String comBGuildWarpPermission() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.guild.permissions.warp");
    }
    // Info.
    public static List<String> comBGuildInfoAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.guild.aliases.info");
     }
    public static String comBGuildInfoPermission() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.guild.permissions.info");
    }
    // Rename.
    public static List<String> comBGuildRenameAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.guild.aliases.rename");
    }
    public static String comBGuildRenamePermission() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.guild.permissions.rename");
    }
    // ... Servers.
    // Lobby.
    public static boolean comBLobby() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.servers.lobby.enabled");
     }
    public static String comBLobbyBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.lobby.base");
     }
    public static List<String> comBLobbyAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.servers.lobby.aliases");
     }
    public static String comBLobbyEnd() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.servers.lobby.points-to");
     }
    public static String comBLobbyPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.servers.lobby.permission");
     }
    // Fabric Fix.
    public static boolean comBFabric() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.servers.fabric-fix.enabled");
     }
    public static String comBFabricEnd() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.servers.fabric-fix.points-to");
     }
    public static String comBFabricPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.servers.fabric-fix.permission");
     }
    // ... Staff.
    // GlobalOnline.
    public static boolean comBGlobalOnline() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.staff.globalonline.enabled");
     }
    public static String comBGlobalOnlineBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.globalonline.base");
     }
    public static List<String> comBGlobalOnlineAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.staff.globalonline.aliases");
     }
    public static String comBGlobalOnlinePerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.globalonline.permission");
     }
    // StaffChat.
    public static boolean comBStaffChat() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.staff.staffchat.enabled");
     }
    public static String comBStaffChatBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.staffchat.base");
     }
    public static List<String> comBStaffChatAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.staff.staffchat.aliases");
     }
    public static String comBStaffChatPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.staffchat.permission");
     }
    // StaffOnline.
    public static boolean comBStaffOnline() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.staff.staffonline.enabled");
     }
    public static String comBStaffOnlineBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.staffonline.base");
     }
    public static List<String> comBStaffOnlineAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.staff.staffonline.aliases");
     }
    public static String comBStaffOnlinePerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.staffonline.permission");
     }
    // Reload.
    public static String comBReloadBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.slreload.base");
     }
    public static List<String> comBReloadAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.staff.slreload.aliases");
     }
    public static String comBReloadPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.slreload.permission");
     }
    // Parties.
    public static boolean comBParties() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.staff.parties.enabled");
     }
    public static String comBPartiesBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.parties.base");
     }
    public static List<String> comBPartiesAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.staff.parties.aliases");
     }
    public static String comBPartiesPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.parties.permission");
     }
    // Guilds.
    public static boolean comBGuilds() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.staff.guilds.enabled");
     }
    public static String comBGuildsBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.guilds.base");
     }
    public static List<String> comBGuildsAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.staff.guilds.aliases");
     }
    public static String comBGuildsPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.guilds.permission");
     }
    // GetStats.
    public static boolean comBGetStats() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.staff.getstats.enabled");
     }
    public static String comBGetStatsBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.getstats.base");
     }
    public static List<String> comBGetStatsAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.staff.getstats.aliases");
     }
    public static String comBGetStatsPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.getstats.permission");
     }
    // BSudo.
    public static boolean comBSudo() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.staff.bsudo.enabled");
     }
    public static String comBSudoBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.bsudo.base");
     }
    public static List<String> comBSudoAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.staff.bsudo.aliases");
     }
    public static String comBSudoPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.bsudo.permission");
     }
    // SSPY.
    public static boolean comBSSPY() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.staff.sspy.enabled");
     }
    public static String comBSSPYBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.sspy.base");
     }
    public static List<String> comBSSPYAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.staff.sspy.aliases");
     }
    public static String comBSSPYPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.sspy.permission");
     }
    // GSPY.
    public static boolean comBGSPY() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.staff.gspy.enabled");
     }
    public static String comBGSPYBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.gspy.base");
     }
    public static List<String> comBGSPYAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.staff.gspy.aliases");
     }
    public static String comBGSPYPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.gspy.permission");
     }
    // PSPY.
    public static boolean comBPSPY() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.staff.pspy.enabled");
     }
    public static String comBPSPYBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.pspy.base");
     }
    public static List<String> comBPSPYAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.staff.pspy.aliases");
     }
    public static String comBPSPYPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.pspy.permission");
     }
    // SCView.
    public static boolean comBSCView() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.staff.scview.enabled");
     }
    public static String comBSCViewBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.scview.base");
     }
    public static List<String> comBSCViewAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.staff.scview.aliases");
     }
    public static String comBSCViewPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.scview.permission");
     }
    // BTag.
    public static boolean comBBTag() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.staff.btag.enabled");
     }
    public static String comBBTagBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.btag.base");
     }
    public static List<String> comBBTagAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.staff.btag.aliases");
     }
    public static String comBBTagPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.btag.permission");
     }
    public static String comBBTagOPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.btag.other-perm");
     }
    public static String comBBTagChPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.btag.change-perm");
     }
    // Event Reload.
    public static boolean comBEReload() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.staff.evreload.enabled");
     }
    public static String comBEReloadBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.evreload.base");
     }
    public static List<String> comBEReloadAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.staff.evreload.aliases");
     }
    public static String comBEReloadPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.evreload.permission");
     }
    // Network Points.
    public static boolean comBPoints() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.staff.points.enabled");
     }
    public static String comBPointsBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.points.base");
     }
    public static List<String> comBPointsAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.staff.points.aliases");
     }
    public static String comBPointsPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.points.permission");
     }
    public static String comBPointsOPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.points.other-perm");
     }
    public static String comBPointsChPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.points.change-perm");
     }
    // Server Ping.
    public static boolean comBSPing() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.staff.serverping.enabled");
     }
    public static String comBSPingBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.serverping.base");
     }
    public static List<String> comBSPingAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.staff.serverping.aliases");
     }
    public static String comBSPingPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.serverping.permission");
     }
    // Mute.
    public static boolean comBMute() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.staff.mute.enabled");
     }
    public static String comBMuteBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.mute.base");
     }
    public static List<String> comBMuteAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.staff.mute.aliases");
     }
    public static String comBMutePerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.mute.permission");
     }
    // Kick.
    public static boolean comBKick() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.staff.kick.enabled");
     }
    public static String comBKickBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.kick.base");
     }
    public static List<String> comBKickAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.staff.kick.aliases");
     }
    public static String comBKickPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.kick.permission");
     }
    // Ban.
    public static boolean comBBan() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.staff.ban.enabled");
     }
    public static String comBBanBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.ban.base");
     }
    public static List<String> comBBanAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.staff.ban.aliases");
     }
    public static String comBBanPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.ban.permission");
     }
    // Ban.
    public static boolean comBIPBan() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.staff.ipban.enabled");
     }
    public static String comBIPBanBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.ipban.base");
     }
    public static List<String> comBIPBanAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.staff.ipban.aliases");
     }
    public static String comBIPBanPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.ipban.permission");
     }
    // Info.
    public static boolean comBInfo() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.staff.info.enabled");
     }
    public static String comBInfoBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.info.base");
     }
    public static List<String> comBInfoAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.staff.info.aliases");
     }
    public static String comBInfoPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.info.permission");
     }
    // End.
    public static boolean comBEnd() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.staff.end.enabled");
     }
    public static String comBEndBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.end.base");
     }
    public static List<String> comBEndAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.staff.end.aliases");
     }
    public static String comBEndPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.end.permission");
     }
    // B-Teleport.
    public static boolean comBTeleport() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.staff.bteleport.enabled");
     }
    public static String comBTeleportBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.bteleport.base");
     }
    public static List<String> comBTeleportAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.staff.bteleport.aliases");
     }
    public static String comBTeleportPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.bteleport.permission");
     }
    // Script.
    public static boolean comBScript() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.staff.script.enabled");
     }
    public static String comBScriptBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.script.base");
     }
    public static List<String> comBScriptAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.staff.script.aliases");
     }
    public static String comBScriptPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.script.permission");
     }
    // Script Reload.
    public static boolean comBScriptRe() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.staff.script-reload.enabled");
    }
    public static String comBScriptReBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.script-reload.base");
    }
    public static List<String> comBScriptReAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.staff.script-reload.aliases");
    }
    public static String comBScriptRePerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.script-reload.permission");
    }
    // Chat History.
    public static boolean comBChH() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.staff.chat-history.enabled");
    }
    public static String comBChHBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.chat-history.base");
    }
    public static List<String> comBChHAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.staff.chat-history.aliases");
    }
    public static String comBChHPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.chat-history.permission");
    }
    // Chat Filter.
    public static boolean comBChF() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.staff.chat-filter.enabled");
    }
    public static String comBChFBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.chat-filter.base");
    }
    public static List<String> comBChFAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.staff.chat-filter.aliases");
    }
    public static String comBChFPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.staff.chat-filter.permission");
    }
    // // Configs.
    // Settings.
    public static boolean comBSettings() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.configs.settings.enabled");
     }
    public static String comBSettingsBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.configs.settings.base");
     }
    public static List<String> comBSettingsAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.configs.settings.aliases");
     }
    public static String comBSettingsPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.configs.settings.permission");
     }
    // Language.
    public static boolean comBLang() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.configs.language.enabled");
     }
    public static String comBLangBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.configs.language.base");
     }
    public static List<String> comBLangAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.configs.language.aliases");
     }
    public static String comBLangPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.configs.language.permission");
     }
    // Votes.
    public static boolean comBVotes() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.configs.votes.enabled");
     }
    public static String comBVotesBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.configs.votes.base");
     }
    public static List<String> comBVotesAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.configs.votes.aliases");
     }
    public static String comBVotesPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.configs.votes.permission");
     }
    // ... Messaging.
    // Ignore.
    public static boolean comBIgnore() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.messaging.ignore.enabled");
     }
    public static String comBIgnoreBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.messaging.ignore.base");
     }
    public static List<String> comBIgnoreAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.messaging.ignore.aliases");
     }
    public static String comBIgnorePerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.messaging.ignore.permission");
     }
    // Message.
    public static boolean comBMessage() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.messaging.message.enabled");
     }
    public static String comBMessageBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.messaging.message.base");
     }
    public static List<String> comBMessageAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.messaging.message.aliases");
     }
    public static String comBMessagePerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.messaging.message.permission");
     }
    // Reply.
    public static boolean comBReply() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.messaging.reply.enabled");
     }
    public static String comBReplyBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.messaging.reply.base");
     }
    public static List<String> comBReplyAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.messaging.reply.aliases");
     }
    public static String comBReplyPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.messaging.reply.permission");
     }
    // Friend.
    public static boolean comBFriend() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.messaging.friend.enabled");
     }
    public static String comBFriendBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.messaging.friend.base");
     }
    public static List<String> comBFriendAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.messaging.friend.aliases");
     }
    public static String comBFriendPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.messaging.friend.permission");
     }
    // Chat Level.
    public static boolean comBChatLevel() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.messaging.chat-level.enabled");
     }
    public static String comBChatLevelBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.messaging.chat-level.base");
     }
    public static List<String> comBChatLevelAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.messaging.chat-level.aliases");
     }
    public static String comBChatLevelPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.messaging.chat-level.permission");
     }
    // BVerify.
    public static boolean comBVerify() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.messaging.bverify.enabled");
     }
    public static String comBVerifyBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.messaging.bverify.base");
     }
    public static List<String> comBVerifyAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.messaging.bverify.aliases");
     }
    public static String comBVerifyPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.messaging.bverify.permission");
     }
    // // Debug.
    // Delete Stat.
    public static boolean comBDeleteStat() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandBoolean("commands.bungee.debug.delete-stat.enabled");
     }
    public static String comBDeleteStatBase() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.debug.delete-stat.base");
     }
    public static List<String> comBDeleteStatAliases() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandStringList("commands.bungee.debug.delete-stat.aliases");
     }
    public static String comBDeleteStatPerm() {
        StreamLine.config.reloadCommands();
        return StreamLine.config.getCommandString("commands.bungee.debug.delete-stat.permission");
     }
}
