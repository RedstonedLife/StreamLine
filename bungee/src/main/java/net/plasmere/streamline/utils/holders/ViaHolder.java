package net.plasmere.streamline.utils.holders;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.ViaAPI;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.utils.MessagingUtils;

import java.util.UUID;

public class ViaHolder {
    public boolean enabled;
    public ViaHolder(){
        enabled = isPresent();
    }

    public boolean isPresent() {
        boolean present = StreamLine.getInstance().getProxy().getPluginManager().getPlugin("viaversion") != null || StreamLine.getInstance().getProxy().getPluginManager().getPlugin("ViaVersion") != null;

        if (! present) return present;

        try {
            ViaAPI<ProxiedPlayer> t = Via.getAPI();
            return true;
        } catch (Exception e) {
//            MessagingUtils.logWarning("ViaVersion support in Strealine is manually disabled due to a bug with ViaVersion on Velocity... :( Hopefully they fix it. :)");
//            MessagingUtils.logWarning("Error: " + e.getMessage());
            return false;
        }
    }

    public ViaAPI<ProxiedPlayer> via() {
        try {
            if (isPresent()) {
                return Via.getAPI();
            }
        } catch (Exception e) {
            MessagingUtils.logInfo("Could not process viaversion get due to an error:");
            e.printStackTrace();
        }

        return null;
    }

    public ProtocolVersion getVersion(int version){
        return ProtocolVersion.getProtocol(version);
    }

    public ProtocolVersion getProtocol(UUID uuid){
        return ProtocolVersion.getProtocol(via().getPlayerVersion(uuid));
    }
}