package net.plasmere.streamline.commands.messaging;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.objects.savable.users.ConsolePlayer;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.objects.savable.users.SavableUser;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.TextUtils;
import net.plasmere.streamline.utils.UUIDUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FriendCommand extends SLCommand {
    public FriendCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void run(CommandSource sender, String[] args) {
        SavableUser stat = PlayerUtils.getOrCreateSavableUser(sender);

        if (stat == null) {
            stat = PlayerUtils.getOrCreateSavableUser(sender);
            if (stat == null) {
                MessagingUtils.logSevere("CANNOT INSTANTIATE THE PLAYER: " + PlayerUtils.getSourceName(sender));
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                return;
            }
        }

        if (args.length <= 0) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
        } else if (args.length < 2 && args[0].equals("list")) {
            MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllPlayerBungee(MessageConfUtils.friendListMain()
                    .replace("%friends%", PlayerUtils.getFriended(stat))
                    .replace("%pending-to%", PlayerUtils.getPTFriended(stat))
                    .replace("%pending-from%", PlayerUtils.getPFFriended(stat))
            , stat));
        } else if (args.length < 2) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
        } else if (args.length > 2) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsLess());
        } else {
            SavableUser other;

            if (args[1].equals("%")) {
                other = PlayerUtils.getOrCreateSUByUUID("%");
            } else {
                if (! PlayerUtils.exists(args[1])) {
                    MessagingUtils.sendBUserMessage(sender, PlayerUtils.noStatsFound);
                    return;
                }

                other = PlayerUtils.getOrGetPlayerStat(args[1]);
            }

            if (other == null) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPlayer());
                return;
            }

            switch (args[0]) {
                case "add":
                case "request":
                    if (stat.equals(other)) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.friendReqNSelf());
                        return;
                    }

                    if (stat.friendList.contains(other.uuid)) {
                        MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllPlayerBungee(MessageConfUtils.friendReqAlready(), sender));
                        return;
                    }

                    if (other.ignoredList.contains(stat.uuid)) {
                        MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllPlayerBungee(MessageConfUtils.friendReqIgnored(), sender));
                        return;
                    }

                    stat.tryAddNewPendingToFriend(other.uuid);
                    other.tryAddNewPendingFromFriend(stat.uuid);
                    MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllPlayerBungee(MessageConfUtils.friendReqSelf(), other));
                    if ((other instanceof SavablePlayer && ((SavablePlayer) other).online) || other instanceof ConsolePlayer) {
                        MessagingUtils.sendBUserMessage(other.findSender(), TextUtils.replaceAllSenderBungee(MessageConfUtils.friendReqOther(), sender));
                    }
                    break;
                case "accept":
                    if (! stat.pendingFromFriendList.contains(other.uuid)) {
                        MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllPlayerBungee(MessageConfUtils.friendAcceptNone(), sender));
                        return;
                    }

                    stat.tryAddNewFriend(other.uuid);
                    other.tryAddNewFriend(stat.uuid);
                    other.tryRemPendingToFriend(stat.uuid);
                    stat.tryRemPendingFromFriend(other.uuid);

                    MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllPlayerBungee(MessageConfUtils.friendAcceptSelf(), other));

                    if ((other instanceof SavablePlayer && ((SavablePlayer) other).online) || other instanceof ConsolePlayer) {
                        MessagingUtils.sendBUserMessage(other.findSender(), TextUtils.replaceAllSenderBungee(MessageConfUtils.friendAcceptOther(), sender));
                    }
                    break;
                case "deny":
                    if (! stat.pendingFromFriendList.contains(other.uuid)) {
                        MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllPlayerBungee(MessageConfUtils.friendDenyNone(), sender));
                        return;
                    }

                    stat.tryRemPendingFromFriend(other.uuid);
                    other.tryRemPendingToFriend(stat.uuid);

                    MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllPlayerBungee(MessageConfUtils.friendDenySelf(), other));
                    if ((other instanceof SavablePlayer && ((SavablePlayer) other).online) || other instanceof ConsolePlayer) {
                        MessagingUtils.sendBUserMessage(other.findSender(), TextUtils.replaceAllSenderBungee(MessageConfUtils.friendDenyOther(), sender));
                    }
                    break;
                case "remove":
                    if (stat.equals(other)) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.friendRemNSelf());
                        return;
                    }

                    if (! stat.friendList.contains(other.uuid)) {
                        MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllPlayerBungee(MessageConfUtils.friendRemAlready(), sender));
                        return;
                    }

                    stat.tryRemPendingToFriend(other.uuid);
                    other.tryRemPendingToFriend(stat.uuid);
                    stat.tryRemPendingFromFriend(other.uuid);
                    other.tryRemPendingFromFriend(stat.uuid);
                    stat.tryRemFriend(other.uuid);
                    other.tryRemFriend(stat.uuid);
                    MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllPlayerBungee(MessageConfUtils.friendRemSelf(), other));
                    if ((other instanceof SavablePlayer && ((SavablePlayer) other).online) || other instanceof ConsolePlayer) {
                        MessagingUtils.sendBUserMessage(other.findSender(), TextUtils.replaceAllSenderBungee(MessageConfUtils.friendRemOther(), sender));
                    }
                    break;
                case "list":
                    MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllPlayerBungee(MessageConfUtils.friendListMain()
                            .replace("%friends%", PlayerUtils.getFriended(other))
                            .replace("%pending-to%", PlayerUtils.getPTFriended(other))
                            .replace("%pending-from%", PlayerUtils.getPFFriended(other))
                    , sender));
                    break;
            }
        }
    }

    @Override
    public Iterable<String> onTabComplete(final CommandSource sender, final String[] args) {
        if (sender instanceof Player) {
            Collection<Player> players = StreamLine.getInstance().getProxy().getAllPlayers();
            List<String> strPlayers = new ArrayList<>();
            List<String> friends = new ArrayList<>();
            List<String> pending = new ArrayList<>();

            SavableUser player = PlayerUtils.getOrCreateSavableUser(sender);

            if (player == null) return new ArrayList<>();

            for (String uuid : player.friendList) {
                friends.add(UUIDUtils.getCachedName(uuid));
            }

            for (String uuid : player.pendingFromFriendList) {
                pending.add(UUIDUtils.getCachedName(uuid));
            }

            for (Player pl : players) {
                if (pl.equals(sender)) continue;
                strPlayers.add(PlayerUtils.getSourceName(pl));
            }

            strPlayers.add("%");

            List<String> options = new ArrayList<>();

            options.add("request");
            options.add("accept");
            options.add("deny");
            options.add("remove");
            options.add("list");
            options.add("add");

            if (args.length == 1) {
                return TextUtils.getCompletion(options, args[0]);
            } else if (args.length == 2) {
                if (args[0].equals("accept") || args[0].equals("deny")) {
                    return TextUtils.getCompletion(pending, args[1]);
                } else if (args[0].equals("remove")) {
                    return TextUtils.getCompletion(friends, args[1]);
                } else {
                    return TextUtils.getCompletion(strPlayers, args[1]);
                }
            }

            return new ArrayList<>();
        }

        return new ArrayList<>();
    }
}
