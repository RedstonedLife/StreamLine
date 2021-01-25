package net.plasmere.streamline.utils;

import com.google.common.cache.Cache;
import com.google.gson.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.objects.Player;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.UUID;

public class UUIDFetcher {
    public static Cache<String, UUID> cachedUUIDs;
    public static Cache<UUID, String> cachedNames;

    public static UUID getCachedUUID(String username) {
        try {
            return cachedUUIDs.get(username, () -> fetch(username));
        } catch (Exception e) {
            e.printStackTrace();
            return UUID.randomUUID();
        }
    }

    public static String getCachedName(UUID uuid) {
        try {
            return cachedNames.get(uuid, () -> getName(uuid.toString()));
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    static public UUID fetch(String username) {
        username = username.toLowerCase(Locale.ROOT);
        System.out.println("fetch name --> " + username);
        try {
            String JSONString = "";

            HttpURLConnection connection = (HttpURLConnection) new URL("https://api.mojang.com/users/profiles/minecraft/" + username).openConnection();
            InputStream is = connection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                StreamLine.getInstance().getLogger().info("DEBUG:\nline --> " + line);
                JSONString = line;
            }

            StreamLine.getInstance().getLogger().info("DEBUG:\nusername --> " + username + "\nJSONString --> " + JSONString);

            JsonElement obj = new JsonParser().parse(JSONString);

            JsonObject jo = (JsonObject) obj;

            String id = jo.get("id").getAsString();

            // String uuid = id.substring(0, 7) + "-" + id.substring(8, 11) + "-"
            //        + id.substring(12, 15) + "-" + id.substring(16, 19) + "-"
            //        + id.substring(20);
            String uuid = formatToUUID(id);

            UUID u = UUID.fromString(uuid);

            cachedUUIDs.put(username, u);

            return u;
            //return UUID.fromString(id);
        } catch (Exception e){
            e.printStackTrace();
        }
        return UUID.randomUUID();
    }

    public static String getName(String uuid) {
        try {
            String JSONString = "";

            HttpURLConnection connection = (HttpURLConnection) new URL("https://api.mojang.com/user/profiles/" + uuid.replace("-", "") + "/names").openConnection();
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

            String name = njo.get("name").toString();

            cachedNames.put(UUID.fromString(uuid), name);

            return name;
        } catch (Exception e){
            e.printStackTrace();
        }
        return "error";
    }

    public static String formatToUUID(String unformatted){
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

    public static ProxiedPlayer getPPlayer(UUID uuid){
        try {
            return StreamLine.getInstance().getProxy().getPlayer(uuid);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static Player getPlayer(UUID uuid){
        try {
            String name = getCachedName(uuid);


            if (PlayerUtils.hasStat(name)) {
                return getPlayer(name);
            } else {
                Player player = new Player(name);

                if (player.latestName == null) {
                    return null;
                } else {
                    return player;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static Player getPlayer(String name){
        try {
            if (PlayerUtils.exists(name)) {
                if (PlayerUtils.hasStat(name)) {
                    return getPlayer(name);
                } else {
                    return new Player(name);
                }
            }

            return null;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String swapUUID(String uuid){
        if (uuid.contains("-")){
            return uuid.replace("-", "");
        } else {
            return formatToUUID(uuid);
        }
    }
}
