package net.plasmere.streamline.utils;

import com.velocitypowered.api.proxy.Player;
import net.plasmere.streamline.services.LuckPermsService;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LPPlaceHolders {
    private static final Pattern pattern = Pattern.compile("%luckperms_\\S+%");
    private static final String META = "%luckperms_meta";
    private static final String PREFIX_ELEMENT = "%luckperms_prefix_element";
    private static final String SUFFIX_ELEMENT = "%luckperms_suffix_element";

    public static String parse(Player player, String string) {
        Matcher matcher = pattern.matcher(string);
        while(matcher.find()) {
            //matcher.group
        }

        return null;
    }

    private static String parseArguments(UUID uuid, String[] args) {
        String result = "";

        if(args.length > 0) {
            switch(args[0]) {
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
            }
        }

        return result;
    }
}
