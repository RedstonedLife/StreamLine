package net.plasmere.streamline.commands.staff.settings;

import com.velocitypowered.api.command.CommandSource;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigHandler;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.command.SLCommand;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.TextUtils;

import java.util.Collection;
import java.util.TreeSet;

public class LanguageCommand extends SLCommand {
    public LanguageCommand(String base, String perm, String[] aliases){
        super(base, perm, aliases);
    }

    @Override
    public void run(CommandSource sender, String[] args) {
        if (args.length < 1) {
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsMore());
        } else if (args.length > 1){
            MessagingUtils.sendBUserMessage(sender, MessageConfUtils.bungeeNeedsLess());
        } else {
            if (! TextUtils.equalsAny(args[0], ConfigHandler.acceptableTranslations())) {
                MessagingUtils.sendBUserMessage(sender, MessageConfUtils.languageInvalidLocale().replace("%locale%", args[0]));
            } else {
                try {
                    StreamLine.config.setLanguage(args[0]);
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.languageMessage().replace("%locale%", args[0]));
                } catch (Exception e) {
                    e.printStackTrace();
                    MessagingUtils.sendBUserMessage(sender, MessageConfUtils.languageInvalidLocale().replace("%locale%", args[0]));
                }
            }
        }
    }

    @Override
    public Collection<String> onTabComplete(final CommandSource sender, final String[] args) {
        TreeSet<String> locales = new TreeSet<>();

        locales.addAll(ConfigHandler.acceptableTranslations());

        if (args.length == 1) {
            return TextUtils.getCompletion(locales, args[0]);
        }

        return new TreeSet<>();
    }
}
