package net.plasmere.streamline.utils;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.PluginManager;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.commands.*;
import net.plasmere.streamline.commands.debug.DeleteStatCommand;
import net.plasmere.streamline.commands.debug.ObjectEditCommand;
import net.plasmere.streamline.commands.messaging.*;
import net.plasmere.streamline.commands.servers.GoToServerLobbyCommand;
import net.plasmere.streamline.commands.sql.MSBExecuteCommand;
import net.plasmere.streamline.commands.sql.MSBQueryCommand;
import net.plasmere.streamline.commands.staff.*;
import net.plasmere.streamline.commands.staff.events.BTagCommand;
import net.plasmere.streamline.commands.staff.events.EventReloadCommand;
import net.plasmere.streamline.commands.staff.punishments.BanCommand;
import net.plasmere.streamline.commands.staff.punishments.IPBanCommand;
import net.plasmere.streamline.commands.staff.punishments.KickCommand;
import net.plasmere.streamline.commands.staff.punishments.MuteCommand;
import net.plasmere.streamline.commands.staff.scripts.ScriptCommand;
import net.plasmere.streamline.commands.staff.scripts.ScriptReloadCommand;
import net.plasmere.streamline.commands.staff.settings.LanguageCommand;
import net.plasmere.streamline.commands.staff.settings.SettingsEditCommand;
import net.plasmere.streamline.commands.staff.spy.GSPYCommand;
import net.plasmere.streamline.commands.staff.spy.PSPYCommand;
import net.plasmere.streamline.commands.staff.spy.SCViewCommand;
import net.plasmere.streamline.commands.staff.spy.SSPYCommand;
import net.plasmere.streamline.config.CommandsConfUtils;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.listeners.*;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.objects.configs.obj.AliasSLCommand;
import net.plasmere.streamline.objects.enums.NetworkState;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.utils.objects.CustomSQLInfo;
import net.plasmere.streamline.utils.objects.SavedQueries;
import net.plasmere.streamline.utils.objects.Syncable;

import java.util.*;

public class PluginUtils {
    public static int commandsAmount = 0;
    public static int listenerAmount = 0;
    public static int aliasesAmount = 0;
    public static NetworkState state = NetworkState.NULL;

    public static boolean isLocked(){
        return state.equals(NetworkState.STOPPING) || state.equals(NetworkState.STOPPED);
    }

    public static void unregisterCommand(SLCommand command){
        commandsAmount --;
        StreamLine.getInstance().getProxy().getPluginManager().unregisterCommand(command);
    }

    public static void unregisterAlias(String aliasName){
        aliasesAmount --;
        StreamLine.getInstance().getProxy().getPluginManager().unregisterCommand(getCommandByName(aliasName));
    }

    public static Command getCommandByName(String name) {
        for (Map.Entry<String, Command> command : StreamLine.getInstance().getProxy().getPluginManager().getCommands()) {
            if (command.getKey().equals(name)) return command.getValue();
        }

        return null;
    }

    public static void unregisterListener(StreamLine plugin, Listener listener){
        listenerAmount --;
        plugin.getProxy().getPluginManager().unregisterListener(listener);
    }

    public static void registerCommand(SLCommand command){
        commandsAmount ++;
//        CommandMeta meta = StreamLine.getInstance().getProxy().getCommandManager().metaBuilder(command.base)
//                .aliases(command.aliases)
//                .build()
//                ;
        StreamLine.getInstance().getProxy().getPluginManager().registerCommand(StreamLine.getInstance(), command);
    }

    public static void registerAlias(AliasSLCommand command){
        aliasesAmount ++;
//        CommandMeta meta = StreamLine.getProxy().getCommandManager().metaBuilder(command.base)
//                .aliases(command.aliases)
//                .build()
//                ;
        StreamLine.getInstance().getProxy().getPluginManager().registerCommand(StreamLine.getInstance(), command);
    }

    public static void registerListener(StreamLine plugin, Listener listener){
        listenerAmount ++;
        plugin.getProxy().getPluginManager().registerListener(plugin, listener);
    }

    public static void loadCommands(StreamLine plugin){
        commandsAmount = 0;

        // Debug.
        if (CommandsConfUtils.comBDeleteStat()) {
            registerCommand(new DeleteStatCommand(CommandsConfUtils.comBDeleteStatBase(), CommandsConfUtils.comBDeleteStatPerm(), stringListToArray(CommandsConfUtils.comBDeleteStatAliases())));
        }
        registerCommand(new ObjectEditCommand("objectedit", "streamline.command.objectedit", stringListToArray(List.of("oje"))));

        // Staff.
        // // Reg.
        if (CommandsConfUtils.comBStream()) {
            registerCommand(new StreamCommand(CommandsConfUtils.comBStreamBase(), CommandsConfUtils.comBStreamPerm(), stringListToArray(CommandsConfUtils.comBStreamAliases())));
        }
        if (CommandsConfUtils.comBStaffChat()) {
            registerCommand(new StaffChatCommand(CommandsConfUtils.comBStaffChatBase(), CommandsConfUtils.comBStaffChatPerm(), stringListToArray(CommandsConfUtils.comBStaffChatAliases())));
        }
        if (CommandsConfUtils.comBSudo()) {
            registerCommand(new SudoCommand(CommandsConfUtils.comBSudoBase(), CommandsConfUtils.comBSudoPerm(), stringListToArray(CommandsConfUtils.comBSudoAliases())));
        }
        if (CommandsConfUtils.comBStaffOnline()) {
            registerCommand(new StaffOnlineCommand(CommandsConfUtils.comBStaffOnlineBase(), CommandsConfUtils.comBStaffOnlinePerm(), stringListToArray(CommandsConfUtils.comBStaffOnlineAliases())));
        }
        if (CommandsConfUtils.comBGlobalOnline() && StreamLine.lpHolder.enabled) {
            registerCommand(new GlobalOnlineCommand(CommandsConfUtils.comBGlobalOnlineBase(), CommandsConfUtils.comBGlobalOnlinePerm(), stringListToArray(CommandsConfUtils.comBGlobalOnlineAliases())));
        }
        if (CommandsConfUtils.comBSettings()) {
            registerCommand(new SettingsEditCommand(CommandsConfUtils.comBSettingsBase(), CommandsConfUtils.comBSettingsPerm(), stringListToArray(CommandsConfUtils.comBSettingsAliases())));
        }
        if (CommandsConfUtils.comBLang()) {
            registerCommand(new LanguageCommand(CommandsConfUtils.comBLangBase(), CommandsConfUtils.comBLangPerm(), stringListToArray(CommandsConfUtils.comBLangAliases())));
        }
        if (CommandsConfUtils.comBVotes() && ConfigUtils.moduleBRanksEnabled()) {
            registerCommand(new VotesCommand(CommandsConfUtils.comBVotesBase(), CommandsConfUtils.comBVotesPerm(), stringListToArray(CommandsConfUtils.comBVotesAliases())));
        }
        if (CommandsConfUtils.comBMSBExecute() && ConfigUtils.mysqlbridgerEnabled()) {
            registerCommand(new MSBExecuteCommand(CommandsConfUtils.comBMSBExecuteBase(), CommandsConfUtils.comBMSBExecutePerm(), stringListToArray(CommandsConfUtils.comBMSBExecuteAliases())));
        }
        if (CommandsConfUtils.comBMSBQuery() && ConfigUtils.mysqlbridgerEnabled()) {
            registerCommand(new MSBQueryCommand(CommandsConfUtils.comBMSBQueryBase(), CommandsConfUtils.comBMSBQueryPerm(), stringListToArray(CommandsConfUtils.comBMSBQueryAliases())));
        }
        if (CommandsConfUtils.comBPlayTime()) {
            registerCommand(new PlayTimeCommand(CommandsConfUtils.comBPlayTimeBase(), CommandsConfUtils.comBPlayTimePerm(), stringListToArray(CommandsConfUtils.comBPlayTimeAliases())));
        }
        if (CommandsConfUtils.comBTeleport()) {
            registerCommand(new TeleportCommand(CommandsConfUtils.comBTeleportBase(), CommandsConfUtils.comBTeleportPerm(), stringListToArray(CommandsConfUtils.comBTeleportAliases())));
        }
        if (CommandsConfUtils.comBChH()) {
            registerCommand(new ChatHistoryCommand(CommandsConfUtils.comBChHBase(), CommandsConfUtils.comBChHPerm(), stringListToArray(CommandsConfUtils.comBChHAliases())));
        }
        if (CommandsConfUtils.comBChF()) {
            registerCommand(new ChatFilterCommand(CommandsConfUtils.comBChFBase(), CommandsConfUtils.comBChFPerm(), stringListToArray(CommandsConfUtils.comBChFAliases())));
        }
        // // Spying.
        if (CommandsConfUtils.comBSSPY()) {
            registerCommand(new SSPYCommand(CommandsConfUtils.comBSSPYBase(), CommandsConfUtils.comBSSPYPerm(), stringListToArray(CommandsConfUtils.comBSSPYAliases())));
        }
        if (CommandsConfUtils.comBGSPY()) {
            registerCommand(new GSPYCommand(CommandsConfUtils.comBGSPYBase(), CommandsConfUtils.comBGSPYPerm(), stringListToArray(CommandsConfUtils.comBGSPYAliases())));
        }
        if (CommandsConfUtils.comBPSPY()) {
            registerCommand(new PSPYCommand(CommandsConfUtils.comBPSPYBase(), CommandsConfUtils.comBPSPYPerm(), stringListToArray(CommandsConfUtils.comBPSPYAliases())));
        }
        if (CommandsConfUtils.comBSCView()) {
            registerCommand(new SCViewCommand(CommandsConfUtils.comBSCViewBase(), CommandsConfUtils.comBSCViewPerm(), stringListToArray(CommandsConfUtils.comBSCViewAliases())));
        }
        // // Punishments.
        if (CommandsConfUtils.comBMute() && ConfigUtils.punMutes()) {
            registerCommand(new MuteCommand(CommandsConfUtils.comBMuteBase(), CommandsConfUtils.comBMutePerm(), stringListToArray(CommandsConfUtils.comBMuteAliases())));
        }
        if (CommandsConfUtils.comBKick()) {
            registerCommand(new KickCommand(CommandsConfUtils.comBKickBase(), CommandsConfUtils.comBKickPerm(), stringListToArray(CommandsConfUtils.comBKickAliases())));
        }
        if (CommandsConfUtils.comBBan() && ConfigUtils.punBans()) {
            registerCommand(new BanCommand(CommandsConfUtils.comBBanBase(), CommandsConfUtils.comBBanPerm(), stringListToArray(CommandsConfUtils.comBBanAliases())));
        }
        if (CommandsConfUtils.comBIPBan() && ConfigUtils.punIPBans()) {
            registerCommand(new IPBanCommand(CommandsConfUtils.comBIPBanBase(), CommandsConfUtils.comBIPBanPerm(), stringListToArray(CommandsConfUtils.comBIPBanAliases())));
        }

        // Utils.
        // // Other.
        registerCommand(new ReloadCommand(CommandsConfUtils.comBReloadBase(), CommandsConfUtils.comBReloadPerm(), stringListToArray(CommandsConfUtils.comBReloadAliases())));
        if (CommandsConfUtils.comBPing()) {
            registerCommand(new PingCommand(CommandsConfUtils.comBPingBase(), CommandsConfUtils.comBPingPerm(), stringListToArray(CommandsConfUtils.comBPingAliases())));
        }
        if (CommandsConfUtils.comBPlugins()) {
            registerCommand(new PluginsCommand(CommandsConfUtils.comBPluginsBase(), CommandsConfUtils.comBPluginsPerm(), stringListToArray(CommandsConfUtils.comBPluginsAliases())));
        }
        if (CommandsConfUtils.comBSPing()) {
            registerCommand(new JDAPingerCommand(CommandsConfUtils.comBSPingBase(), CommandsConfUtils.comBSPingPerm(), stringListToArray(CommandsConfUtils.comBSPingAliases())));
        }
        if (CommandsConfUtils.comBInfo()) {
            registerCommand(new InfoCommand(CommandsConfUtils.comBInfoBase(), CommandsConfUtils.comBInfoPerm(), stringListToArray(CommandsConfUtils.comBInfoAliases())));
        }
        if (ConfigUtils.onCloseHackEnd()) {
            try {
                PluginManager pm = StreamLine.getInstance().getProxy().getPluginManager();

                List<Map.Entry<String, Command>> commands = new ArrayList<>(pm.getCommands());

                List<Command> unregCommands = new ArrayList<>();

                for (Map.Entry<String, Command> commandEntry : commands) {
                    if (commandEntry.getValue().getName().equals("end")) unregCommands.add(commandEntry.getValue());
                }

                for (Command command : unregCommands) {
                    pm.unregisterCommand(command);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (CommandsConfUtils.comBEnd()) {
            registerCommand(new EndCommand(CommandsConfUtils.comBEndBase(), CommandsConfUtils.comBEndPerm(), stringListToArray(CommandsConfUtils.comBEndAliases())));
        }
        // // Events.
        if (CommandsConfUtils.comBEReload()) {
            registerCommand(new EventReloadCommand(CommandsConfUtils.comBEReloadBase(), CommandsConfUtils.comBEReloadPerm(), stringListToArray(CommandsConfUtils.comBEReloadAliases())));
        }
        // // Scripts.
        if (CommandsConfUtils.comBScript()) {
            registerCommand(new ScriptCommand(CommandsConfUtils.comBScriptBase(), CommandsConfUtils.comBScriptPerm(), stringListToArray(CommandsConfUtils.comBScriptAliases())));
        }
        if (CommandsConfUtils.comBScriptRe()) {
            registerCommand(new ScriptReloadCommand(CommandsConfUtils.comBScriptReBase(), CommandsConfUtils.comBScriptRePerm(), stringListToArray(CommandsConfUtils.comBScriptReAliases())));
        }

        // All players.
        // // Reports.
        if (CommandsConfUtils.comBReport()) {
            registerCommand(new ReportCommand(CommandsConfUtils.comBReportBase(), CommandsConfUtils.comBReportPerm(), stringListToArray(CommandsConfUtils.comBReportAliases())));
        }
        // // Messaging.
        if (CommandsConfUtils.comBIgnore()) {
            registerCommand(new IgnoreCommand(CommandsConfUtils.comBIgnoreBase(), CommandsConfUtils.comBIgnorePerm(), stringListToArray(CommandsConfUtils.comBIgnoreAliases())));
        }
        if (CommandsConfUtils.comBMessage()) {
            registerCommand(new MessageCommand(CommandsConfUtils.comBMessageBase(), CommandsConfUtils.comBMessagePerm(), stringListToArray(CommandsConfUtils.comBMessageAliases())));
        }
        if (CommandsConfUtils.comBReply()) {
            registerCommand(new ReplyCommand(CommandsConfUtils.comBReplyBase(), CommandsConfUtils.comBReplyPerm(), stringListToArray(CommandsConfUtils.comBReplyAliases())));
        }
        if (CommandsConfUtils.comBFriend()) {
            registerCommand(new FriendCommand(CommandsConfUtils.comBFriendBase(), CommandsConfUtils.comBFriendPerm(), stringListToArray(CommandsConfUtils.comBFriendAliases())));
        }
        if (CommandsConfUtils.comBChatLevel()) {
            registerCommand(new ChatChannelCommand(CommandsConfUtils.comBChatLevelBase(), CommandsConfUtils.comBChatLevelPerm(), stringListToArray(CommandsConfUtils.comBChatLevelAliases())));
        }
        if (CommandsConfUtils.comBVerify()) {
            registerCommand(new BVerifyCommand(CommandsConfUtils.comBVerifyBase(), CommandsConfUtils.comBVerifyPerm(), stringListToArray(CommandsConfUtils.comBVerifyAliases())));
        }
        if (CommandsConfUtils.comBVoice()) {
            registerCommand(new VoiceCommand(CommandsConfUtils.comBVoiceBase(), CommandsConfUtils.comBVoicePerm(), stringListToArray(CommandsConfUtils.comBVoiceAliases())));
        }
        if (CommandsConfUtils.comBBroadcast()) {
            registerCommand(new BroadcastCommand(CommandsConfUtils.comBBroadcastBase(), CommandsConfUtils.comBBroadcastPerm(), stringListToArray(CommandsConfUtils.comBBroadcastAliases())));
        }
        if (CommandsConfUtils.comBBypass()) {
            registerCommand(new BypassPCCommand(CommandsConfUtils.comBBypassBase(), CommandsConfUtils.comBBypassPerm(), stringListToArray(CommandsConfUtils.comBBypassAliases())));
        }

        // Servers.
        if (CommandsConfUtils.comBLobby()) {
            registerCommand(new GoToServerLobbyCommand(CommandsConfUtils.comBLobbyBase(), CommandsConfUtils.comBLobbyPerm(), stringListToArray(CommandsConfUtils.comBLobbyAliases())));
        }
//        if (CommandsConfUtils.comBFabric()) {
//            registerCommand(new GoToServerVanillaCommand(CommandsConfUtils.comBFabricPerm()));
//        }

        // Parties / Guilds / Stats.
        // // Stats.
        if (CommandsConfUtils.comBGetStats()) {
            registerCommand(new GetStatsCommand(CommandsConfUtils.comBGetStatsBase(), CommandsConfUtils.comBGetStatsPerm(), stringListToArray(CommandsConfUtils.comBGetStatsAliases())));
        }
        if (CommandsConfUtils.comBStats()) {
            registerCommand(new StatsCommand(CommandsConfUtils.comBStatsBase(), CommandsConfUtils.comBStatsPerm(), stringListToArray(CommandsConfUtils.comBStatsAliases())));
        }
        if (CommandsConfUtils.comBBTag()) {
            registerCommand(new BTagCommand(CommandsConfUtils.comBBTagBase(), CommandsConfUtils.comBBTagPerm(), stringListToArray(CommandsConfUtils.comBBTagAliases())));
        }
        if (CommandsConfUtils.comBPoints()) {
            registerCommand(new NetworkPointsCommand(CommandsConfUtils.comBPointsBase(), CommandsConfUtils.comBPointsPerm(), stringListToArray(CommandsConfUtils.comBPointsAliases())));
        }
        // // Parties.
        if (CommandsConfUtils.comBParties()) {
            registerCommand(new PartiesCommand(CommandsConfUtils.comBPartiesBase(), CommandsConfUtils.comBPartiesPerm(), stringListToArray(CommandsConfUtils.comBPartiesAliases())));
        }
        if (CommandsConfUtils.comBParty()) {
            registerCommand(new PartyCommand(CommandsConfUtils.comBPartyBase(), CommandsConfUtils.comBParPerm(), stringListToArray(CommandsConfUtils.comBParMainAliases())));
        }
        if (CommandsConfUtils.comBParQuick()) {
            registerCommand(new PCQuickCommand("pc", CommandsConfUtils.comBParPerm(), stringListToArray(Arrays.asList("pch", "pchat"))));
        }
        // // Guilds.
        if (CommandsConfUtils.comBGuilds()) {
            registerCommand(new GuildsCommand(CommandsConfUtils.comBGuildsBase(), CommandsConfUtils.comBGuildsPerm(), stringListToArray(CommandsConfUtils.comBGuildsAliases())));
        }
        if (CommandsConfUtils.comBGuild()) {
            registerCommand(new GuildCommand(CommandsConfUtils.comBGuildBase(), CommandsConfUtils.comBGuildPerm(), stringListToArray(CommandsConfUtils.comBGuildMainAliases())));
        }
        if (CommandsConfUtils.comBGuildQuick()) {
            registerCommand(new GCQuickCommand("gc", CommandsConfUtils.comBGuildPerm(), stringListToArray(Arrays.asList("gch", "gchat"))));
        }

        plugin.getLogger().info("Loaded " + commandsAmount + " command(s) into memory...!");
    }

    public static String[] stringListToArray(List<String> aliases){
        String[] a = new String[aliases.size()];

        int i = 0;
        for (String alias : aliases){
            a[i] = alias;
            i++;
        }

        return a;
    }

    public static String[] stringListToArray(TreeSet<String> aliases){
        String[] a = new String[aliases.size()];

        int i = 0;
        for (String alias : aliases){
            a[i] = alias;
            i++;
        }

        return a;
    }

    public static void loadListeners(StreamLine plugin){
        listenerAmount = 0;

        registerListener(plugin, new ChatListener());
        registerListener(plugin, new JoinLeaveListener());
        registerListener(plugin, new ProxyPingListener());
        registerListener(plugin, new PluginMessagingListener());
        if (StreamLine.voteHolder.isPresent()) {
            PluginUtils.registerListener(plugin, new BasicVoteListener());
        }

        plugin.getLogger().info("Loaded " + listenerAmount + " listener(s) into memory...!");
    }

    public static int getCeilingInt(Set<Integer> ints){
        int value = 0;

        for (Integer i : ints) {
            if (i >= value) value = i;
        }

        return value;
    }

    public static boolean checkEqualsStrings(String toCheck, String... checks) {
        for (String check : checks) {
            if (toCheck.equals(check)) return true;
        }

        return false;
    }

    public static boolean isFreshInstall() {
        return StreamLine.constantsConfig.streamlineConstants.isFresh;
    }

    public static Map.Entry<Integer, String> findHighestNumberWithBasePermission(SavablePlayer player, String basePermission) {
        String permission = "";

        TreeMap<Integer, String> hasPerm = new TreeMap<>();

        hasPerm.put(1, basePermission + 1);

        for (int i = 2; i <= 100; i ++){
            permission = basePermission + i;
            if (player.hasPermission(permission)) hasPerm.put(i, permission);
        }

        return hasPerm.lastEntry();
    }

    public static Map.Entry<Integer, String> findHighestNumberWithBasePermission(ProxiedPlayer player, String basePermission) {
        String permission = "";

        TreeMap<Integer, String> hasPerm = new TreeMap<>();

        hasPerm.put(1, basePermission + 1);

        for (int i = 2; i <= 100; i ++){
            permission = basePermission + i;
            if (player.hasPermission(permission)) hasPerm.put(i, permission);
        }

        return hasPerm.lastEntry();
    }

    public static Map.Entry<Integer, String> findHighestNumberWithBasePermissionOrNull(ProxiedPlayer player, String basePermission) {
        String permission = "";

        TreeMap<Integer, String> hasPerm = new TreeMap<>();

        for (int i = 1; i <= 1000; i ++){
            permission = basePermission + i;
            if (player.hasPermission(permission)) hasPerm.put(i, permission);
        }

        if (hasPerm.isEmpty()) {
            return null;
        }

        return hasPerm.lastEntry();
    }

    public static int findHighestNumberWithBasePermissionAsInt(ProxiedPlayer player, String basePermission, int defaultIfNull, int max) {
        String permission = "";

        TreeMap<Integer, String> hasPerm = new TreeMap<>();

        for (int i = 1; i <= max; i ++){
            permission = basePermission + i;
            if (player.hasPermission(permission)) hasPerm.put(i, permission);
        }

        if (hasPerm.isEmpty()) {
            return defaultIfNull;
        }

        return hasPerm.lastKey();
    }

    public static List<String> getSyncablesAsStrings() {
        List<String> strings = new ArrayList<>();

        for (Syncable syncable : StreamLine.msbConfig.loadedSyncables) {
            strings.add(syncable.identifier);
        }

        return strings;
    }

    public static Syncable getSyncableByIdentifier(String identifier) {
        for (Syncable syncable : StreamLine.msbConfig.loadedSyncables) {
            if (identifier.equals(syncable.identifier)) return syncable;
        }

        return null;
    }

    public static List<String> getQueriesAsStrings() {
        List<String> strings = new ArrayList<>();

        for (CustomSQLInfo sqlInfo : StreamLine.msbConfig.loadedQueries) {
            strings.add(sqlInfo.identifier);
        }

        return strings;
    }

    public static CustomSQLInfo getQueryByIdentifier(String identifier) {
        for (CustomSQLInfo sqlInfo : StreamLine.msbConfig.loadedQueries) {
            if (identifier.equals(sqlInfo.identifier)) return sqlInfo;
        }

        return null;
    }

    public static List<String> getExecutionsAsStrings() {
        List<String> strings = new ArrayList<>();

        for (CustomSQLInfo sqlInfo : StreamLine.msbConfig.loadedExecutions) {
            strings.add(sqlInfo.identifier);
        }

        return strings;
    }

    public static CustomSQLInfo getExecutionByIdentifier(String identifier) {
        for (CustomSQLInfo sqlInfo : StreamLine.msbConfig.loadedExecutions) {
            if (identifier.equals(sqlInfo.identifier)) return sqlInfo;
        }

        return null;
    }

    public static SavedQueries getSavedQueryByPlayer(String playerUUID) {
        for (SavedQueries q : StreamLine.msbConfig.loadedSavedQueries) {
            if (q.playerUUID.equals(playerUUID)) return q;
        }

        return null;
    }

    public static String getQueryResult(String playerUUID, String identifier) {
        StreamLine.msbConfig.reloadSavedQueries();
        SavedQueries q = getSavedQueryByPlayer(playerUUID);
        if (q == null) return null;
        return q.getResult(identifier);
    }

    public static SavedQueries putQueryResult(String playerUUID, String identifer, String result) {
        SavedQueries q = getSavedQueryByPlayer(playerUUID);
        if (q == null) {
            q = createNewSavedQueries(playerUUID);
        }
        q = q.append(identifer, StreamLine.msbConfig.getResyncSeconds(), result);
        StreamLine.msbConfig.addSavedQueries(q);
        StreamLine.msbConfig.saveQueriedResult(q);
        return q;
    }

    public static SavedQueries createNewSavedQueries(String playerUUID) {
        return StreamLine.msbConfig.addSavedQueries(new SavedQueries(playerUUID));
    }

    public static void tickTillExpiry(SavedQueries q) {
        try {
            for (String identifier : q.results.keySet()) {
                int tillExpiry = q.getTillExpiry(identifier);

                if (tillExpiry > 0) {
                    q.updateTillExpiry(identifier, tillExpiry - 1);
                }
            }
        } catch (Exception e) {
            // do nothing.
        }
    }

    public static List<String> getCommandAliases() {
        List<String> strings = new ArrayList<>();

        for (Map.Entry<String, Command> entry : StreamLine.getInstance().getProxy().getPluginManager().getCommands()) {
            strings.add(entry.getKey());
        }

        return strings;
    }
}
