package net.plasmere.streamline.objects.configs.obj;

import de.leonhard.storage.Config;
import de.leonhard.storage.LightningBuilder;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.objects.lists.SingleSet;
import net.plasmere.streamline.objects.savable.users.SavableUser;
import net.plasmere.streamline.utils.TextUtils;
import net.plasmere.streamline.utils.sql.BridgerDataSource;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MSBConfig {
    Config config;
    String cstring = "mysqlbridger.yml";
    File file = new File(StreamLine.getInstance().getConfDir(), cstring);

    public MSBConfig() {
        config = loadConfig();
    }

    public Config loadConfig() {
        if (! file.exists()) {
            try {
                StreamLine.getInstance().getConfDir().mkdirs();
                try (InputStream in = StreamLine.getInstance().getResourceAsStream(cstring)) {
                    Files.copy(in, file.toPath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return LightningBuilder.fromFile(file).createConfig();
    }

    public void reloadConfig() {
        config = loadConfig();
    }

    public SingleSet<String, String> getQueryDatabaseSet(String from) {
        reloadConfig();

        return new SingleSet<>(
                config.getOrDefault("queries." + from + ".user", "Your database user is not set up correctly!"),
                config.getOrDefault("queries." + from + ".pass", "Your database password is not set up correctly!")
        );
    }

    public SingleSet<String, String> getExecutionDatabaseSet(String from) {
        reloadConfig();

        return new SingleSet<>(
                config.getOrDefault("executions." + from + ".user", "Your database user is not set up correctly!"),
                config.getOrDefault("executions." + from + ".pass", "Your database password is not set up correctly!")
        );
    }

    public String getQuery(String from) {
        reloadConfig();

        return config.getOrDefault("queries." + from + ".query", "Your database query is not set up correctly!");
    }

    public String getExecution(String from) {
        reloadConfig();

        return config.getOrDefault("executions." + from + ".execution", "Your database query is not set up correctly!");
    }

    public String getQueryLink(String from) {
        reloadConfig();

        return config.getOrDefault("queries." + from + ".link", "Your database link is not set up correctly!");
    }

    public String getExecutionLink(String from) {
        reloadConfig();

        return config.getOrDefault("executions." + from + ".link", "Your database link is not set up correctly!");
    }

    public String getValuesNotSetYet() {
        reloadConfig();

        return config.getString("values.not-set-yet");
    }

    public String getValuesError() {
        reloadConfig();

        return config.getString("values.error");
    }

    public boolean isValidQuery(String toTest) {
        for (String valid : getQueryNames()) {
            if (valid.equals(toTest)) return true;
        }

        return false;
    }

    public boolean isValidExecution(String toTest) {
        for (String valid : getExecutionNames()) {
            if (valid.equals(toTest)) return true;
        }

        return false;
    }

    public String getSetAs(String from) {
        reloadConfig();

        return config.getOrDefault("queries." + from + ".set-as", "");
    }

    public Set<String> getQueryNames() {
        reloadConfig();

        return config.getSection("queries").singleLayerKeySet();
    }

    public Set<String> getExecutionNames() {
        reloadConfig();

        return config.getSection("executions").singleLayerKeySet();
    }

    public TreeMap<String, String> getDefaultHolders() {
        TreeMap<String, String> map = new TreeMap<>();

        for (String name : getQueryNames()) {
            map.put(getSetAs(name), getValuesNotSetYet());
        }

        return map;
    }

//    public String parsePlaceholder(String from) {
//        String pattern = "((%mysqlb_).*?[%])";
//
//        Pattern search = Pattern.compile(pattern);
//        Matcher matcher = search.matcher(from);
//
//        TreeMap<String, String> toReplace = new TreeMap<>();
//
//        int i = 1;
//        while (matcher.find()) {
//            String matched = matcher.group(i);
//
//
//            i ++;
//        }
//    }

    public String onRequest(SavableUser user, String params) {
        if (params.contains("_")) {
            String[] things = params.split("_");
            String[] args = TextUtils.argsMinus(things, 0);

            String queryAnswer = BridgerDataSource.doQuery(things[0], TextUtils.argsMinus(args, 0));
            StreamLine.holders.put(StreamLine.msbConfig.getSetAs(things[0]), queryAnswer);

            return StreamLine.holders.get(things[0]);
        }

        if (params.contains(".")) {
            String[] things = params.split("\\.");
            String[] args = TextUtils.argsMinus(things, 0);
            if (args[0].equals("self")) args[0] = user.latestName;
            if (args[0].equals("selfuuid")) args[0] = user.uuid;

            String queryAnswer = BridgerDataSource.doQuery(things[0], args);
            StreamLine.holders.put(StreamLine.msbConfig.getSetAs(things[0]), queryAnswer);

            return StreamLine.holders.get(things[0]);
        }

        return StreamLine.holders.get(params);
    }
}
