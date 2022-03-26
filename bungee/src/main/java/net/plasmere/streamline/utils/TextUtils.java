package net.plasmere.streamline.utils;

import net.dv8tion.jda.api.entities.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.objects.configs.obj.ConfigSection;
import net.plasmere.streamline.objects.lists.SingleSet;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.objects.savable.users.SavableUser;
import net.plasmere.streamline.placeholder.PlaceholderUtils;
import org.apache.commons.collections4.list.TreeList;

import java.awt.*;
import java.time.Duration;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextUtils {
    public static Title codedTitle(String main, String sub, int fadeIn, int stay, int fadeOut) {
        return StreamLine.getInstance().getProxy().createTitle().title(codedText(main)).subTitle(codedText(sub)).fadeIn(fadeIn).stay(stay).fadeOut(fadeOut);
    }

    public static BaseComponent getCodedTextFromList(List<String> strings) {
        BaseComponent textComponent = codedText(strings.get(0));

        for (int i = 1; i < strings.size(); i ++) {
            textComponent.addExtra(codedText("\n" + strings.get(i)));
        }

        return textComponent;
    }

    public static List<String> getCodedPlayerStringListBungee(List<String> strings, CommandSender player) {
        List<String> toReturn = new ArrayList<>();

        for (String string : strings) {
            toReturn.add(TextUtils.replaceAllPlayerBungee(string, player));
        }

        return toReturn;
    }

    public static String removeExtraDot(String string){
        String s = string.replace("..", ".");

        if (s.endsWith(".")) {
            s = s.substring(0, s.lastIndexOf('.'));
        }

        return s;
    }

    public static String replaceArgs(String from, String... args) {
        String to = from;

        int i = 1;
        for (String arg : args) {
            to = to.replace("%arg" + i + "%", arg);
            i ++;
        }

        return to;
    }

    public static String resize(String text, int digits) {
        try {
            digits = getDigits(digits, text.length());
            return text.substring(0, digits);
        } catch (Exception e) {
            return text;
        }
    }

    public static String truncate(String text, int digits) {
        if (! text.contains(".")) return text;

        try {
            digits = getDigits(text.indexOf(".") + digits + 1, text.length());
            return text.substring(0, digits);
        } catch (Exception e) {
            return text;
        }
    }

    public static int getDigits(int start, int otherSize){
        if (start <= otherSize) {
            return start;
        } else {
            return otherSize;
        }
    }

//    public static TextComponent hexedText(String text) {
//        text = codedString(text);
//
//        try {
//            //String ntext = text.replace(ConfigUtils.linkPre, "").replace(ConfigUtils.linkSuff, "");
//
//            Pattern pattern = Pattern.compile("([<][#][1-9a-f][1-9a-f][1-9a-f][1-9a-f][1-9a-f][1-9a-f][>])+", Pattern.CASE_INSENSITIVE);
//            Matcher matcher = pattern.matcher(stripColor(text));
//            String found = "";
//
//            String textLeft = text;
//
//            TextComponent tc = new TextComponent();
//
//            int i = 0;
//            boolean find = false;
//            TreeMap<Integer, String> founds = new TreeMap<>();
//
//            while (matcher.find()) {
//                find = true;
//                found = matcher.group(0);
//
//                founds.put(i, found);
//
//                i ++;
//            }
//            if (! find) return new TextComponent(text);
//
//            TreeMap<Integer, String> pieces = new TreeMap<>();
//            int iter = 0;
//            int from = 0;
//            for (Integer key : founds.keySet()) {
//                int at = text.indexOf(founds.get(key), from);
//                pieces.put(iter, text.substring(at));
//                from = at;
//                iter ++;
//            }
//
//            tc = new TextComponent(pieces.get(0));
//
//            for (Integer key : pieces.keySet()) {
//                if (key == 0) continue;
//
//                String p = pieces.get(key);
//                String f = p.substring(0, "<#123456>".length());
//
//                String colorHex = f.substring(1, f.indexOf('>'));
//                String after = p.substring(f.length());
//
////                tc.addExtra(new LiteralText(after).styled(style -> style.withColor(Integer.decode(colorHex))));
//                BaseComponent[] bc = new ComponentBuilder(after).color(ChatColor.of(Color.decode(colorHex))).create();
//
//                for (BaseComponent b : bc) {
//                    tc.addExtra(b);
//                }
//            }
//
//            return tc;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new TextComponent(text);
//        }
//    }

    public static TreeMap<Integer, String> comparedConfiguration(ConfigSection configuration){
        TreeMap<Integer, String> thing = new TreeMap<>();

        for (String key : configuration.getKeys()) {
            int it = 0;
            try {
                it = Integer.parseInt(key);
            } catch (Exception e) {
                continue; // Do nothing.
            }

            thing.put(it, configuration.s.getString(key));
        }

        return thing;
    }

    public static TreeSet<String> getCompletion(List<String> of, String param){
        return of.stream()
                .filter(completion -> completion.toLowerCase(Locale.ROOT).startsWith(param.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toCollection(TreeSet::new));
    }

    public static TreeSet<String> getCompletion(TreeSet<String> of, String param){
        return of.stream()
                .filter(completion -> completion.toLowerCase(Locale.ROOT).startsWith(param.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toCollection(TreeSet::new));
    }

    public static TreeSet<String> getCompletion(TreeList<String> of, String param){
        return of.stream()
                .filter(completion -> completion.toLowerCase(Locale.ROOT).startsWith(param.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toCollection(TreeSet::new));
    }

    public static String stripColor(String string){
        return ChatColor.stripColor(string)
                .replaceAll("([<][#][1-9a-f][1-9a-f][1-9a-f][1-9a-f][1-9a-f][1-9a-f][>])+", "")
                .replaceAll("[&][1-9a-f]", "");
    }

    public static TextComponent hexedText(String text) {
        text = codedString(text);

        try {
            //String ntext = text.replace(ConfigUtils.linkPre(), "").replace(ConfigUtils.linkSuff, "");

            Pattern pattern = Pattern.compile("([<][#][1-9a-f][1-9a-f][1-9a-f][1-9a-f][1-9a-f][1-9a-f][>])+", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(stripColor(text));
            String found = "";

            String textLeft = text;

            TextComponent tc = new TextComponent();

            int i = 0;
            boolean find = false;
            TreeMap<Integer, String> founds = new TreeMap<>();

            while (matcher.find()) {
                find = true;
                found = matcher.group(0);

                founds.put(i, found);

                i ++;
            }
            if (! find) return new TextComponent(text);

            TreeMap<Integer, String> pieces = new TreeMap<>();
            int iter = 0;
            int from = 0;
            for (Integer key : founds.keySet()) {
                int at = text.indexOf(founds.get(key), from);
                pieces.put(iter, text.substring(at));
                from = at;
                iter ++;
            }

            tc = new TextComponent(pieces.get(0));

            for (Integer key : pieces.keySet()) {
                if (key == 0) continue;

                String p = pieces.get(key);
                String f = p.substring(0, "<#123456>".length());

                String colorHex = f.substring(1, f.indexOf('>'));
                String after = p.substring(f.length());

//                tc.addExtra(new LiteralText(after).styled(style -> style.withColor(Integer.decode(colorHex))));
                BaseComponent[] bc = new ComponentBuilder(after).color(ChatColor.of(Color.decode(colorHex))).create();

                for (BaseComponent b : bc) {
                    tc.addExtra(b);
                }
            }

            return tc;
        } catch (Exception e) {
            e.printStackTrace();
            return new TextComponent(text);
        }
    }

    public static String[] argsMinus(String[] args, int... toRemove) {
        TreeMap<Integer, String> argsSet = new TreeMap<>();

        for (int i = 0; i < args.length; i++) {
            argsSet.put(i, args[i]);
        }

        for (int remove : toRemove) {
            argsSet.remove(remove);
        }

        return argsSet.values().toArray(new String[0]);
    }

    public static String argsToStringMinus(String[] args, int... toRemove){
        return normalize(argsMinus(args, toRemove));
    }

    public static String argsToString(String[] args){
        TreeMap<Integer, String> argsSet = new TreeMap<>();

        for (int i = 0; i < args.length; i++) {
            argsSet.put(i, args[i]);
        }

        return normalize(argsSet);
    }

    public static TextComponent codedText(String text) {
        text = ChatColor.translateAlternateColorCodes('&', newLined(text));

        try {
            //String ntext = text.replace(ConfigUtils.linkPre(), "").replace(ConfigUtils.linkSuff, "");

            Pattern pattern = Pattern.compile("(http|ftp|https)://([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(stripColor(text));
            String foundUrl = "";

            while (matcher.find()) {
                foundUrl = matcher.group(0);

                return makeLinked(text, foundUrl);
            }
        } catch (Exception e) {
            return hexedText(text);
        }
        return hexedText(text);
    }

    public static TextComponent clhText(String text, String hoverPrefix){
        text = ChatColor.translateAlternateColorCodes('&', newLined(text));

        try {
            //String ntext = text.replace(ConfigUtils.linkPre(), "").replace(ConfigUtils.linkSuff, "");

            Pattern pattern = Pattern.compile("(http|ftp|https)://([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(stripColor(text));
            String foundUrl = "";

            while (matcher.find()) {
                foundUrl = matcher.group(0);

                TextComponent tc = makeLinked(text, foundUrl);
                return makeHoverable(tc, hoverPrefix + foundUrl);
            }
        } catch (Exception e) {
            return hexedText(text);
        }
        return hexedText(text);
    }

    public static String formatted(String string) {
        String[] strings = string.split(" ");

        for (int i = 0; i < strings.length; i ++) {
            if (strings[i].toLowerCase(Locale.ROOT).startsWith("<to_upper>")) {
                strings[i] = strings[i].toUpperCase(Locale.ROOT).replace("<TO_UPPER>", "");
            }
            if (strings[i].toLowerCase(Locale.ROOT).startsWith("<to_lower>")) {
                strings[i] = strings[i].toLowerCase(Locale.ROOT).replace("<to_lower>", "");
            }
        }

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < strings.length; i ++) {
            if (i == strings.length - 1) {
                builder.append(strings[i]);
            } else {
                builder.append(strings[i]).append(" ");
            }
        }

        return builder.toString();
    }

    public static TextComponent makeLinked(String text, String url){
        TextComponent tc = hexedText(text);
        ClickEvent ce = new ClickEvent(ClickEvent.Action.OPEN_URL, url);
        tc.setClickEvent(ce);
        return tc;
    }

    public static TextComponent makeHoverable(String text, String hoverText){
        TextComponent tc = new TextComponent(text);
        HoverEvent he = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(codedString(hoverText)));
        tc.setHoverEvent(he);
        return tc;
    }

    public static TextComponent makeHoverable(TextComponent textComponent, String hoverText){
        HoverEvent he = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(codedString(hoverText)));
        textComponent.setHoverEvent(he);
        return textComponent;
    }

    public static TreeMap<Integer, ProxiedPlayer> getTaggedPlayersIndexed(String[] args, String serverName) {
        TreeMap<Integer, ProxiedPlayer> toIndex = new TreeMap<>();
        List<ProxiedPlayer> players = PlayerUtils.getServeredPPlayers(serverName);

        for (ProxiedPlayer player : players) {
            for (int i = 0; i < args.length; i ++) {
                if (player.getName().equals(args[i])) {
                    toIndex.put(i, player);
                }
            }
        }

        return toIndex;
    }

    public static SingleSet<String, List<ProxiedPlayer>> getMessageWithTags(ProxiedPlayer sender, String message, String format) {
        String[] args = message.split(" ");

        String chatColor = isolateChatColor(format);

        ServerInfo serverConnection = sender.getServer().getInfo();
        if (serverConnection == null) return new SingleSet<>("", new ArrayList<>());

        TreeMap<Integer, ProxiedPlayer> indexed = getTaggedPlayersIndexed(args, serverConnection.getName());

        for (Integer index : indexed.keySet()) {
            args[index] = StreamLine.serverConfig.getTagsPrefix() + args[index];
        }

        for (int i = 0; i < args.length; i ++) {
            args[i] = chatColor + args[i];
        }

        return new SingleSet<>(normalize(args), new ArrayList<>(indexed.values()));
    }

    public static String isolateChatColor(String format) {
        String[] strings = format.split(" ");

        for (String string : strings) {
            if (string.contains("%message%")) {
                String[] gotten = string.split("%message%");
                return gotten[0];
            }
        }

        return "";
    }

    public static String newLined(String text){
        try {
            return text.replace("%newline%", "\n").replace("%uniques%", String.valueOf(StreamLine.getInstance().getPlDir().listFiles().length));
        } catch (Exception e) {
            return text.replace("%newline%", "\n");
        }
    }

    public static boolean isCommand(String msg){
        return msg.startsWith("/");
    }

    public static String normalize(String[] splitMsg){
        int i = 0;
        StringBuilder text = new StringBuilder();

        for (String split : splitMsg){
            i++;
            if (split.equals("")) continue;

            if (i < splitMsg.length)
                text.append(split).append(" ");
            else
                text.append(split);
        }

        return text.toString();
    }

    public static String normalize(TreeSet<String> splitMsg) {
        int i = 0;
        StringBuilder text = new StringBuilder();

        for (String split : splitMsg){
            i++;
            if (split.equals("")) continue;

            if (i < splitMsg.size())
                text.append(split).append(" ");
            else
                text.append(split);
        }

        return text.toString();
    }

    public static String normalize(TreeMap<Integer, String> splitMsg) {
        int i = 0;
        StringBuilder text = new StringBuilder();

        for (Integer split : splitMsg.keySet()){
            i++;
            if (splitMsg.get(split).equals("")) continue;

            if (i < splitMsg.size())
                text.append(splitMsg.get(split)).append(" ");
            else
                text.append(splitMsg.get(split));
        }

        return text.toString();
    }

    public static String getMessageWithEmotes(ProxiedPlayer player, String input) {
        for (String emote : StreamLine.serverConfig.getEmotes()) {
            if (! player.hasPermission(StreamLine.serverConfig.getEmotePermission(emote))) continue;
            input = input.replace(emote, StreamLine.serverConfig.getEmote(emote));
        }

        return input;
    }

    public static boolean equalsAll(Object object, Object... toEqual){
        for (Object equal : toEqual) {
            if (! object.equals(equal)) return false;
        }

        return true;
    }

    public static boolean equalsAll(Object object, Collection<Object> toEqual){
        for (Object equal : toEqual) {
            if (! object.equals(equal)) return false;
        }

        return true;
    }

    public static boolean equalsAny(Object object, Collection<?> toEqual){
        for (Object equal : toEqual) {
            if (object.equals(equal)) return true;
        }

        return false;
    }

    public static String replaceBasicPlaceholders(String of, SavableUser user) {
        return PlaceholderUtils.parsePlaceholder(StreamLine.placeholderExpansion, user, of);
    }

    public static String replaceAllAnyIdentifierBungee(String of, SavableUser user, String identifier) {
        if (user == null) {
            return of;
        }

        of = of
                .replace("%" + identifier + "_uuid%", user.uuid)
                .replace("%" + identifier + "_server%", user.findServer())

                .replace("%" + identifier + "_absolute%", PlayerUtils.getAbsoluteDiscord(user))
                .replace("%" + identifier + "_normal%", PlayerUtils.getOffOnRegDiscord(user))
                .replace("%" + identifier + "_display%", PlayerUtils.getOffOnDisplayDiscord(user))
                .replace("%" + identifier + "_formatted%", PlayerUtils.getJustDisplayDiscord(user))

                .replace("%" + identifier + "_points%", String.valueOf(user.points))

                .replace("%" + identifier + "_prefix%", PlayerUtils.getLuckPermsPrefix(user.latestName, true))
                .replace("%" + identifier + "_suffix%", PlayerUtils.getLuckPermsSuffix(user.latestName, true))

                .replace("%" + identifier + "_guild_name%", PlayerUtils.getPlayerGuildNameDiscord(user))
                .replace("%" + identifier + "_guild_members%", PlayerUtils.getPlayerGuildMembers(user))
                .replace("%" + identifier + "_guild_leader_uuid%", PlayerUtils.getPlayerGuildLeaderUUID(user))
                .replace("%" + identifier + "_guild_leader_absolute%", PlayerUtils.getPlayerGuildLeaderAbsoluteDiscord(user))
                .replace("%" + identifier + "_guild_leader_formatted%", PlayerUtils.getPlayerGuildLeaderJustDisplayDiscord(user))
                .replace("%" + identifier + "_guild_leader_normal%", PlayerUtils.getPlayerGuildLeaderOffOnRegDiscord(user))
                .replace("%" + identifier + "_guild_leader_display%", PlayerUtils.getPlayerGuildLeaderOffOnDisplayDiscord(user))

                .replace("%" + identifier + "_level%", (user instanceof SavablePlayer ? String.valueOf(((SavablePlayer) user).level) : ""))
                .replace("%" + identifier + "_xp_current%", (user instanceof SavablePlayer ? String.valueOf(((SavablePlayer) user).currentXP) : ""))
                .replace("%" + identifier + "_xp_total%", (user instanceof SavablePlayer ? String.valueOf(((SavablePlayer) user).totalXP) : ""))
                .replace("%" + identifier + "_play_seconds%", (user instanceof SavablePlayer ? ((SavablePlayer) user).getPlaySecondsAsString() : ""))
                .replace("%" + identifier + "_play_minutes%", (user instanceof SavablePlayer ? ((SavablePlayer) user).getPlayMinutesAsString() : ""))
                .replace("%" + identifier + "_play_hours%", (user instanceof SavablePlayer ? ((SavablePlayer) user).getPlayHoursAsString() : ""))
                .replace("%" + identifier + "_play_days%", (user instanceof SavablePlayer ? ((SavablePlayer) user).getPlayDaysAsString() : ""))
                .replace("%" + identifier + "_votes%", (user instanceof SavablePlayer && ConfigUtils.moduleBRanksEnabled() ? String.valueOf(PlayerUtils.getVotesForPlayer((SavablePlayer) user)) : ""))
        ;

        if (ConfigUtils.mysqlbridgerEnabled()) {
            of  = StreamLine.msbConfig.parsePlaceholder(of, user);
        }

        return of;
    }

    public static String replaceAllAnyIdentifierDiscord(String of, SavableUser user, String identifier) {
        if (user == null) return of;

        if (ConfigUtils.mysqlbridgerEnabled()) {
            of  = StreamLine.msbConfig.parsePlaceholder(of, user);
        }

        return of
                .replace("%player_uuid%", user.uuid)

                .replace("%player_absolute%", PlayerUtils.getAbsoluteBungee(user))
                .replace("%player_normal%", PlayerUtils.getOffOnRegBungee(user))
                .replace("%player_display%", PlayerUtils.getOffOnDisplayBungee(user))
                .replace("%player_formatted%", PlayerUtils.getJustDisplayBungee(user))

                .replace("%player_points%", String.valueOf(user.points))

                .replace("%player_prefix%", PlayerUtils.getLuckPermsPrefix(user.latestName, true))
                .replace("%player_suffix%", PlayerUtils.getLuckPermsSuffix(user.latestName, true))

                .replace("%player_guild_name%", PlayerUtils.getPlayerGuildName(user))
                .replace("%player_guild_members%", PlayerUtils.getPlayerGuildMembers(user))
                .replace("%player_guild_leader_uuid%", PlayerUtils.getPlayerGuildLeaderUUID(user))
                .replace("%player_guild_leader_absolute%", PlayerUtils.getPlayerGuildLeaderAbsoluteBungee(user))
                .replace("%player_guild_leader_formatted%", PlayerUtils.getPlayerGuildLeaderJustDisplayBungee(user))
                .replace("%player_guild_leader_normal%", PlayerUtils.getPlayerGuildLeaderOffOnRegBungee(user))
                .replace("%player_guild_leader_display%", PlayerUtils.getPlayerGuildLeaderOffOnDisplayBungee(user))

                .replace("%player_level%", (user instanceof SavablePlayer ? String.valueOf(((SavablePlayer) user).level) : ""))
                .replace("%player_xp_current%", (user instanceof SavablePlayer ? String.valueOf(((SavablePlayer) user).currentXP) : ""))
                .replace("%player_xp_total%", (user instanceof SavablePlayer ? String.valueOf(((SavablePlayer) user).totalXP) : ""))
                .replace("%player_play_seconds%", (user instanceof SavablePlayer ? ((SavablePlayer) user).getPlaySecondsAsString() : ""))
                .replace("%player_play_minutes%", (user instanceof SavablePlayer ? ((SavablePlayer) user).getPlayMinutesAsString() : ""))
                .replace("%player_play_hours%", (user instanceof SavablePlayer ? ((SavablePlayer) user).getPlayHoursAsString() : ""))
                .replace("%player_play_days%", (user instanceof SavablePlayer ? ((SavablePlayer) user).getPlayDaysAsString() : ""))
                .replace("%player_votes%", (user instanceof SavablePlayer && ConfigUtils.moduleBRanksEnabled() ? String.valueOf(PlayerUtils.getVotesForPlayer((SavablePlayer) user)) : ""))
                ;
    }

    public static String replaceAllPlayerBungee(String of, SavableUser user) {
        of = replaceBasicPlaceholders(of, user);
        return replaceAllAnyIdentifierBungee(of, user, "player");
    }

    public static String replaceAllPlayerBungee(String of, String uuid) {
        if (! uuid.contains("-") && ! uuid.equals("%")) return replaceAllPlayerBungeeFromDiscord(of, uuid);

        return replaceAllPlayerBungee(of, PlayerUtils.getOrGetSavableUser(uuid));
    }

    public static String replaceAllPlayerBungee(String of, CommandSender sender) {
        return replaceAllPlayerBungee(of, PlayerUtils.getOrGetSavableUser(sender));
    }

    public static String replaceAllPlayerBungeeFromDiscord(String of, String discordID) {
        long dID = 0L;

        try {
            dID = Long.parseLong(discordID);
        } catch (Exception e) {
            e.printStackTrace();
            return of;
        }

        if (StreamLine.discordData.isVerified(dID)) {
            SavableUser user = PlayerUtils.getOrGetSavableUser(StreamLine.discordData.getUUIDOfVerified(dID));
            of = replaceBasicPlaceholders(of, user);
            return replaceAllAnyIdentifierBungee(of, user, "player");
        } else {
            User user = StreamLine.getJda().getUserById(dID);

            if (user == null) {
                if (ConfigUtils.debug()) MessagingUtils.logInfo("Discord User of ID " + dID + " returned null...");
                return of;
            }

            return of
                    .replace("%player_absolute%", user.getName())
                    .replace("%player_normal%", user.getName())
                    .replace("%player_display%", user.getName())
                    .replace("%player_formatted%", user.getName())
                    ;
        }
    }

    public static String replaceAllUserBungee(String of, SavableUser user) {
        of = replaceBasicPlaceholders(of, user);
        return replaceAllAnyIdentifierBungee(of, user, "user");
    }

    public static String replaceAllUserBungee(String of, String uuid) {
        if (! uuid.contains("-") && ! uuid.equals("%")) return replaceAllUserBungeeFromDiscord(of, uuid);

        return replaceAllUserBungee(of, PlayerUtils.getOrGetSavableUser(uuid));
    }

    public static String replaceAllUserBungee(String of, CommandSender sender) {
        return replaceAllUserBungee(of, PlayerUtils.getOrGetSavableUser(sender));
    }

    public static String replaceAllUserBungeeFromDiscord(String of, String discordID) {
        long dID = 0L;

        try {
            dID = Long.parseLong(discordID);
        } catch (Exception e) {
            e.printStackTrace();
            return of;
        }

        if (StreamLine.discordData.isVerified(dID)) {
            SavableUser user = PlayerUtils.getOrGetSavableUser(StreamLine.discordData.getUUIDOfVerified(dID));

            of = replaceBasicPlaceholders(of, user);
            return replaceAllAnyIdentifierBungee(of, user, "user");
        } else {
            User user = StreamLine.getJda().getUserById(dID);

            if (user == null) {
                if (ConfigUtils.debug()) MessagingUtils.logInfo("Discord User of ID " + dID + " returned null...");
                return of;
            }

            return of
                    .replace("%user_absolute%", user.getName())
                    .replace("%user_normal%", user.getName())
                    .replace("%user_display%", user.getName())
                    .replace("%user_formatted%", user.getName())
                    ;
        }
    }

    public static String replaceAllSenderBungee(String of, SavableUser user) {
        of = replaceBasicPlaceholders(of, user);
        return replaceAllAnyIdentifierBungee(of, user, "sender");
    }

    public static String replaceAllSenderBungee(String of, String uuid) {
        if (! uuid.contains("-") && ! uuid.equals("%")) return replaceAllSenderBungeeFromDiscord(of, uuid);

        return replaceAllSenderBungee(of, PlayerUtils.getOrGetSavableUser(uuid));
    }

    public static String replaceAllSenderBungee(String of, CommandSender sender) {
        return replaceAllSenderBungee(of, PlayerUtils.getOrGetSavableUser(sender));
    }

    public static String replaceAllSenderBungeeFromDiscord(String of, String discordID) {
        long dID = 0L;

        try {
            dID = Long.parseLong(discordID);
        } catch (Exception e) {
//            e.printStackTrace();
            return of;
        }

        User u = StreamLine.getJda().getUserById(dID);
        if (u == null) return of;

        if (StreamLine.discordData.isVerified(dID)) {
            SavableUser user = PlayerUtils.getOrGetSavableUser(StreamLine.discordData.getUUIDOfVerified(dID));

            of = replaceBasicPlaceholders(of, user);
            return replaceAllAnyIdentifierBungee(of, user, "sender");
        } else {
            User user = StreamLine.getJda().getUserById(dID);

            if (user == null) {
                if (ConfigUtils.debug()) MessagingUtils.logInfo("Discord User of ID " + dID + " returned null...");
                return of;
            }

            return of
                    .replace("%sender_absolute%", user.getName())
                    .replace("%sender_normal%", user.getName())
                    .replace("%sender_display%", user.getName())
                    .replace("%sender_formatted%", user.getName())
                    ;
        }
    }

    public static String replaceAllPlayerDiscord(String of, SavableUser user) {
        of = replaceBasicPlaceholders(of, user);
        return replaceAllAnyIdentifierDiscord(of, user, "player");
    }

    public static String replaceAllPlayerDiscord(String of, String uuid) {
        return replaceAllPlayerDiscord(of, PlayerUtils.getOrGetSavableUser(uuid));
    }

    public static String replaceAllPlayerDiscord(String of, CommandSender sender) {
        return replaceAllPlayerDiscord(of, PlayerUtils.getOrGetSavableUser(sender));
    }

    public static String replaceAllUserDiscord(String of, SavableUser user) {

        of = replaceBasicPlaceholders(of, user);
        return replaceAllAnyIdentifierDiscord(of, user, "user");
    }

    public static String replaceAllUserDiscord(String of, String uuid) {
        return replaceAllUserDiscord(of, PlayerUtils.getOrGetSavableUser(uuid));
    }

    public static String replaceAllUserDiscord(String of, CommandSender sender) {
        return replaceAllUserDiscord(of, PlayerUtils.getOrGetSavableUser(sender));
    }

    public static String replaceAllSenderDiscord(String of, SavableUser user) {
        of = replaceBasicPlaceholders(of, user);
        return replaceAllAnyIdentifierDiscord(of, user, "sender");
    }

    public static String replaceAllSenderDiscord(String of, String uuid) {
        return replaceAllSenderDiscord(of, PlayerUtils.getOrGetSavableUser(uuid));
    }

    public static String replaceAllSenderDiscord(String of, CommandSender sender) {
        return replaceAllSenderDiscord(of, PlayerUtils.getOrGetSavableUser(sender));
    }

    public static Collection<ServerInfo> getServers() {
        return StreamLine.getInstance().getProxy().getServers().values();
    }

    public static int replaceAllPlayerRanks(SavablePlayer player) {
        String of = ConfigUtils.moduleBRanksUses()
                .replace("%player_level%", String.valueOf(player.level))
                .replace("%player_xp_current%", String.valueOf(player.currentXP))
                .replace("%player_xp_total%", String.valueOf(player.totalXP))
                .replace("%player_play_seconds%", String.valueOf(player.playSeconds))
                .replace("%player_play_minutes%", String.valueOf(player.getPlayMinutes()))
                .replace("%player_play_hours%", String.valueOf(player.getPlayHours()))
                .replace("%player_play_days%", String.valueOf(player.getPlayDays()))
                .replace("%player_votes%", String.valueOf(PlayerUtils.getVotesForPlayer(player)))
                ;

        int toReturn = 0;

        try {
            toReturn = Integer.parseInt(of);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return toReturn;
    }

    public static boolean equalsAnyServer(String servername) {
        for (ServerInfo server : getServers()) {
            if (server.getName().equals(servername)) return true;
        }

        return false;
    }

    public static List<String> getStringListFromString(String string) {
        String[] strings = string.split(",");

        return List.of(strings);
    }

    public static boolean isNullOrLessThanEqualTo(Object[] thingArray, int lessThanOrEqualTo) {
        if (thingArray == null) return true;
        return thingArray.length <= lessThanOrEqualTo;
    }

    public static String codedString(String replace) {
        return ChatColor.translateAlternateColorCodes('&', replace);
    }
}
