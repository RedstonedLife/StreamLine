package net.plasmere.streamline.utils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.DiscordBotConfUtils;
import net.plasmere.streamline.objects.enums.CategoryType;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DiscordUtils {
    public static VoiceChannel createVoice(String name, CategoryType type, SavablePlayer... players) {
        JDA jda = StreamLine.getJda();

        if (DiscordUtils.vcNameAlreadyExists(name, type)) return null;

        List<Long> discordIDs = new ArrayList<>();
        for (SavablePlayer player : players) {
            discordIDs.add(StreamLine.discordData.getIDOfVerified(player.uuid));
        }

        Guild guild = jda.getGuildById(DiscordBotConfUtils.guildID());

        if (! discordGuildHasPlayers(players)) return null;
        Category category = guild.getCategoryById(DiscordBotConfUtils.categoryGet(type));
        if (category == null) return null;
        VoiceChannel voiceChannel = guild.createVoiceChannel(name, category).complete();

        if (ConfigUtils.debug())
            MessagingUtils.logInfo("Created voice channel " + voiceChannel.getName() + " (" + voiceChannel.getId() + ")");

        for (long l : discordIDs) {
            User user = jda.getUserById(l);
            if (user == null) continue;
            Member member = guild.getMember(user);
            if (member == null) {
                MessagingUtils.logWarning("Member with id " + l + " returned null!");
                continue;
            }
            voiceChannel.createPermissionOverride(member).setAllow(
                    Permission.VIEW_CHANNEL,
                    Permission.VOICE_CONNECT, Permission.VOICE_SPEAK,
                    Permission.VOICE_STREAM, Permission.VOICE_USE_VAD,
                    Permission.VOICE_START_ACTIVITIES, Permission.VIEW_CHANNEL
            ).complete();
            voiceChannel.upsertPermissionOverride(guild.getPublicRole()).setDeny(
                    Permission.VIEW_CHANNEL,
                    Permission.VOICE_CONNECT, Permission.VOICE_SPEAK,
                    Permission.VOICE_STREAM, Permission.VOICE_USE_VAD,
                    Permission.VOICE_START_ACTIVITIES, Permission.VIEW_CHANNEL
            );

            StreamLine.discordData.addToVoice(l, voiceChannel.getIdLong());
            if (ConfigUtils.debug())
                MessagingUtils.logInfo("Added " + l + " to voice channel " + voiceChannel.getName() + " (" + voiceChannel.getId() + ")!");
        }

        return voiceChannel;
    }

    public static String deleteVoice(long voiceID, CategoryType type) {
        JDA jda = StreamLine.getJda();

        Guild guild = jda.getGuildById(DiscordBotConfUtils.guildID());
        VoiceChannel channel = guild.getVoiceChannelById(voiceID);
        if (channel == null) return null;

        for (long id : StreamLine.discordData.idsForVoice(channel.getIdLong())) {
            StreamLine.discordData.removeFromVoice(id, channel.getIdLong());
        }

        String name = channel.getName();

        channel.delete().complete();
        return name;
    }

    public static VoiceChannel addToVoice(long voiceID, SavablePlayer... players) {
        VoiceChannel channel = null;

        for (SavablePlayer player : players) {
            if (!StreamLine.discordData.isVerified(player.uuid)) continue;

            JDA jda = StreamLine.getJda();
            long l = StreamLine.discordData.getIDOfVerified(player.uuid);

            Guild guild = jda.getGuildById(DiscordBotConfUtils.guildID());
            if (guild == null) return null;

            channel = guild.getVoiceChannelById(voiceID);
            if (channel == null) continue;

            User user = jda.getUserById(l);
            if (user == null) continue;
            Member member = guild.getMember(user);
            if (member == null) {
                MessagingUtils.logWarning("Member with id " + l + " returned null!");
                continue;
            }

            if (isInPermissionOverride(member, channel)) continue;

            channel.createPermissionOverride(member).setAllow(
                    Permission.VIEW_CHANNEL,
                    Permission.VOICE_CONNECT, Permission.VOICE_SPEAK,
                    Permission.VOICE_STREAM, Permission.VOICE_USE_VAD,
                    Permission.VOICE_START_ACTIVITIES, Permission.VIEW_CHANNEL
            ).complete();

            StreamLine.discordData.addToVoice(l, channel.getIdLong());
        }
        return channel;
    }

    public static boolean isInPermissionOverride(Member member, VoiceChannel channel) {
        for (PermissionOverride p : channel.getMemberPermissionOverrides()) {
            if (Objects.equals(p.getMember(), member)) return true;
        }

        return false;
    }

    public static VoiceChannel removeFromVoice(long voiceID, SavablePlayer... players) {
        VoiceChannel channel = null;

        for (SavablePlayer player : players) {
            if (!StreamLine.discordData.isVerified(player.uuid)) continue;

            JDA jda = StreamLine.getJda();
            long l = StreamLine.discordData.getIDOfVerified(player.uuid);

            Guild guild = jda.getGuildById(DiscordBotConfUtils.guildID());
            channel = guild.getVoiceChannelById(voiceID);
            if (channel == null) continue;

            User user = jda.getUserById(l);
            if (user == null) continue;
            Member member = guild.getMember(user);
            if (member == null) {
                MessagingUtils.logWarning("Member with id " + l + " returned null!");
                continue;
            }
            channel.putPermissionOverride(member).resetAllow().complete();
            guild.kickVoiceMember(member).complete();

            StreamLine.discordData.removeFromVoice(l, channel.getIdLong());
        }

        return channel;
    }

    public static List<VoiceChannel> getVoiceChannelsByPlayer(SavablePlayer player) {
        List<VoiceChannel> channels = new ArrayList<>();

        if (ConfigUtils.debug()) MessagingUtils.logInfo("Trying on player: " + player.latestName);

        if (! StreamLine.discordData.isVerified(player.uuid)) {
            if (ConfigUtils.debug()) MessagingUtils.logInfo(player.latestName + " is not verified...");
            return channels;
        }

        for (long l : StreamLine.discordData.getVoiceFrom(StreamLine.discordData.getIDOfVerified(player.uuid))) {
            if (ConfigUtils.debug()) MessagingUtils.logInfo("Found channel id: " + l);

            for (Guild guild : StreamLine.getJda().getGuilds()) {
                if (ConfigUtils.debug()) MessagingUtils.logInfo("Trying on guild with id: " + guild.getId());
                VoiceChannel channel = guild.getVoiceChannelById(l);
                if (channel == null) continue;
                channels.add(channel);
                if (ConfigUtils.debug()) MessagingUtils.logInfo("Added channel with name: " + channel.getName());
            }
        }

        return channels;
    }

    public static boolean discordGuildHasPlayers(SavablePlayer... players) {
        JDA jda = StreamLine.getJda();

        List<Long> discordIDs = new ArrayList<>();
        for (SavablePlayer player : players) {
            discordIDs.add(StreamLine.discordData.getIDOfVerified(player.uuid));
        }

        Guild guild = jda.getGuildById(DiscordBotConfUtils.guildID());
        if (guild == null) return false;

        for (long l : discordIDs) {
            if (guild.getMemberById(l) == null) return false;
        }

        return true;
    }

    public static List<VoiceChannel> getVoice(SavablePlayer of, String name) {
        List<VoiceChannel> channels = new ArrayList<>();

        for (VoiceChannel channel : getVoiceChannelsByPlayer(of)) {
            if (channel.getName().equals(name)) {
                channels.add(channel);
            }
        }

        return channels;
    }

    public static Category getCategory(CategoryType type) {
        long id = DiscordBotConfUtils.categoryGet(type);

        for (Guild guild : StreamLine.getJda().getGuilds()) {
            Category category = guild.getCategoryById(id);
            if (category == null) continue;
            return category;
        }

        return null;
    }

    public static boolean canCreateMoreVoice(SavablePlayer player) {
        List<Long> vcList = StreamLine.discordData.getVoiceFrom(StreamLine.discordData.getIDOfVerified(player.uuid));

        if (vcList == null) return true;

        return vcList.size() < PluginUtils.findHighestNumberWithBasePermission(player, ConfigUtils.moduleBVoiceMaxBasePerm()).getKey();
    }

    public static boolean vcNameAlreadyExists(String vcName, CategoryType categoryType) {
        JDA jda = StreamLine.getJda();
        Guild guild = jda.getGuildById(DiscordBotConfUtils.guildID());
        if (guild == null) return false;

        Category category = guild.getCategoryById(DiscordBotConfUtils.categoryGet(categoryType));
        if (category == null) return false;

        for (VoiceChannel voiceChannel : category.getVoiceChannels()) {
            if (voiceChannel.getName().equals(vcName)) return true;
        }

        return false;
    }
}
