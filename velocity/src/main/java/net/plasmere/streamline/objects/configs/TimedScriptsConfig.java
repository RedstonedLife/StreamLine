package net.plasmere.streamline.objects.configs;

import de.leonhard.storage.Config;
import de.leonhard.storage.LightningBuilder;
import de.leonhard.storage.sections.FlatFileSection;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.objects.configs.obj.AliasCommand;
import net.plasmere.streamline.objects.configs.obj.TimedScript;
import net.plasmere.streamline.utils.MessagingUtils;
import org.apache.commons.collections4.list.TreeList;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class TimedScriptsConfig {
    private Config config;
    private final String cstring = "timed-scripts.yml";
    private final File file = new File(StreamLine.getInstance().getConfDir(), cstring);

    public List<TimedScript> loadedTimedScripts = new ArrayList<>();

    public TimedScriptsConfig(){
        if (! StreamLine.getInstance().getConfDir().exists()) {
            if (StreamLine.getInstance().getConfDir().mkdir()) {
                MessagingUtils.logInfo("Made folder: " + StreamLine.getInstance().getConfDir().getName());
            }
        }

        config = loadConfig();
        reloadTimedScripts();
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

    public void reloadTimedScripts() {
        loadedTimedScripts.clear();

        loadedTimedScripts = loadTimedScripts();
    }

    public TreeList<String> getRawTimedScripts() {
        reloadConfig();
        FlatFileSection section = config.getSection("aliases");
        return new TreeList<>(section.singleLayerKeySet());
    }

    public TreeList<String> getValidTimedScripts() {
        reloadTimedScripts();

        TreeList<String> strings = new TreeList<>();

        for (TimedScript script : loadedTimedScripts) {
            if (! script.enabled) continue;
            strings.add(script.identifier);
        }

        return strings;
    }

    public List<TimedScript> loadTimedScripts() {
        reloadConfig();

        FlatFileSection groupedSection = config.getSection("timed-scripts");

        List<TimedScript> timedScripts = new ArrayList<>();

        for (String key : groupedSection.singleLayerKeySet()) {
            FlatFileSection section = config.getSection("timed-scripts." + key);

            List<String> strings = section.getListParameterized("time");
            List<Integer> integers = new ArrayList<>();

            for (String s : strings) {
                try {
                    integers.add(Integer.parseInt(s));
                } catch (Exception e) {
                    // do nothing
                }
            }

            TimedScript aliasCommand = new TimedScript(key,
                    section.getString("script"),
                    integers
            );

            if (! aliasCommand.enabled) continue;

            timedScripts.add(aliasCommand);
        }

        return timedScripts;
    }
}
