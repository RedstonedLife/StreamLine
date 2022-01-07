package net.plasmere.streamline.commands.messaging;

import com.velocitypowered.api.command.CommandSource;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.TextUtils;

import java.util.*;

public class ChatHistoryCommand extends SLCommand {
    public ChatHistoryCommand(String base, String perm, String... aliases) {
        super(base, perm, aliases);
    }

    @Override
    public void run(CommandSource sender, String[] args) {
        if (args.length < 3) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
            return;
        }
        if (args.length > 4) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsLess());
            return;
        }

        SavablePlayer player = PlayerUtils.getOrGetPlayerStat(args[0]);

        if (player == null) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPlayer());
            return;
        }

        int view = ConfigUtils.chatHistoryViewDefault();

        if (args.length > 3) {
            try {
                view = Integer.parseInt(args[3]);
            } catch (Exception e) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd()
                            .replace("%class%", this.getClass().getName())
                    );
                return;
            }
        }

        if (view > ConfigUtils.chatHistoryViewMax()) {
            view = ConfigUtils.chatHistoryViewMax();
        }

        MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllSenderBungee(TextUtils.replaceAllPlayerBungee(MessageConfUtils.historyMessage()
                                , player)
                        , sender)
                .replace("%chat_bulk%", getChatBulk(player, view, args[1], args[2]))
        );
    }

    public String getChatBulk(SavablePlayer of, int view, String server, String timestampFrom) {
        TreeMap<Long, String> map = PlayerUtils.getChatHistory(of.uuid).getTimestampsWithMessageFrom(timestampFrom, server);
        StringBuilder builder = new StringBuilder();

        int i = 0;
        for (Long l : map.keySet()) {
            if (i >= view) break;

            if (i == view - 1 || i == map.size() - 1) {
                builder.append(MessageConfUtils.historyChatBulk()
                        .replace("%timestamp%", new Date(l).toString())
                        .replace("%message%", map.get(l))
                );
            } else  {
                builder.append(MessageConfUtils.historyChatBulk()
                        .replace("%timestamp%", new Date(l).toString())
                        .replace("%message%", map.get(l))
                ).append("\n");
            }
            i ++;
        }

        return builder.toString();
    }

    @Override
    public Collection<String> onTabComplete(CommandSource sender, String[] args) {
        if (args.length <= 1) {
            return TextUtils.getCompletion(PlayerUtils.getPlayerNamesForAllOnline(), args[0]);
        }

        SavablePlayer player = PlayerUtils.getOrGetPlayerStat(args[0]);

        if (args.length == 2) {
            if (player == null) return new ArrayList<>();

            return TextUtils.getCompletion(PlayerUtils.getChatHistory(player.uuid).getTalkedInServers(), args[1]);
        }

        if (args.length == 3) {
            if (player == null) return new ArrayList<>();

            return TextUtils.getCompletion(PlayerUtils.getChatHistory(player.uuid).getTimestamps(args[1]), args[2]);
        }

        if (args.length == 4) {
            if (player == null) return new ArrayList<>();

            return List.of(String.valueOf(ConfigUtils.chatHistoryViewDefault()));
        }

        return new ArrayList<>();
    }
}
