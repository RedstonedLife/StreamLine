package net.plasmere.streamline.config.from;

import net.plasmere.streamline.utils.MessagingUtils;

import java.util.Arrays;

public class From_1_0_14_5 extends From{
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
    }

    @Override
    public void setupChatsFix() {

    }
}
