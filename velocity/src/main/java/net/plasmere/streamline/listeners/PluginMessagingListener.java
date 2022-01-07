package net.plasmere.streamline.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.scripts.Script;
import net.plasmere.streamline.scripts.ScriptsHandler;
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
        if (subChannel.equalsIgnoreCase("send.displayname")) {
            // the receiver is a Player when a server talks to the proxy
            if (event.getTarget() instanceof Player) {
                Player receiver = (Player) event.getTarget();

                String data = in.readUTF();
                if (data.equals("")) data = PlayerUtils.getOrGetSavableUser(receiver).displayName;

                MessagingUtils.serveredUsernames.put(receiver, data);
            }
        }
        if (subChannel.equalsIgnoreCase("run.script")) {
            if (event.getTarget() instanceof Player) {
                Player receiver = (Player) event.getTarget();

                String data = in.readUTF();
                if (data.equals("")) data = PlayerUtils.getOrGetSavableUser(receiver).latestName;
                String scriptName = in.readUTF();
                if (! scriptName.equals("")) {
                    SavablePlayer player = PlayerUtils.getOrGetPlayerStat(data);

                    Script script = ScriptsHandler.getScript(scriptName);
                    if (script != null) {
                        script.execute(StreamLine.getInstance().getProxy().getConsoleCommandSource(), player);
                    }
                }
            }
        }
    }
}
