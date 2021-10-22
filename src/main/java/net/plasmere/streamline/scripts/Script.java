package net.plasmere.streamline.scripts;

import net.md_5.bungee.api.CommandSender;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.Guild;
import net.plasmere.streamline.objects.lists.SingleSet;
import net.plasmere.streamline.objects.savable.users.SavableUser;
import net.plasmere.streamline.utils.GuildUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.TextUtils;

import java.io.File;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.TreeMap;

public class Script {
    public TreeMap<Integer, SingleSet<ScriptAs, String>> toExecute = new TreeMap<>();
    public String name;

    public Script(File file) {
        this.name = file.getName();
        try {
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String l = reader.nextLine();

                if (l.startsWith("#")) continue;

                if (l.startsWith("!")) {
                    toExecute.put(getLastKey(), new SingleSet<>(ScriptAs.OPERATOR, l.substring(1)));
                    continue;
                }
                if (l.startsWith("?")) {
                    toExecute.put(getLastKey(), new SingleSet<>(ScriptAs.CONSOLE, l.substring(1)));
                    continue;
                }
                if (l.startsWith(".")) {
                    toExecute.put(getLastKey(), new SingleSet<>(ScriptAs.SENDER, l.substring(1)));
                    continue;
                }
                toExecute.put(getLastKey(), new SingleSet<>(ScriptAs.PLAYER, l));
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getLastKey() {
        if (toExecute == null) {
            toExecute = new TreeMap<>();
            return 0;
        }
        try {
            return (toExecute.lastKey() == null ? 0 : toExecute.lastKey() + 1);
        } catch (NoSuchElementException e) {
            return 0;
        }
    }

    public void execute(CommandSender sender, SavableUser player) {
        for (Integer i : toExecute.keySet()) {
            switch (toExecute.get(i).key) {
                case CONSOLE:
                    StreamLine.getInstance().getProxy().getPluginManager().dispatchCommand(StreamLine.getInstance().getProxy().getConsole(), getVariablized(toExecute.get(i).value, sender, player));
                    break;
                case OPERATOR:
                    if (player.online) {
                        boolean bool = player.hasPermission("*");

                        if (! bool) player.setPermission("*", true);
                        StreamLine.getInstance().getProxy().getPluginManager().dispatchCommand(player.findSender(), getVariablized(toExecute.get(i).value, sender, player));
                        if (! bool) player.setPermission("*", false);
                    }
                    break;
                case SENDER:
                    StreamLine.getInstance().getProxy().getPluginManager().dispatchCommand(sender, getVariablized(toExecute.get(i).value, sender, player));
                    break;
                case PLAYER:
                    if (player.online) {
                        StreamLine.getInstance().getProxy().getPluginManager().dispatchCommand(player.findSender(), getVariablized(toExecute.get(i).value, sender, player));
                    }
                    break;
            }
        }
    }

    public String getVariablized(String string, CommandSender sender, SavableUser player) {
        return TextUtils.replaceAllSenderBungee(TextUtils.replaceAllPlayerBungee(string, player), sender);
    }

    public String toString() {
        return this.name;
    }
}
