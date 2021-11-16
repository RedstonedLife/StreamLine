package net.plasmere.streamline.objects.filters;

import com.velocitypowered.api.command.CommandSource;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.objects.lists.SingleSet;
import net.plasmere.streamline.scripts.Script;
import net.plasmere.streamline.scripts.ScriptsHandler;
import net.plasmere.streamline.utils.PlayerUtils;

import java.util.List;
import java.util.Random;

public class ChatFilter {
    public String name;
    public boolean enabled;
    public String scriptName;
    public String bypassPermission;
    public boolean blocked;
    public String regex;
    public List<String> replacements;

    public ChatFilter(String name, boolean enabled, String scriptName, String bypassPermission, boolean blocked, String regex, List<String> replacements) {
        this.name = name;
        this.enabled = enabled;
        this.scriptName = scriptName;
        this.bypassPermission = bypassPermission;
        this.blocked = blocked;
        this.regex = regex;
        this.replacements = replacements;
    }

    public SingleSet<Boolean, String> applyFilter(String toFilter, CommandSource involved) {
        if (checkBypasses(involved)) return new SingleSet<>(false, toFilter);

        boolean isDifferent = false;

        Random RNG = new Random();
        int replaceWith = RNG.nextInt(this.replacements.size());
        if (replaceWith == 0) replaceWith ++;
        String toReturn = toFilter.replaceAll(this.regex, this.replacements.get(replaceWith - 1));

        if (! toReturn.equals(toFilter)) isDifferent = true;

        if (isDifferent) {
            Script script = ScriptsHandler.getScript(scriptName);
            if (script != null) script.execute(StreamLine.getProxy().getConsoleCommandSource(), PlayerUtils.getOrGetSavableUser(involved));
        }

        return new SingleSet<>(this.blocked && isDifferent, toReturn);
    }

    public boolean checkBypasses(CommandSource checkOn) {
        return checkOn.hasPermission(this.bypassPermission);
    }

    public boolean toggleEnabled() {
        this.enabled = ! this.enabled;
        save();
        return this.enabled;
    }

    public void save() {
        StreamLine.chatFilters.saveFilter(this);
    }
}
