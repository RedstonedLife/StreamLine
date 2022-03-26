package net.plasmere.streamline.objects.configs.obj;

import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.scripts.ScriptsHandler;
import net.plasmere.streamline.utils.MessagingUtils;

public class AliasCommand {
    public String identifier;
    public String permission;
    public String scriptName;
    public boolean enabled;

    public AliasCommand(String identifier, String permission, String scriptName) {
        this.identifier = identifier;
        this.permission = permission;
        this.scriptName = scriptName;
        this.enabled = isValid();
    }

    public boolean isValid() {
        try {
            validate();
            return true;
        } catch (Exception e) {
            MessagingUtils.logWarning("AliasCommand with identifier of '" + this.identifier + "' errored out due to: " + e.getMessage());
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
}