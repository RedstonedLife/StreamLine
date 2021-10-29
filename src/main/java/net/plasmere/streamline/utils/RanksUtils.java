package net.plasmere.streamline.utils;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.utils.holders.LPHolder;

import java.util.TreeMap;
import java.util.UUID;

public class RanksUtils {
    private static int SUCCESS = 1;
    private static int FAILED = -1;
    private static int OTHER = 0;

    public static int fromName(String name){
        for (int n : StreamLine.ranksConfig.checkedGroups().keySet()) {
            if (StreamLine.ranksConfig.checkedGroups().get(n).equals(name)) return n;
        }

        return -1;
    }

    public static boolean canChange(UUID uuid) {
        if (! StreamLine.lpHolder.enabled) return false;

        LuckPerms api = StreamLine.lpHolder.api;

        User user = api.getUserManager().getUser(uuid);
        if (user == null) return false;

        String primay = user.getPrimaryGroup();
        return StreamLine.ranksConfig.checkedGroups().containsValue(primay);
    }

    public static boolean canChange(User user) {
        String primay = user.getPrimaryGroup();
        return StreamLine.ranksConfig.checkedGroups().containsValue(primay);
    }

    public static String getNewGroup(SavablePlayer player) {
        int intToTest = TextUtils.replaceAllPlayerRanks(player);

        if (intToTest > StreamLine.ranksConfig.checkedGroups().firstKey()) {
            int currentReq = StreamLine.ranksConfig.checkedGroups().lastKey();
            while (intToTest < currentReq) {
                currentReq = iterateRequirement(currentReq);
            }

            return StreamLine.ranksConfig.checkedGroups().get(currentReq);
        }

        return StreamLine.ranksConfig.checkedGroups().get(StreamLine.ranksConfig.checkedGroups().firstKey());
    }

    public static int iterateRequirement(int lastKey){
        return StreamLine.ranksConfig.checkedGroups().lowerKey(lastKey);
    }

    public static int checkAndChange(SavablePlayer player){
        if (! StreamLine.lpHolder.enabled) return FAILED;

        LuckPerms api = StreamLine.lpHolder.api;
        try {
            User user = api.getUserManager().getUser(player.getUniqueId());
            if (user == null) return FAILED;

            Group group = api.getGroupManager().getGroup(getNewGroup(player));
            if (group == null) return FAILED;

            if (getNewGroup(player).equals(user.getPrimaryGroup())) return OTHER;

            if (! canChange(user)) return OTHER;

            api.getUserManager().modifyUser(player.getUniqueId(), (User u) -> {
                // Remove all other inherited groups the user had before.
                u.data().clear(NodeType.INHERITANCE::matches);

                // Create a node to add to the player.
                Node node = InheritanceNode.builder(group).build();

                // Add the node to the user.
                u.data().add(node);
            });

            return SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return FAILED;
        }
    }
}
