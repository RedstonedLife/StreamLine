package net.plasmere.streamline.discordbot;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.objects.lists.SingleSet;
import net.plasmere.streamline.objects.savable.users.SavableUser;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.TextUtils;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class MemberListener extends ListenerAdapter {
    public MemberListener() {
        if (! ConfigUtils.moduleDEnabled()) return;
        MessagingUtils.logInfo("Member listener registered!");
    }

    @Override
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
        MessagingUtils.logInfo(event.getUser().getName() + " updated " + event.getMember().getUser().getName() + "'s roles!");

        StreamLine.discordData.loadSynced();

        for (String id : StreamLine.discordData.getSyncedRoles().keySet()) {
            SingleSet<Long, String> synced = StreamLine.discordData.getSyncedRoles().get(id);

            if (ConfigUtils.debug()) MessagingUtils.logInfo("Synced set : < " + synced.key + " , " + synced.value + " >");

            for (Role role : event.getRoles()) {
                if (synced.key == role.getIdLong()) {
                    Member member = event.getMember();
                    if (StreamLine.discordData.isVerified(member.getIdLong())) {
                        String uuid = StreamLine.discordData.getUUIDOfVerified(member.getIdLong());

                        if (StreamLine.lpHolder.enabled) {
                            LuckPerms api = StreamLine.lpHolder.api;

                            try {
                                User user = api.getUserManager().getUser(UUID.fromString(uuid));
                                if (user == null) {
                                    if (ConfigUtils.debug())
                                        MessagingUtils.logInfo("User returned null in MemberListener.");
                                    return;
                                }

                                Group group = api.getGroupManager().getGroup(synced.value);
                                if (group == null) {
                                    if (ConfigUtils.debug())
                                        MessagingUtils.logInfo("Group returned null in MemberListener.");
                                    return;
                                }

                                if (group.getName().equals(user.getPrimaryGroup())) {
                                    if (ConfigUtils.debug())
                                        MessagingUtils.logInfo("Group returned same as Primary Group in MemberListener.");
                                    return;
                                }

                                MessagingUtils.logInfo("Adding " + user.getUsername() + " to group " + group.getName());

                                api.getUserManager().modifyUser(user.getUniqueId(), (User u) -> {
                                    // Remove all other inherited groups the user had before.
                                    u.data().clear(NodeType.INHERITANCE::matches);

                                    // Create a node to add to the player.
                                    Node node = InheritanceNode.builder().group(group).build();

                                    // Add the node to the user.
                                    u.data().add(node);
                                });
//                                user.setPrimaryGroup(group.getName());
//                                api.getUserManager().saveUser(user);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onGuildMemberUpdateNickname(@NotNull GuildMemberUpdateNicknameEvent event) {
        if (ConfigUtils.moduleDPCChangeOnVerifyUnchangeable()) {
            String uuid = StreamLine.discordData.getUUIDOfVerified(event.getMember().getIdLong());
            if (uuid == null) return;
            if (uuid.equals("")) return;

            SavableUser user = PlayerUtils.getOrGetSavableUser(uuid);

            try {
                if (ConfigUtils.moduleDPCChangeOnVerifyType().equals("discord")) {
                    event.getMember().modifyNickname(TextUtils.replaceAllPlayerDiscord(ConfigUtils.moduleDPCChangeOnVerifyTo(), user)).complete();
                } else if (ConfigUtils.moduleDPCChangeOnVerifyType().equals("bungee")) {
                    event.getMember().modifyNickname(TextUtils.replaceAllPlayerBungee(ConfigUtils.moduleDPCChangeOnVerifyTo(), user)).complete();
                }
            } catch (HierarchyException e) {
                // do nothing
            }
        }
    }
}
