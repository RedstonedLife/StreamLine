package net.plasmere.streamline.commands.messaging;

import com.velocitypowered.api.command.CommandSource;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.objects.filters.ChatFilter;
import net.plasmere.streamline.objects.filters.FilterHandler;
import net.plasmere.streamline.scripts.ScriptsHandler;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.TextUtils;
import org.apache.commons.collections4.list.TreeList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ChatFilterCommand extends SLCommand {

    public ChatFilterCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void run(CommandSource sender, String[] args) {
        if (args.length < 2) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
            return;
        }

        switch (args[0]) {
            case "create":
                if (args.length < 7) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
                    return;
                }

                try {
                    String name = args[1];
                    boolean enabled = Boolean.parseBoolean(args[2]);
                    String scriptName = args[3];
                    String bypassPermission = args[4];
                    boolean blocked = Boolean.parseBoolean(args[5]);
                    String regex = args[6];
                    List<String> replacements = List.of(TextUtils.argsMinus(args, 0, 1, 2, 3, 4, 5, 6));

                    ChatFilter filter = FilterHandler.addFilter(new ChatFilter(name, enabled, scriptName, bypassPermission, blocked, regex, replacements));
                    MessagingUtils.sendChatFilterMessage(sender, filter, TextUtils.replaceAllSenderBungee(MessageConfUtils.filtersCommandCreate(), sender));
                } catch (Exception e) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                    e.printStackTrace();
                    return;
                }
                break;
            case "toggle":
                try {
                    FilterHandler.reloadAllFilters();

                    ChatFilter filter = FilterHandler.getFilterByName(TextUtils.argsToStringMinus(args, 0));
                    filter.toggleEnabled();
                    MessagingUtils.sendChatFilterMessage(sender, filter, TextUtils.replaceAllSenderBungee(MessageConfUtils.filtersCommandToggle(), sender));
                } catch (Exception e) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                    e.printStackTrace();
                    return;
                }
                break;
        }
    }

    @Override
    public Collection<String> onTabComplete(CommandSource sender, String[] args) {
        TreeList<String> options1 = new TreeList<>();
        options1.add("create");
        options1.add("toggle");

        if (args.length <= 1) {
            return TextUtils.getCompletion(options1, args[0]);
        }
        if (args.length == 2) {
            if (args[0].equals("toggle")) {
                return TextUtils.getCompletion(FilterHandler.getAllFiltersByName(), args[1]);
            }
        }

        if (args.length == 4) {
            if (ConfigUtils.scriptsEnabled()) {
                return TextUtils.getCompletion(ScriptsHandler.getScriptNames(), args[3]);
            }
        }

        return new ArrayList<>();
    }
}
