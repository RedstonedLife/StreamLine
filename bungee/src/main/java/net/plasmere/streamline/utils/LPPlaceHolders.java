package net.plasmere.streamline.utils;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.services.LuckPermsService;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Supported LuckPerms PlaceHolders (Taken from https://luckperms.net/wiki/Placeholders):
 *
 * %luckperms_prefix%		            Returns the player's prefix
 * %luckperms_suffix%		            Returns the players suffix
 * %luckperms_meta_<meta key>%	        Returns a single value for the given meta key (Player's Contexts)
 * %luckperms_meta_all_<meta key>%	    Returns all assigned values for the given meta key (Global)
 */
public class LPPlaceHolders {
    private static final Pattern pattern = Pattern.compile("%luckperms_[^\\s%]+%");

    /**
     * Parses a string and replaces placeholders with their assigned values
     * 
     * @param player Velocity Player
     * @param string String to be parsed
     * @return Parsed string
     */
    public static String parse(ProxiedPlayer player, String string) {
        String result = string;

        if(StreamLine.lpHolder.enabled) {
            StringBuilder builder = new StringBuilder();
            Matcher matcher = pattern.matcher(string);
            UUID uuid = player.getUniqueId();

            while (matcher.find()) {
                matcher.appendReplacement(builder, parseArguments(uuid, matcher.group().split("_")));
            }
            matcher.appendTail(builder);
            result = builder.toString();
        }

        return result;
    }

    /**
     * Internal Placeholder Assignment
     *
     * @param uuid Player's uuid
     * @param args Whole string to be replaced, split by '_' into an array
     *             i.e. %luckperms_prefix% would turn into
     *             { '%luckperms', 'prefix%' }
     * @return The placeholder's value, to be filled by parse()
     */
    private static String parseArguments(UUID uuid, String[] args) {
        String result;

        //%luckperms_meta_all_bypass%
        //     1      2    3     4    (length)
        //     0      1    2     3    (index)
        if(args.length > 1) {
            switch(args[1].replace("%", "")) {
                case "prefix" -> result = LuckPermsService.getPrefix(uuid).join();
                case "suffix" -> result = LuckPermsService.getSuffix(uuid).join();
                case "meta" -> {
                    if(args.length > 2) {
                        if(args[2].equals("all")) {
                            if(args.length == 4) {
                                result = LuckPermsService.getMeta(uuid, args[3].replace("%", "")).join();
                            } else {
                                result = invalidString(args);
                            }
                        } else {
                            result = LuckPermsService.getMetaContext(uuid, args[2].replace("%", "")).join();
                        }
                    } else {
                        result = invalidString(args);
                    }
                }
                default -> result = invalidString(args);
            }
        } else {
            result = invalidString(args);
        }

        return result;
    }

    /**
     * Turns the list back into a string using the '_' delimiter
     * i.e. { '%luckperms', 'invalidPlaceholder%' } would turn into
     * %luckperms_invalidPlaceholder%
     *
     * @param args The args of parseArguments()
     * @return The invalid placeholder
     */
    private static String invalidString(String[] args) {
        return String.join("_", args);
    }
}
