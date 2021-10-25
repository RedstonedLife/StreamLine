package net.plasmere.streamline.commands;

import net.plasmere.streamline.StreamLine;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSource;
import net.md_5.bungee.api.chat.TextComponent;
import com.velocitypowered.api.proxy.Player;
import net.md_5.bungee.api.plugin.Command;

public class PingCommand extends Command {
    private String perm = "";

    public PingCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);

        this.perm = perm;
    }

    @Override
    public void execute(CommandSource sender, String[] args){
        if (sender instanceof Player){
            Player player = (Player) sender;

            if (player.hasPermission(perm)) {

                int ping = player.getPing();

                player.sendMessage(new TextComponent(ChatColor.GRAY + "Ping" +
                        ChatColor.DARK_GRAY + ": " +
                        ChatColor.GOLD + ping));
            } else {
                player.sendMessage(new TextComponent(ChatColor.RED + "Sorry, but you don't have permission to do this..."));
            }
        } else {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Sorry, but only a player can do this..."));
        }
    }
}
