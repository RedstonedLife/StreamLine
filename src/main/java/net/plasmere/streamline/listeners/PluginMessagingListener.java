package net.plasmere.streamline.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;

public class PluginMessagingListener {
    @Subscribe(order = PostOrder.FIRST)
    public void onPluginMessage(PluginMessageEvent event) {
        /*
        https://www.spigotmc.org/wiki/sending-a-custom-plugin-message-from-bungeecord/
         */

        if (! event.getIdentifier().equals(StreamLine.customIdentifier)) return;

        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        String subChannel = in.readUTF();
        if (subChannel.equalsIgnoreCase("send.displayname"))
        {
            // the receiver is a Player when a server talks to the proxy
            if (event.getTarget() instanceof Player)
            {
                Player receiver = (Player) event.getSource();

                String data = in.readUTF();
                if (data.equals("")) data = PlayerUtils.getOrCreatePlayerStat(receiver).displayName;

                MessagingUtils.serveredUsernames.put(receiver, data);
            }
        }
    }
}
