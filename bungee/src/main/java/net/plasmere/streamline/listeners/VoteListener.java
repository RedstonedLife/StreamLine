package net.plasmere.streamline.listeners;

import com.vexsoftware.votifier.bungee.events.VotifierEvent;
import com.vexsoftware.votifier.model.Vote;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.scripts.Script;
import net.plasmere.streamline.scripts.ScriptsHandler;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;

public class VoteListener implements Listener {
    public VoteListener() {
        MessagingUtils.logInfo("Vote listener registered!");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVote(VotifierEvent event){
        if (ConfigUtils.debug()) MessagingUtils.logWarning("Got vote!");

        if (! ConfigUtils.moduleBVotifierEnabled()) {
            if (ConfigUtils.debug()) MessagingUtils.logWarning("Could not handle vote as votifier was disabled in my config!");
            return;
        }

        Vote vote = event.getVote();

//        StreamLine.votes.addVotes(UUID.fromString(UUIDUtils.getCachedUUID(vote.getUsername())), 1);

        // TODO: Add config for this!

        if (! ConfigUtils.scriptsEnabled()) {
            MessagingUtils.logWarning("Could not handle a vote event as the scripts were not enabled!");
            return;
        }

        Script script = ScriptsHandler.getScript(ConfigUtils.moduleBVotifierRun());

        if (script == null) {
            MessagingUtils.logWarning("Could not handle a vote event as the script '" + ConfigUtils.moduleBVotifierRun() + "' was not found!");
            return;
        }

        script.execute(StreamLine.getInstance().getProxy().getConsole(), PlayerUtils.getOrGetSavableUser(vote.getUsername()));
    }
}
