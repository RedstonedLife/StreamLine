package net.plasmere.streamline.discordbot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.DiscordBotConfUtils;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.UUIDUtils;

public class VerifyCommand {
    public static void sendMessage(String command, MessageReceivedEvent event){
        EmbedBuilder eb = new EmbedBuilder();
        event.getChannel().sendMessageEmbeds(eb.setDescription(compileCommands(event)).build()).queue();
        if (ConfigUtils.debug) MessagingUtils.logInfo("Sent message for \"" + command + "\"!");
    }

    private static String compileCommands(MessageReceivedEvent event){
        String[] args = event.getMessage().getContentRaw().toLowerCase().substring(DiscordBotConfUtils.botPrefix.length()).split(" ");

        try {
            String uuid = UUIDUtils.getCachedUUID(args[1]);

            int verifyNumber = StreamLine.discordData.getVerification(uuid);
            int trying = Integer.parseInt(args[2]);

//            Member member = StreamLine.getJda().getGuildById(event.getGuild().getIdLong()).getMemberById(event.getMessage().getAuthor().getIdLong());
//            if (member == null) return "We could not find you as a member!";

            SavablePlayer player = PlayerUtils.getOrGetPlayerStatByUUID(uuid);
            if (player == null) return "We could not find that player!";

            if (verifyNumber == trying) {
                StreamLine.discordData.doVerify(uuid, event.getMessage().getAuthor(), event.getGuild());
                return "Success! Discord account linked to " + player.latestName + "!";
            } else {
                return "You did not enter the correct number!";
            }
        } catch (Exception e) {
            if (ConfigUtils.debug) e.printStackTrace();
            return "You did not enter a number or you might need to do /bverify on the network!";
        }
    }
}
