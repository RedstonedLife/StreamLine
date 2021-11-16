package net.plasmere.streamline.objects.lists;

import com.velocitypowered.api.proxy.Player;
import net.plasmere.streamline.utils.PlayerUtils;

import java.util.Collection;
import java.util.TreeMap;

public class ProxiedPlayerSet {
    TreeMap<String, Player> sorted = new TreeMap<>();

    public ProxiedPlayerSet(){

    }

    public ProxiedPlayerSet(Collection<? extends Player> oldPlayers){
        addForPlayers(oldPlayers);
    }

    public void addForPlayers(Collection<? extends Player> oldPlayers){
        for (Player player : oldPlayers) {
            addPlayer(player);
        }
    }

    public void addPlayer(Player player) {
        sorted.put(PlayerUtils.getSourceName(player), player);
    }

    public void remPlayer(Player player){
        sorted.remove(PlayerUtils.getSourceName(player));
    }

    public Player[] getAll() {
        Player[] players = new Player[sorted.size()];

        int i = 0;
        for (Player player : sorted.values()){
            players[i] = player;
            i ++;
        }

        return players;
    }
}