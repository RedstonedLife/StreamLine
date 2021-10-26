package net.plasmere.streamline.listeners;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.utils.TextUtils;

import java.util.UUID;

public class ProxyPingListener {
    @Subscribe(order = PostOrder.FIRST)
    public void onProxyPing(ProxyPingEvent event){
        ServerPing response = event.getPing();

        if (response == null) return;

        ServerPing.Builder builder = response.asBuilder();

        ServerPing.Players players = response.getPlayers().get();
        int onlinePlayers = players.getOnline();
        int maxPlayers = players.getMax();

        if (ConfigUtils.scOnlinePlayers) {
            onlinePlayers = StreamLine.serverConfig.onlinePlayers();

            builder.onlinePlayers(onlinePlayers);
        }

        if (ConfigUtils.scMaxPlayers) {
            maxPlayers = StreamLine.serverConfig.maxPlayers();

            builder.maximumPlayers(maxPlayers);
        }

        if (ConfigUtils.scMOTD) {
            builder.description(TextUtils.clhText(StreamLine.getInstance().getCurrentMOTD()
                        .replace("%online%", String.valueOf(onlinePlayers))
                        .replace("%max%", String.valueOf(maxPlayers))
                    , ConfigUtils.linkPre));

//            if (ConfigUtils.debug) MessagingUtils.logInfo(TextUtils.codedString(StreamLine.getInstance().getCurrentMOTD()
//                    .replace("%online%", String.valueOf(StreamLine.getInstance().getProxy().getAllPlayers().size()))
//                    .replace("%max%", String.valueOf(StreamLine.getInstance().getProxy().getConfig().getPlayerLimit()))));
        }

        if (ConfigUtils.scVersion) {
            builder.version(new ServerPing.Version(response.getVersion().getProtocol(), StreamLine.serverConfig.getVersion()));

//            if (ConfigUtils.debug) MessagingUtils.logInfo(StreamLine.serverConfig.getVersion());
        }

        if (ConfigUtils.scSample) {
            UUID fake = new UUID(0, 0);
            String[] sampleString = StreamLine.serverConfig.getSampleArray();
            ServerPing.SamplePlayer[] sample = new ServerPing.SamplePlayer[sampleString.length];

            for (int i = 0; i < sampleString.length; i++) {
                sample[i] = new ServerPing.SamplePlayer(sampleString[i]
                        .replace("%online%", String.valueOf(onlinePlayers))
                        .replace("%max%", String.valueOf(maxPlayers))
                        , fake);
            }

            builder.samplePlayers(sample);

//            if (ConfigUtils.debug) {
//                for (String s : sampleString) {
//                    MessagingUtils.logInfo(s);
//                }
//            }
        }

        event.setPing(builder.build());
    }
}
