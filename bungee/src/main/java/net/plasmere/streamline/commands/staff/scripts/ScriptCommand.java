package net.plasmere.streamline.commands.staff.scripts;

import net.md_5.bungee.api.CommandSender;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.objects.savable.users.SavableUser;
import net.plasmere.streamline.scripts.Script;
import net.plasmere.streamline.scripts.ScriptsHandler;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.TextUtils;
import org.apache.commons.collections4.list.TreeList;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public class ScriptCommand extends SLCommand {
    public ScriptCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (args.length < 3) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
            return;
        }
        if (args.length > 3) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsLess());
            return;
        }

        switch (args[0]) {
            case "run":
            default:
                SavableUser user = PlayerUtils.getOrGetSavableUser(args[2]);
                Script script = ScriptsHandler.getScript(args[1]);

                if (user == null) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPlayer());
                    return;
                }

                if (script == null) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.scriptNoScript());
                    return;
                }

                script.execute(sender, user);
                MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllSenderBungee(TextUtils.replaceAllPlayerBungee(MessageConfUtils.scriptMessage()
                        .replace("%script%", script.name)
                        , user
                ), sender));
                break;
        }
    }

    @Override
    public Collection<String> tabComplete(CommandSender sender, String[] args) {
        TreeList<String> scripts = new TreeList<>();

        File folder = StreamLine.getInstance().getScriptsDir();
        File[] files = folder.listFiles();

        if (files == null) return scripts;

        for (File file : files) {
            if (file.isDirectory()) continue;
            if (! file.getName().endsWith(".sl")) continue;

            scripts.add(file.getName().replace(".properties", ""));
        }

        TreeList<String> options1 = new TreeList<>();
        options1.add("run");

        if (args.length == 1) {
            return TextUtils.getCompletion(options1, args[0]);
        }
        if (args.length == 2) {
            return TextUtils.getCompletion(scripts, args[1]);
        }
        if (args.length == 3) {
            return TextUtils.getCompletion(PlayerUtils.getPlayerNamesForAllOnline(), args[2]);
        }

        return new ArrayList<>();
    }
}
