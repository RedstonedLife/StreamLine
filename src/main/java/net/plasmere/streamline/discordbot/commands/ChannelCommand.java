package net.plasmere.streamline.discordbot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.DiscordBotConfUtils;
import net.plasmere.streamline.objects.chats.ChatChannel;
import net.plasmere.streamline.objects.chats.ChatsHandler;
import net.plasmere.streamline.utils.MessagingUtils;

public class ChannelCommand {
    public static String usage = "Usage: channel <set | remove> <channel name> <identifier> <bypass: true or false> <joins: true or false> <leaves: true or false>";

    public static void sendMessage(String command, MessageReceivedEvent event){
        EmbedBuilder eb = new EmbedBuilder();
        event.getChannel().sendMessageEmbeds(eb.setDescription(compileCommands(event)).build()).queue();
        if (ConfigUtils.debug()) MessagingUtils.logInfo("Sent message for \"" + command + "\"!");
    }

    private static String compileCommands(MessageReceivedEvent event){
        // .channel <set | remove> <global | local | guild | party> <identifier>
        String message = event.getMessage().getContentRaw();
        String[] args = event.getMessage().getContentRaw().toLowerCase().substring(DiscordBotConfUtils.botPrefix().length()).split(" ");

        long channelID = event.getChannel().getIdLong();

        if (args.length < 2) {
            return "Improper syntax!\n" + usage;
        }

        switch (args[1]) {
            case "set":
                if (args.length < 4) {
                    return "Improper syntax!\n" + usage;
                }

                boolean bypass = false;

                try {
                    bypass = Boolean.parseBoolean(args[4]);
                } catch (Exception e) {
//                    e.printStackTrace();
                    return "Must specify if bypass is enabled!\n" + usage;
                }

                boolean joins = false;

                try {
                    joins = Boolean.parseBoolean(args[5]);
                } catch (Exception e) {
//                    e.printStackTrace();
                    return "Must specify if joins are enabled!\n" + usage;
                }

                boolean leaves = false;

                try {
                    leaves = Boolean.parseBoolean(args[6]);
                } catch (Exception e) {
//                    e.printStackTrace();
                    return "Must specify if leaves are enabled!\n" + usage;
                }

                ChatChannel chatChannel = ChatsHandler.getChannel(args[2]);
                if (chatChannel == null)
                    return "The specified channel could not be found...";

                StreamLine.discordData.addChannel(channelID, chatChannel.toString(), args[3], bypass, joins, leaves);
                return "Successfully added channel ``" + channelID + "`` to your set channels!" +
                        "\n``---`` Set As ``---``" +
                        "\nChannel: " + chatChannel +
                        "\nIdentifier: " + args[3] +
                        "\nBypasses proxy chat: " + bypass +
                        "\nSends join messages: " + joins +
                        "\nSends leaves messages: " + leaves;
            case "remove":
                StreamLine.discordData.remChannel(channelID);
                return "Successfully removed channel ``" + channelID + "`` from your set channels!";
            default:
                return "Improper syntax!\n" + usage;
        }
    }
}
