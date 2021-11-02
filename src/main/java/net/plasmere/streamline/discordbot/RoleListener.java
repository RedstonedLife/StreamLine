package net.plasmere.streamline.discordbot;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.objects.lists.SingleSet;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.utils.PlayerUtils;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class RoleListener implements EventListener {
    @Override
    public void onEvent(@NotNull GenericEvent genericEvent) {
        if (genericEvent instanceof GuildMemberRoleAddEvent) {
            for (String id : StreamLine.discordData.getSyncedRoles().keySet()) {
                SingleSet<Long, String> synced = StreamLine.discordData.getSyncedRoles().get(id);

                for (Role role : ((GuildMemberRoleAddEvent) genericEvent).getRoles()) {
                    if (synced.key == role.getIdLong()) {
                        Member member = ((GuildMemberRoleAddEvent) genericEvent).getMember();
                        if (StreamLine.discordData.isVerified(member.getIdLong())) {
                            String uuid = StreamLine.discordData.getUUIDOfVerified(member.getIdLong());

                            if (StreamLine.lpHolder.enabled) {
                                LuckPerms api = StreamLine.lpHolder.api;

                                try {
                                    User user = api.getUserManager().getUser(UUID.fromString(uuid));
                                    if (user == null) return;

                                    Group group = api.getGroupManager().getGroup(synced.value);
                                    if (group == null) return;

                                    if (group.getName().equals(user.getPrimaryGroup())) return;

                                    api.getUserManager().modifyUser(UUID.fromString(uuid), (User u) -> {
                                        // Remove all other inherited groups the user had before.
                                        u.data().clear(NodeType.INHERITANCE::matches);

                                        // Create a node to add to the player.
                                        Node node = InheritanceNode.builder(group).build();

                                        // Add the node to the user.
                                        u.data().add(node);
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
