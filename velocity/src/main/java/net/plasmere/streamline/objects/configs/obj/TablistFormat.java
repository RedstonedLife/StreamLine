package net.plasmere.streamline.objects.configs.obj;

import net.plasmere.streamline.utils.MessagingUtils;

import java.util.List;

public class TablistFormat {
    public String identifier;
    public int id;
    public String playerName;
    public List<String> header;
    public List<String> footer;
    public boolean enabled;

    public TablistFormat(String identifier, String playerName, List<String> header, List<String> footer) {
        this.identifier = identifier;
        this.playerName = playerName;
        this.header = header;
        this.footer = footer;
        this.enabled = this.isValid();
    }

    public boolean isValid() {
        if (this.identifier.equals("general")) {
            this.id = 0;
            return true;
        }
        try {
            this.id = validate();
            return true;
        } catch (Exception e) {
            MessagingUtils.logWarning("Tablist format with identifier of '" + this.identifier + "' errored out due to: " + e.getMessage());
            return false;
        }
    }

    public int validate() throws Exception {
        int trial = 0;
        try {
            trial = Integer.parseInt(identifier);
        } catch (Exception e) {
            throw new Exception("Not a valid number for tablist format...");
        }
        return trial;
    }
}
