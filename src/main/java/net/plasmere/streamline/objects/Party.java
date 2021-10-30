package net.plasmere.streamline.objects;

import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.query.QueryOptions;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PartyUtils;
import net.plasmere.streamline.utils.PlayerUtils;

import java.util.*;

public class Party {
    public int maxSize;
    public SavablePlayer leader;
    public String leaderUUID;
    public List<SavablePlayer> totalMembers = new ArrayList<>();
    public List<String> totalMembersByUUID = new ArrayList<>();
    public List<SavablePlayer> members = new ArrayList<>();
    public List<String> membersByUUID = new ArrayList<>();
    public List<SavablePlayer> moderators = new ArrayList<>();
    public List<String> modsByUUID = new ArrayList<>();
    public String name;
    public boolean isPublic = false;
    public boolean isMuted = false;
    // to , from
    public List<SavablePlayer> invites = new ArrayList<>();
    public List<String> invitesByUUID = new ArrayList<>();

    public enum Level {
        MEMBER,
        MODERATOR,
        LEADER
    }

    public Party(SavablePlayer leader){
        this.leader = leader;
        this.leaderUUID = leader.uuid;
        this.totalMembers.add(leader);
        this.totalMembersByUUID.add(leaderUUID);
        this.maxSize = getMaxSize(leader);
        this.isPublic = false;
    }

    public Party(SavablePlayer leader, int size){
        this.leader = leader;
        this.leaderUUID = leader.uuid;
        this.totalMembers.add(leader);
        this.totalMembersByUUID.add(leaderUUID);
        this.maxSize = Math.min(size, getMaxSize(leader));
        this.isPublic = true;
    }

    public void toggleMute() {
        isMuted = ! isMuted;
    }

    public void dispose() {
        this.leader = null;
        try {
            this.finalize();
        } catch (Throwable e){
            e.printStackTrace();
        }
    }

    public Level getLevel(SavablePlayer member){
        if (this.members.contains(member))
            return Level.MEMBER;
        else if (this.moderators.contains(member))
            return Level.MODERATOR;
        else if (this.leader.equals(member))
            return Level.LEADER;
        else
            return Level.MEMBER;
    }

    public void addInvite(SavablePlayer invite){
        this.invites.add(invite);
        this.invitesByUUID.add(invite.uuid);
    }

    public void removeInvite(SavablePlayer invite){
        this.invites.remove(invite);
        this.invitesByUUID.remove(invite.uuid);
    }

    public void setPublic(boolean bool){
        this.isPublic = bool;
    }

    public void setMaxSize(int size){
        if (size < getMaxSize(this.leader))
            this.maxSize = size;
    }

    public int getSize(){
        return totalMembers.size();
    }

    public int getMaxSize() { return maxSize; }

    public void replaceLeader(SavablePlayer newLeader){
        setModerator(leader);

        removeMember(newLeader);
        removeMod(newLeader);

        this.leader = newLeader;
        this.leaderUUID = newLeader.uuid;
    }

    public void removeMod(SavablePlayer mod){
        removeFromModerators(mod);
    }

    public void removeMember(SavablePlayer member){
        remFromMembers(member);
    }

    public void setModerator(SavablePlayer mod){
        removeFromModerators(mod);
        this.moderators.add(mod);
        this.modsByUUID.add(mod.uuid);
        this.members.remove(mod);
        this.membersByUUID.remove(mod.uuid);
    }

    public void setMember(SavablePlayer member){
        remFromMembers(member);
        this.members.add(member);
        this.membersByUUID.add(member.uuid);
        this.moderators.remove(member);
        this.modsByUUID.remove(member.uuid);
    }

    public void addMember(SavablePlayer member){
        removeMemberFromParty(member);
        this.members.add(member);
        this.membersByUUID.add(member.uuid);
        this.totalMembers.add(member);
        this.totalMembersByUUID.add(member.uuid);
    }

    public void removeMemberFromParty(SavablePlayer member){
        remFromMembers(member);
        removeFromModerators(member);
        remFromTMembers(member);
    }

    public String remFromMembers(SavablePlayer player){
        membersByUUID.remove(player.uuid);
        members.remove(player);

        StringBuilder builder = new StringBuilder();

        int i = 0;
        for (String uuid : membersByUUID) {
            i++;
            if (i != membersByUUID.size()) {
                builder.append(uuid).append(".");
            } else {
                builder.append(uuid);
            }
        }

        return builder.toString();
    }

    public String removeFromModerators(SavablePlayer player){
        modsByUUID.remove(player.uuid);
        moderators.remove(player);

        StringBuilder builder = new StringBuilder();

        int i = 0;
        for (String uuid : modsByUUID) {
            i++;
            if (i != modsByUUID.size()) {
                builder.append(uuid).append(".");
            } else {
                builder.append(uuid);
            }
        }

        return builder.toString();
    }

    public String remFromTMembers(SavablePlayer player){
        totalMembersByUUID.remove(player.uuid);
        totalMembers.remove(player);

        StringBuilder builder = new StringBuilder();

        int i = 0;
        for (String uuid : totalMembersByUUID) {
            i++;
            if (i != totalMembersByUUID.size()) {
                builder.append(uuid).append(".");
            } else {
                builder.append(uuid);
            }
        }

        return builder.toString();
    }

    public String remFromInvites(SavablePlayer from, SavablePlayer player){
        invitesByUUID.remove(player.uuid);
        invites.remove(player);

        StringBuilder builder = new StringBuilder();

        int i = 0;
        for (String uuid : invitesByUUID) {
            i++;
            if (i != invitesByUUID.size()) {
                builder.append(uuid).append(".");
            } else {
                builder.append(uuid);
            }
        }

        PartyUtils.removeInvite(PartyUtils.getParty(from), player);

        return builder.toString();
    }

    public boolean hasMember(SavablePlayer member){
        if (this.totalMembers.contains(member)) return true;

        loadLists();

        return this.totalMembers.contains(member) || this.totalMembersByUUID.contains(member.uuid);
    }

    public void loadLists(){
        totalMembers.clear();
        for (String u : totalMembersByUUID) {
            SavablePlayer p = PlayerUtils.getOrGetPlayerStatByUUID(u);

            if (p == null) continue;

            totalMembers.add(p);
        }

        members.clear();
        for (String u : membersByUUID) {
            SavablePlayer p = PlayerUtils.getOrGetPlayerStatByUUID(u);

            if (p == null) continue;

            members.add(p);
        }

        moderators.clear();
        for (String u : modsByUUID) {
            SavablePlayer p = PlayerUtils.getOrGetPlayerStatByUUID(u);

            if (p == null) continue;

            moderators.add(p);
        }

        invites.clear();
        for (String u : invitesByUUID) {
            SavablePlayer p = PlayerUtils.getOrGetPlayerStatByUUID(u);

            if (p == null) continue;

            invites.add(p);
        }

        this.leader = PlayerUtils.getOrGetPlayerStatByUUID(leaderUUID);

        /*
        =================================================
        TODO FIX THIS SOON! THIS IS JUST A PATCH FOR THIS!
        =================================================
         */

        if (this.leader == null) {
            for (SavablePlayer player : totalMembers) {
                MessagingUtils.sendBPUserMessage(this, PlayerUtils.getConsoleStat().findSender(), player.findSender(), PartyUtils.disbandMembers);
            }

            PartyUtils.removeParty(this);
            this.dispose();
        }
    }

    public boolean isModerator(SavablePlayer member) {
        return this.moderators.contains(member) || this.modsByUUID.contains(member.uuid);
    }

    public boolean isLeader(SavablePlayer member) {
        return this.leader.equals(member) || this.leaderUUID.equals(member.uuid);
    }

    public boolean hasModPerms(SavablePlayer member){
        return isModerator(member) || isLeader(member);
    }

    public boolean hasModPerms(String uuid) {
        try {
            return modsByUUID.contains(uuid) || leaderUUID.equals(uuid);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hasGroupedSize(String group) {
        for (String key : ConfigUtils.getGroupSizeConfig().getKeys()) {
            if (group.equals(key)) return true;
        }

        return false;
    }

    public int getMaxSize(SavablePlayer leader){
        if (! StreamLine.lpHolder.enabled) return ConfigUtils.partyMax();

        try {
            String group = StreamLine.lpHolder.api.getUserManager().getUser(leader.latestName).getPrimaryGroup();

            if (group.equals("")){
                group = "default";
            }

            int max = 0;

            if (! hasGroupedSize(group) && ! group.equals("default")) {
                group = "default";
            } else if (! hasGroupedSize(group) && group.equals("default")){
                return 1;
            }

            return ConfigUtils.getGroupedSize(group);
        } catch (Exception e) {
            e.printStackTrace();
            return ConfigUtils.partyMax();
        }
    }
}
