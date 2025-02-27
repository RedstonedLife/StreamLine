package net.plasmere.streamline.config.from;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigHandler;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.lists.SingleSet;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.PluginUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public abstract class From {
    public enum FileType {
        CONFIG,
        TRANSLATION,
        SERVERCONFIG,
        DISCORDBOT,
        COMMANDS,
        CHATS,
        RANKS,
        VOTES,
    }

    // TreeMap < locale name , TreeMap < order , SingleSet < path , stringed value > > >
    public TreeMap<String, TreeMap<Integer, SingleSet<String, Object>>> locales = new TreeMap<>();
    // TreeMap < order , SingleSet < path , stringed value > >
    public TreeMap<Integer, SingleSet<String, Object>> config = new TreeMap<>();
    public TreeMap<Integer, SingleSet<String, Object>> serverConfig = new TreeMap<>();
    public TreeMap<Integer, SingleSet<String, Object>> discordBot = new TreeMap<>();
    public TreeMap<Integer, SingleSet<String, Object>> commands = new TreeMap<>();
    public TreeMap<Integer, SingleSet<String, Object>> chats = new TreeMap<>();

    public Configuration c;
    public Configuration m;
    public Configuration sc;
    public Configuration dis;
    public Configuration comm;
    public Configuration ch;

    //    public static final StreamLine inst = StreamLine.getInstance();
    public final String cstring = "config.yml";
    public final File cfile = new File(StreamLine.getInstance().getDataFolder(), cstring);
    public final File translationPath = new File(StreamLine.getInstance().getDataFolder() + File.separator + "translations" + File.separator);
    public final String en_USString = "en_US.yml";
    public final File en_USFile = new File(translationPath, en_USString);
    public final String fr_FRString = "fr_FR.yml";
    public final File fr_FRFile = new File(translationPath, fr_FRString);
    public final String setstring = "settings.yml";
    public final File scfile = new File(StreamLine.getInstance().getConfDir(), setstring);
    public final String disbotString = "discord-bot.yml";
    public final File disbotFile = new File(StreamLine.getInstance().getDataFolder(), disbotString);
    public final String commandString = "commands.yml";
    public final File commandFile = new File(StreamLine.getInstance().getDataFolder(), commandString);
    public final String chatsString = "chats.yml";
    public final File chatsFile = new File(StreamLine.getInstance().getConfDir(), chatsString);

    public TreeMap<String, String> catchAll_values = new TreeMap<>();

    public String language = "";

    public File mfile(String language) {
        return new File(translationPath, (language.endsWith(".yml") ? language : language + ".yml"));
    }

    public abstract String versionFrom();

    public From(String language) {
        this.language = language;
        getAllConfigurations();
        chargeLocalesMaps();

        getCatchAlls();

        setupConfigFix();
        setupLocalesFix();
        setupServerConfigFix();
        setupDiscordBotFix();
        setupCommandsFix();
        setupChatsFix();

        applyConfig();
        applyLocales();
        applyServerConfig();
        applyDiscordBot();
        applyCommands();
        applyChats();

        applyCatchAlls();

        MessagingUtils.logInfo("Updated your files from previous version: " + versionFrom());
    }

    public void clearAllApplications() {
        config = new TreeMap<>();
        locales = new TreeMap<>();
        serverConfig = new TreeMap<>();
        discordBot = new TreeMap<>();
        commands = new TreeMap<>();

        chargeLocalesMaps();
    }

    public void getCatchAlls() {
        getCatchAll_values();
    }

    public void addCatchAll_values(String regex, String to) {
        catchAll_values.put(regex, to);
    }

    public abstract void getCatchAll_values();

    public void getAllConfigurations() {
        c = getFirstConfig();
        m = getFirstTranslations(this.language);
        sc = getFirstServerConfig();
        dis = getFirstDiscordBot();
        comm = getFirstCommands();
        ch = getFirstChats();
    }

    public Configuration getFirstConfig() {
        if (! cfile.exists()){
            try	(InputStream in = StreamLine.getInstance().getResourceAsStream(cstring)){
                Files.copy(in, cfile.toPath());
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        Configuration thing = new Configuration();

        try {
            thing = ConfigurationProvider.getProvider(YamlConfiguration.class).load(cfile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return thing;
    }

    public Configuration getFirstTranslations(String language) {
        if (! translationPath.exists()) if (! translationPath.mkdirs()) MessagingUtils.logSevere("COULD NOT MAKE TRANSLATION FOLDER(S)!");

        if (! en_USFile.exists()) {
            try (InputStream in = StreamLine.getInstance().getResourceAsStream(en_USString)) {
                Files.copy(in, en_USFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (! fr_FRFile.exists()) {
            try (InputStream in = StreamLine.getInstance().getResourceAsStream(fr_FRString)) {
                Files.copy(in, fr_FRFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Configuration thing = new Configuration();

        try {
            thing = ConfigurationProvider.getProvider(YamlConfiguration.class).load(mfile(language));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return thing;
    }

    public Configuration getFirstServerConfig() {
        if (! scfile.exists()){
            try	(InputStream in = StreamLine.getInstance().getResourceAsStream(setstring)){
                Files.copy(in, scfile.toPath());
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        Configuration thing = new Configuration();

        try {
            thing = ConfigurationProvider.getProvider(YamlConfiguration.class).load(scfile); // ???
        } catch (Exception e) {
            e.printStackTrace();
        }

        return thing;
    }

    public Configuration getFirstDiscordBot() {
        if (! disbotFile.exists()){
            try	(InputStream in = StreamLine.getInstance().getResourceAsStream(disbotString)){
                Files.copy(in, disbotFile.toPath());
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        Configuration thing = new Configuration();

        try {
            thing = ConfigurationProvider.getProvider(YamlConfiguration.class).load(disbotFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return thing;
    }

    public Configuration getFirstCommands() {
        if (! commandFile.exists()){
            try	(InputStream in = StreamLine.getInstance().getResourceAsStream(commandString)){
                Files.copy(in, commandFile.toPath());
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        Configuration thing = new Configuration();

        try {
            thing = ConfigurationProvider.getProvider(YamlConfiguration.class).load(commandFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return thing;
    }

    public Configuration getFirstChats() {
        if (! chatsFile.exists()){
            try	(InputStream in = StreamLine.getInstance().getResourceAsStream(chatsString)){
                Files.copy(in, chatsFile.toPath());
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        Configuration thing = new Configuration();

        try {
            thing = ConfigurationProvider.getProvider(YamlConfiguration.class).load(chatsFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return thing;
    }

    public abstract void setupConfigFix();
    public abstract void setupLocalesFix();
    public abstract void setupServerConfigFix();
    public abstract void setupDiscordBotFix();
    public abstract void setupCommandsFix();
    public abstract void setupChatsFix();

    public void addUpdatedConfigEntry(String path, Object object) {
        int putInt = 0;

        if (config.size() > 0) putInt = config.lastKey() + 1;

        config.put(putInt, new SingleSet<>(path, object));
    }

    public void chargeLocalesMaps() {
        for (String locale : ConfigHandler.acceptableTranslations()) {
            locales.put(locale, new TreeMap<>());
        }
    }

    public void addUpdatedLocalesEntry(String path, Object object, String locale) {
        int putInt = 0;

        if (locales.get(locale).size() > 0) putInt = locales.get(locale).lastKey() + 1;

        TreeMap<Integer, SingleSet<String, Object>> map = new TreeMap<>(locales.get(locale));
        map.put(putInt, new SingleSet<>(path, object));

        locales.put(locale, map);
    }

    public void addUpdatedServerConfigEntry(String path, Object object) {
        int putInt = 0;

        if (serverConfig.size() > 0) putInt = serverConfig.lastKey() + 1;

        serverConfig.put(putInt, new SingleSet<>(path, object));
    }

    public void addUpdatedDiscordBotEntry(String path, Object object) {
        int putInt = 0;

        if (discordBot.size() > 0) putInt = discordBot.lastKey() + 1;

        discordBot.put(putInt, new SingleSet<>(path, object));
    }

    public void addUpdatedCommandsEntry(String path, Object object) {
        int putInt = 0;

        if (commands.size() > 0) putInt = commands.lastKey() + 1;

        commands.put(putInt, new SingleSet<>(path, object));
    }

    public void addUpdatedChatsEntry(String path, Object object) {
        int putInt = 0;

        if (chats.size() > 0) putInt = chats.lastKey() + 1;

        chats.put(putInt, new SingleSet<>(path, object));
    }

    public int applyConfig() {
        int applied = 0;

        for (int itgr : config.keySet()) {
            c.set(config.get(itgr).key, config.get(itgr).value);
            applied ++;
        }

        if (applied > 0) {
            try {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(c, cfile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return applied;
    }

    public int applyLocales() {
        int applied = 0;

        for (String locale : locales.keySet()) {
            for (int itgr : locales.get(locale).keySet()) {
                m.set(locales.get(locale).get(itgr).key, locales.get(locale).get(itgr).value);
                applied ++;
            }

            if (locales.get(locale).keySet().size() > 0) {
                try {
                    ConfigurationProvider.getProvider(YamlConfiguration.class).save(m, mfile(locale));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return applied;
    }

    public int applyServerConfig() {
        int applied = 0;

        for (int itgr : serverConfig.keySet()) {
            sc.set(serverConfig.get(itgr).key, serverConfig.get(itgr).value);
            applied ++;
        }

        if (applied > 0) {
            try {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(sc, scfile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return applied;
    }

    public int applyDiscordBot() {
        int applied = 0;

        for (int itgr : discordBot.keySet()) {
            dis.set(discordBot.get(itgr).key, discordBot.get(itgr).value);
            applied ++;
        }

        if (applied > 0) {
            try {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(dis, disbotFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return applied;
    }

    public int applyCommands() {
        int applied = 0;

        for (int itgr : commands.keySet()) {
            comm.set(commands.get(itgr).key, commands.get(itgr).value);
            applied ++;
        }

        if (applied > 0) {
            try {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(comm, commandFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return applied;
    }

    public int applyChats() {
        int applied = 0;

        for (int itgr : chats.keySet()) {
            ch.set(chats.get(itgr).key, chats.get(itgr).value);
            applied ++;
        }

        if (applied > 0) {
            try {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(ch, chatsFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return applied;
    }

    public void saveAllConfigurations() {
        saveConfig();
        saveLocales();
        saveSettingsConfig();
        saveDiscordBot();
        saveCommands();
        saveChats();
    }

    public void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(c, cfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveLocales() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(m, mfile(this.language));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveSettingsConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(sc, scfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveDiscordBot() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(dis, disbotFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveCommands() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(comm, commandFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveChats() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(ch, chatsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasKeys(Configuration configuration) {
        if (configuration.getKeys().size() > 0) return true;
        else return false;
    }

    public void findDeepKeys(Configuration base, String currSearch, FileType toFileType) {
        if (hasKeys(base)) {
            boolean trial = false;

            try {
                trial = hasKeys(base.getSection(currSearch));
            } catch (Exception e) {
                // do nothing
            }

            if (trial) {
                for (String key : base.getSection(currSearch).getKeys()) {
                    findDeepKeys(base, currSearch + "." + key, toFileType);
                }
            } else {
                switch (toFileType) {
                    case CONFIG:
                        addUpdatedConfigEntry(currSearch, base.get(currSearch));
                        break;
                    case TRANSLATION:
                        addUpdatedLocalesEntry(currSearch, base.get(currSearch), "en_US");
                        break;
                    case SERVERCONFIG:
                        addUpdatedServerConfigEntry(currSearch, base.get(currSearch));
                        break;
                    case DISCORDBOT:
                        addUpdatedDiscordBotEntry(currSearch, base.get(currSearch));
                        break;
                    case COMMANDS:
                        addUpdatedCommandsEntry(currSearch, base.get(currSearch));
                        break;
                    case CHATS:
                        addUpdatedChatsEntry(currSearch, base.get(currSearch));
                        break;
                }
            }
        }
    }

    public void setObjectVariedSetNull(String path, String oldPath, FileType of, String language) {
        switch (of) {
            case CONFIG:
                addUpdatedConfigEntry(path, setNull(c, oldPath));
                break;
            case TRANSLATION:
                addUpdatedLocalesEntry(path, setNull(m, oldPath), language);
                break;
            case SERVERCONFIG:
                addUpdatedServerConfigEntry(path, setNull(sc, oldPath));
                break;
            case DISCORDBOT:
                addUpdatedDiscordBotEntry(path, setNull(dis, oldPath));
                break;
            case COMMANDS:
                addUpdatedCommandsEntry(path, setNull(comm, oldPath));
                break;
            case CHATS:
                addUpdatedChatsEntry(path, setNull(ch, oldPath));
                break;
        }
    }

    public void setObjectVaried(String path, Object toSet, FileType of, String language) {
        switch (of) {
            case CONFIG:
                addUpdatedConfigEntry(path, toSet);
                break;
            case TRANSLATION:
                addUpdatedLocalesEntry(path, toSet, language);
                break;
            case SERVERCONFIG:
                addUpdatedServerConfigEntry(path, toSet);
                break;
            case DISCORDBOT:
                addUpdatedDiscordBotEntry(path, toSet);
                break;
            case COMMANDS:
                addUpdatedCommandsEntry(path, toSet);
                break;
            case CHATS:
                addUpdatedChatsEntry(path, toSet);
                break;
        }
    }

    public void iterateDeepPaths(Configuration base, String fromPath, String toPath, String currSearch, FileType fileType, String language) {
        boolean trial = false;

        try {
            trial = hasKeys(base.getSection(currSearch));
        } catch (Exception e) {
            // do nothing
        }

        if (! trial) {
            String newPath = currSearch.replace(fromPath, toPath);

            Object obj = base.get(currSearch);

            setObjectVaried(newPath, obj, fileType, language);
            setObjectVaried(currSearch, null, fileType, language);

//            base.set(newPath, obj);
//            base.set(currSearch, null);
        } else {
            for (String key : base.getSection(currSearch).getKeys()) {
                iterateDeepPaths(base, fromPath, toPath, currSearch + "." + key, fileType, language);
            }
        }
    }

    public void renameDeep(String fromPath, String toPath, FileType of, String language) {
        switch (of) {
            case CONFIG:
                if (hasKeys(c)) {
                    iterateDeepPaths(c, fromPath, toPath, fromPath, of, language);
                }
                break;
            case TRANSLATION:
                if (hasKeys(m)) {
                    iterateDeepPaths(m, fromPath, toPath, fromPath, of, language);
                }
                break;
            case SERVERCONFIG:
                if (hasKeys(sc)) {
                    iterateDeepPaths(sc, fromPath, toPath, fromPath, of, language);
                }
                break;
            case DISCORDBOT:
                if (hasKeys(dis)) {
                    iterateDeepPaths(dis, fromPath, toPath, fromPath, of, language);
                }
                break;
            case COMMANDS:
                if (hasKeys(comm)) {
                    iterateDeepPaths(comm, fromPath, toPath, fromPath, of, language);
                }
                break;
            case CHATS:
                if (hasKeys(ch)) {
                    iterateDeepPaths(ch, fromPath, toPath, fromPath, of, language);
                }
                break;
        }
    }

    public void rename(String fromPath, String toPath, FileType fileType, String language) {
        switch (fileType) {
            case CONFIG:
                addUpdatedConfigEntry(toPath, setNull(c, fromPath));
                break;
            case TRANSLATION:
                addUpdatedLocalesEntry(toPath, setNull(m, fromPath), language);
                break;
            case SERVERCONFIG:
                addUpdatedServerConfigEntry(toPath, setNull(sc, fromPath));
                break;
            case DISCORDBOT:
                addUpdatedDiscordBotEntry(toPath, setNull(dis, fromPath));
                break;
            case COMMANDS:
                addUpdatedCommandsEntry(toPath, setNull(comm, fromPath));
                break;
            case CHATS:
                addUpdatedChatsEntry(toPath, setNull(ch, fromPath));
                break;
        }
    }

    public Object setNull(Configuration configuration, String path) {
        Object obj = configuration.get(path);
        configuration.set(path, null);
        return obj;
    }

    public void replaceAllOccurrencesInFile(String from, String to, File file) {
        try {
            TreeMap<Integer, String> lines = new TreeMap<>();
            int l = 0;

            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                lines.put(l, line);
                l ++;
            }
            bufferedReader.close();

            file.delete();
            file.createNewFile();

            FileWriter writer = new FileWriter(file);
            for (int i = 0; i < lines.size(); i ++) {
                writer.write(lines.get(i).replace(from, to) + "\n");
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void replaceDeep(Configuration base, String currSearch, FileType toFileType, String from, String to) {
        if (hasKeys(base)) {
            boolean trial = false;

            try {
                trial = hasKeys(base.getSection(currSearch));
            } catch (Exception e) {
                // do nothing
            }

            if (trial) {
                for (String key : base.getSection(currSearch).getKeys()) {
                    findDeepKeys(base, currSearch + "." + key, toFileType);
                }
            } else {
                Object obj = base.get(currSearch);
                String thing = obj.toString();

//                try {
//                    thing = (String) obj;
//                } catch (Exception e) {
//                    // Isn't string, so return.
//                    return;
//                }

                thing = thing.replace(from, to);

                switch (toFileType) {
                    case CONFIG:
                        addUpdatedConfigEntry(currSearch, thing);
                        break;
                    case TRANSLATION:
                        addUpdatedLocalesEntry(currSearch, thing, "en_US");
                        break;
                    case SERVERCONFIG:
                        addUpdatedServerConfigEntry(currSearch, thing);
                        break;
                    case DISCORDBOT:
                        addUpdatedDiscordBotEntry(currSearch, thing);
                        break;
                    case COMMANDS:
                        addUpdatedCommandsEntry(currSearch, thing);
                        break;
                    case CHATS:
                        addUpdatedChatsEntry(currSearch, thing);
                        break;
                }
            }
        }
    }

    public void replaceLoop(Configuration base, FileType of, String from, String to) {
        for (String key : base.getKeys()) {
            replaceDeep(base, key, of, from, to);
        }
    }

    public void replaceAllOccurrences(FileType of, String from, String to) {
        switch (of) {
            case CONFIG:
                replaceLoop(c, of, from, to);
                break;
            case TRANSLATION:
                replaceLoop(m, of, from, to);
                break;
            case SERVERCONFIG:
                replaceLoop(sc, of, from, to);
                break;
            case DISCORDBOT:
                replaceLoop(dis, of, from, to);
                break;
            case COMMANDS:
                replaceLoop(comm, of, from, to);
                break;
            case CHATS:
                replaceLoop(ch, of, from, to);
                break;
        }
    }

    public void applyCatchAlls() {
        MessagingUtils.logSevere("Could not fix occurrences in your server config because it is currently disabled by the plugin maker! (To fix!)");
        for (String regex : catchAll_values.keySet()) {
            for (FileType of : FileType.values()) {
                replaceAllOccurrencesInFiles(of, regex, catchAll_values.get(regex));
            }
        }
    }

    public void replaceAllOccurrencesInFiles(FileType of, String from, String to) {
        switch (of) {
            case CONFIG:
                replaceAllOccurrencesInFile(from, to, cfile);
                break;
            case TRANSLATION:
                for (String language : ConfigHandler.acceptableTranslations()) {
                    replaceAllOccurrencesInFile(from, to, mfile(language));
                }
                break;
            case SERVERCONFIG:
//                replaceAllOccurrencesInFile(from, to, scfile);
                break;
            case DISCORDBOT:
                replaceAllOccurrencesInFile(from, to, disbotFile);
                break;
            case COMMANDS:
                replaceAllOccurrencesInFile(from, to, commandFile);
                break;
            case CHATS:
                replaceAllOccurrencesInFile(from, to, chatsFile);
                break;
        }
    }
}
