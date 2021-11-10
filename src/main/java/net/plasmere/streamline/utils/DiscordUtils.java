package net.plasmere.streamline.utils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.DiscordBotConfUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.enums.CategoryType;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;

import java.util.ArrayList;
import java.util.List;

public class DiscordUtils {
    public static List<VoiceChannel> createVoice(String name, CategoryType type, SavablePlayer... players) {
        JDA jda = StreamLine.getJda();

        List<Long> discordIDs = new ArrayList<>();
        for (SavablePlayer player : players) {
            discordIDs.add(StreamLine.discordData.getIDOfVerified(player.uuid));
        }

        List<VoiceChannel> channels = new ArrayList<>();

        for (Guild guild : jda.getGuilds()) {
            if (!discordGuildHasPlayers(players)) continue;
            Category category = guild.getCategoryById(DiscordBotConfUtils.categoryGet(type));
            if (category == null) continue;
            VoiceChannel voiceChannel = guild.createVoiceChannel(name, category).complete();

            if (ConfigUtils.debug()) MessagingUtils.logInfo("Created voice channel " + voiceChannel.getName() + " (" + voiceChannel.getId() + ")");

            for (long l : discordIDs) {
                User user = jda.getUserById(l);
                if (user == null) continue;
                Member member = guild.getMember(user);
                if (member == null) {
                    MessagingUtils.logWarning("Member with id " + l + " returned null!");
                    continue;
                }
                voiceChannel.createPermissionOverride(member).setAllow(
                        Permission.VOICE_CONNECT, Permission.VOICE_SPEAK,
                        Permission.VOICE_STREAM, Permission.VOICE_USE_VAD,
                        Permission.VOICE_START_ACTIVITIES, Permission.VIEW_CHANNEL
                ).complete();

                StreamLine.discordData.addToVoice(l, voiceChannel.getIdLong());
                if (ConfigUtils.debug()) MessagingUtils.logInfo("Added " + l + " to voice channel " + voiceChannel.getName() + " (" + voiceChannel.getId() + ")!");
            }

            channels.add(voiceChannel);
        }

        return channels;
    }

    public static List<String> deleteVoice(long voiceID, CategoryType type) {
        JDA jda = StreamLine.getJda();

        List<String> channels = new ArrayList<>();

        for (Guild guild : jda.getGuilds()) {
            VoiceChannel channel = guild.getVoiceChannelById(voiceID);
            if (channel == null) continue;

            channels.add(channel.getName());

            for (long id : StreamLine.discordData.idsForVoice(channel.getIdLong())) {
                StreamLine.discordData.removeFromVoice(id, channel.getIdLong());
            }

            channel.delete().complete();
        }

        return channels;
    }

    public static List<VoiceChannel> addToVoice(long voiceID, SavablePlayer... players) {
        List<VoiceChannel> channels = new ArrayList<>();

        for (SavablePlayer player : players) {
            if (! StreamLine.discordData.isVerified(player.uuid)) continue;

            JDA jda = StreamLine.getJda();
            long l = StreamLine.discordData.getIDOfVerified(player.uuid);

            for (Guild guild : jda.getGuilds()) {
                VoiceChannel channel = guild.getVoiceChannelById(voiceID);
                if (channel == null) continue;

                User user = jda.getUserById(l);
                if (user == null) continue;
                Member member = guild.getMember(user);
                if (member == null) {
                    MessagingUtils.logWarning("Member with id " + l + " returned null!");
                    continue;
                }
                channel.createPermissionOverride(member).setAllow(
                        Permission.VOICE_CONNECT, Permission.VOICE_SPEAK,
                        Permission.VOICE_STREAM, Permission.VOICE_USE_VAD,
                        Permission.VOICE_START_ACTIVITIES
                ).complete();

                StreamLine.discordData.addToVoice(l, channel.getIdLong());
                channels.add(channel);
            }
        }

        return channels;
    }

    public static List<VoiceChannel> removeFromVoice(long voiceID, SavablePlayer... players) {
        List<VoiceChannel> channels = new ArrayList<>();

        for (SavablePlayer player : players) {
            if (! StreamLine.discordData.isVerified(player.uuid)) continue;

            JDA jda = StreamLine.getJda();
            long l = StreamLine.discordData.getIDOfVerified(player.uuid);

            for (Guild guild : jda.getGuilds()) {
                VoiceChannel channel = guild.getVoiceChannelById(voiceID);
                if (channel == null) continue;

                User user = jda.getUserById(l);
                if (user == null) continue;
                Member member = guild.getMember(user);
                if (member == null) {
                    MessagingUtils.logWarning("Member with id " + l + " returned null!");
                    continue;
                }
                channel.putPermissionOverride(member).resetAllow().complete();

                StreamLine.discordData.removeFromVoice(l, channel.getIdLong());
                channels.add(channel);
            }
        }

        return channels;
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

        for (Guild guild : jda.getGuilds()) {
            for (long l : discordIDs) {
                if (guild.getMemberById(l) == null) return false;
            }
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
}
