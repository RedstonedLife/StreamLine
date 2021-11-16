package net.plasmere.streamline.commands.messaging;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.DiscordBotConfUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.TextUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

public class BVerifyCommand extends SLCommand {

    public BVerifyCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void run(CommandSource sender, String[] args) {
        if (sender instanceof Player) {
            long verificationNum = StreamLine.discordData.getVerification(((Player) sender).getUniqueId().toString());
            MessagingUtils.sendBUserMessage(sender, "&aYour verification number: &6" + verificationNum +
                    "\n&aGo onto the discord and type &d" + DiscordBotConfUtils.botPrefix() + "verify " + PlayerUtils.getSourceName(sender) + " " + verificationNum + " &ato verify!");
        } else {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.onlyPlayers());
        }
    }

    @Override
    public Collection<String> onTabComplete(CommandSource sender, String[] args) {
        return new ArrayList<>();
    }
}
