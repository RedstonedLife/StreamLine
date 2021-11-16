package net.plasmere.streamline.commands.messaging;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.DiscordBotConfUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.utils.MessagingUtils;

import java.util.ArrayList;
import java.util.Collection;

public class BVerifyCommand extends SLCommand {

    public BVerifyCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            long verificationNum = StreamLine.discordData.getVerification(((ProxiedPlayer) sender).getUniqueId().toString());
            MessagingUtils.sendBUserMessage(sender, "&aYour verification number: &6" + verificationNum +
                    "\n&aGo onto the discord and type &d" + DiscordBotConfUtils.botPrefix() + "verify " + sender.getName() + " " + verificationNum + " &ato verify!");
        } else {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.onlyPlayers());
        }
    }

    @Override
    public Collection<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
