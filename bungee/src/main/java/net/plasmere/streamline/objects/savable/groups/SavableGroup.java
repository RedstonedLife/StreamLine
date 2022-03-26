package net.plasmere.streamline.objects.savable.groups;

import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.objects.savable.SavableAdapter;
import net.plasmere.streamline.objects.savable.SavableFile;
import net.plasmere.streamline.objects.savable.users.SavableConsole;
import net.plasmere.streamline.objects.savable.users.SavableUser;
import net.plasmere.streamline.utils.*;
import net.plasmere.streamline.utils.sql.DataSource;

import java.io.File;
import java.nio.file.Files;
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

        if (firstLoad) {
            SavableUser user = PlayerUtils.getOrGetSavableUser(uuid);
            if (user == null) return;

            addToTMembers(user);

            switch (type) {
                case PARTY -> {
                    user.setParty(uuid);
                    this.databaseID = ConfigUtils.moduleDBUse() ? DataSource.createParty(user, (SavableParty) this) : -1;
                }
                case GUILD -> {
                    user.setGuild(uuid);
                    this.databaseID = ConfigUtils.moduleDBUse() ? DataSource.createGuild(user, (SavableGuild) this) : -1;
                }
            }
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
            SavableUser u = PlayerUtils.addStatByUUID(uuid);
            if (u == null) continue;
            if (users.contains(u)) continue;

            users.add(u);
        }

        return users;
    }

    public List<String> parseUUIDListFromUsers(List<SavableUser> users) {
        List<String> uuids = new ArrayList<>();

        for (SavableUser user : users) {
            if (uuids.contains(user.uuid)) continue;

            uuids.add(user.uuid);
        }

        return uuids;
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
        set("users.moderators", parseUUIDListFromUsers(moderators));
        set("users.members", parseUUIDListFromUsers(members));
        set("users.total", parseUUIDListFromUsers(totalMembers));
        set("users.invites", parseUUIDListFromUsers(invites));
        // Settings.
        set("settings.mute.toggled", isMuted);
        set("settings.public.toggled", isPublic);
        // Discord.
        set("discord.channel.voice", voiceID);

        saveMore();
    }

    abstract public void saveMore();

    public void syncWithDatabase() {
        if (! ConfigUtils.moduleDBUse()) return;

        if (this instanceof SavableGuild) {
            DataSource.updateGuildData((SavableGuild) this);
        } else if (this instanceof SavableParty) {
            DataSource.updatePartyData((SavableParty) this);
        } else {
            MessagingUtils.logInfo("Group with UUID " + this.uuid + " could not be synced due to it not being of a syncable type!");
            try {
                throw new Exception("Group not of syncable type!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public SavableUser getMember(String uuid) {
//        loadValues();
        return PlayerUtils.getOrGetSavableUser(uuid);
    }

    public void removeUUIDCompletely(String uuid) {
//        loadValues();
        SavableUser user = PlayerUtils.getOrGetSavableUser(uuid);
        if (user == null) return;

        moderators.remove(user);
        members.remove(user);
        totalMembers.remove(user);
//        invites.remove(user);
//        saveAll();
    }

    public boolean hasMember(String uuid){
//        loadValues();
        for (SavableUser user : totalMembers) {
            if (user.uuid.equals(uuid)) return true;
        }

        return false;
    }

    public boolean hasMember(SavableUser stat){
//        loadValues();
        return hasMember(stat.uuid);
    }

    public int getSize(){
//        loadValues();
        return totalMembers.size();
    }

    public void removeFromModerators(SavableUser stat){
//        loadValues();
        if (! moderators.contains(stat)) return;
        moderators.remove(stat);
//        saveAll();
    }

    public void remFromMembers(SavableUser stat){
//        loadValues();
        if (! members.contains(stat)) return;
        members.remove(stat);
//        saveAll();
    }

    public void remFromTMembers(SavableUser stat){
//        loadValues();
        if (! totalMembers.contains(stat)) return;
        totalMembers.remove(stat);

        switch (type) {
            case PARTY -> {
                DataSource.removePlayerFromParty(stat, (SavableParty) this);
            }
            case GUILD -> {
                DataSource.removePlayerFromGuild(stat, (SavableGuild) this);
            }
        }
//        saveAll();
    }

    public void remFromInvites(SavableUser from, SavableUser stat){
//        loadValues();
        if (! invites.contains(stat)) return;
        invites.remove(stat);
//        saveAll();
    }

    public void remFromInvitesCompletely(SavableUser stat){
//        loadValues();
        if (! invites.contains(stat)) return;
        invites.remove(stat);
//        saveAll();
    }

    public void addToModerators(SavableUser stat){
//        loadValues();
        if (moderators.contains(stat)) return;
        moderators.add(stat);
//        saveAll();
    }

    public void addToMembers(SavableUser stat){
//        loadValues();
        if (members.contains(stat)) return;
        members.add(stat);
//        saveAll();
    }

    public void addToTMembers(SavableUser stat){
//        loadValues();
        if (totalMembers.contains(stat)) return;
        totalMembers.add(stat);
//        saveAll();
    }

    public void addInvite(SavableUser to) {
//        loadValues();
        if (invites.contains(to)) return;
        invites.add(to);
//        saveAll();
    }

    public void addMember(SavableUser stat){
//        loadValues();
        addToTMembers(stat);
        addToMembers(stat);


        switch (type) {
            case PARTY -> {
                stat.setParty(uuid);
                DataSource.addPlayerToParty(stat, (SavableParty) this);
            }
            case GUILD -> {
                stat.setGuild(uuid);
                DataSource.addPlayerToGuild(stat, (SavableGuild) this);
            }
        }
//        saveAll();
    }

    public void removeMemberFromGroup(SavableUser stat){
//        loadValues();
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

        remFromInvitesCompletely(stat);
        removeFromModerators(stat);
        remFromMembers(stat);
        remFromTMembers(stat);

//        saveAll();
    }

    public void setMuted(boolean bool) {
//        loadValues();
        isMuted = bool;
//        saveAll();
    }

    public void toggleMute(){
        setMuted(! isMuted);
    }

    public void setPublic(boolean bool){
//        loadValues();
        isPublic = bool;
//        saveAll();
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
//        loadValues();
        Random RNG = new Random();

        remFromMembers(stat);

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

        addToModerators(stat);

//        saveAll();
    }

    public void setMember(SavableUser stat){
//        loadValues();
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

        addToMembers(stat);
        addToTMembers(stat);

//        saveAll();
    }

    public void replaceLeader(SavableUser with){
        try {
            Files.delete(file.toPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
//        loadValues();
        addToModerators(PlayerUtils.getOrGetSavableUser(uuid));
        removeFromModerators(with);
        remFromMembers(with);
        remFromInvitesCompletely(with);

        this.uuid = with.uuid;

        try {
            file = new File(type.path, with.uuid + type.suffix);
            if (file.exists()) Files.delete(file.toPath());
            file.createNewFile();
            saveAll();
            loadValues();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (SavableUser user : totalMembers) {
            if (this instanceof SavableGuild) user.guild = with.uuid;
            if (this instanceof SavableParty) user.party = with.uuid;
            user.saveAll();
        }
//        saveAll();
    }

    public void setVoiceID(long voiceID) {
//        loadValues();
        this.voiceID = voiceID;
//        saveAll();
    }

    public void setDatabaseID(int id) {
//        loadValues();
        this.databaseID = id;
//        saveAll();
    }

    public boolean hasModPerms(String uuid) {
//        loadValues();
        try {
            return hasModPerms(PlayerUtils.getOrGetSavableUser(uuid));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hasModPerms(SavableUser stat) {
//        loadValues();
        try {
            return moderators.contains(stat) || uuid.equals(stat.uuid);
        } catch (Exception e) {
            return false;
        }
    }

    public void setMaxSize(int size){
//        loadValues();
        SavableUser user = PlayerUtils.getOrGetSavableUser(uuid);
        if (user == null) return;

        if (size <= getMaxSize(user))
            this.maxSize = size;

//        saveAll();
    }

    public int getMaxSize(SavableUser leader){
//        loadValues();
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
//        loadValues();
        for (String key : ConfigUtils.getGroupSizeConfig().singleLayerKeySet()) {
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
