package net.plasmere.streamline.config.from;

import net.plasmere.streamline.utils.MessagingUtils;

import java.util.Arrays;

public class From_1_0_14_5 extends From {
    public From_1_0_14_5(String language) {
    super(language);
}

    @Override
    public String versionFrom() {
        return "1.0.14.5";
    }

    @Override
    public void getCatchAll_values() {

    }

    @Override
    public void setupConfigFix() {
        MessagingUtils.logSevere("Set up you bStats settings. They are at the bottom of your config.yml.");
        addUpdatedConfigEntry("modules.bstats.make-server-discoverable", true);
        addUpdatedConfigEntry("modules.bstats.discoverable-ip", "my.server.net");

        addUpdatedConfigEntry("modules.bungee.ranks.enable", true);
        addUpdatedConfigEntry("modules.bungee.ranks.points.uses", "%player_votes%");

        addUpdatedConfigEntry("modules.bungee.parties.group-size.default", 10);
        addUpdatedConfigEntry("modules.bungee.parties.group-size.admin", 100);

        addUpdatedConfigEntry("modules.bungee.chat-history.view.default", 7);
        addUpdatedConfigEntry("modules.bungee.chat-history.view.max", 20);

        addUpdatedConfigEntry("modules.bungee.chat-filters.enabled", true);
    }

    @Override
    public void setupLocalesFix() {
        addUpdatedLocalesEntry("votes.console.toggle", "&eToggled console printing to %toggle%&8!", "en_US");
        addUpdatedLocalesEntry("votes.console.enabled", "&aTRUE", "en_US");
        addUpdatedLocalesEntry("votes.console.disabled", "&cFALSE", "en_US");
        addUpdatedLocalesEntry("votes.get", "&eVotes of &d%player_formatted%&8: &6%player_votes%", "en_US");
        addUpdatedLocalesEntry("votes.remove", "&eRemoved &6%votes% &evote(s) from &d%player_formatted%&8! &eCurrent&8: &6%player_votes%", "en_US");
        addUpdatedLocalesEntry("votes.add", "&eAdded &6%votes% &evote(s) to &d%player_formatted%&8! &eCurrent&8: &6%player_votes%", "en_US");
        addUpdatedLocalesEntry("votes.set", "&eSet &6%votes% &evote(s) for &d%player_formatted%&8! &eCurrent&8: &6%player_votes%", "en_US");

        addUpdatedLocalesEntry("history.message", "&eShowing &cchat history &efor &d%player_formatted%&8:%newline%%chat_bulk%", "en_US");
        addUpdatedLocalesEntry("history.chat-bulk.message", "&b%timestamp% &4> &r%message%", "en_US");

        addUpdatedLocalesEntry("filters.enabled", "&aTRUE", "en_US");
        addUpdatedLocalesEntry("filters.disabled", "&cFALSE", "en_US");
        addUpdatedLocalesEntry("filters.replacements.last", "&c%replacement%", "en_US");
        addUpdatedLocalesEntry("filters.replacements.not-last", "&c%replacement%&8, ", "en_US");
        addUpdatedLocalesEntry("filters.command.toggle.message", "&eJust toggled chat filter &b\"&c%name%&b\" &eto %toggle%", "en_US");
        addUpdatedLocalesEntry("filters.command.create.message", "&eJust created a new chat filter as &0(%toggle%&8, &eregex&8: &c%regex%&8, &ereplacements&8: &0(%replacements%&0) &0)", "en_US");

        // FR
        // ||
        // vv

        addUpdatedLocalesEntry("votes.console.toggle", "&eToggled console printing to %toggle%&8!", "fr_FR");
        addUpdatedLocalesEntry("votes.console.enabled", "&aTRUE", "fr_FR");
        addUpdatedLocalesEntry("votes.console.disabled", "&cFALSE", "fr_FR");
        addUpdatedLocalesEntry("votes.get", "&eVotes of &d%player_formatted%&8: &6%player_votes%", "fr_FR");
        addUpdatedLocalesEntry("votes.remove", "&eRemoved &6%votes% &evote(s) from &d%player_formatted%&8! &eCurrent&8: &6%player_votes%", "fr_FR");
        addUpdatedLocalesEntry("votes.add", "&eAdded &6%votes% &evote(s) to &d%player_formatted%&8! &eCurrent&8: &6%player_votes%", "fr_FR");
        addUpdatedLocalesEntry("votes.set", "&eSet &6%votes% &evote(s) for &d%player_formatted%&8! &eCurrent&8: &6%player_votes%", "fr_FR");

        addUpdatedLocalesEntry("history.message", "&eShowing &cchat history &efor &d%player_formatted%&8:%newline%%chat_bulk%", "fr_FR");
        addUpdatedLocalesEntry("history.chat-bulk.message", "&b%timestamp% &4> &r%message%", "fr_FR");

        addUpdatedLocalesEntry("filters.enabled", "&aTRUE", "fr_FR");
        addUpdatedLocalesEntry("filters.disabled", "&cFALSE", "fr_FR");
        addUpdatedLocalesEntry("filters.replacements.last", "&c%replacement%", "fr_FR");
        addUpdatedLocalesEntry("filters.replacements.not-last", "&c%replacement%&8, ", "fr_FR");
        addUpdatedLocalesEntry("filters.command.toggle.message", "&eJust toggled chat filter &b\"&c%name%&b\" &eto %toggle%", "fr_FR");
        addUpdatedLocalesEntry("filters.command.create.message", "&eJust created a new chat filter as &0(%toggle%&8, &eregex&8: &c%regex%&8, &ereplacements&8: &0(%replacements%&0) &0)", "fr_FR");
    }

    @Override
    public void setupServerConfigFix() {

    }

    @Override
    public void setupDiscordBotFix() {

    }

    @Override
    public void setupCommandsFix() {
        addUpdatedCommandsEntry("commands.bungee.ping.view-others.enabled", true);
        addUpdatedCommandsEntry("commands.bungee.ping.view-others.permission", "streamline.command.ping.others");

        addUpdatedCommandsEntry("commands.bungee.configs.votes.enabled", true);
        addUpdatedCommandsEntry("commands.bungee.configs.votes.base", "votes");
        addUpdatedCommandsEntry("commands.bungee.configs.votes.permission", "streamline.command.votes");
        addUpdatedCommandsEntry("commands.bungee.configs.votes.aliases", Arrays.asList("bvotes", "bvot"));

        addUpdatedCommandsEntry("commands.bungee.staff.script-reload.enabled", true);
        addUpdatedCommandsEntry("commands.bungee.staff.script-reload.base", "slsr");
        addUpdatedCommandsEntry("commands.bungee.staff.script-reload.permission", "streamline.command.scripts-reload");
        addUpdatedCommandsEntry("commands.bungee.staff.script-reload.aliases", Arrays.asList("rslsc", "reloadslsc"));

        addUpdatedCommandsEntry("commands.bungee.staff.chat-history.enabled", true);
        addUpdatedCommandsEntry("commands.bungee.staff.chat-history.base", "chat-history");
        addUpdatedCommandsEntry("commands.bungee.staff.chat-history.permission", "streamline.command.chat-history");
        addUpdatedCommandsEntry("commands.bungee.staff.chat-history.aliases", Arrays.asList("chath", "chh"));

        addUpdatedCommandsEntry("commands.bungee.party.permissions.join", "streamline.command.party.join");
        addUpdatedCommandsEntry("commands.bungee.party.permissions.leave", "streamline.command.party.leave");
        addUpdatedCommandsEntry("commands.bungee.party.permissions.create", "streamline.command.party.create");
        addUpdatedCommandsEntry("commands.bungee.party.permissions.promote", "streamline.command.party.promote");
        addUpdatedCommandsEntry("commands.bungee.party.permissions.chat", "streamline.command.party.demote");
        addUpdatedCommandsEntry("commands.bungee.party.permissions.list", "streamline.command.party.list");
        addUpdatedCommandsEntry("commands.bungee.party.permissions.open", "streamline.command.party.open");
        addUpdatedCommandsEntry("commands.bungee.party.permissions.close", "streamline.command.party.close");
        addUpdatedCommandsEntry("commands.bungee.party.permissions.disband", "streamline.command.party.disband");
        addUpdatedCommandsEntry("commands.bungee.party.permissions.accept", "streamline.command.party.accept");
        addUpdatedCommandsEntry("commands.bungee.party.permissions.deny", "streamline.command.party.deny");
        addUpdatedCommandsEntry("commands.bungee.party.permissions.invite", "streamline.command.party.invite");
        addUpdatedCommandsEntry("commands.bungee.party.permissions.kick", "streamline.command.party.kick");
        addUpdatedCommandsEntry("commands.bungee.party.permissions.mute", "streamline.command.party.mute");
        addUpdatedCommandsEntry("commands.bungee.party.permissions.warp", "streamline.command.party.warp");

        addUpdatedCommandsEntry("commands.bungee.guild.permissions.join", "streamline.command.guild.join");
        addUpdatedCommandsEntry("commands.bungee.guild.permissions.leave", "streamline.command.guild.leave");
        addUpdatedCommandsEntry("commands.bungee.guild.permissions.create", "streamline.command.guild.create");
        addUpdatedCommandsEntry("commands.bungee.guild.permissions.promote", "streamline.command.guild.promote");
        addUpdatedCommandsEntry("commands.bungee.guild.permissions.chat", "streamline.command.guild.demote");
        addUpdatedCommandsEntry("commands.bungee.guild.permissions.list", "streamline.command.guild.list");
        addUpdatedCommandsEntry("commands.bungee.guild.permissions.open", "streamline.command.guild.open");
        addUpdatedCommandsEntry("commands.bungee.guild.permissions.close", "streamline.command.guild.close");
        addUpdatedCommandsEntry("commands.bungee.guild.permissions.disband", "streamline.command.guild.disband");
        addUpdatedCommandsEntry("commands.bungee.guild.permissions.accept", "streamline.command.guild.accept");
        addUpdatedCommandsEntry("commands.bungee.guild.permissions.deny", "streamline.command.guild.deny");
        addUpdatedCommandsEntry("commands.bungee.guild.permissions.invite", "streamline.command.guild.invite");
        addUpdatedCommandsEntry("commands.bungee.guild.permissions.kick", "streamline.command.guild.kick");
        addUpdatedCommandsEntry("commands.bungee.guild.permissions.mute", "streamline.command.guild.mute");
        addUpdatedCommandsEntry("commands.bungee.guild.permissions.warp", "streamline.command.guild.warp");
        addUpdatedCommandsEntry("commands.bungee.guild.permissions.info", "streamline.command.guild.info");
        addUpdatedCommandsEntry("commands.bungee.guild.permissions.rename", "streamline.command.guild.rename");

        addUpdatedCommandsEntry("commands.bungee.staff.chat-filter.enabled", true);
        addUpdatedCommandsEntry("commands.bungee.staff.chat-filter.base", "chat-filter");
        addUpdatedCommandsEntry("commands.bungee.staff.chat-filter.permission", "streamline.command.chat-filter");
        addUpdatedCommandsEntry("commands.bungee.staff.chat-filter.aliases", Arrays.asList("chatf", "chf", "filter"));
    }

    @Override
    public void setupChatsFix() {

    }
}
