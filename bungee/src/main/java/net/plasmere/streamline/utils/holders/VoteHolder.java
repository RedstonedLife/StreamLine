package net.plasmere.streamline.utils.holders;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.ViaAPI;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.md_5.bungee.api.ProxyServer;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.listeners.VoteListener;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PluginUtils;

import java.util.UUID;

public class VoteHolder {
    public boolean enabled;

    public VoteHolder(){
        enabled = isPresent();
    }

    public boolean isPresent(){
        if (ProxyServer.getInstance().getPluginManager().getPlugin("NuVotifier") == null) {
            return false;
        }

        try {
            PluginUtils.registerListener(StreamLine.getInstance(), new VoteListener());
            return true;
        } catch (Exception e) {
            MessagingUtils.logSevere("Votifier not loaded... Disabling Votifier support...");
        }
        return false;
    }
}