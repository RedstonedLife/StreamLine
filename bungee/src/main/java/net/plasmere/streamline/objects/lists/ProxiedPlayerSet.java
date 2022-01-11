package net.plasmere.streamline.objects.lists;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.plasmere.streamline.utils.PlayerUtils;

import java.util.Collection;
import java.util.TreeMap;

public class ProxiedPlayerSet {
    TreeMap<String, ProxiedPlayer> sorted = new TreeMap<>();

    public ProxiedPlayerSet(){

    }

    public ProxiedPlayerSet(Collection<? extends ProxiedPlayer> oldPlayers){
        addForPlayers(oldPlayers);
    }

    public void addForPlayers(Collection<? extends ProxiedPlayer> oldPlayers){
        for (ProxiedPlayer player : oldPlayers) {
            addPlayer(player);
        }
    }

    public void addPlayer(ProxiedPlayer player) {
        sorted.put(PlayerUtils.getSourceName(player), player);
    }

    public void remPlayer(ProxiedPlayer player){
        sorted.remove(PlayerUtils.getSourceName(player));
    }

    public ProxiedPlayer[] getAll() {
        ProxiedPlayer[] players = new ProxiedPlayer[sorted.size()];

        int i = 0;
        for (ProxiedPlayer player : sorted.values()){
            players[i] = player;
            i ++;
        }

        return players;
    }
}