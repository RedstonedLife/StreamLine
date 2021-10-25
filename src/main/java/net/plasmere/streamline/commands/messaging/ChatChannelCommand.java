package net.plasmere.streamline.commands.messaging;

import net.md_5.bungee.api.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.chats.Chat;
import net.plasmere.streamline.objects.chats.ChatsHandler;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.utils.*;

import java.util.ArrayList;
import java.util.Arrays;

public class ChatChannelCommand extends Command implements TabExecutor {

    public ChatChannelCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void execute(CommandSource sender, String[] args) {
        if (sender instanceof Player) {
            SavablePlayer player = PlayerUtils.getPlayerStat(sender);
            if (player == null) return;

            if (args.length <= 0) {
                boolean allowGlobal = StreamLine.serverConfig.getAllowGlobal();
                boolean allowLocal = StreamLine.serverConfig.getAllowLocal();

                if (allowGlobal || allowLocal) {
                    if (! allowGlobal && player.chatIdentifier.equals(ChatsHandler.getChannel("global"))) {
                        player.setChat("local", "network");
                        return;
                    }
                    if (! allowLocal && player.chatIdentifier.equals(ChatsHandler.getChannel("local"))) {
                        player.setChat("global", "network");
                        return;
                    }

                    if (player.chatIdentifier.equals(ChatsHandler.getChannel("global"))) player.setChat("local", "network");
                    if (player.chatIdentifier.equals(ChatsHandler.getChannel("local"))) player.setChat("global", "network");
                    return;
                } else {
                    player.setChat("local", "network");
                }

                return;
            }

            if (args.length < 2) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
                return;
            }

            if (args.length > 2) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsLess());
                return;
            }

            if (! args[1].contains("-") && TextUtils.equalsAny(args[0], Arrays.asList("party", "guild"))) {
                args[1] = UUIDUtils.getCachedUUID(args[1]);
            }

            Chat chat = ChatsHandler.getChat(args[0], args[1]);

            if (chat == null) {
                if (player.hasPermission(StreamLine.chatConfig.getDefaultPerm(ChatsHandler.getChannel(args[0])))) {
                    player.setChat(args[0], args[1]);
                } else {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPerm());
                }
                return;
            }

            if (player.hasPermission(chat.bypassPermission)) {
                player.setChat(args[0], args[1]);
            } else {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPerm());
            }
        } else {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.onlyPlayers());
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSource sender, String[] args) {
        if (args.length <= 1) {
            return TextUtils.getCompletion(ChatsHandler.getChannelsAsStrings(), args[0]);
        }
        if (args.length == 2) {
            if (TextUtils.equalsAny(args[0], ChatsHandler.getChannelsAsStrings())) {
                return TextUtils.getCompletion(ChatsHandler.getPossibleIdentifiersAsStringsByChannelPermissionedByChatChannel(sender, args[0]), args[1]);
            }
        }

        return new ArrayList<>();
    }
}
