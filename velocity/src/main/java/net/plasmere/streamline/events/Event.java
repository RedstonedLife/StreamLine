package net.plasmere.streamline.events;

import de.leonhard.storage.Config;
import de.leonhard.storage.LightningBuilder;
import de.leonhard.storage.sections.FlatFileSection;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;

import net.plasmere.streamline.events.enums.Action;
import net.plasmere.streamline.events.enums.Condition;
import net.plasmere.streamline.objects.configs.obj.ConfigSection;
import net.plasmere.streamline.objects.lists.SingleSet;
import net.plasmere.streamline.utils.MessagingUtils;

import java.io.File;
import java.util.List;
import java.util.TreeMap;

public class Event {
    public File path = StreamLine.getInstance().getEDir();

    public Config configuration;
    public List<String> tags;
    public TreeMap<Integer, SingleSet<Condition, String>> conditions = new TreeMap<>();
    public TreeMap<Integer, SingleSet<Action, String>> actions = new TreeMap<>();
    public String name;
    public File file;

    public Event(File file){
        try {
            configuration = LightningBuilder.fromFile(file).createConfig();

            tags = configuration.getStringList("tags");

            this.conditions = compileCond();
            this.actions = compileAction();
            this.file = file;
            this.name = file.getName();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TreeMap<Integer, SingleSet<Condition, String>> compileCond() {
        TreeMap<Integer, SingleSet<Condition, String>> c = new TreeMap<>();

        ConfigSection conditionsConf = (ConfigSection) configuration.getSection("conditions");
        int i = 1;

        if (ConfigUtils.debug()) {
            MessagingUtils.logInfo("Amount of conditions --> " + conditionsConf.keySet().size());
        }

        for (String string : conditionsConf.keySet()) {
            try {
                ConfigSection cond = (ConfigSection) configuration.getSection(conditionsConf.getPathPrefix() + string);

                c.put(i,
                        new SingleSet<>(
                                Condition.fromString(cond.getString("type")),
                                cond.getString("value")
                        )
                );
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
            i ++;
        }

        if (ConfigUtils.debug()) {
            MessagingUtils.logInfo("Event#compileCond():");
            for (Integer it : c.keySet()) {
                MessagingUtils.logInfo("   > " + it + " : ( ( " +
                        c.get(it).key + " , " +
                        c.get(it).value + " ) )"
                );
            }
        }

        return c;
    }

    public TreeMap<Integer, SingleSet<Action, String>> compileAction() {
        TreeMap<Integer, SingleSet<Action, String>> a = new TreeMap<>();

        ConfigSection actionsConf = (ConfigSection) configuration.getSection("actions");
        int i = 1;

        for (String string : actionsConf.keySet()) {
            try {
                ConfigSection act = (ConfigSection) configuration.getSection(actionsConf.getPathPrefix() + string);

                a.put(i,
                        new SingleSet<>(
                                Action.fromString(act.getString("type")),
                                act.getString("value")
                        )
                );
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
            i++;
        }

        return a;
    }

    @Override
    public String toString() {
        return "Event{ " +
                "path=" + path +
                ", configuration=" + configuration +
                ", tags=" + tags +
                ", compiled=(" + conditions + " , " + actions +
                ") }";
    }
}
