package net.plasmere.streamline.objects.lists;

import com.velocitypowered.api.proxy.Player;

import java.util.Collection;
import java.util.TreeMap;

public class PlayerSet {
    TreeMap<String, Player> sorted = new TreeMap<>();

    public PlayerSet(){

    }

    public PlayerSet(Collection<? extends Player> oldPlayers){
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