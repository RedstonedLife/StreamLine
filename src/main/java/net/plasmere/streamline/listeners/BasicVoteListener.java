package net.plasmere.streamline.listeners;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.support.forwarding.ForwardedVoteListener;
import com.vexsoftware.votifier.velocity.event.VotifierEvent;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.scripts.Script;
import net.plasmere.streamline.scripts.ScriptsHandler;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.UUIDUtils;

import java.util.UUID;

public class BasicVoteListener {
    public BasicVoteListener() {
        MessagingUtils.logInfo("Vote listener registered!");
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onVotifierEvent(VotifierEvent event) {
        if (! ConfigUtils.moduleBVotifierEnabled()) return;

        Vote vote = event.getVote();

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

        script.execute(StreamLine.getInstance().getProxy().getConsoleCommandSource(), PlayerUtils.getOrGetSavableUser(vote.getUsername()));
    }
}
