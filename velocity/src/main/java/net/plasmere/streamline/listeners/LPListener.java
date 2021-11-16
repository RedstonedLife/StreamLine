package net.plasmere.streamline.listeners;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.node.NodeAddEvent;
import net.luckperms.api.model.PermissionHolder;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.objects.lists.SingleSet;
import net.plasmere.streamline.utils.MessagingUtils;

public class LPListener {
    public LPListener(LuckPerms luckPerms) {
        EventBus eventBus = luckPerms.getEventBus();

        eventBus.subscribe(StreamLine.getInstance(), NodeAddEvent.class, this::onUserSetPrimaryGroup);
    }

    private void onUserSetPrimaryGroup(NodeAddEvent event) {
        if (! StreamLine.discordData.getRolesPriority().equals("proxy")) return;

        if (event.getNode().getType().equals(NodeType.INHERITANCE)) {
//            if (ConfigUtils.debug()) MessagingUtils.logInfo("Is inheritance!");

            InheritanceNode node = (InheritanceNode) event.getNode();
            String group = node.getGroupName();
            if (ConfigUtils.debug()) MessagingUtils.logInfo("Node key = " + group);

            PermissionHolder holder = event.getTarget();
            if (event.isUser()) {
                User user = (User) holder;
                if (ConfigUtils.debug()) MessagingUtils.logInfo(this.getClass().getName() + " : Is user!");

                for (SingleSet<Long, String> thing : StreamLine.discordData.getSyncedRoles().values()) {
                    if (! thing.value.equals(group)) continue;

                    if (StreamLine.discordData.isVerified(user.getUniqueId().toString())) {
                        long discordID = StreamLine.discordData.getIDOfVerified(user.getUniqueId().toString());
                        for (Guild guild : StreamLine.getJda().getGuilds()) {
                            Role role = guild.getRoleById(thing.key);

                            if (role == null) {
                                MessagingUtils.logWarning("Role with id '" + thing.key + "' could not be found!");
                                continue;
                            }

                            try {
                                MessagingUtils.logInfo("Applying role with id '" + role.getName() + "' to '" + discordID + "'!");
                                guild.addRoleToMember(discordID, role).complete();
                            } catch (Exception e) {
                                e.printStackTrace();
                                continue;
                            }

                            MessagingUtils.logInfo("Discord ID " + discordID + " now has role " + role.getName() + "!");
                        }
                    } else {
                        if (ConfigUtils.debug()) MessagingUtils.logInfo("User " + user.getUsername() + " is not verified! Cancelling Discord role update!");
                    }
                }
            }
        }
    }
}