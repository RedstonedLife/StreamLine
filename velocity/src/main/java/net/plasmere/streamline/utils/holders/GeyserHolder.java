package net.plasmere.streamline.utils.holders;

import com.velocitypowered.api.proxy.Player;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.objects.GeyserFile;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import org.geysermc.geyser.GeyserImpl;
import org.geysermc.geyser.GeyserMain;
import org.geysermc.geyser.session.GeyserSession;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GeyserHolder {
    public File playerPath = new File(StreamLine.getInstance().getPlDir(), "geyser" + File.separator);
    public GeyserImpl geyser;
    public boolean enabled;
    public GeyserFile file;

    public GeyserHolder(){
        enabled = isPresent();

        if (enabled) {
            setUpPath();
            file = new GeyserFile(false);
        }
    }

    public boolean isPresent(){
        if (StreamLine.getProxy().getPluginManager().getPlugin("geyser").isEmpty() && StreamLine.getProxy().getPluginManager().getPlugin("Geyser").isEmpty()) {
            return false;
        }

        try {
            this.geyser = GeyserImpl.getInstance();
            if (this.geyser == null) MessagingUtils.logWarning("Geyser is installed, but we could not get the instance!");
            else MessagingUtils.logInfo("Geyser is installed! Using Geyser support!");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void setUpPath(){
        if (! playerPath.exists()) {
            if (! playerPath.mkdirs()) {
                MessagingUtils.logSevere("Error setting up the Geyser player path...");
            }
        }
    }

    public void checkConnector(){
        if (geyser == null) this.geyser = GeyserImpl.getInstance();
    }

    public List<GeyserSession> getPlayers() {
        checkConnector();

        return new ArrayList<>(geyser.onlineConnections());
    }

    public String getName(GeyserSession session) {
        return session.getPlayerEntity().getNametag();
    }

    public String getXUID(GeyserSession session) {
        return session.getAuthData().xuid();
    }

    public boolean isGeyserPlayer(Player player) {
        checkConnector();

        for (GeyserSession session : getPlayers()) {
            if (getName(session).equals(PlayerUtils.getSourceName(player))) return true;
        }

        return false;
    }

    public boolean isGeyserPlayer(String player) {
        checkConnector();

        for (GeyserSession session : getPlayers()) {
            if (getName(session).equals(player)) return true;
        }

        return false;
    }

    public String getGeyserUUID(String player) {
        checkConnector();

        for (GeyserSession session : getPlayers()) {
            if (getName(session).equals(player)) {
                return getXUID(session);
            }
        }

        return null;
    }

    public Player getPPlayerByUUID(String uuid){
        checkConnector();

        for (GeyserSession session : getPlayers()) {
            if (getXUID(session).equals(uuid)) StreamLine.getProxy().getPlayer(getName(session));
        }

        return null;
    }
}
