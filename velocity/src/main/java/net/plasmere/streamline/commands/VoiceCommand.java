package net.plasmere.streamline.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.objects.enums.CategoryType;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.utils.DiscordUtils;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.TextUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class VoiceCommand extends SLCommand {
    private String perm = "";

    public VoiceCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);

        this.perm = perm;
    }

    @Override
    public void run(CommandSource sender, String[] args) {
        if (sender instanceof Player){
            switch (args[0]) {
                case "create" -> {
                    if (args.length < 2) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
                        return;
                    }
                    if (args.length > 2) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsLess());
                        return;
                    }

                    SavablePlayer player = PlayerUtils.getOrGetPlayerStat(PlayerUtils.getSourceName(sender));
                    if (player == null) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorNoYou()
                            .replace("%class%", this.getClass().getName())
                    );
                        return;
                    }

                    if (!StreamLine.discordData.isVerified(player.uuid)) {
                        MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllSenderBungee(TextUtils.replaceAllPlayerBungee(MessageConfUtils.voiceNotVerified(), player), sender));
                        return;
                    }

                    if (!DiscordUtils.canCreateMoreVoice(player)) {
                        MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllSenderBungee(TextUtils.replaceAllPlayerBungee(MessageConfUtils.voiceTooMany(), player), sender));
                        return;
                    }

                    if (hasVoice(player, args[1])) {
                        MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllSenderBungee(MessageConfUtils.voiceAlreadyVoice(), sender));
                        return;
                    }

                    VoiceChannel channel = DiscordUtils.createVoice(args[1], CategoryType.VOICE, player);

                    MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllSenderBungee(MessageConfUtils.voiceCreate()
                            .replace("%name%", channel.getName()), sender
                    ));
                }
                case "delete" -> {
                    if (args.length < 2) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
                        return;
                    }
                    if (args.length > 2) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsLess());
                        return;
                    }

                    SavablePlayer player = PlayerUtils.getOrGetPlayerStat(PlayerUtils.getSourceName(sender));
                    if (player == null) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorNoYou()
                            .replace("%class%", this.getClass().getName())
                    );
                        return;
                    }

                    if (! hasVoice(player, args[1])) {
                        MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllSenderBungee(MessageConfUtils.voiceNoVoice(), sender));
                        return;
                    }

                    List<VoiceChannel> voiceChannels = DiscordUtils.getVoiceChannelsByPlayer(player);

                    for (VoiceChannel channel : voiceChannels) {
                        if (! channel.getName().equals(args[1])) continue;

                        for (long id : StreamLine.discordData.idsForVoice(channel.getIdLong())) {
                            if (id == StreamLine.discordData.getIDOfVerified(player.uuid)) {
                                MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllSenderBungee(MessageConfUtils.voiceDeleteSender()
                                        .replace("%name%", channel.getName()), sender
                                ));
                            } else {
                                MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllSenderBungee(MessageConfUtils.voiceDeleteOther()
                                        .replace("%name%", channel.getName()), sender
                                ));
                            }
                        }

                        DiscordUtils.deleteVoice(channel.getIdLong(), CategoryType.VOICE);
                    }
                }
                case "add" -> {
                    if (args.length < 3) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
                        return;
                    }
                    if (args.length > 3) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsLess());
                        return;
                    }

                    SavablePlayer player = PlayerUtils.getOrGetPlayerStat(PlayerUtils.getSourceName(sender));
                    if (player == null) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorNoYou()
                            .replace("%class%", this.getClass().getName())
                    );
                        return;
                    }

                    SavablePlayer other = PlayerUtils.getOrGetPlayerStat(args[2]);
                    if (other == null) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPlayer());
                        return;
                    }

                    if (! StreamLine.discordData.isVerified(other.uuid)) {
                        MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllSenderBungee(TextUtils.replaceAllPlayerBungee(MessageConfUtils.voiceNotVerified(), other), sender));
                        return;
                    }

                    if (! hasVoice(player, args[1])) {
                        MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllSenderBungee(MessageConfUtils.voiceNoVoice(), sender));
                        return;
                    }

                    for (VoiceChannel c : DiscordUtils.getVoice(player, args[1])) {
                        VoiceChannel channel = DiscordUtils.addToVoice(c.getIdLong(), other);
                        MessagingUtils.sendBUserMessage(other, TextUtils.replaceAllSenderBungee(TextUtils.replaceAllPlayerBungee(MessageConfUtils.voiceAddOther()
                                        .replace("%name%", channel.getName())
                                , other), sender));
                        MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllSenderBungee(TextUtils.replaceAllPlayerBungee(MessageConfUtils.voiceAddSender()
                                        .replace("%name%", channel.getName())
                                , other), sender));

                    }
                }
                case "remove" -> {
                    if (args.length < 3) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
                        return;
                    }
                    if (args.length > 3) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsLess());
                        return;
                    }

                    SavablePlayer player = PlayerUtils.getOrGetPlayerStat(PlayerUtils.getSourceName(sender));
                    if (player == null) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorNoYou()
                            .replace("%class%", this.getClass().getName())
                    );
                        return;
                    }

                    SavablePlayer other = PlayerUtils.getOrGetPlayerStat(args[2]);
                    if (other == null) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPlayer());
                        return;
                    }

                    if (! StreamLine.discordData.isVerified(other.uuid)) {
                        MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllSenderBungee(TextUtils.replaceAllPlayerBungee(MessageConfUtils.voiceNotVerified(), other), sender));
                        return;
                    }

                    if (! hasVoice(player, args[1])) {
                        MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllSenderBungee(MessageConfUtils.voiceNoVoice(), sender));
                        return;
                    }

                    for (VoiceChannel c : DiscordUtils.getVoice(player, args[1])) {
                        VoiceChannel channel = DiscordUtils.removeFromVoice(c.getIdLong(), other);
                        MessagingUtils.sendBUserMessage(other, TextUtils.replaceAllSenderBungee(TextUtils.replaceAllPlayerBungee(MessageConfUtils.voiceRemoveOther()
                                        .replace("%name%", channel.getName())
                                , other), sender));
                        MessagingUtils.sendBUserMessage(sender, TextUtils.replaceAllSenderBungee(TextUtils.replaceAllPlayerBungee(MessageConfUtils.voiceRemoveSender()
                                        .replace("%name%", channel.getName())
                                , other), sender));

                    }
                }
//                case "check" -> {
//
//                }
                default -> {
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
                    return;
                }
            }
        } else {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.onlyPlayers());
        }
    }

    public boolean hasVoice(SavablePlayer player, String name) {
        List<VoiceChannel> currents = DiscordUtils.getVoiceChannelsByPlayer(player);

        for (VoiceChannel channel : currents) {
            if (channel.getName().equals(name)) return true;
        }

        return false;
    }

    @Override
    public Collection<String> onTabComplete(CommandSource sender, String[] args) {
        List<String> options = new ArrayList<>();

        options.add("create");
        options.add("delete");
        options.add("add");
        options.add("remove");

        if (! (sender instanceof Player)) return new ArrayList<>();

        SavablePlayer player = PlayerUtils.getOrGetPlayerStat(PlayerUtils.getSourceName(sender));
        if (player == null) return new ArrayList<>();

        if (args.length == 1) {
            return TextUtils.getCompletion(options, args[0]);
        }
        if (args.length == 2 && (args[0].equals("add") || args[0].equals("delete") || args[0].equals("remove"))) {
            List<String> voiceCalls = new ArrayList<>();
            for (VoiceChannel voiceChannel : DiscordUtils.getVoiceChannelsByPlayer(player)) {
                voiceCalls.add(voiceChannel.getName());
            }

            return TextUtils.getCompletion(voiceCalls, args[1]);
        }
        if (args.length == 3 && (args[0].equals("add") || args[0].equals("remove"))) {
            return TextUtils.getCompletion(PlayerUtils.getPlayerNamesForAllOnline(), args[2]);
        }

        return new ArrayList<>();
    }
}
