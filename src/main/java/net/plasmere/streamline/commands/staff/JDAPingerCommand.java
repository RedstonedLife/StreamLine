package net.plasmere.streamline.commands.staff;

import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.DiscordBotConfUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.utils.JDAPingerUtils;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.TextUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.plasmere.streamline.objects.command.SLCommand;

import java.util.ArrayList;
import java.util.Collection;

import net.dv8tion.jda.api.JDA;

import java.util.Objects;

public class JDAPingerCommand extends SLCommand {
    public JDAPingerCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void run(CommandSender sender, String[] strings) {
        if (! ConfigUtils.moduleDEnabled()) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeCommandErrorUnd());
            return;
        }

        JDA jda = StreamLine.getJda();

        ProxiedPlayer player = (ProxiedPlayer) sender;
        player.sendMessage(TextUtils.codedText("&aAttempting to ping..."));

        JDAPingerUtils.sendMessage(Objects.requireNonNull(jda.getTextChannelById(DiscordBotConfUtils.textChannelBConsole())));
    }

    @Override
    public Collection<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
