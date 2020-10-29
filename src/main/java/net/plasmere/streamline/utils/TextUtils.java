package net.plasmere.streamline.utils;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.plasmere.streamline.config.ConfigUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {
    public static TextComponent codedText(String text) {
        text = ChatColor.translateAlternateColorCodes('&', newLined(text));

        try {
            String ntext = text.replace(ConfigUtils.linkPre, "").replace(ConfigUtils.linkSuff, "");

            Pattern pattern = Pattern.compile("(http|ftp|https)://([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(ntext);
            String foundUrl = "";

            while (matcher.find()) {
                foundUrl = matcher.group(0);

                return makeLinked(text, foundUrl);
            }
        } catch (Exception e) {
            return new TextComponent(text);
        }
        return new TextComponent(text);
    }

    public static TextComponent clhText(String text, String hoverPrefix){
        text = ChatColor.translateAlternateColorCodes('&', newLined(text));

        try {
            String ntext = text.replace(ConfigUtils.linkPre, "").replace(ConfigUtils.linkSuff, "");

            Pattern pattern = Pattern.compile("(http|ftp|https)://([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(ntext);
            String foundUrl = "";

            while (matcher.find()) {
                foundUrl = matcher.group(0);

                TextComponent tc = makeLinked(text, foundUrl);
                return makeHoverable(tc, hoverPrefix + foundUrl);
            }
        } catch (Exception e) {
            return new TextComponent(text);
        }
        return new TextComponent(text);
    }

    public static String codedString(String text){
        return ChatColor.translateAlternateColorCodes('&', newLined(text));
    }

    public static TextComponent makeLinked(String text, String url){
        TextComponent tc = new TextComponent(text);
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

    public static String newLined(String text){
        return text.replace("%newline%", "\n");
    }

    public static boolean isCommand(String msg){
        return msg.startsWith("/");
    }

    public static String concat(String[] splitMsg){
        int i = 0;
        StringBuilder text = new StringBuilder();

        for (String split : splitMsg){
            i++;
            if (i < splitMsg.length)
                text.append(split).append(" ");
            else
                text.append(split);
        }

        return text.toString();
    }

    public static String concat(List<String> splitMsg) {
        int i = 0;
        StringBuilder text = new StringBuilder();

        for (String split : splitMsg){
            i++;
            if (i < splitMsg.size())
                text.append(split).append(" ");
            else
                text.append(split);
        }

        return text.toString();
    }
}
