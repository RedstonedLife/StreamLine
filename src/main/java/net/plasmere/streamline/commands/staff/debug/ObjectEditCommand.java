package net.plasmere.streamline.commands.staff.debug;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.config.from.From;
import net.plasmere.streamline.objects.savable.users.SavableUser;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ObjectEditCommand extends Command implements TabExecutor {
    public ObjectEditCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        try {
            From.FileType fileType = From.FileType.valueOf(args[0]);

            switch (fileType) {
                case CONFIG:
                    StreamLine.config.setObjectConf(args[1], args[2].contains(",") ? TextUtils.getStringListFromString(args[2]) : args[2]);
                    break;
                case TRANSLATION:
                    StreamLine.config.setObjectMess(args[1], args[2].contains(",") ? TextUtils.getStringListFromString(args[2]) : args[2]);
                    break;
                case COMMANDS:
                    StreamLine.config.setObjectCommand(args[1], args[2].contains(",") ? TextUtils.getStringListFromString(args[2]) : args[2]);
                    break;
                case DISCORDBOT:
                    StreamLine.config.setObjectDisBot(args[1], args[2].contains(",") ? TextUtils.getStringListFromString(args[2]) : args[2]);
                    break;
                case SERVERCONFIG:
                    StreamLine.serverConfig.setObject(args[1], args[2].contains(",") ? TextUtils.getStringListFromString(args[2]) : args[2]);
                    break;
                case CHATS:
                    StreamLine.chatConfig.setObject(args[1], args[2].contains(",") ? TextUtils.getStringListFromString(args[2]) : args[2]);
                    break;
                case RANKS:
                    StreamLine.ranksConfig.setObject(args[1], args[2].contains(",") ? TextUtils.getStringListFromString(args[2]) : args[2]);
                    break;
                case VOTES:
                    StreamLine.votes.setObject(args[1], args[2].contains(",") ? TextUtils.getStringListFromString(args[2]) : args[2]);
                    break;
            }
        } catch (Throwable e) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
//            e.printStackTrace();
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> options = new ArrayList<>();

        for (From.FileType fileType : From.FileType.values()) {
            options.add(fileType.name());
        }

        if (args.length <= 1) {
            return TextUtils.getCompletion(options, args[0]);
        }

        return new ArrayList<>();
    }
}
