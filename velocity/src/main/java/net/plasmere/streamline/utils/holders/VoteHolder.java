package net.plasmere.streamline.utils.holders;

import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.listeners.BasicVoteListener;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PluginUtils;

public class VoteHolder {
    public boolean enabled;

    public VoteHolder(){
        enabled = isPresent();
    }

    public boolean isPresent(){
        if (StreamLine.getProxy().getPluginManager().getPlugin("nuvotifier").isEmpty()) {
            return false;
        }

        try {
//            PluginUtils.registerListener(StreamLine.getInstance(), new BasicVoteListener());
            return true;
        } catch (Exception e) {
            MessagingUtils.logSevere("Votifier not loaded... Disabling Votifier support...");
        }
        return false;
    }
}