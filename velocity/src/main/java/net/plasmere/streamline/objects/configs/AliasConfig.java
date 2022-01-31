package net.plasmere.streamline.objects.configs;

import de.leonhard.storage.Config;
import de.leonhard.storage.LightningBuilder;
import de.leonhard.storage.sections.FlatFileSection;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.objects.configs.obj.AliasCommand;
import net.plasmere.streamline.objects.configs.obj.TablistFormat;
import net.plasmere.streamline.utils.MessagingUtils;
import org.apache.commons.collections4.list.TreeList;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class AliasConfig {
    private Config config;
    private final String cstring = "aliases.yml";
    private final File file = new File(StreamLine.getInstance().getConfDir(), cstring);

    public List<AliasCommand> loadedAliasCommands = new ArrayList<>();

    public AliasConfig(){
        if (! StreamLine.getInstance().getConfDir().exists()) {
            if (StreamLine.getInstance().getConfDir().mkdir()) {
                MessagingUtils.logInfo("Made folder: " + StreamLine.getInstance().getConfDir().getName());
            }
        }

        config = loadConfig();
        reloadAliasCommands();
    }

    public Config getConfig() { return config; }

    public void reloadConfig(){
        try {
            config = loadConfig();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public Config loadConfig(){
        if (! file.exists()){
            try	(InputStream in = StreamLine.getInstance().getResourceAsStream(cstring)){
                Files.copy(in, file.toPath());
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        return LightningBuilder.fromFile(file).createConfig();
    }

    public void reloadAliasCommands() {
        loadedAliasCommands.clear();

        loadedAliasCommands = loadAliasCommands();
    }

    public TreeList<String> getRawAliasCommands() {
        reloadConfig();
        FlatFileSection section = config.getSection("aliases");
        return new TreeList<>(section.singleLayerKeySet());
    }

    public TreeList<String> getValidAliasCommands() {
        reloadAliasCommands();

        TreeList<String> strings = new TreeList<>();

        for (AliasCommand command : loadedAliasCommands) {
            if (! command.enabled) continue;
            strings.add(command.identifier);
        }

        return strings;
    }

    public List<AliasCommand> loadAliasCommands() {
        reloadConfig();

        FlatFileSection groupedSection = config.getSection("aliases");

        List<AliasCommand> aliases = new ArrayList<>();

        for (String key : groupedSection.singleLayerKeySet()) {
            FlatFileSection section = config.getSection("aliases." + key);

            AliasCommand aliasCommand = new AliasCommand(key,
                    section.getString("permission"),
                    section.getString("script")
            );

            if (! aliasCommand.enabled) continue;

            aliases.add(aliasCommand);
        }

        return aliases;
    }
}
