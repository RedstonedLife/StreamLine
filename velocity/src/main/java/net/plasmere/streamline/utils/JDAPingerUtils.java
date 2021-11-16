package net.plasmere.streamline.utils;

import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.plasmere.streamline.StreamLine;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Collection;

public class JDAPingerUtils {
    private static final EmbedBuilder eb = new EmbedBuilder();

    private static int i = 0;

    private static String doPing(RegisteredServer server, StreamLine plugin){
        i++;
        StringBuilder text = new StringBuilder();
        
        try {
            Socket sock = new Socket(server.getServerInfo().getAddress().getAddress(), server.getServerInfo().getAddress().getPort());

            DataOutputStream out = new DataOutputStream(sock.getOutputStream());
            DataInputStream in = new DataInputStream(sock.getInputStream());

            out.write(0xFE);

            int b;
            StringBuilder str = new StringBuilder();

            try {
                while ((b = in.read()) != -1) {
                    if (b > 16 && b != 255 && b != 23 && b != 24) {
                        str.append((char) b);
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }

            String[] data = str.toString().split("§");

            text.append((data[0] == null) ? "not reachable" : "reachable");
        } catch (Exception e) {
            text.append("not reachable");
            plugin.getLogger().info("[ " + server.getServerInfo().getName().toLowerCase() + " : " + i + " / " + plugin.getProxy().getAllServers().size() + " ] " + server.getServerInfo().getAddress() + ":" +
                    server.getServerInfo().getAddress().getPort() + " (" + server.getPlayersConnected().size() + ") : " + text + " --> \n" + e.getMessage());
            return text.toString();
        }

        plugin.getLogger().info("[ " + server.getServerInfo().getName().toLowerCase() + " : " + i + " / " + plugin.getProxy().getAllServers().size() + " ] " + server.getServerInfo().getAddress() + ":" +
                server.getServerInfo().getAddress().getPort() + " (" + server.getPlayersConnected().size() + ") : " + text);

        return text.toString();
    }

    public static void sendMessage(TextChannel channel){
        StreamLine plugin = StreamLine.getInstance();

        eb.setDescription("Pinging " + plugin.getProxy().getAllServers().size() + " servers... Give me about " + plugin.getProxy().getAllServers().size() * 1.2 + " seconds...");
        channel.sendMessage(eb.build()).queue();
        Collection<RegisteredServer> servers = plugin.getProxy().getAllServers();
        try {
            i = 0;

            // DEBUG
            int it = 0;
            for (RegisteredServer server : servers){
                it++;
                plugin.getLogger().info("DEBUG : [ " + server.getServerInfo().getName().toLowerCase() + " : " + it + " / " + plugin.getProxy().getAllServers().size() + " ] " + server.getServerInfo().getAddress().getAddress() + ":" +
                        server.getServerInfo().getAddress().getPort() + " (" + server.getPlayersConnected().size() + ")");
            }

            for (RegisteredServer server : servers) {
                String msg = "";
                try {
                    msg = server.getServerInfo().getName().toUpperCase() + " " + i + " / " + plugin.getProxy().getAllServers().size() + " [ " + server.getServerInfo().getAddress().getAddress() + server.getServerInfo().getAddress().getPort() + " ] (Online: " +
                            server.getPlayersConnected().size() + ") : " + doPing(server, plugin) + "\n";
                    channel.sendMessage(eb.setDescription(msg).build()).queue();
                } catch (Exception e){
                    channel.sendMessage(eb.setDescription("Sorry, but the ports couldn't be checked...").build()).queue();
                }
            }
        } catch (NullPointerException n){
            n.printStackTrace();
        } catch (Exception e){
            plugin.getLogger().warn("An unknown error occurred with sending JDAPinger message...");
            e.printStackTrace();
        }
        plugin.getLogger().info("Sent ping message!");
    }
}
