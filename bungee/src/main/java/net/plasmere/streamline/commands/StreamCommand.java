package net.plasmere.streamline.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.TextUtils;

import java.util.ArrayList;
import java.util.Collection;

public class StreamCommand extends SLCommand {
    private String perm = "";

    public StreamCommand(String base, String perm, String[] aliases) {
        super(base, perm, aliases);

        this.perm = perm;
    }

    @Override
    public void run(CommandSender sender, String[] args){
        if (sender instanceof ProxiedPlayer){
            ProxiedPlayer player = (ProxiedPlayer) sender;

            if (player.hasPermission(perm)){
                if (args.length != 1){
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.streamNeedLink());
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeImproperUsage()
                            .replace("%usage%", "/stream <link>")
                    );
                } else {
                    if (! args[0].startsWith("https://") && ! args[0].startsWith("http://") && ! args[0].startsWith("ftp://") && ! args[0].startsWith("sftp://")) {
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.streamNotLink());
                        MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeImproperUsage()
                                .replace("%usage%", "/stream <link>")
                        );
                    } else {
                        MessagingUtils.sendBCLHBroadcast(sender, TextUtils.replaceAllPlayerBungee(MessageConfUtils.streamMessage(), sender)
                                .replace("%link%", args[0])
                                , MessageConfUtils.streamHoverPrefix()
                        );
                    }
                }
            } else {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.noPerm());
            }
        } else {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.onlyPlayers());
        }
    }

    @Override
    public Collection<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
