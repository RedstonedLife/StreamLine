package net.plasmere.streamline.objects.savable.groups;

import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.objects.savable.SavableAdapter;
import net.plasmere.streamline.objects.savable.SavableFile;
import net.plasmere.streamline.objects.savable.users.SavableConsole;
import net.plasmere.streamline.objects.savable.users.SavableUser;
import net.plasmere.streamline.utils.*;

import java.io.File;
import java.util.*;

public abstract class SavableGroup extends SavableFile {
    public List<SavableUser> moderators = new ArrayList<>();
    public List<SavableUser> members = new ArrayList<>();
    public List<SavableUser> totalMembers = new ArrayList<>();
    public List<SavableUser> invites = new ArrayList<>();
    public boolean isMuted;
    public boolean isPublic;
    public long voiceID;
    public int maxSize;

    public int databaseID;

    public enum Level {
        MEMBER,
        MODERATOR,
        LEADER
    }

    public SavableGroup(SavableUser leader, SavableAdapter.Type type) {
        this(leader.uuid, type);
    }

    public SavableGroup(String uuid, SavableAdapter.Type type) {
        super(uuid, type);

        SavableUser user = PlayerUtils.getOrGetSavableUser(uuid);
        if (user == null) return;

        switch (type) {
            case GUILD -> user.setGuild(uuid);
            case PARTY -> user.setParty(uuid);
        }
    }

    public void populateDefaults() {
        // Users.
        moderators = parseUserListFromUUIDs(getOrSetDefault("users.moderators", new ArrayList<>()));
        members = parseUserListFromUUIDs(getOrSetDefault("users.members", new ArrayList<>()));
        totalMembers = parseUserListFromUUIDs(getOrSetDefault("users.total", List.of(uuid)));
        invites = parseUserListFromUUIDs(getOrSetDefault("users.invites", new ArrayList<>()));
        // Settings.
        isMuted = getOrSetDefault("settings.mute.toggled", false);
        isPublic = getOrSetDefault("settings.public.toggled", false);
        maxSize = getOrSetDefault("settings.size.max", ConfigUtils.partyMax());
        // Discord.
        voiceID = getOrSetDefault("discord.channel.voice", 0L);
        // Database.
        if (ConfigUtils.moduleDBUse()) {
            databaseID = getOrSetDefault("database.id", 1);
        }

        populateMoreDefaults();
    }

    public List<SavableUser> parseUserListFromUUIDs(List<String> uuids) {
        List<SavableUser> users = new ArrayList<>();

        for (String uuid : uuids) {
            SavableUser u = PlayerUtils.getOrGetSavableUser(uuid);

            if (users.contains(u)) continue;

            users.add(u);
        }

        return users;
    }

    abstract public void populateMoreDefaults();

    public void loadValues(){
        // Users.
        moderators = parseUserListFromUUIDs(getOrSetDefault("users.moderators", new ArrayList<>()));
        members = parseUserListFromUUIDs(getOrSetDefault("users.members", new ArrayList<>()));
        totalMembers = parseUserListFromUUIDs(getOrSetDefault("users.total", List.of(uuid)));
        invites = parseUserListFromUUIDs(getOrSetDefault("users.invites", new ArrayList<>()));
        // Settings.
        isMuted = getOrSetDefault("settings.mute.toggled", isMuted);
        isPublic = getOrSetDefault("settings.public.toggled", isPublic);
        maxSize = getOrSetDefault("settings.size.max", maxSize);
        // Discord.
        voiceID = getOrSetDefault("discord.channel.voice", voiceID);
        // Database.
        if (ConfigUtils.moduleDBUse()) {
            databaseID = getOrSetDefault("database.id", databaseID);
        }

        loadMoreValues();
    }

    abstract public void loadMoreValues();

    public void saveAll() {
        // Users.
        set("users.moderators", moderators);
        set("users.members", members);
        set("users.total", totalMembers);
        set("users.invites", invites);
        // Settings.
        set("settings.mute.toggled", isMuted);
        set("settings.public.toggled", isPublic);
        // Discord.
        set("discord.channel.voice", voiceID);

        saveMore();
    }

    abstract public void saveMore();

    public SavableUser getMember(String uuid) {
        return PlayerUtils.getOrGetSavableUser(uuid);
    }

    public void removeUUIDCompletely(String uuid) {
        SavableUser user = PlayerUtils.getOrGetSavableUser(uuid);
        if (user == null) return;

        moderators.remove(user);
        members.remove(user);
        totalMembers.remove(user);
//        invites.remove(user);
    }

    public boolean hasMember(String uuid){
        for (SavableUser user : totalMembers) {
            if (user.uuid.equals(uuid)) return true;
        }

        return false;
    }

    public boolean hasMember(SavableUser stat){
        return hasMember(stat.uuid);
    }

    public int getSize(){
        return totalMembers.size();
    }

    public void removeFromModerators(SavableUser stat){
        moderators.remove(stat);
    }

    public void remFromMembers(SavableUser stat){
        members.remove(stat);
    }

    public void remFromTMembers(SavableUser stat){
        totalMembers.remove(stat);
    }

    public void remFromInvites(SavableUser from, SavableUser stat){
        invites.remove(stat);
    }

    public void remFromInvitesCompletely(SavableUser stat){
        invites.remove(stat);
    }

    public void addToModerators(SavableUser stat){
        moderators.add(stat);
    }

    public void addToMembers(SavableUser stat){
        members.add(stat);
    }

    public void addToTMembers(SavableUser stat){
        totalMembers.add(stat);
    }

    public void addToInvites(SavableUser stat){
        invites.add(stat);
    }

    public void addMember(SavableUser stat){
        addToTMembers(stat);
        addToMembers(stat);

        stat.setGuild(uuid);
    }

    public void removeMemberFromGroup(SavableUser stat){
        Random RNG = new Random();

        if (uuid.equals(stat.uuid)){
            if (totalMembers.size() <= 1) {
                try {
                    remFromInvitesCompletely(stat);
                    removeFromModerators(stat);
                    remFromMembers(stat);
                    remFromTMembers(stat);
                    disband();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            } else {
                if (moderators.size() > 0) {
                    int r = RNG.nextInt(moderators.size());
                    SavableUser newLeader = moderators.get(r);

                    totalMembers.remove(stat);
                    uuid = newLeader.uuid;
                    moderators.remove(newLeader);
                } else {
                    if (members.size() > 0) {
                        int r = RNG.nextInt(members.size());
                        SavableUser newLeader = members.get(r);

                        totalMembers.remove(stat);
                        uuid = newLeader.uuid;
                        members.remove(newLeader);
                    }
                }
            }
        }
    }

    public void addInvite(SavableUser to) {
        invites.add(to);
    }

    public void setMuted(boolean bool) {
        isMuted = bool;
    }

    public void toggleMute(){
        setMuted(! isMuted);
    }

    public void setPublic(boolean bool){
        isPublic = bool;
    }

    public void togglePublic() {
        setPublic(! isPublic);
    }

    public Level getLevel(SavableUser member){
        if (this.members.contains(member))
            return Level.MEMBER;
        else if (this.moderators.contains(member))
            return Level.MODERATOR;
        else if (this.uuid.equals(member.uuid))
            return Level.LEADER;
        else
            return Level.MEMBER;
    }

    public void setModerator(SavableUser stat){
        Random RNG = new Random();

        removeFromModerators(stat);

        if (uuid.equals(stat.uuid)){
            if (totalMembers.size() <= 1) {
                try {
                    remFromInvitesCompletely(stat);
                    removeFromModerators(stat);
                    remFromMembers(stat);
                    remFromTMembers(stat);
                    disband();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            } else {
                if (moderators.size() > 0) {
                    int r = RNG.nextInt(moderators.size());
                    SavableUser newLeader = moderators.get(r);

                    moderators.add(stat);
                    uuid = newLeader.uuid;
                    moderators.remove(newLeader);
                } else {
                    if (members.size() > 0) {
                        int r = RNG.nextInt(members.size());
                        SavableUser newLeader = members.get(r);

                        moderators.add(stat);
                        uuid = newLeader.uuid;
                        members.remove(newLeader);
                    }
                }
            }
        }
    }

    public void setMember(SavableUser stat){
        Random RNG = new Random();

        removeFromModerators(stat);

        if (uuid.equals(stat.uuid)){
            if (totalMembers.size() <= 1) {
                try {
                    remFromInvitesCompletely(stat);
                    removeFromModerators(stat);
                    remFromMembers(stat);
                    remFromTMembers(stat);
                    disband();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            } else {
                if (moderators.size() > 0) {
                    int r = RNG.nextInt(moderators.size());
                    SavableUser newLeader = moderators.get(r);

                    members.add(stat);
                    uuid = newLeader.uuid;
                    moderators.remove(newLeader);
                } else {
                    if (members.size() > 0) {
                        int r = RNG.nextInt(members.size());
                        SavableUser newLeader = members.get(r);

                        members.add(stat);
                        uuid = newLeader.uuid;
                        members.remove(newLeader);
                    }
                }
            }
        }
    }

    public void replaceLeader(SavableUser with){
        addToModerators(PlayerUtils.getOrGetSavableUser(uuid));

        file.delete();

//        if (! file.renameTo(new File(filePrePath + uuid.toString() + ".properties"))){
//            MessagingUtils.logInfo("Could not rename a guild file for " + uuid + "...");
//        }

        file = null;
        file = new File(type.path + uuid + type.suffix);

        try {
            for (SavableUser p : totalMembers) {
                p.setGuild(uuid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        saveAll();
    }

    public void setVoiceID(long voiceID) {
        this.voiceID = voiceID;
    }

    public void setDatabaseID(int id) {
        this.databaseID = id;
    }

    public boolean hasModPerms(String uuid) {
        try {
            return hasModPerms(PlayerUtils.getOrGetSavableUser(uuid));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hasModPerms(SavableUser stat) {
        try {
            return moderators.contains(stat) || uuid.equals(stat.uuid);
        } catch (Exception e) {
            return false;
        }
    }

    public void setMaxSize(int size){
        SavableUser user = PlayerUtils.getOrGetSavableUser(uuid);
        if (user == null) return;

        if (size <= getMaxSize(user))
            this.maxSize = size;
    }

    public int getMaxSize(SavableUser leader){
        if (! StreamLine.lpHolder.enabled || leader instanceof SavableConsole) {
            switch (type) {
                case GUILD -> {
                    return ConfigUtils.guildMax();
                }
                case PARTY -> {
                    return ConfigUtils.partyMax();
                }
            }
        }

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
            switch (type) {
                case GUILD -> {
                    return ConfigUtils.guildMax();
                }
                case PARTY -> {
                    return ConfigUtils.partyMax();
                }
                default -> {
                    return 1;
                }
            }
        }
    }

    public boolean hasGroupedSize(String group) {
        for (String key : ConfigUtils.getGroupSizeConfig().keySet()) {
            if (group.equals(key)) return true;
        }

        return false;
    }

    public void disband(){
        switch (type) {
            case GUILD -> {
                for (SavableUser user : totalMembers) {
                    user.setGuild("");
                }

                GuildUtils.removeGuild((SavableGuild) this);
            }
            case PARTY -> {
                for (SavableUser user : totalMembers) {
                    user.setParty("");
                }

                PartyUtils.removeParty((SavableParty) this);
            }
        }

        file.delete();

        try {
            dispose();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public String toString(){
        return PlayerUtils.forStats(totalMembers);
    }
}
