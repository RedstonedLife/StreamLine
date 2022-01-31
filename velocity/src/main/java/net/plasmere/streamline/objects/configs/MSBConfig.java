package net.plasmere.streamline.objects.configs;

import de.leonhard.storage.Config;
import de.leonhard.storage.LightningBuilder;
import de.leonhard.storage.sections.FlatFileSection;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.objects.savable.users.SavableUser;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PluginUtils;
import net.plasmere.streamline.utils.TextUtils;
import net.plasmere.streamline.utils.objects.*;
import net.plasmere.streamline.utils.sql.BridgerDataSource;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MSBConfig {
    Config config;
    Config queries;
    String cstring = "mysqlbridger.yml";
    String qstring = "saved-queries.yml";
    File file = new File(StreamLine.getInstance().getSQLDir(), cstring);
    File qfile = new File(StreamLine.getInstance().getSQLDir(), qstring);

    public List<Host> loadedHosts = new ArrayList<>();
    public List<Syncable> loadedSyncables = new ArrayList<>();
    public List<CustomSQLInfo> loadedQueries = new ArrayList<>();
    public List<CustomSQLInfo> loadedExecutions = new ArrayList<>();
    public List<SavedQueries> loadedSavedQueries = new ArrayList<>();

    public MSBConfig() {
        this.config = loadConfig();

        this.loadedHosts = getHosts();
        this.loadedSyncables = getSyncables();
        this.loadedQueries = getQueries();
        this.loadedExecutions = getExecutions();

        this.queries = loadSavedQueries();
        this.loadedSavedQueries = getSavedQueries();
    }

    public String parsePlaceholder(String from, SavableUser on) {
        String pattern = "((%mysqlb_).*?[%])";

        Pattern search = Pattern.compile(pattern);
        Matcher matcher = search.matcher(from);

        TreeMap<String, String> toReplace = new TreeMap<>();

        int i = 1;
        while (matcher.find()) {
            try {
                String matched = matcher.group(i);

                if (ConfigUtils.debug()) MessagingUtils.logInfo("Found: " + matched);

                if (matched.length() <= "%mysqlb_".length()) {
                    i ++;
                    continue;
                }

                String replace = onRequest(on, matched.substring("%mysqlb_".length()).substring(0, matched.substring("%mysqlb_".length()).length() - 1));

                toReplace.put(matched, replace);
            } catch (Exception e) {
                e.printStackTrace();
            }
            i ++;
        }

        for (String match : toReplace.keySet()) {
            from = from.replace(match, toReplace.get(match));
        }

        return from;
    }

    public String onRequest(SavableUser user, String params) {
        if (params.contains(".")) {
            String[] things = params.split("\\.");
            String[] args = TextUtils.argsMinus(things, 0);
            if (args[0] == null) return StreamLine.msbConfig.getValuesError();

            if (args[0].equals("self")) args[0] = user.getName();
            else if (args[0].equals("selfuuid")) args[0] = user.uuid;

            String[] query = TextUtils.argsMinus(args, 0);

            CustomSQLInfo sqlInfo = PluginUtils.getQueryByIdentifier(things[0]);
            if (sqlInfo == null) return StreamLine.msbConfig.getValuesNoQuery();

            return BridgerDataSource.query(sqlInfo, user, query);
        } else if (params.contains("_")) {
            String[] things = params.split("_");
            String[] args = TextUtils.argsMinus(things, 0);
            if (args[0] == null) return StreamLine.msbConfig.getValuesError();

            if (args[0].equals("self")) args[0] = user.getName();
            else if (args[0].equals("selfuuid")) args[0] = user.uuid;

            String[] query = TextUtils.argsMinus(args, 0);

            CustomSQLInfo sqlInfo = PluginUtils.getQueryByIdentifier(things[0]);
            if (sqlInfo == null) return StreamLine.msbConfig.getValuesNoQuery();

            return BridgerDataSource.query(sqlInfo, user, query);
        } else {
            CustomSQLInfo sqlInfo = PluginUtils.getQueryByIdentifier(params);
            if (sqlInfo == null) return StreamLine.msbConfig.getValuesNoQuery();

            return BridgerDataSource.query(sqlInfo, user);
        }
    }

    public Config loadConfig() {
        if (! file.exists()) {
            try {
                StreamLine.getInstance().getSQLDir().mkdirs();
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

    public Config loadSavedQueries() {
        if (! qfile.exists()) {
            try {
                StreamLine.getInstance().getSQLDir().mkdirs();
                qfile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return LightningBuilder.fromFile(qfile).createConfig();
    }

    public void reloadConfig() {
        config = loadConfig();
    }

    public void reloadSavedQueries() {
        queries = loadSavedQueries();
    }

    public void reloadHosts() {
        loadedHosts = getHosts();

        BridgerDataSource.reloadHikariHosts();
    }

    public void reloadSyncables() {
        loadedSyncables = getSyncables();
    }

    public void reloadQueries() {
        loadedQueries = getQueries();
    }

    public void reloadExecutions() {
        loadedExecutions = getExecutions();
    }

    public String getValuesNotSet() {
        reloadConfig();

        return config.getString("values.not-set-yet");
    }

    public String getValuesError() {
        reloadConfig();

        return config.getString("values.error");
    }

    public String getValuesNoQuery() {
        reloadConfig();

        return config.getString("values.no-query");
    }

    public List<Host> getHosts() {
        reloadConfig();

        FlatFileSection section = config.getSection("hosts");

        List<Host> list = new ArrayList<>();

        for (String key : section.singleLayerKeySet()) {
            FlatFileSection host_section = config.getSection("hosts." + key);

            Host host = new Host(
                    key,
                    host_section.getString("link"),
                    host_section.getString("user"),
                    host_section.getString("pass")
            );

            list.add(host);
        }

        return list;
    }

    public List<Syncable> getSyncables() {
        reloadConfig();

        FlatFileSection section = config.getSection("syncables");

        List<Syncable> list = new ArrayList<>();

        for (String key : section.singleLayerKeySet()) {
            FlatFileSection host_section = config.getSection("syncables." + key);
            FlatFileSection pull_section = config.getSection("syncables." + key + ".pull");
            FlatFileSection push_section = config.getSection("syncables." + key + ".push");

            PullAndPushInfo pull = new PullAndPushInfo(
                    pull_section.getString("column"),
                    pull_section.getString("host"),
                    pull_section.getString("table"),
                    pull_section.getString("where")
            );
            PullAndPushInfo push = new PullAndPushInfo(
                    push_section.getString("column"),
                    push_section.getString("host"),
                    push_section.getString("table"),
                    push_section.getString("where")
            );

            Syncable syncable = new Syncable(
                    key,
                    host_section.getBoolean("is-string"),
                    pull,
                    push
            );

            list.add(syncable);
        }

        return list;
    }

    public AutoSyncInfo getAutoSync() {
        reloadConfig();

        FlatFileSection section = config.getSection("auto-sync");

        return new AutoSyncInfo(
                section.getInt("every"),
                section.getBoolean("join"),
                section.getBoolean("leave")
        );
    }

    public List<CustomSQLInfo> getQueries() {
        reloadConfig();

        FlatFileSection section = config.getSection("queries");

        List<CustomSQLInfo> list = new ArrayList<>();

        for (String key : section.singleLayerKeySet()) {
            FlatFileSection sql_section = config.getSection("queries." + key);

            CustomSQLInfo csql = new CustomSQLInfo(
                    key,
                    sql_section.getString("host"),
                    sql_section.getString("sql")
            );

            list.add(csql);
        }

        return list;
    }

    public List<CustomSQLInfo> getExecutions() {
        FlatFileSection section = config.getSection("executions");

        List<CustomSQLInfo> list = new ArrayList<>();

        for (String key : section.singleLayerKeySet()) {
            FlatFileSection sql_section = config.getSection("executions." + key);

            CustomSQLInfo csql = new CustomSQLInfo(
                    key,
                    sql_section.getString("host"),
                    sql_section.getString("sql")
            );

            list.add(csql);
        }

        return list;
    }

    public boolean isValidSyncable(String toTest) {
        for (String valid : PluginUtils.getSyncablesAsStrings()) {
            if (valid.equals(toTest)) return true;
        }

        return false;
    }

    public boolean isValidQuery(String toTest) {
        for (String valid : PluginUtils.getQueriesAsStrings()) {
            if (valid.equals(toTest)) return true;
        }

        return false;
    }

    public boolean isValidExecution(String toTest) {
        for (String valid : PluginUtils.getExecutionsAsStrings()) {
            if (valid.equals(toTest)) return true;
        }

        return false;
    }

    public List<SavedQueries> getSavedQueries() {
        reloadSavedQueries();

        List<SavedQueries> qs = new ArrayList<>();

        for (String key : queries.singleLayerKeySet()) {
            SavedQueries saved = new SavedQueries(key);

            for (String identifier : queries.getSection(key).singleLayerKeySet()) {
                saved = saved.append(identifier, getResyncSeconds(), queries.getSection(key).getString(identifier));
            }

            qs.add(saved);
        }

        return qs;
    }

    public void saveAllQueriedResults() {
        try {
            queries.getFile().delete();
            queries.getFile().createNewFile();
            for (SavedQueries q : loadedSavedQueries) {
                for (String identifier : q.results.keySet()) {
                    queries.set(q.playerUUID + "." + identifier, q.results.get(identifier));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveQueriedResult(SavedQueries q) {
//        MultiDB.instance.getLogger().info("Player Name: " + q.playerUUID);
        for (String identifier : q.results.keySet()) {
//            MultiDB.instance.getLogger().info("Setting: < " + identifier + " , " + q.results.get(identifier).value + ">");
            queries.set(q.playerUUID + "." + identifier, q.results.get(identifier).value);
        }
    }

//    public void putToBeSaved(SavedQueries q) {
//        toSave.add(q);
//    }
//
//    public void takeToBeSaved(SavedQueries q) {
//        toSave.remove(q);
//    }

    public SavedQueries addSavedQueries(SavedQueries q) {
        loadedSavedQueries.removeIf(qu -> qu.playerUUID.equals(q.playerUUID));

        loadedSavedQueries.add(q);

        return q;
    }

    public int getResyncSeconds() {
        reloadConfig();

        return config.getInt("resync.every");
    }
}
