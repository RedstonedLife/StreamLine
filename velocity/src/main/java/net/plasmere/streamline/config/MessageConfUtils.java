package net.plasmere.streamline.config;

import net.plasmere.streamline.StreamLine;

public class MessageConfUtils {
    // Messages:
    public static String prefix() {
        return StreamLine.config.getMessString("message-prefix");
    }

    public static String noPerm() {
        return StreamLine.config.getMessString("no-permission");
    }

    public static String reload() {
        return StreamLine.config.getMessString("reload-message");
    }

    public static String onlyPlayers() {
        return StreamLine.config.getMessString("only-players");
    }

    public static String noPlayer() {
        return StreamLine.config.getMessString("no-player");
    }

    public static String notSet() {
        return StreamLine.config.getMessString("not-set");
    }

    public static String discordErrTitle() {
        return StreamLine.config.getMessString("discord-err-title");
    }

    // ... Command Error.
    // Discord.
    public static String discordCommandErrorUnd() {
        return StreamLine.config.getMessString("command-error.discord.undefined");
    }

    // Bungee.
    public static String bungeeCommandErrorUnd() {
        return StreamLine.config.getMessString("command-error.bungee.undefined");
    }

    public static String bungeeCommandErrorInt() {
        return StreamLine.config.getMessString("command-error.bungee.needs-int");
    }

    public static String bungeeCommandErrorSTime() {
        return StreamLine.config.getMessString("command-error.bungee.needs-stringed-time");
    }

    public static String bungeeCommandErrorNoYou() {
        return StreamLine.config.getMessString("command-error.bungee.cant-find-you");
    }

    // Command Disabled.
    public static String discordCommandDisabled() {
        return StreamLine.config.getMessString("command-disabled.discord");
    }

    public static String bungeeCommandDisabled() {
        return StreamLine.config.getMessString("command-disabled.bungee");
    }

    // Module Disabled.
    public static String discordModuleDisabled() {
        return StreamLine.config.getMessString("module-disabled.discord");
    }

    public static String bungeeModuleDisabled() {
        return StreamLine.config.getMessString("module-disabled.bungee");
    }

    // Not command / improper usage.
    public static String discordNotACommand() {
        return StreamLine.config.getMessString("not-a-command.discord");
    }

    public static String bungeeImproperUsage() {
        return StreamLine.config.getMessString("improper-usage.bungee");
    }

    // Command needs args.
    public static String discordNeedsMore() {
        return StreamLine.config.getMessString("command-needs-args.more.discord");
    }

    public static String bungeeNeedsMore() {
        return StreamLine.config.getMessString("command-needs-args.more.bungee");
    }

    public static String discordNeedsLess() {
        return StreamLine.config.getMessString("command-needs-args.less.discord");
    }

    public static String bungeeNeedsLess() {
        return StreamLine.config.getMessString("command-needs-args.less.bungee");
    }

    // Players.
    public static String offlineB() {
        return StreamLine.config.getMessString("players.bungee.offline");
    }

    public static String onlineB() {
        return StreamLine.config.getMessString("players.bungee.online");
    }

    public static String nullB() {
        return StreamLine.config.getMessString("players.bungee.null");
    }

    public static String offlineD() {
        return StreamLine.config.getMessString("players.discord.offline");
    }

    public static String onlineD() {
        return StreamLine.config.getMessString("players.discord.online");
    }

    public static String nullD() {
        return StreamLine.config.getMessString("players.discord.null");
    }

    // Redirect.
    public static String vbBlocked() {
        return StreamLine.config.getMessString("redirect.by-version.blocked");
    }

    // Kicks.
    public static String kicksStopping() {
        return StreamLine.config.getMessString("kicks.stopping");
    }

    // ... Punishments.
    // Mutes.
    public static String punMutedTemp() {
        return StreamLine.config.getMessString("punishments.muted.temp");
    }

    public static String punMutedPerm() {
        return StreamLine.config.getMessString("punishments.muted.perm");
    }

    // Bans.
    public static String punBannedTemp() {
        return StreamLine.config.getMessString("punishments.banned.temp");
    }

    public static String punBannedPerm() {
        return StreamLine.config.getMessString("punishments.banned.perm");
    }

    // IPBans.
    public static String punIPBannedTemp() {
        return StreamLine.config.getMessString("punishments.ip-banned.temp");
    }

    public static String punIPBannedPerm() {
        return StreamLine.config.getMessString("punishments.ip-banned.perm");
    }

    // Reports.
    public static String reportEmbedTitle() {
        return StreamLine.config.getMessString("report-message.embed-title");
    }

    public static String dToDReportMessage() {
        return StreamLine.config.getMessString("report-message.from-discord.discord");
    }

    public static String dToBReportMessage() {
        return StreamLine.config.getMessString("report-message.from-discord.bungee");
    }

    public static String dConfirmReportMessage() {
        return StreamLine.config.getMessString("report-message.from-discord.confirmation");
    }

    public static String bToDReportMessage() {
        return StreamLine.config.getMessString("report-message.from-bungee.discord");
    }

    public static String bToBReportMessage() {
        return StreamLine.config.getMessString("report-message.from-bungee.bungee");
    }

    public static String bConfirmReportMessage() {
        return StreamLine.config.getMessString("report-message.from-bungee.confirmation");
    }

    // Start.
    public static String startTitle() {
        return StreamLine.config.getMessString("start.embed-title");
    }

    public static String startMessage() {
        return StreamLine.config.getMessString("start.message");
    }

    // Stop.
    public static String shutdownTitle() {
        return StreamLine.config.getMessString("shutdown.embed-title");
    }

    public static String shutdownMessage() {
        return StreamLine.config.getMessString("shutdown.message");
    }

    // StaffChat.
    public static String staffChatEmbedTitle() {
        return StreamLine.config.getMessString("staffchat.message.embed-title");
    }

    public static String discordStaffChatMessage() {
        return StreamLine.config.getMessString("staffchat.message.discord");
    }

    public static String bungeeStaffChatMessage() {
        return StreamLine.config.getMessString("staffchat.message.bungee");
    }

    public static String discordStaffChatFrom() {
        return StreamLine.config.getMessString("staffchat.message.from.discord");
    }

    public static String bungeeStaffChatFrom() {
        return StreamLine.config.getMessString("staffchat.message.from.bungee");
    }

    public static String staffChatJustPrefix() {
        return StreamLine.config.getMessString("staffchat.just-prefix");
    }

    public static String staffChatToggle() {
        return StreamLine.config.getMessString("staffchat.message.toggle");
    }

    public static String staffChatOn() {
        return StreamLine.config.getMessString("staffchat.toggle.true");
    }

    public static String staffChatOff() {
        return StreamLine.config.getMessString("staffchat.toggle.false");
    }

    // Online.
    public static String onlineMessageNoPlayers() {
        return StreamLine.config.getMessString("online.message.no-players");
    }

    public static String onlineMessageNoGroups() {
        return StreamLine.config.getMessString("online.message.no-groups");
    }

    public static String onlineMessageEmbedTitle() {
        return StreamLine.config.getMessString("online.message.embed-title");
    }

    public static String onlineMessageDiscord() {
        return StreamLine.config.getMessString("online.message.discord");
    }

    public static String onlineMessageBMain() {
        return StreamLine.config.getMessString("online.message.bungee.main");
    }

    public static String onlineMessageBServers() {
        return StreamLine.config.getMessString("online.message.bungee.servers");
    }

    public static String onlineMessageBPlayersMain() {
        return StreamLine.config.getMessString("online.message.bungee.players.main");
    }

    public static String onlineMessageBPlayersBulkNotLast() {
        return StreamLine.config.getMessString("online.message.bungee.players.playerbulk.if-not-last");
    }

    public static String onlineMessageBPlayersBulkLast() {
        return StreamLine.config.getMessString("online.message.bungee.players.playerbulk.if-last");
    }

    // ... Join Leaves.
    // Discord.
    public static String discordOnline() {
        return StreamLine.config.getMessString("join-leave.discord.online.text");
    }

    public static String discordOnlineEmbed() {
        return StreamLine.config.getMessString("join-leave.discord.online.embed");
    }

    public static String discordOffline() {
        return StreamLine.config.getMessString("join-leave.discord.offline.text");
    }

    public static String discordOfflineEmbed() {
        return StreamLine.config.getMessString("join-leave.discord.offline.embed");
    }

    // Bungee.
    public static String bungeeOnline() {
        return StreamLine.config.getMessString("join-leave.bungee.online");
    }

    public static String bungeeOffline() {
        return StreamLine.config.getMessString("join-leave.bungee.offline");
    }

    // ... StaffOnline.
    // Discord.
    public static String sOnlineMessageEmbedTitle() {
        return StreamLine.config.getMessString("staffonline.message.embed-title");
    }

    public static String sOnlineDiscordMain() {
        return StreamLine.config.getMessString("staffonline.message.discord.main");
    }

    public static String sOnlineDiscordBulkNotLast() {
        return StreamLine.config.getMessString("staffonline.message.discord.staffbulk.if-not-last");
    }

    public static String sOnlineDiscordBulkLast() {
        return StreamLine.config.getMessString("staffonline.message.discord.staffbulk.if-last");
    }

    // Bungee.
    public static String sOnlineBungeeMain() {
        return StreamLine.config.getMessString("staffonline.message.bungee.main");
    }

    public static String sOnlineBungeeBulkNotLast() {
        return StreamLine.config.getMessString("staffonline.message.bungee.staffbulk.if-not-last");
    }

    public static String sOnlineBungeeBulkLast() {
        return StreamLine.config.getMessString("staffonline.message.bungee.staffbulk.if-last");
    }

    // Stream.
    public static String streamNeedLink() {
        return StreamLine.config.getMessString("stream.need-link");
    }

    public static String streamNotLink() {
        return StreamLine.config.getMessString("stream.not-link");
    }

    public static String streamMessage() {
        return StreamLine.config.getMessString("stream.message");
    }

    public static String streamHoverPrefix() {
        return StreamLine.config.getMessString("stream.hover-prefix");
    }

    // SavableParty.
    public static String partyConnect() {
        return StreamLine.config.getMessString("party.connect");
    }

    public static String partyDisconnect() {
        return StreamLine.config.getMessString("party.disconnect");
    }

    // SavableGuild.
    public static String guildConnect() {
        return StreamLine.config.getMessString("guild.connect");
    }

    public static String guildDisconnect() {
        return StreamLine.config.getMessString("guild.disconnect");
    }

    // Parties.
    public static String partiesNone() {
        return StreamLine.config.getMessString("parties.no-parties");
    }

    public static String partiesMessage() {
        return StreamLine.config.getMessString("parties.parties");
    }

    public static String partiesModsNLast() {
        return StreamLine.config.getMessString("parties.mods.not-last");
    }

    public static String partiesModsLast() {
        return StreamLine.config.getMessString("parties.mods.last");
    }

    public static String partiesMemsNLast() {
        return StreamLine.config.getMessString("parties.members.not-last");
    }

    public static String partiesMemsLast() {
        return StreamLine.config.getMessString("parties.members.last");
    }

    public static String partiesTMemsNLast() {
        return StreamLine.config.getMessString("parties.totalmembers.not-last");
    }

    public static String partiesTMemsLast() {
        return StreamLine.config.getMessString("parties.totalmembers.last");
    }

    public static String partiesInvsNLast() {
        return StreamLine.config.getMessString("parties.invites.not-last");
    }

    public static String partiesInvsLast() {
        return StreamLine.config.getMessString("parties.invites.last");
    }

    public static String partiesIsPublicTrue() {
        return StreamLine.config.getMessString("parties.ispublic.true");
    }

    public static String partiesIsPublicFalse() {
        return StreamLine.config.getMessString("parties.ispublic.false");
    }

    public static String partiesIsMutedTrue() {
        return StreamLine.config.getMessString("parties.ismuted.true");
    }

    public static String partiesIsMutedFalse() {
        return StreamLine.config.getMessString("parties.ismuted.false");
    }

    public static String partiesSave() {
        return StreamLine.config.getMessString("parties.save");
    }

    public static String partiesReload() {
        return StreamLine.config.getMessString("parties.reload");
    }

    // Guilds.
    public static String guildsNone() {
        return StreamLine.config.getMessString("guilds.no-guilds");
    }

    public static String guildsMessage() {
        return StreamLine.config.getMessString("guilds.guilds");
    }

    public static String guildsModsNLast() {
        return StreamLine.config.getMessString("guilds.mods.not-last");
    }

    public static String guildsModsLast() {
        return StreamLine.config.getMessString("guilds.mods.last");
    }

    public static String guildsMemsNLast() {
        return StreamLine.config.getMessString("guilds.members.not-last");
    }

    public static String guildsMemsLast() {
        return StreamLine.config.getMessString("guilds.members.last");
    }

    public static String guildsTMemsNLast() {
        return StreamLine.config.getMessString("guilds.totalmembers.not-last");
    }

    public static String guildsTMemsLast() {
        return StreamLine.config.getMessString("guilds.totalmembers.last");
    }

    public static String guildsInvsNLast() {
        return StreamLine.config.getMessString("guilds.invites.not-last");
    }

    public static String guildsInvsLast() {
        return StreamLine.config.getMessString("guilds.invites.last");
    }

    public static String guildsIsPublicTrue() {
        return StreamLine.config.getMessString("guilds.ispublic.true");
    }

    public static String guildsIsPublicFalse() {
        return StreamLine.config.getMessString("guilds.ispublic.false");
    }

    public static String guildsIsMutedTrue() {
        return StreamLine.config.getMessString("guilds.ismuted.true");
    }

    public static String guildsIsMutedFalse() {
        return StreamLine.config.getMessString("guilds.ismuted.false");
    }

    public static String guildsSave() {
        return StreamLine.config.getMessString("guilds.save");
    }

    public static String guildsReload() {
        return StreamLine.config.getMessString("guilds.reload");
    }

    // Sudo.
    public static String sudoWorked() {
        return StreamLine.config.getMessString("sudo.worked");
    }

    public static String sudoNoWork() {
        return StreamLine.config.getMessString("sudo.no-work");
    }

    public static String sudoNoSudo() {
        return StreamLine.config.getMessString("sudo.no-sudo");
    }

    // SSPY.
    public static String sspyToggle() {
        return StreamLine.config.getMessString("sspy.message");
    }

    public static String sspyOn() {
        return StreamLine.config.getMessString("sspy.toggle.true");
    }

    public static String sspyOff() {
        return StreamLine.config.getMessString("sspy.toggle.false");
    }

    // GSPY.
    public static String gspyToggle() {
        return StreamLine.config.getMessString("gspy.message");
    }

    public static String gspyOn() {
        return StreamLine.config.getMessString("gspy.toggle.true");
    }

    public static String gspyOff() {
        return StreamLine.config.getMessString("gspy.toggle.false");
    }

    // PSPY.
    public static String pspyToggle() {
        return StreamLine.config.getMessString("pspy.message");
    }

    public static String pspyOn() {
        return StreamLine.config.getMessString("pspy.toggle.true");
    }

    public static String pspyOff() {
        return StreamLine.config.getMessString("pspy.toggle.false");
    }

    // PSPY.
    public static String scViewToggle() {
        return StreamLine.config.getMessString("sc-view.message");
    }

    public static String scViewOn() {
        return StreamLine.config.getMessString("sc-view.toggle.true");
    }

    public static String scViewOff() {
        return StreamLine.config.getMessString("sc-view.toggle.false");
    }

    // SSPYVS.
    public static String sspyvsToggle() {
        return StreamLine.config.getMessString("sspyvs.message");
    }

    public static String sspyvsOn() {
        return StreamLine.config.getMessString("sspyvs.toggle.true");
    }

    public static String sspyvsOff() {
        return StreamLine.config.getMessString("sspyvs.toggle.false");
    }

    // PSPYVS.
    public static String pspyvsToggle() {
        return StreamLine.config.getMessString("pspyvs.message");
    }

    public static String pspyvsOn() {
        return StreamLine.config.getMessString("pspyvs.toggle.true");
    }

    public static String pspyvsOff() {
        return StreamLine.config.getMessString("pspyvs.toggle.false");
    }

    // GSPYVS.
    public static String gspyvsToggle() {
        return StreamLine.config.getMessString("gspyvs.message");
    }

    public static String gspyvsOn() {
        return StreamLine.config.getMessString("gspyvs.toggle.true");
    }

    public static String gspyvsOff() {
        return StreamLine.config.getMessString("gspyvs.toggle.false");
    }

    // SCVS.
    public static String scvsToggle() {
        return StreamLine.config.getMessString("scvs.message");
    }

    public static String scvsOn() {
        return StreamLine.config.getMessString("scvs.toggle.true");
    }

    public static String scvsOff() {
        return StreamLine.config.getMessString("scvs.toggle.false");
    }

    // EVReload.
    public static String evReload() {
        return StreamLine.config.getMessString("evreload.message");
    }

    // Points.
    public static String pointsViewS() {
        return StreamLine.config.getMessString("points.view.self");
    }

    public static String pointsViewO() {
        return StreamLine.config.getMessString("points.view.other");
    }

    public static String pointsAddS() {
        return StreamLine.config.getMessString("points.add.self");
    }

    public static String pointsAddO() {
        return StreamLine.config.getMessString("points.add.other");
    }

    public static String pointsRemoveS() {
        return StreamLine.config.getMessString("points.remove.self");
    }

    public static String pointsRemoveO() {
        return StreamLine.config.getMessString("points.remove.other");
    }

    public static String pointsSetS() {
        return StreamLine.config.getMessString("points.set.self");
    }

    public static String pointsSetO() {
        return StreamLine.config.getMessString("points.set.other");
    }

    // Ignore.
    public static String ignoreAddSelf() {
        return StreamLine.config.getMessString("ignore.add.self");
    }

    public static String ignoreAddIgnored() {
        return StreamLine.config.getMessString("ignore.add.ignored");
    }

    public static String ignoreAddAlready() {
        return StreamLine.config.getMessString("ignore.add.already");
    }

    public static String ignoreAddNSelf() {
        return StreamLine.config.getMessString("ignore.add.not-self");
    }

    public static String ignoreRemSelf() {
        return StreamLine.config.getMessString("ignore.remove.self");
    }

    public static String ignoreRemIgnored() {
        return StreamLine.config.getMessString("ignore.remove.ignored");
    }

    public static String ignoreRemAlready() {
        return StreamLine.config.getMessString("ignore.remove.already");
    }

    public static String ignoreRemNSelf() {
        return StreamLine.config.getMessString("ignore.remove.not-self");
    }

    public static String ignoreListMain() {
        return StreamLine.config.getMessString("ignore.list.main");
    }

    public static String ignoreListNLast() {
        return StreamLine.config.getMessString("ignore.list.ignores.not-last");
    }

    public static String ignoreListLast() {
        return StreamLine.config.getMessString("ignore.list.ignores.last");
    }

    // Message.
    public static String messageSender() {
        return StreamLine.config.getMessString("message.sender");
    }

    public static String messageTo() {
        return StreamLine.config.getMessString("message.to");
    }

    public static String messageIgnored() {
        return StreamLine.config.getMessString("message.ignored");
    }

    public static String messageSSPY() {
        return StreamLine.config.getMessString("message.sspy");
    }

    // Reply.
    public static String replySender() {
        return StreamLine.config.getMessString("message.sender");
    }

    public static String replyTo() {
        return StreamLine.config.getMessString("message.to");
    }

    public static String replyIgnored() {
        return StreamLine.config.getMessString("message.ignored");
    }

    public static String replySSPY() {
        return StreamLine.config.getMessString("message.sspy");
    }

    // Mute.
    public static String muteEmbed() {
        return StreamLine.config.getMessString("mute.discord-embed-title");
    }

    public static String muteCannot() {
        return StreamLine.config.getMessString("mute.cannot");
    }

    public static String muteMTempSender() {
        return StreamLine.config.getMessString("mute.mute.temp.sender");
    }

    public static String muteMTempMuted() {
        return StreamLine.config.getMessString("mute.mute.temp.muted");
    }

    public static String muteMTempAlready() {
        return StreamLine.config.getMessString("mute.mute.temp.already");
    }

    public static String muteMTempStaff() {
        return StreamLine.config.getMessString("mute.mute.temp.staff");
    }

    public static String muteMTempDiscord() {
        return StreamLine.config.getMessString("mute.mute.temp.discord");
    }

    public static String muteMPermSender() {
        return StreamLine.config.getMessString("mute.mute.perm.sender");
    }

    public static String muteMPermMuted() {
        return StreamLine.config.getMessString("mute.mute.perm.muted");
    }

    public static String muteMPermAlready() {
        return StreamLine.config.getMessString("mute.mute.perm.already");
    }

    public static String muteMPermStaff() {
        return StreamLine.config.getMessString("mute.mute.perm.staff");
    }

    public static String muteMPermDiscord() {
        return StreamLine.config.getMessString("mute.mute.perm.discord");
    }

    public static String muteUnSender() {
        return StreamLine.config.getMessString("mute.unmute.sender");
    }

    public static String muteUnMuted() {
        return StreamLine.config.getMessString("mute.unmute.muted");
    }

    public static String muteUnAlready() {
        return StreamLine.config.getMessString("mute.unmute.already");
    }

    public static String muteUnStaff() {
        return StreamLine.config.getMessString("mute.unmute.staff");
    }

    public static String muteUnDiscord() {
        return StreamLine.config.getMessString("mute.unmute.discord");
    }

    public static String muteCheckMain() {
        return StreamLine.config.getMessString("mute.check.main");
    }

    public static String muteCheckMuted() {
        return StreamLine.config.getMessString("mute.check.muted");
    }

    public static String muteCheckUnMuted() {
        return StreamLine.config.getMessString("mute.check.unmuted");
    }

    public static String muteCheckNoDate() {
        return StreamLine.config.getMessString("mute.check.no-date");
    }

    // Kick.
    public static String kickEmbed() {
        return StreamLine.config.getMessString("kick.discord-embed-title");
    }

    public static String kickCannot() {
        return StreamLine.config.getMessString("kick.cannot");
    }

    public static String kickSender() {
        return StreamLine.config.getMessString("kick.sender");
    }

    public static String kickKicked() {
        return StreamLine.config.getMessString("kick.kicked");
    }

    public static String kickStaff() {
        return StreamLine.config.getMessString("kick.staff");
    }

    public static String kickDiscord() {
        return StreamLine.config.getMessString("kick.discord");
    }

    // Ban.
    public static String banEmbed() {
        return StreamLine.config.getMessString("ban.discord-embed-title");
    }

    public static String banCannot() {
        return StreamLine.config.getMessString("ban.cannot");
    }

    public static String banBTempSender() {
        return StreamLine.config.getMessString("ban.ban.temp.sender");
    }

    public static String banBTempAlready() {
        return StreamLine.config.getMessString("ban.ban.temp.already");
    }

    public static String banBTempStaff() {
        return StreamLine.config.getMessString("ban.ban.temp.staff");
    }

    public static String banBTempDiscord() {
        return StreamLine.config.getMessString("ban.ban.temp.discord");
    }

    public static String banBPermSender() {
        return StreamLine.config.getMessString("ban.ban.perm.sender");
    }

    public static String banBPermAlready() {
        return StreamLine.config.getMessString("ban.ban.perm.already");
    }

    public static String banBPermStaff() {
        return StreamLine.config.getMessString("ban.ban.perm.staff");
    }

    public static String banBPermDiscord() {
        return StreamLine.config.getMessString("ban.ban.perm.discord");
    }

    public static String banUnSender() {
        return StreamLine.config.getMessString("ban.unban.sender");
    }

    public static String banUnAlready() {
        return StreamLine.config.getMessString("ban.unban.already");
    }

    public static String banUnStaff() {
        return StreamLine.config.getMessString("ban.unban.staff");
    }

    public static String banUnDiscord() {
        return StreamLine.config.getMessString("ban.unban.discord");
    }

    public static String banCheckMain() {
        return StreamLine.config.getMessString("ban.check.main");
    }

    public static String banCheckBanned() {
        return StreamLine.config.getMessString("ban.check.banned");
    }

    public static String banCheckUnBanned() {
        return StreamLine.config.getMessString("ban.check.unbanned");
    }

    public static String banCheckNoDate() {
        return StreamLine.config.getMessString("ban.check.no-date");
    }

    // IPBan.
    public static String ipBanEmbed() {
        return StreamLine.config.getMessString("ipban.discord-embed-title");
    }

    public static String ipBanCannot() {
        return StreamLine.config.getMessString("ipban.cannot");
    }

    public static String ipBanBTempSender() {
        return StreamLine.config.getMessString("ipban.ban.temp.sender");
    }

    public static String ipBanBTempAlready() {
        return StreamLine.config.getMessString("ipban.ban.temp.already");
    }

    public static String ipBanBTempStaff() {
        return StreamLine.config.getMessString("ipban.ban.temp.staff");
    }

    public static String ipBanBTempDiscord() {
        return StreamLine.config.getMessString("ipban.ban.temp.discord");
    }

    public static String ipBanBPermSender() {
        return StreamLine.config.getMessString("ipban.ban.perm.sender");
    }

    public static String ipBanBPermAlready() {
        return StreamLine.config.getMessString("ipban.ban.perm.already");
    }

    public static String ipBanBPermStaff() {
        return StreamLine.config.getMessString("ipban.ban.perm.staff");
    }

    public static String ipBanBPermDiscord() {
        return StreamLine.config.getMessString("ipban.ban.perm.discord");
    }

    public static String ipBanUnSender() {
        return StreamLine.config.getMessString("ipban.unban.sender");
    }

    public static String ipBanUnAlready() {
        return StreamLine.config.getMessString("ipban.unban.already");
    }

    public static String ipBanUnStaff() {
        return StreamLine.config.getMessString("ipban.unban.staff");
    }

    public static String ipBanUnDiscord() {
        return StreamLine.config.getMessString("ipban.unban.discord");
    }

    public static String ipBanCheckMain() {
        return StreamLine.config.getMessString("ipban.check.main");
    }

    public static String ipBanCheckBanned() {
        return StreamLine.config.getMessString("ipban.check.banned");
    }

    public static String ipBanCheckUnBanned() {
        return StreamLine.config.getMessString("ipban.check.unbanned");
    }

    public static String ipBanCheckNoDate() {
        return StreamLine.config.getMessString("ipban.check.no-date");
    }

    // Ignore.
    public static String friendReqSelf() {
        return StreamLine.config.getMessString("friend.request.self");
    }

    public static String friendReqOther() {
        return StreamLine.config.getMessString("friend.request.other");
    }

    public static String friendReqAlready() {
        return StreamLine.config.getMessString("friend.request.already");
    }

    public static String friendReqNSelf() {
        return StreamLine.config.getMessString("friend.request.not-self");
    }

    public static String friendReqIgnored() {
        return StreamLine.config.getMessString("friend.request.ignored");
    }

    public static String friendAcceptSelf() {
        return StreamLine.config.getMessString("friend.accept.self");
    }

    public static String friendAcceptOther() {
        return StreamLine.config.getMessString("friend.accept.other");
    }

    public static String friendAcceptNone() {
        return StreamLine.config.getMessString("friend.accept.none");
    }

    public static String friendDenySelf() {
        return StreamLine.config.getMessString("friend.deny.self");
    }

    public static String friendDenyOther() {
        return StreamLine.config.getMessString("friend.deny.other");
    }

    public static String friendDenyNone() {
        return StreamLine.config.getMessString("friend.deny.none");
    }

    public static String friendRemSelf() {
        return StreamLine.config.getMessString("friend.remove.self");
    }

    public static String friendRemOther() {
        return StreamLine.config.getMessString("friend.remove.other");
    }

    public static String friendRemAlready() {
        return StreamLine.config.getMessString("friend.remove.already");
    }

    public static String friendRemNSelf() {
        return StreamLine.config.getMessString("friend.remove.not-self");
    }

    public static String friendListMain() {
        return StreamLine.config.getMessString("friend.list.main");
    }

    public static String friendListFNLast() {
        return StreamLine.config.getMessString("friend.list.friends.not-last");
    }

    public static String friendListFLast() {
        return StreamLine.config.getMessString("friend.list.friends.last");
    }

    public static String friendListPTNLast() {
        return StreamLine.config.getMessString("friend.list.pending-to.not-last");
    }

    public static String friendListPTLast() {
        return StreamLine.config.getMessString("friend.list.pending-to.last");
    }

    public static String friendListPFNLast() {
        return StreamLine.config.getMessString("friend.list.pending-from.not-last");
    }

    public static String friendListPFLast() {
        return StreamLine.config.getMessString("friend.list.pending-from.last");
    }

    public static String friendConnect() {
        return StreamLine.config.getMessString("friend.connect");
    }

    public static String friendDisconnect() {
        return StreamLine.config.getMessString("friend.disconnect");
    }

    // GetStats.
    public static String getStatsNone() {
        return StreamLine.config.getMessString("getstats.no-stats");
    }

    public static String getStatsMessage() {
        return StreamLine.config.getMessString("getstats.message.main");
    }

    public static String getStatsNLast() {
        return StreamLine.config.getMessString("getstats.message.not-last");
    }

    public static String getStatsLast() {
        return StreamLine.config.getMessString("getstats.message.last");
    }

    public static String getStatsSave() {
        return StreamLine.config.getMessString("getstats.save");
    }

    public static String getStatsReload() {
        return StreamLine.config.getMessString("getstats.reload");
    }

    // // Settings.
    // Set.
    public static String settingsSetMOTD() {
        return StreamLine.config.getMessString("settings.set.motd");
    }

    public static String settingsSetMOTDTime() {
        return StreamLine.config.getMessString("settings.set.motd-time");
    }

    public static String settingsSetVersion() {
        return StreamLine.config.getMessString("settings.set.version");
    }

    public static String settingsSetSample() {
        return StreamLine.config.getMessString("settings.set.sample");
    }

    public static String settingsSetMaxP() {
        return StreamLine.config.getMessString("settings.set.max-players");
    }

    public static String settingsSetOnlineP() {
        return StreamLine.config.getMessString("settings.set.online-players");
    }

    public static String settingsSetPCEnabled() {
        return StreamLine.config.getMessString("settings.set.proxy-chat-enabled");
    }

    public static String settingsSetChatToConsole() {
        return StreamLine.config.getMessString("settings.set.proxy-chat-to-console");
    }

    public static String settingsSetPCChats() {
        return StreamLine.config.getMessString("settings.set.proxy-chat-chats");
    }

    public static String settingsSetPCBPerm() {
        return StreamLine.config.getMessString("settings.set.proxy-chat-base-perm");
    }

    public static String settingsSetTagsEnablePing() {
        return StreamLine.config.getMessString("settings.set.tags-enable-ping");
    }

    public static String settingsSetTagsTagPrefix() {
        return StreamLine.config.getMessString("settings.set.tags-tag-prefix");
    }

    public static String settingsSetEmotes() {
        return StreamLine.config.getMessString("settings.set.emotes");
    }

    public static String settingsSetEmotePermissions() {
        return StreamLine.config.getMessString("settings.set.emote-permissions");
    }

    public static String settingsSetMaintenanceModeEnabled() {
        return StreamLine.config.getMessString("settings.set.maintenance-mode-enabled");
    }

    // Get.
    public static String settingsGetMOTD() {
        return StreamLine.config.getMessString("settings.get.motd");
    }

    public static String settingsGetMOTDTime() {
        return StreamLine.config.getMessString("settings.get.motd-time");
    }

    public static String settingsGetVersion() {
        return StreamLine.config.getMessString("settings.get.version");
    }

    public static String settingsGetSample() {
        return StreamLine.config.getMessString("settings.get.sample");
    }

    public static String settingsGetMaxP() {
        return StreamLine.config.getMessString("settings.get.max-players");
    }

    public static String settingsGetOnlineP() {
        return StreamLine.config.getMessString("settings.get.online-players");
    }

    public static String settingsGetPCEnabled() {
        return StreamLine.config.getMessString("settings.get.proxy-chat-enabled");
    }

    public static String settingsGetChatToConsole() {
        return StreamLine.config.getMessString("settings.get.proxy-chat-to-console");
    }

    public static String settingsGetPCChats() {
        return StreamLine.config.getMessString("settings.get.proxy-chat-chats");
    }

    public static String settingsGetPCBPerm() {
        return StreamLine.config.getMessString("settings.get.proxy-chat-base-perm");
    }

    public static String settingsGetTagsEnablePing() {
        return StreamLine.config.getMessString("settings.get.tags-enable-ping");
    }

    public static String settingsGetTagsTagPrefix() {
        return StreamLine.config.getMessString("settings.get.tags-tag-prefix");
    }

    public static String settingsGetEmotes() {
        return StreamLine.config.getMessString("settings.get.emotes");
    }

    public static String settingsGetEmotePermissions() {
        return StreamLine.config.getMessString("settings.get.emote-permissions");
    }

    public static String settingsGetMaintenanceModeEnabled() {
        return StreamLine.config.getMessString("settings.set.maintenance-mode-enabled");
    }

    // // Info.
    public static String info() {
        return StreamLine.config.getMessString("info");
    }

    // // Graceful End.
    public static String gracefulEndSender() {
        return StreamLine.config.getMessString("graceful-end.sender");
    }

    public static String gracefulEndKickMessage() {
        return StreamLine.config.getMessString("graceful-end.kick-message");
    }

    // Delete Stat.
    public static String deleteStatMessage() {
        return StreamLine.config.getMessString("delete-stat.message");
    }

    // B-Teleport.
    public static String bteleport() {
        return StreamLine.config.getMessString("bteleport");
    }

    // Language
    public static String languageMessage() {
        return StreamLine.config.getMessString("language.message");
    }

    public static String languageInvalidLocale() {
        return StreamLine.config.getMessString("language.invalid-locale");
    }

    public static String chatChannelsSwitch() {
        return StreamLine.config.getMessString("chat-channels.switch");
    }

    public static String scriptMessage() {
        return StreamLine.config.getMessString("script.message");
    }

    public static String scriptNoScript() {
        return StreamLine.config.getMessString("script.no-script");
    }

    public static String scriptReload() {
        return StreamLine.config.getMessString("script.reload");
    }

    public static String votesConsoleToggle() {
        return StreamLine.config.getMessString("votes.console.toggle");
    }

    public static String votesConsoleEnabled() {
        return StreamLine.config.getMessString("votes.console.enabled");
    }

    public static String votesConsoleDisabled() {
        return StreamLine.config.getMessString("votes.console.disabled");
    }

    public static String votesGet() {
        return StreamLine.config.getMessString("votes.get");
    }

    public static String votesRemove() {
        return StreamLine.config.getMessString("votes.remove");
    }

    public static String votesAdd() {
        return StreamLine.config.getMessString("votes.add");
    }

    public static String votesSet() {
        return StreamLine.config.getMessString("votes.set");
    }

    public static String historyMessage() {
        return StreamLine.config.getMessString("history.message");
    }

    public static String historyChatBulk() {
        return StreamLine.config.getMessString("history.chat-bulk.message");
    }

    public static String filtersEnabled() {
        return StreamLine.config.getMessString("filters.enabled");
    }
    public static String filtersDisabled() {
        return StreamLine.config.getMessString("filters.disabled");
    }
    public static String filtersReplacementsLast() {
        return StreamLine.config.getMessString("filters.replacements.last");
    }
    public static String filtersReplacementsNLast() {
        return StreamLine.config.getMessString("filters.replacements.not-last");
    }
    public static String filtersCommandToggle() {
        return StreamLine.config.getMessString("filters.command.toggle.message");
    }
    public static String filtersCommandCreate() {
        return StreamLine.config.getMessString("filters.command.create.message");
    }

    public static String voiceCreate() {
        return StreamLine.config.getMessString("voice.create");
    }
    public static String voiceDeleteSender() {
        return StreamLine.config.getMessString("voice.delete.sender");
    }
    public static String voiceDeleteOther() {
        return StreamLine.config.getMessString("voice.delete.other");
    }
    public static String voiceAddSender() {
        return StreamLine.config.getMessString("voice.add.sender");
    }
    public static String voiceAddOther() {
        return StreamLine.config.getMessString("voice.add.other");
    }
    public static String voiceRemoveSender() {
        return StreamLine.config.getMessString("voice.remove.sender");
    }
    public static String voiceRemoveOther() {
        return StreamLine.config.getMessString("voice.remove.other");
    }
    public static String voiceNotVerified() {
        return StreamLine.config.getMessString("voice.not-verified");
    }
    public static String voiceNoVoice() {
        return StreamLine.config.getMessString("voice.no-voice");
    }
    public static String voiceAlreadyVoice() {
        return StreamLine.config.getMessString("voice.already-voice");
    }
    public static String voiceTooMany() {
        return StreamLine.config.getMessString("voice.too-many");
    }

    public static String broadcastMessageWith() {
        return StreamLine.config.getMessString("broadcast.message.with-prefix");
    }
    public static String broadcastMessageWithout() {
        return StreamLine.config.getMessString("broadcast.message.without-prefix");
    }
    public static String broadcastPrefix() {
        return StreamLine.config.getMessString("broadcast.prefix");
    }

    public static String bypassPCMessage() {
        return StreamLine.config.getMessString("bypass-proxychat.message");
    }
    public static String bypassPCDone() {
        return StreamLine.config.getMessString("bypass-proxychat.done");
    }

    public static String playtimeConsoleToggle() {
        return StreamLine.config.getMessString("playtime.console.toggle");
    }

    public static String playtimeConsoleEnabled() {
        return StreamLine.config.getMessString("playtime.console.enabled");
    }

    public static String playtimeConsoleDisabled() {
        return StreamLine.config.getMessString("playtime.console.disabled");
    }

    public static String playtimeGet() {
        return StreamLine.config.getMessString("playtime.get");
    }

    public static String playtimeRemove() {
        return StreamLine.config.getMessString("playtime.remove");
    }

    public static String playtimeAdd() {
        return StreamLine.config.getMessString("playtime.add");
    }

    public static String playtimeSet() {
        return StreamLine.config.getMessString("playtime.set");
    }

    public static String playtimeSyncStart() {
        return StreamLine.config.getMessString("playtime.sync.start");
    }

    public static String playtimeSyncFinish() {
        return StreamLine.config.getMessString("playtime.sync.finish");
    }

    public static String playtimeTop() {
        return StreamLine.config.getMessString("playtime.top");
    }

    public static String msbExecuteNotSupplied() {
        return StreamLine.config.getMessString("mysqlbridger.execute.not-supplied");
    }

    public static String msbExecuteNotValid() {
        return StreamLine.config.getMessString("mysqlbridger.execute.not-valid");
    }

    public static String msbExecuteComplete() {
        return StreamLine.config.getMessString("mysqlbridger.execute.complete");
    }

    public static String msbQueryNotSupplied() {
        return StreamLine.config.getMessString("mysqlbridger.query.not-supplied");
    }

    public static String msbQueryNotValid() {
        return StreamLine.config.getMessString("mysqlbridger.query.not-valid");
    }

    public static String msbQueryComplete() {
        return StreamLine.config.getMessString("mysqlbridger.query.complete");
    }

    public static String proxyTextSent() {
        return StreamLine.config.getMessString("proxytext.sent");
    }

    public static String proxyTitleSent() {
        return StreamLine.config.getMessString("proxytitle.sent");
    }
}