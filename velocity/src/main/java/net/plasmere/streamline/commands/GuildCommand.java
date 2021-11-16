package net.plasmere.streamline.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.CommandsConfUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.SavableGuild;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.objects.savable.users.SavableUser;
import net.plasmere.streamline.utils.GuildUtils;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.TextUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GuildCommand extends SLCommand {
    public GuildCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void run(CommandSource sender, String[] args) {
        SavableUser stat = PlayerUtils.getOrGetSavableUser(sender);

        if (stat == null) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorNoYou());
            return;
        }

        // Usage: /guild <join !|leave !|create !|promote !|demote !|chat !|list !|open !|close !|disband !|accept !|deny !|invite !|kick|mute|warp>
        if (args.length <= 0 || args[0].length() <= 0) {
            try {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
            } catch (Exception e) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                e.printStackTrace();
            }
        } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBGuildJoinAliases())) {
            if (! sender.hasPermission(CommandsConfUtils.comBGuildJoinPermission())) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPerm());
                return;
            }

            if (args.length <= 1) {
                try {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
                } catch (Exception e) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                    e.printStackTrace();
                }
            } else {
                try {
                    SavableUser user = PlayerUtils.getOrGetSavableUser(args[1]);
                    if (user == null) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPlayer());
                        return;
                    }

                    GuildUtils.joinGuild(stat, user);
                } catch (Exception e) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                    e.printStackTrace();
                }
            }
        } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBGuildLeaveAliases())) {
            if (! sender.hasPermission(CommandsConfUtils.comBGuildLeavePermission())) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPerm());
                return;
            }

            try {
                GuildUtils.leaveGuild(stat);
            } catch (Exception e) {
                MessagingUtils.sendBUserMessage(stat.findSender(), MessageConfUtils.bungeeCommandErrorUnd());
                e.printStackTrace();
            }
        } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBGuildCreateAliases())) {
            if (! sender.hasPermission(CommandsConfUtils.comBGuildCreatePermission())) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPerm());
                return;
            }

            if (args.length <= 1) {
                try {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
                } catch (Exception e) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                    e.printStackTrace();
                }
            } else {
                try {
                    GuildUtils.createGuild(stat, TextUtils.argsToStringMinus(args, 0));
                } catch (Exception e) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                    e.printStackTrace();
                }
            }
        } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBGuildPromoteAliases())) {
            if (! sender.hasPermission(CommandsConfUtils.comBGuildPromotePermission())) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPerm());
                return;
            }

            if (args.length <= 1) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
            } else {
                try {
                    SavableUser user = PlayerUtils.getOrGetSavableUser(args[1]);
                    if (user == null) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPlayer());
                        return;
                    }

                    GuildUtils.promotePlayer(stat, user);
                } catch (Exception e) {
                    MessagingUtils.sendBUserMessage(stat.findSender(), MessageConfUtils.bungeeCommandErrorUnd());
                    e.printStackTrace();
                }
            }
        } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBGuildDemoteAliases())) {
            if (! sender.hasPermission(CommandsConfUtils.comBGuildDemotePermission())) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPerm());
                return;
            }

            if (args.length <= 1) {
                try {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
                } catch (Exception e) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                    e.printStackTrace();
                }
            } else {
                try {
                    SavableUser user = PlayerUtils.getOrGetSavableUser(args[1]);
                    if (user == null) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPlayer());
                        return;
                    }

                    GuildUtils.demotePlayer(stat, user);
                } catch (Exception e) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                    e.printStackTrace();
                }
            }
        } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBGuildChatAliases())) {
            if (! sender.hasPermission(CommandsConfUtils.comBGuildChatPermission())) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPerm());
                return;
            }

            if (args.length <= 1) {
                try {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
                } catch (Exception e) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                    e.printStackTrace();
                }
            } else {
                try {
                    GuildUtils.sendChat(stat, TextUtils.argsToStringMinus(args, 0));
                } catch (Exception e) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                    e.printStackTrace();
                }
            }
        } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBGuildListAliases())) {
            if (! sender.hasPermission(CommandsConfUtils.comBGuildListPermission())) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPerm());
                return;
            }

            try {
                GuildUtils.listGuild(stat);
            } catch (Exception e) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                e.printStackTrace();
            }
        } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBGuildOpenAliases())) {
            if (! sender.hasPermission(CommandsConfUtils.comBGuildOpenPermission())) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPerm());
                return;
            }

            try {
                GuildUtils.openGuild(stat);
            } catch (Exception e) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                e.printStackTrace();
            }
        } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBGuildCloseAliases())) {
            if (! sender.hasPermission(CommandsConfUtils.comBGuildClosePermission())) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPerm());
                return;
            }

            try {
                GuildUtils.closeGuild(stat);
            } catch (Exception e) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                e.printStackTrace();
            }
        } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBGuildDisbandAliases())) {
            if (! sender.hasPermission(CommandsConfUtils.comBGuildDisbandPermission())) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPerm());
                return;
            }

            try {
                GuildUtils.disband(stat);
            } catch (Throwable e) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                e.printStackTrace();
            }
        } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBGuildAcceptAliases())) {
            if (! sender.hasPermission(CommandsConfUtils.comBGuildAcceptPermission())) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPerm());
                return;
            }

            if (args.length <= 1) {
                try {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
                } catch (Exception e) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                    e.printStackTrace();
                }
            } else {
                try {
                    SavableUser user = PlayerUtils.getOrGetSavableUser(args[1]);
                    if (user == null) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPlayer());
                        return;
                    }

                    GuildUtils.acceptInvite(stat, user);
                } catch (Exception e) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                    e.printStackTrace();
                }
            }
        } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBGuildDenyAliases())) {
            if (! sender.hasPermission(CommandsConfUtils.comBGuildDenyPermission())) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPerm());
                return;
            }

            if (args.length <= 1) {
                try {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
                } catch (Exception e) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                    e.printStackTrace();
                }
            } else {
                try {
                    SavableUser user = PlayerUtils.getOrGetSavableUser(args[1]);
                    if (user == null) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPlayer());
                        return;
                    }

                    GuildUtils.denyInvite(stat, user);
                } catch (Exception e) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                    e.printStackTrace();
                }
            }
        } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBGuildInvAliases())) {
            if (! sender.hasPermission(CommandsConfUtils.comBGuildInvitePermission())) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPerm());
                return;
            }

            if (args.length <= 1) {
                try {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
                } catch (Exception e) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                    e.printStackTrace();
                }
            } else {
                try {
                    SavableUser user = PlayerUtils.getOrGetSavableUser(args[1]);
                    if (user == null) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPlayer());
                        return;
                    }

                    GuildUtils.sendInvite(user, stat);
                } catch (Exception e) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                    e.printStackTrace();
                }
            }
        } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBGuildKickAliases())) {
            if (! sender.hasPermission(CommandsConfUtils.comBGuildKickPermission())) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPerm());
                return;
            }

            if (args.length <= 1) {
                try {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
                } catch (Exception e) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                    e.printStackTrace();
                }
            } else {
                try {
                    SavableUser user = PlayerUtils.getOrGetSavableUser(args[1]);
                    if (user == null) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPlayer());
                        return;
                    }

                    GuildUtils.kickMember(stat, user);
                } catch (Exception e) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                    e.printStackTrace();
                }
            }
        } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBGuildMuteAliases())) {
            if (! sender.hasPermission(CommandsConfUtils.comBGuildMutePermission())) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPerm());
                return;
            }

            try {
                GuildUtils.muteGuild(stat);
            } catch (Throwable e) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                e.printStackTrace();
            }
        } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBGuildWarpAliases())) {
            if (! sender.hasPermission(CommandsConfUtils.comBGuildWarpPermission())) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPerm());
                return;
            }
            try {
                GuildUtils.warpGuild(stat);
            } catch (Throwable e) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                e.printStackTrace();
            }
        } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBGuildInfoAliases())) {
            if (! sender.hasPermission(CommandsConfUtils.comBGuildInfoPermission())) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPerm());
                return;
            }

            try {
                GuildUtils.info(stat);
            } catch (Throwable e) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                e.printStackTrace();
            }
        } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBGuildRenameAliases())) {
            if (! sender.hasPermission(CommandsConfUtils.comBGuildRenamePermission())) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPerm());
                return;
            }

            if (args.length <= 1) {
                try {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
                } catch (Exception e) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                    e.printStackTrace();
                }
            } else {
                try {
                    GuildUtils.rename(stat, TextUtils.argsToStringMinus(args, 0));
                } catch (Exception e) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                    e.printStackTrace();
                }
            }
        } else {
            try {
                SavableUser user = PlayerUtils.getOrGetSavableUser(args[0]);
                if (user == null) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPlayer());
                    return;
                }

                GuildUtils.sendInvite(user, stat);
            } catch (Exception e) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                e.printStackTrace();
            }
        }

        try {
            SavableGuild guild = GuildUtils.getGuild(stat);
            if (guild == null) return;
            guild.saveInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Usage: /guild <join|leave|create|promote|demote|chat|list|open|close|disband|accept|deny|invite|kick|mute|warp>
    @Override
    public Collection<String> onTabComplete(final CommandSource sender, final String[] args)
    {
        Collection<Player> players = StreamLine.getInstance().getProxy().getAllPlayers();
        List<String> strPlayers = new ArrayList<>();

        for (Player player : players){
            strPlayers.add(PlayerUtils.getSourceName(player));
        }

        strPlayers.add("%");

        if (args.length > 2) return new ArrayList<>();
        if (args.length == 1) {
            List<String> tabArgs1 = new ArrayList<>();
            tabArgs1.add("join");
            tabArgs1.add("leave");
            tabArgs1.add("create");
            tabArgs1.add("promote");
            tabArgs1.add("demote");
            tabArgs1.add("chat");
            tabArgs1.add("list");
            tabArgs1.add("open");
            tabArgs1.add("close");
            tabArgs1.add("disband");
            tabArgs1.add("accept");
            tabArgs1.add("deny");
            tabArgs1.add("invite");
            tabArgs1.add("mute");
            tabArgs1.add("warp");
            tabArgs1.add("rename");

            return TextUtils.getCompletion(tabArgs1, args[0]);
        }
        if (args.length == 2) {
            if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBGuildJoinAliases())) {
                if (! sender.hasPermission(CommandsConfUtils.comBGuildJoinPermission())) return new ArrayList<>();
                return TextUtils.getCompletion(strPlayers, args[1]);
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBGuildLeaveAliases())) {
                return new ArrayList<>();
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBGuildCreateAliases())) {
                return new ArrayList<>();
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBGuildPromoteAliases())) {
                if (! sender.hasPermission(CommandsConfUtils.comBGuildPromotePermission())) return new ArrayList<>();
                return TextUtils.getCompletion(strPlayers, args[1]);
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBGuildDemoteAliases())) {
                if (! sender.hasPermission(CommandsConfUtils.comBGuildDemotePermission())) return new ArrayList<>();
                return TextUtils.getCompletion(strPlayers, args[1]);
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBGuildChatAliases())) {
                return new ArrayList<>();
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBGuildListAliases())) {
                return new ArrayList<>();
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBGuildOpenAliases())) {
                return new ArrayList<>();
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBGuildCloseAliases())) {
                return new ArrayList<>();
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBGuildDisbandAliases())) {
                return new ArrayList<>();
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBGuildAcceptAliases())) {
                if (! sender.hasPermission(CommandsConfUtils.comBGuildAcceptPermission())) return new ArrayList<>();
                return TextUtils.getCompletion(strPlayers, args[1]);
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBGuildDenyAliases())) {
                if (! sender.hasPermission(CommandsConfUtils.comBGuildDenyPermission())) return new ArrayList<>();
                return TextUtils.getCompletion(strPlayers, args[1]);
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBGuildInvAliases())) {
                if (! sender.hasPermission(CommandsConfUtils.comBGuildInvitePermission())) return new ArrayList<>();
                return TextUtils.getCompletion(strPlayers, args[1]);
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBGuildKickAliases())) {
                if (! sender.hasPermission(CommandsConfUtils.comBGuildKickPermission())) return new ArrayList<>();
                return TextUtils.getCompletion(strPlayers, args[0]);
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBGuildMuteAliases())) {
                return new ArrayList<>();
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBGuildWarpAliases())) {
                return new ArrayList<>();
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBGuildRenameAliases())) {
                return new ArrayList<>();
            } else {
                return TextUtils.getCompletion(strPlayers, args[1]);
            }
        }
        return new ArrayList<>();
    }
}
