package net.plasmere.streamline.config.from;

import java.util.Arrays;

public class From_1_0_14_8 extends From{
    public From_1_0_14_8(String language) {
        super(language);
    }

    @Override
    public String versionFrom() {
        return "1.0.14.8";
    }

    @Override
    public void getCatchAll_values() {

    }

    @Override
    public void setupConfigFix() {
        addUpdatedConfigEntry("modules.discord.proxy-chat.display-names.verifying.change.unchangeable", true);

        addUpdatedConfigEntry("modules.bungee.votifier.enabled", true);
        addUpdatedConfigEntry("modules.bungee.votifier.on-vote.run", "on-vote.sl");

        addUpdatedConfigEntry("modules.discord.guilds.sync", true);
        addUpdatedConfigEntry("modules.discord.parties.sync", true);

        addUpdatedConfigEntry("modules.bungee.voice.max.default", 1);
        addUpdatedConfigEntry("modules.bungee.voice.max.base-permission", "streamline.voice.limit.");
    }

    @Override
    public void setupLocalesFix() {
        addUpdatedLocalesEntry("voice.create", "&eYou have created a voice channel named &c%name%&e!", "en_US");
        addUpdatedLocalesEntry("voice.delete.sender", "&eYou have deleted your voice channel named &c%name%&e!", "en_US");
        addUpdatedLocalesEntry("voice.delete.other", "%sender_formatted% &ehas deleted their voice channel named &c%name% &ethat you were added to!", "en_US");
        addUpdatedLocalesEntry("voice.add.sender", "&eYou have added %player_formatted% &eto your voice channel called &c%name%&e!", "en_US");
        addUpdatedLocalesEntry("voice.add.other", "&eYou have been added to %sender_formatted%&e's &evoice channel called &c%name%&e!", "en_US");
        addUpdatedLocalesEntry("voice.remove.sender", "&eYou have removed %player_formatted% &efrom your voice channel called &c%name%&e!", "en_US");
        addUpdatedLocalesEntry("voice.remove.other", "&eYou have been removed from %sender_formatted%&e's &evoice channel called &c%name%&e!", "en_US");
        addUpdatedLocalesEntry("voice.not-verified", "%player_formatted% &cis not verified with the discord!", "en_US");
        addUpdatedLocalesEntry("voice.no-voice", "&cCould not locate that voice channel!", "en_US");
        addUpdatedLocalesEntry("voice.already-voice", "&cYou already made that voice channel!", "en_US");
        addUpdatedLocalesEntry("voice.too-many", "&cYou cannot create any more voice channels!", "en_US");

        addUpdatedLocalesEntry("voice.create", "&eYou have created a voice channel named &c%name%&e!", "fr_FR");
        addUpdatedLocalesEntry("voice.delete.sender", "&eYou have deleted your voice channel named &c%name%&e!", "fr_FR");
        addUpdatedLocalesEntry("voice.delete.other", "%sender_formatted% &ehas deleted their voice channel named &c%name% &ethat you were added to!", "fr_FR");
        addUpdatedLocalesEntry("voice.add.sender", "&eYou have added %player_formatted% &eto your voice channel called &c%name%&e!", "fr_FR");
        addUpdatedLocalesEntry("voice.add.other", "&eYou have been added to %sender_formatted%&e's &evoice channel called &c%name%&e!", "fr_FR");
        addUpdatedLocalesEntry("voice.remove.sender", "&eYou have removed %player_formatted% &efrom your voice channel called &c%name%&e!", "fr_FR");
        addUpdatedLocalesEntry("voice.remove.other", "&eYou have been removed from %sender_formatted%&e's &evoice channel called &c%name%&e!", "fr_FR");
        addUpdatedLocalesEntry("voice.not-verified", "%player_formatted% &cis not verified with the discord!", "fr_FR");
        addUpdatedLocalesEntry("voice.no-voice", "&cCould not locate that voice channel!", "fr_FR");
        addUpdatedLocalesEntry("voice.already-voice", "&cYou already made that voice channel!", "fr_FR");
        addUpdatedLocalesEntry("voice.too-many", "&cYou cannot create any more voice channels!", "fr_FR");
    }

    @Override
    public void setupServerConfigFix() {

    }

    @Override
    public void setupDiscordBotFix() {
        addUpdatedDiscordBotEntry("discord.categories.guilds", "put_id_here");
        addUpdatedDiscordBotEntry("discord.categories.parties", "put_id_here");
        addUpdatedDiscordBotEntry("discord.categories.voice", "put_id_here");

        addUpdatedDiscordBotEntry("discord.guild-id", "put_id_here");
    }

    @Override
    public void setupCommandsFix() {
        addUpdatedCommandsEntry("commands.bungee.messaging.voice.enabled", true);
        addUpdatedCommandsEntry("commands.bungee.messaging.voice.base", "voice");
        addUpdatedCommandsEntry("commands.bungee.messaging.voice.permission", "streamline.command.voice");
        addUpdatedCommandsEntry("commands.bungee.messaging.voice.aliases", Arrays.asList("call", "vce"));
    }

    @Override
    public void setupChatsFix() {
        addUpdatedChatsEntry("chats.default-just-first-join", true);
    }
}
