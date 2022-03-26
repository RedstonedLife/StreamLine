package net.plasmere.streamline.objects.configs.obj;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.packet.PlayerListItem;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.PluginUtils;
import net.plasmere.streamline.utils.TextUtils;

import java.util.*;

public class TablistHandler {
    public static void tickPlayers() {
        for (ProxiedPlayer player : PlayerUtils.getOnlinePPlayers()) {
            tickPlayer(player);
        }
    }

    public static void tickPlayer(ProxiedPlayer player) {
        HashMap<ProxiedPlayer, TablistFormat> formats = getAllPlayerTablistFormats();

        TablistFormat format = formats.get(player);

        if (format == null) format = StreamLine.tablistConfig.getGeneralTablistFormat();

        player.setTabHeader(TextUtils.getCodedTextFromList(TextUtils.getCodedPlayerStringListBungee(format.header, player)), TextUtils.getCodedTextFromList(TextUtils.getCodedPlayerStringListBungee(format.footer, player)));
        updateTablistEntriesFor(player);
    }

    public static TablistFormat getPlayerTablistFormat(ProxiedPlayer player) {
        StreamLine.tablistConfig.reloadLoadedTablistFormats();

        int highestPerm = PluginUtils.findHighestNumberWithBasePermissionAsInt(player, StreamLine.tablistConfig.getBasePermission(), 0, StreamLine.tablistConfig.loadedTablistFormats.size() - 1);

        TablistFormat format = StreamLine.tablistConfig.loadedTablistFormats.get(highestPerm);

        return format == null ? StreamLine.tablistConfig.getGeneralTablistFormat() : format;
    }

    public static HashMap<ProxiedPlayer, TablistFormat> getAllPlayerTablistFormats() {
        HashMap<ProxiedPlayer, TablistFormat> formats = new HashMap<>();

        for (ProxiedPlayer player : PlayerUtils.getOnlinePPlayers()) {
            formats.put(player, getPlayerTablistFormat(player));
        }

        return formats;
    }

    public static void updateTablistEntriesFor(ProxiedPlayer player) {
//        PlayerListItem list = player.();
//        Collection<TabListEntry> entries = list.getEntries();
//        Collection<TabListEntry> newEntries = new ArrayList<>();
//        if (StreamLine.tablistConfig.isGlobal()) {
//            for (Player p : PlayerUtils.getOnlinePPlayers()) {
//                TablistFormat format = getPlayerTablistFormat(p);
//
//                if (format == null) {
//                    format = StreamLine.tablistConfig.getGeneralTablistFormat();
//                }
//
//                newEntries.add(TabListEntry.builder()
//                        .tabList(list)
//                        .profile(p.getGameProfile())
//                        .displayName(TextUtils.codedText(TextUtils.replaceAllPlayerBungee(format.playerName, p)))
//                        .build());
//            }
//        } else {
//            String serverName = "";
//            try {
//                serverName = player.getCurrentServer().get().getServerInfo().getName();
//            } catch (Exception e) {
//                return;
//            }
//            for (Player p : PlayerUtils.getServeredPPlayers(serverName)) {
//                TablistFormat format = getPlayerTablistFormat(p);
//
//                if (format == null) {
//                    format = StreamLine.tablistConfig.getGeneralTablistFormat();
//                }
//
//                newEntries.add(TabListEntry.builder()
//                        .tabList(list)
//                        .profile(p.getGameProfile())
//                        .displayName(TextUtils.codedText(TextUtils.replaceAllPlayerBungee(format.playerName, p)))
//                        .build());
//            }
//        }
//
//        if (entriesSame(entries, newEntries)) {
//            return;
//        }
//
//        for (TabListEntry entry : player.getTabList().getEntries()) {
//            list.removeEntry(entry.getProfile().getId());
//        }
//
//        for (TabListEntry entry : newEntries) {
//            list.addEntry(entry);
//        }
    }

//    public static boolean entriesSame(Collection<PlayerListItem> entries, Collection<PlayerListItem> newEntries) {
//        if (entries.size() == newEntries.size()) {
//            for (PlayerListItem entry : newEntries) {
//                boolean found = false;
//
//                for (PlayerListItem e : entries) {
//                    if (entry.equals(e)) {
//                        found = true;
//                        break;
//                    }
//                }
//
//                if (! found) {
//                    return false;
//                }
//            }
//        }
//
//        return false;
//    }
//
//    public static void insertIntoTabListCleanly(PlayerListItem list, TabListEntry entry, List<UUID> toKeep) {
//        UUID inUUID = entry.getProfile().getId();
//        List<UUID> containedUUIDs = new ArrayList<>();
//        Map<UUID, TabListEntry> cache = new HashMap<>();
//        for (TabListEntry current : list.getEntries()) {
//            containedUUIDs.add(current.getProfile().getId());
//            cache.put(current.getProfile().getId(), current);
//        }
//        if (!containedUUIDs.contains(inUUID)) {
//            list.addEntry(entry);
//            toKeep.add(inUUID);
//            return;
//        } else {
//            TabListEntry currentEntr = cache.get(inUUID);
//            if (!currentEntr.getDisplayNameComponent().equals(entry.getDisplayNameComponent())) {
//                list.removeEntry(inUUID);
//                list.addEntry(entry);
//                toKeep.add(inUUID);
//            } else
//                toKeep.add(inUUID);
//        }
//    }
}