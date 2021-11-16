package net.plasmere.streamline.utils;

import com.velocitypowered.api.proxy.Player;
import net.plasmere.streamline.services.LuckPermsService;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LPPlaceHolders {
    private static final Pattern pattern = Pattern.compile("%luckperms_[^\\s%]+%");

    public static String parse(Player player, String string) {
        StringBuilder builder = new StringBuilder();
        Matcher matcher = pattern.matcher(string);
        UUID uuid = player.getUniqueId();

        while(matcher.find()) {
            matcher.appendReplacement(builder, parseArguments(uuid, matcher.group().split("_")));
        }
        matcher.appendTail(builder);

        return builder.toString();
    }

    private static String parseArguments(UUID uuid, String[] args) {
        String result = "";

        if(args.length > 1) {
            switch(args[1].replace("%", "")) {
                case "prefix" -> {
                    result = LuckPermsService.getPrefix(uuid).join();
                }
                case "suffix" -> {
                    result = LuckPermsService.getSuffix(uuid).join();
                }
                case "primary" -> {
                    if(args.length == 3) {
                        if(args[1].equals("group") && args[2].equals("name")) {

                        }
                    }
                }
                case "meta" -> {

                }
                default -> {
                    result = String.join("_", args);
                }
            }
        } else {
            result = String.join("_", args);
        }

        return result;
    }
}
