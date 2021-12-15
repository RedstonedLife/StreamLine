package net.plasmere.streamline.config;

import de.leonhard.storage.Config;
import de.leonhard.storage.LightningBuilder;
import de.leonhard.storage.sections.FlatFileSection;
import net.plasmere.streamline.StreamLine;

import net.plasmere.streamline.objects.configs.obj.ConfigSection;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

public class ConfigHandler {
    public Config conf;
    //    public Configuration oConf;
    public Config mess;
    //    public Configuration oMess;
    public Config discordBot;
    public Config commands;

    public static String language = "";

//    public final String configVer = "13.3";
//    public final String messagesVer = "13.3";

    //    public static final StreamLine inst = StreamLine.getInstance();
    public final String cstring = "config.yml";
    public final File cfile = new File(StreamLine.getInstance().getDataFolder(), cstring);
    public final File translationPath = new File(StreamLine.getInstance().getDataFolder() + File.separator + "translations" + File.separator);
    public final String en_USString = "en_US.yml";
    public final File en_USFile = new File(translationPath, en_USString);
    public final String fr_FRString = "fr_FR.yml";
    public final File fr_FRFile = new File(translationPath, fr_FRString);
    public final String disbotString = "discord-bot.yml";
    public final File disbotFile = new File(StreamLine.getInstance().getDataFolder(), disbotString);
    public final String commandString = "commands.yml";
    public final File commandFile = new File(StreamLine.getInstance().getDataFolder(), commandString);

    public File mfile(String language) {
        return new File(translationPath, (language.endsWith(".yml") ? language : language + ".yml"));
    }

    public ConfigHandler(String language){
        ConfigHandler.language = language;

        conf = loadConf();
        mess = loadLocales(language);
        discordBot = loadDiscordBot();
        commands = loadCommands();
    }

    public void setLanguage(String language) throws Exception {
        if (! TextUtils.equalsAny(language, acceptableTranslations())) throw new Exception("Unsupported language!");

        ConfigHandler.language = language;
        this.reloadLocales(language);
        int localeLineNumber = 3;

        MessagingUtils.logWarning("[DEBUG] local locale = " + language + " , this.locale = " + ConfigHandler.language);

        List<String> lines = Files.readAllLines(StreamLine.getInstance().languageFile().toPath(), StandardCharsets.UTF_8);
        lines.set(localeLineNumber - 1, language);
        Files.write(StreamLine.getInstance().languageFile().toPath(), lines, StandardCharsets.UTF_8);
    }

    public void reloadConfig() {
        conf = loadConf();
    }

    public void reloadLocales(String language) {
        mess = loadLocales(language);
    }

    public void reloadLocales(){
        reloadLocales(ConfigHandler.language);
    }

    public void reloadDiscordBot() {
        discordBot = loadDiscordBot();
    }

    public void reloadCommands() {
        commands = loadCommands();
    }

    public Config loadConf(){
        if (! cfile.exists()){
            try	(InputStream in = StreamLine.getInstance().getResourceAsStream(cstring)){
                Files.copy(in, cfile.toPath());
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        return LightningBuilder.fromFile(cfile).createConfig();
    }

    public static TreeSet<String> acceptableTranslations() {
        TreeSet<String> trans = new TreeSet<>();

        trans.add("en_US");
        trans.add("fr_FR");

        return trans;
    }

    public Config loadLocales(String language) {
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

        return LightningBuilder.fromFile(mfile(language)).createConfig();
    }

    public Config loadDiscordBot(){
        if (! disbotFile.exists()){
            try	(InputStream in = StreamLine.getInstance().getResourceAsStream(disbotString)){
                Files.copy(in, disbotFile.toPath());
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        return LightningBuilder.fromFile(disbotFile).createConfig();
    }

    public Config loadCommands(){
        if (! commandFile.exists()){
            try	(InputStream in = StreamLine.getInstance().getResourceAsStream(commandString)){
                Files.copy(in, commandFile.toPath());
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        return LightningBuilder.fromFile(commandFile).createConfig();
    }

    /*
    \        /\        /\        /\        /\        /\        /\        /\        /\        /\        /\        /\        /\        /\        /\        /\        /
     \      /  \      /  \      /  \      /  \      /  \      /  \      /  \      /  \      /  \      /  \      /  \      /  \      /  \      /  \      /  \      /
      \    /    \    /    \    /    \    /    \    /    \    /    \    /    \    /    \    /    \    /    \    /    \    /    \    /    \    /    \    /    \    /
       \  /      \  /      \  /      \  /      \  /      \  /      \  /      \  /      \  /      \  /      \  /      \  /      \  /      \  /      \  /      \  /
        \/        \/        \/        \/        \/        \/        \/        \/        \/        \/        \/        \/        \/        \/        \/        \/
     */

    public String getConfString(String path) {
        reloadConfig();
        return conf.getString(path);
    }

    public boolean getConfBoolean(String path) {
        reloadConfig();
        return conf.getBoolean(path);
    }

    public int getConfInteger(String path) {
        reloadConfig();
        return conf.getInt(path);
    }

    public List<String> getConfStringList(String path) {
        reloadConfig();
        return conf.getStringList(path);
    }

    public List<Integer> getConfIntegerList(String path) {
        reloadConfig();
        return conf.getIntegerList(path);
    }

    public ConfigSection getConfSection(String path) {
        reloadConfig();
        return new ConfigSection(conf.getSection(path));
    }

    public Object getObjectConf(String path){
        reloadConfig();
        return conf.get(path);
    }

    public void setObjectConf(String path, Object thing){
        conf.set(path, thing);
        reloadConfig();
    }

    public Collection<String> getConfKeys() {
        reloadConfig();
        return conf.keySet();
    }

    public String getMessString(String path) {
        reloadLocales();
        return mess.getString(path);
    }

    public boolean getMessBoolean(String path) {
        reloadLocales();
        return mess.getBoolean(path);
    }

    public int getMessInteger(String path) {
        reloadLocales();
        return mess.getInt(path);
    }

    public List<String> getMessStringList(String path) {
        reloadLocales();
        return mess.getStringList(path);
    }

    public List<Integer> getMessIntegerList(String path) {
        reloadLocales();
        return conf.getIntegerList(path);
    }

    public ConfigSection getMessSection(String path) {
        reloadLocales();
        return new ConfigSection( mess.getSection(path));
    }

    public Object getObjectMess(String path){
        reloadLocales();
        return mess.get(path);
    }

    public void setObjectMess(String path, Object thing){
        mess.set(path, thing);
        reloadLocales();
    }

    public Collection<String> getMessKeys() {
        reloadLocales();
        return mess.keySet();
    }

    public String getDisBotString(String path) {
        reloadDiscordBot();
        return discordBot.getString(path);
    }

    public boolean getDisBotBoolean(String path) {
        reloadDiscordBot();
        return discordBot.getBoolean(path);
    }

    public int getDisBotInteger(String path) {
        reloadDiscordBot();
        return discordBot.getInt(path);
    }

    public long getDisBotLong(String path) {
        reloadDiscordBot();
        return discordBot.getLong(path);
    }

    public List<String> getDisBotStringList(String path) {
        reloadDiscordBot();
        return discordBot.getStringList(path);
    }

    public List<Integer> getDisBotIntegerList(String path) {
        reloadDiscordBot();
        return discordBot.getIntegerList(path);
    }

    public ConfigSection getDisBotSection(String path) {
        reloadDiscordBot();
        return new ConfigSection(discordBot.getSection(path));
    }

    public Object getObjectDisBot(String path){
        reloadDiscordBot();
        return discordBot.get(path);
    }

    public void setObjectDisBot(String path, Object thing){
        discordBot.set(path, thing);
        reloadDiscordBot();
    }

    public Collection<String> getDisBotKeys() {
        reloadDiscordBot();
        return discordBot.keySet();
    }

    public String getCommandString(String path) {
        reloadCommands();
        return commands.getString(path);
    }

    public boolean getCommandBoolean(String path) {
        reloadCommands();
        return commands.getBoolean(path);
    }

    public int getCommandInteger(String path) {
        reloadCommands();
        return commands.getInt(path);
    }

    public List<String> getCommandStringList(String path) {
        reloadCommands();
        return commands.getStringList(path);
    }

    public List<Integer> getCommandIntegerList(String path) {
        reloadCommands();
        return commands.getIntegerList(path);
    }

    public ConfigSection getCommandSection(String path) {
        reloadCommands();
        return new ConfigSection(commands.getSection(path));
    }

    public Object getObjectCommand(String path){
        reloadCommands();
        return commands.get(path);
    }

    public void setObjectCommand(String path, Object thing){
        commands.set(path, thing);
        reloadCommands();
    }

    public Collection<String> getCommandKeys() {
        reloadCommands();
        return commands.keySet();
    }
}