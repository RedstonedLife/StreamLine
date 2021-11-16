package net.plasmere.streamline.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.scripts.Script;
import net.plasmere.streamline.scripts.ScriptsHandler;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;

public class PluginMessagingListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPluginMessage(PluginMessageEvent event) {
        /*
        https://www.spigotmc.org/wiki/sending-a-custom-plugin-message-from-bungeecord/
         */

        if (! event.getTag().equalsIgnoreCase(StreamLine.customChannel)) return;

        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        String subChannel = in.readUTF();
        if (subChannel.equalsIgnoreCase("send.displayname")) {
            // the receiver is a ProxiedPlayer when a server talks to the proxy
            if (event.getReceiver() instanceof ProxiedPlayer) {
                ProxiedPlayer receiver = (ProxiedPlayer) event.getReceiver();

                String data = in.readUTF();
                if (data.equals("")) data = PlayerUtils.getOrCreatePlayerStat(receiver).displayName;

                MessagingUtils.serveredUsernames.put(receiver, data);
            }
        }
        if (subChannel.equalsIgnoreCase("run.script")) {
            if (event.getReceiver() instanceof ProxiedPlayer) {
                ProxiedPlayer receiver = (ProxiedPlayer) event.getReceiver();

                String data = in.readUTF();
                if (data.equals("")) data = PlayerUtils.getOrCreatePlayerStat(receiver).latestName;
                String scriptName = in.readUTF();
                if (! scriptName.equals("")) {
                    SavablePlayer player = PlayerUtils.getOrGetPlayerStat(data);

                    Script script = ScriptsHandler.getScript(scriptName);
                    if (script != null) {
                        script.execute(StreamLine.getInstance().getProxy().getConsole(), player);
                    }
                }
            }
        }
    }
}
