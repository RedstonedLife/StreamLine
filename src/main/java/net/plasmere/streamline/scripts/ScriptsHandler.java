package net.plasmere.streamline.scripts;

import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.utils.MessagingUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ScriptsHandler {
    public static List<Script> scripts = new ArrayList<>();

    public static Script addScript(File file) {
        Script script = new Script(file);

        scripts.add(script);

        if (ConfigUtils.debug) MessagingUtils.logInfo("Loaded script: " + script);

        return script;
    }

    public static Script addScript(Script script) {
        scripts.add(script);
        if (ConfigUtils.debug) MessagingUtils.logInfo("Loaded script: " + script);
        return script;
    }

    public static void remScript(Script script) {
        scripts.remove(script);
    }

    public static Script getAsScript(File file) {
        return new Script(file);
    }

    public static Script getScript(String name) {
        for (Script s : scripts) {
            if (s.name.equals(name)) return s;
        }

        return null;
    }

    public static void unloadScripts() {
        scripts = new ArrayList<>();
    }
}
