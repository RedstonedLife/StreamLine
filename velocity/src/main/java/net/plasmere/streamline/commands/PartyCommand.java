package net.plasmere.streamline.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.CommandsConfUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PartyUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.TextUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PartyCommand extends SLCommand {
    public PartyCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void run(CommandSource sender, String[] args) {
        if (sender instanceof Player) {
            SavablePlayer player = PlayerUtils.getOrGetPlayerStat(((Player) sender).getUsername());

            if (player == null) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                return;
            }

            // Usage: /party <join !|leave |create !|promote !|demote !|chat !|list !|open !|close !|disband |accept !|deny !|invite !|kick !|mute !|warp !>
            if (args.length <= 0 || args[0].length() <= 0) {
                try {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
                } catch (Exception e) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                    e.printStackTrace();
                }
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBParJoinAliases())) {
                if (! sender.hasPermission(CommandsConfUtils.comBParJoinPermission())) {
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
                        PartyUtils.joinParty(player, PlayerUtils.getOrGetPlayerStat(args[1]));
                    } catch (Exception e) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                        e.printStackTrace();
                    }
                }
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBParLeaveAliases())) {
                if (! sender.hasPermission(CommandsConfUtils.comBParLeavePermission())) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPerm());
                    return;
                }

                try {
                    PartyUtils.leaveParty(player);
                } catch (Exception e) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                    e.printStackTrace();
                }
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBParCreateAliases())) {
                if (! sender.hasPermission(CommandsConfUtils.comBParCreatePermission())) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPerm());
                    return;
                }

                try {
                    PartyUtils.createParty(player);
                } catch (Exception e) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                    e.printStackTrace();
                }
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBParPromoteAliases())) {
                if (! sender.hasPermission(CommandsConfUtils.comBParPromotePermission())) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPerm());
                    return;
                }

                if (args.length <= 1) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
                } else {
                    try {
                        PartyUtils.promotePlayer(player, PlayerUtils.getOrGetPlayerStat(args[1]));
                    } catch (Exception e) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                        e.printStackTrace();
                    }
                }
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBParDemoteAliases())) {
                if (! sender.hasPermission(CommandsConfUtils.comBParDemotePermission())) {
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
                        PartyUtils.demotePlayer(player, PlayerUtils.getOrGetPlayerStat(args[1]));
                    } catch (Exception e) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                        e.printStackTrace();
                    }
                }
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBParChatAliases())) {
                if (! sender.hasPermission(CommandsConfUtils.comBParChatPermission())) {
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
                        PartyUtils.sendChat(player, TextUtils.argsToStringMinus(args, 0));
                    } catch (Exception e) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                        e.printStackTrace();
                    }
                }
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBParListAliases())) {
                if (! sender.hasPermission(CommandsConfUtils.comBParListPermission())) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPerm());
                    return;
                }

                try {
                    PartyUtils.listParty(player);
                } catch (Exception e) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                    e.printStackTrace();
                }
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBParOpenAliases())) {
                if (! sender.hasPermission(CommandsConfUtils.comBParOpenPermission())) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPerm());
                    return;
                }

                if (args.length <= 1) {
                    try {
                        PartyUtils.openParty(player);
                    }  catch (Exception e) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                        e.printStackTrace();
                    }
                } else {
                    try {
                        if (PartyUtils.getParty(PlayerUtils.getPlayerStat(sender)) != null) {
                            PartyUtils.openPartySized(player, Integer.parseInt(args[1]));
                        } else {
                            PartyUtils.createPartySized(player, Integer.parseInt(args[1]));
                        }
                    } catch (Exception e) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                        e.printStackTrace();
                    }
                }
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBParCloseAliases())) {
                if (! sender.hasPermission(CommandsConfUtils.comBParClosePermission())) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPerm());
                    return;
                }

                try {
                    PartyUtils.closeParty(player);
                } catch (Exception e) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                    e.printStackTrace();
                }
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBParDisbandAliases())) {
                if (! sender.hasPermission(CommandsConfUtils.comBParDisbandPermission())) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPerm());
                    return;
                }

                try {
                    PartyUtils.disband(player);
                } catch (Throwable e) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                    e.printStackTrace();
                }
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBParAcceptAliases())) {
                if (! sender.hasPermission(CommandsConfUtils.comBParAcceptPermission())) {
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
                        PartyUtils.acceptInvite(player, PlayerUtils.getOrGetPlayerStat(args[1]));
                    } catch (Exception e) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                        e.printStackTrace();
                    }
                }
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBParDenyAliases())) {
                if (! sender.hasPermission(CommandsConfUtils.comBParDenyPermission())) {
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
                        PartyUtils.denyInvite(player, PlayerUtils.getOrGetPlayerStat(args[1]));
                    } catch (Exception e) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                        e.printStackTrace();
                    }
                }
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBParInvAliases())) {
                if (! sender.hasPermission(CommandsConfUtils.comBParInvitePermission())) {
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
                        PartyUtils.sendInvite(PlayerUtils.getOrGetPlayerStat(args[1]), player);
                    } catch (Exception e) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                        e.printStackTrace();
                    }
                }
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBParKickAliases())) {
                if (! sender.hasPermission(CommandsConfUtils.comBParKickPermission())) {
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
                        PartyUtils.kickMember(player, PlayerUtils.getOrGetPlayerStat(args[1]));
                    } catch (Exception e) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                        e.printStackTrace();
                    }
                }
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBParMuteAliases())) {
                if (! sender.hasPermission(CommandsConfUtils.comBParMutePermission())) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPerm());
                    return;
                }

                try {
                    PartyUtils.muteParty(player);
                } catch (Throwable e) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                    e.printStackTrace();
                }
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBParWarpAliases())) {
                if (! sender.hasPermission(CommandsConfUtils.comBParWarpPermission())) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPerm());
                    return;
                }

                try {
                    PartyUtils.warpParty(player);
                } catch (Throwable e) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                    e.printStackTrace();
                }
            } else {
                try {
                    SavablePlayer p = PlayerUtils.getOrGetPlayerStat(args[0]);

                    if (p == null) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                        return;
                    }

                    PartyUtils.sendInvite(p, player);
                } catch (Exception e) {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
                    e.printStackTrace();
                }
            }
        } else {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.onlyPlayers());
        }
    }

    // Usage: /party <join|leave|create|promote|demote|chat|list|open|close|disband|accept|deny|invite|kick|mute|warp>
    @Override
    public Collection<String> onTabComplete(final CommandSource sender, final String[] args)
    {
        Collection<Player> players = StreamLine.getInstance().getProxy().getAllPlayers();
        List<String> strPlayers = new ArrayList<>();

        for (Player player : players){
            strPlayers.add(PlayerUtils.getSourceName(player));
        }

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

            return TextUtils.getCompletion(tabArgs1, args[0]);
        }
        if (args.length == 2) {
            if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBParJoinAliases())) {
                if (! sender.hasPermission(CommandsConfUtils.comBParJoinPermission())) return new ArrayList<>();
                return TextUtils.getCompletion(strPlayers, args[1]);
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBParLeaveAliases())) {
                return new ArrayList<>();
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBParCreateAliases())) {
                return new ArrayList<>();
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBParPromoteAliases())) {
                if (! sender.hasPermission(CommandsConfUtils.comBParPromotePermission())) return new ArrayList<>();
                return TextUtils.getCompletion(strPlayers, args[1]);
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBParDemoteAliases())) {
                if (! sender.hasPermission(CommandsConfUtils.comBParDemotePermission())) return new ArrayList<>();
                return TextUtils.getCompletion(strPlayers, args[1]);
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBParChatAliases())) {
                return new ArrayList<>();
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBParListAliases())) {
                return new ArrayList<>();
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBParOpenAliases())) {
                return new ArrayList<>();
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBParCloseAliases())) {
                return new ArrayList<>();
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBParDisbandAliases())) {
                return new ArrayList<>();
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBParAcceptAliases())) {
                if (! sender.hasPermission(CommandsConfUtils.comBParAcceptPermission())) return new ArrayList<>();
                return TextUtils.getCompletion(strPlayers, args[1]);
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBParDenyAliases())) {
                if (! sender.hasPermission(CommandsConfUtils.comBParDenyPermission())) return new ArrayList<>();
                return TextUtils.getCompletion(strPlayers, args[1]);
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBParInvAliases())) {
                if (! sender.hasPermission(CommandsConfUtils.comBParInvitePermission())) return new ArrayList<>();
                return TextUtils.getCompletion(strPlayers, args[1]);
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBParKickAliases())) {
                if (! sender.hasPermission(CommandsConfUtils.comBParKickPermission())) return new ArrayList<>();
                return TextUtils.getCompletion(strPlayers, args[0]);
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBParMuteAliases())) {
                return new ArrayList<>();
            } else if (MessagingUtils.compareWithList(args[0], CommandsConfUtils.comBParWarpAliases())) {
                return new ArrayList<>();
            } else {
                return TextUtils.getCompletion(strPlayers, args[1]);
            }
        }
        return new ArrayList<>();
    }
}
