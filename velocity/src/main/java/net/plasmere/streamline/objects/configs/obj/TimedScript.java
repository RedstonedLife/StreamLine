package net.plasmere.streamline.objects.configs.obj;

import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.objects.lists.SingleSet;
import net.plasmere.streamline.scripts.Script;
import net.plasmere.streamline.scripts.ScriptsHandler;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;

import java.util.List;
import java.util.TreeMap;

public class TimedScript {
    public String identifier;
    public List<Integer> timers;
    public String scriptName;
    public TreeMap<Integer, Integer> countdowns;
    public boolean enabled;

    public TimedScript(String identifier, String scriptName, List<Integer> timers) {
        this.identifier = identifier;
        this.scriptName = scriptName;
        this.timers = timers;
        this.countdowns = getCountdown();
        this.enabled = isValid();
    }

    public boolean isValid() {
        try {
            validate();
            return true;
        } catch (Exception e) {
            MessagingUtils.logWarning("TimedScript with identifier of '" + this.identifier + "' errored out due to: " + e.getMessage());
            return false;
        }
    }

    public void validate() throws Exception {
        if (! ConfigUtils.scriptsEnabled()) {
            throw new Exception("Scripts are not enabled!");
        }

        if (ScriptsHandler.getScript(this.scriptName) == null) {
            throw new Exception("Invalid script name! Script '" + this.scriptName + "' does not exist!");
        }
    }

    public TreeMap<Integer, Integer> getCountdown() {
        TreeMap<Integer, Integer> map = new TreeMap<>();

        for (int f : this.timers) {
            map.put(f, f);
        }

        return map;
    }

    public void tickCountdowns() {
        for (int i : countdowns.keySet()) {
            int cooldown = countdowns.get(i);

            if (cooldown <= 0) {
                cooldown = i;
                countdowns.put(i, cooldown);
                if (ConfigUtils.scriptsEnabled()) {
                    Script script = ScriptsHandler.getScript(scriptName);
                    if (script != null) script.execute(PlayerUtils.getConsoleStat().findSender(), PlayerUtils.getConsoleStat());
                }
            }

            countdowns.put(i, cooldown - 1);
        }
    }
}
