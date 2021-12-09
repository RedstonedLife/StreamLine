package net.plasmere.streamline.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.Objects;
import java.util.TreeMap;
import java.util.UUID;

public class UUIDUtils {
    public static TreeMap<String, String> cachedUUIDs = new TreeMap<>();
    public static TreeMap<String, String> cachedNames = new TreeMap<>();
    public static TreeMap<String, File> cachedPlayerFiles = new TreeMap<>();
    public static TreeMap<String, File> cachedGuildFiles = new TreeMap<>();
    public static TreeMap<String, File> cachedPartyFiles = new TreeMap<>();
    public static TreeMap<String, File> cachedOtherFiles = new TreeMap<>();

    public static String getCachedUUID(String username) {
        if (username.equals("%")) return username;
        if (username.contains("-")) return username;

        if (ConfigUtils.offlineMode()) {
            String u = StreamLine.offlineStats.getUUID(username);
            if (u != null && u.contains("-")) {
                cachedUUIDs.put(username, u);
                if (ConfigUtils.debug()) MessagingUtils.logInfo("$getCachedUUID = " + u);
                return u;
            }
        }

        try {
            String finalUsername = username.replace("\"", "").toLowerCase(Locale.ROOT);
            String uuid = cachedUUIDs.get(finalUsername);
            if (uuid != null && uuid.contains("-")) return uuid;
            cachedUUIDs.put(finalUsername, fetch(finalUsername));
            return cachedUUIDs.get(finalUsername);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "error";
    }

    public static String getCachedName(String uuid) {
        if (uuid.equals("%")) return uuid;
        if (! uuid.contains("-")) return uuid;

        if (ConfigUtils.offlineMode()) {
            String n = StreamLine.offlineStats.getPlayerName(uuid);
            if (n != null && n.length() > 0) {
                cachedNames.put(uuid, n);
                return n;
            }
        }

        try {
            String name = cachedNames.get(uuid);
            if (name != null && name.length() > 0) return name;
            cachedNames.put(uuid, getName(uuid));
            return cachedUUIDs.get(uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "error";
    }

    static public String fetch(String username) {
        if (username.contains("-")) return getName(username);

        if (ConfigUtils.offlineMode()) {
            return StreamLine.offlineStats.getUUID(username);
        }

        try {
            if (StreamLine.geyserHolder.enabled) {
                if (StreamLine.geyserHolder.isGeyserPlayer(username)) {
                    return StreamLine.geyserHolder.file.getUUID(username);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        username = username.toLowerCase(Locale.ROOT);
        try {
            String JSONString = "";

            HttpURLConnection connection = (HttpURLConnection) new URL("https://api.mojang.com/users/profiles/minecraft/" + username).openConnection();
            InputStream is = connection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                JSONString = line;
            }

            JsonElement obj = new JsonParser().parse(JSONString);

            JsonObject jo = (JsonObject) obj;

            String id = jo.get("id").getAsString();

            String uuid = makeDashedUUID(id);

            return uuid;
            //return UUID.fromString(id);
        } catch (Exception e){
            MessagingUtils.logInfo("Error on Username of: " + username);
            e.printStackTrace();
        }
        return UUID.randomUUID().toString();
    }

    public static String getName(String uuid) {
        if (! uuid.contains("-")) return fetch(uuid);

        if (ConfigUtils.offlineMode()) {
            return StreamLine.offlineStats.getPlayerName(uuid);
        }

        try {
            if (StreamLine.geyserHolder.enabled) {
                String name = StreamLine.geyserHolder.file.getName(uuid);
                if (name != null) if (! name.equals("")) return name;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String JSONString = "";

            HttpURLConnection connection = (HttpURLConnection) new URL("https://api.mojang.com/user/profiles/" + uuid + "/names").openConnection();
            InputStream is = connection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = br.readLine()) != null) {
                JSONString = line;
            }

            Object obj = new JsonParser().parse(JSONString);
            JsonArray jo = (JsonArray) obj;
            String last = jo.get(jo.size() - 1).toString();
            Object job = new JsonParser().parse(last);
            JsonObject njo = (JsonObject) job;

            return njo.get("name").toString();
        } catch (Exception e){
            MessagingUtils.logInfo("Error on UUID of: " + uuid);
            e.printStackTrace();
        }
        return "error";
    }

    public static String makeDashedUUID(String unformatted){
        StringBuilder formatted = new StringBuilder();
        int i = 1;
        for (Character character : unformatted.toCharArray()){
            if (i == 9 || i == 13 || i == 17 || i == 21){
                formatted.append("-").append(character);
            } else {
                formatted.append(character);
            }
            i++;
        }

        return formatted.toString();
    }

    public static String swapUUID(String uuid){
        if (uuid.contains("-")){
            return stripUUID(uuid);
        } else {
            return makeDashedUUID(uuid);
        }
    }

    public static String stripUUID(String uuid) {
        return uuid.replace("-", "");
    }

    public static String swapToUUID(String thingThatMightBeAName){
        String uuid = thingThatMightBeAName;

        if (! thingThatMightBeAName.contains("-") && ! (thingThatMightBeAName.equals("%"))) {
            uuid = getCachedUUID(thingThatMightBeAName);
        }

        return uuid;
    }

    public static String swapToName(String thingThatMightBeAUUID){
        String name = thingThatMightBeAUUID;

        if (thingThatMightBeAUUID.equals("%")) {
            return ConfigUtils.consoleName();
        }

        if (thingThatMightBeAUUID.contains("-")) {
            name = getCachedName(thingThatMightBeAUUID);
        }

        return name;
    }

    public static File getCachedPlayerFile(String thing) {
        try {
            File file = cachedPlayerFiles.get(swapToUUID(thing));

            if (file == null) return getPlayerFile(swapToUUID(thing));

            return file;
        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public static File getPlayerFile(String uuid){
        File file = new File(StreamLine.getInstance().getPlDir(), uuid + ".properties");
        cachedPlayerFiles.put(uuid, file);
        return file;
    }

    public static File getCachedGuildFile(String thing) {
        try {
            File file = cachedGuildFiles.get(swapToUUID(thing));

            if (file == null) return getGuildFile(swapToUUID(thing));

            return file;
        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public static File getGuildFile(String uuid){
        File file = new File(StreamLine.getInstance().getGDir(), uuid + ".properties");
        cachedGuildFiles.put(uuid, file);
        return file;
    }

    public static File getCachedPartyFile(String thing) {
        try {
            File file = cachedPartyFiles.get(swapToUUID(thing));

            if (file == null) return getPartyFile(swapToUUID(thing));

            return file;
        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public static File getPartyFile(String uuid){
        File file = new File(StreamLine.getInstance().getPDir(), uuid + ".properties");
        cachedPartyFiles.put(uuid, file);
        return file;
    }

    public static File getCachedOtherFile(String thing) {
        try {
            File file = cachedOtherFiles.get(thing);

            if (file == null) return getOtherFile(thing);

            return file;
        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public static File getOtherFile(String thing){
        File file = new File(StreamLine.getInstance().getPDir(), thing);
        cachedOtherFiles.put(thing, file);
        return file;
    }

    public static File getCachedFile(String pathTo, String thing) {
        return getCachedFile(new File(pathTo), thing);
    }

    public static File getCachedFile(File path, String thing) {
        if (! path.isDirectory()) return null;

        if (thing == null) return null;
        if (thing.equals("")) return null;

        try {
            if (path.equals(StreamLine.getInstance().getPlDir())) {
                return getCachedPlayerFile(thing);
            } else if (path.equals(StreamLine.getInstance().getGDir())) {
                return getCachedGuildFile(thing);
            } else if (path.equals(StreamLine.getInstance().getPDir())) {
                return getCachedPartyFile(thing);
            } else {
                return getCachedOtherFile(thing);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }
}
