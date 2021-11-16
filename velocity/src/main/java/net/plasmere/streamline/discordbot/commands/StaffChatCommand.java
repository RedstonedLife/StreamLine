package net.plasmere.streamline.discordbot.commands;

import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.DiscordBotConfUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.TextUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.velocitypowered.api.proxy.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class StaffChatCommand {
    private static final EmbedBuilder eb = new EmbedBuilder();

    public static void sendMessage(String command, MessageReceivedEvent event){
        String om = event.getMessage().getContentDisplay();
        String prefix = DiscordBotConfUtils.botPrefix();

        String msg = om.substring((prefix + command + " ").length());

        MessagingUtils.sendStaffMessageFromDiscord(event.getAuthor().getId(), MessageConfUtils.discordStaffChatFrom(), msg);

        try {
            event.getChannel().sendMessage(eb.setTitle("Success!").setDescription("Message sent!").build()).queue();
        } catch (Exception e){
            e.printStackTrace();
        }

        if (ConfigUtils.debug()) MessagingUtils.logInfo("Sent message for \"" + command + "\"!");
    }
}
