package net.plasmere.streamline.config.from;

import java.util.Arrays;

public class From_1_0_15_2 extends From{
    public From_1_0_15_2(String language) {
        super(language);
    }

    @Override
    public String versionFrom() {
        return "1.0.15.0";
    }

    @Override
    public void getCatchAll_values() {

    }

    @Override
    public void setupConfigFix() {
        addUpdatedConfigEntry("modules.bungee.mysqlbridger.enabled", true);
    }

    @Override
    public void setupLocalesFix() {
        addUpdatedLocalesEntry("mysqlbridger.execute.not-supplied", "&cYou haven't supplied an execution!", "en_US");
        addUpdatedLocalesEntry("mysqlbridger.execute.not-valid", "&cYou haven't supplied a valid execution!", "en_US");
        addUpdatedLocalesEntry("mysqlbridger.execute.complete", "&eExecution &7(&c%set%&7) &efinished&8!", "en_US");

        addUpdatedLocalesEntry("mysqlbridger.query.not-supplied", "&cYou haven't supplied an query!", "en_US");
        addUpdatedLocalesEntry("mysqlbridger.query.not-valid", "&cYou haven't supplied a valid query!", "en_US");
        addUpdatedLocalesEntry("mysqlbridger.query.complete", "&eQuery came back as &7(&c%set%&7)&8: &r%return%", "en_US");

        addUpdatedLocalesEntry("mysqlbridger.execute.not-supplied", "&cYou haven't supplied an execution!", "fr_FR");
        addUpdatedLocalesEntry("mysqlbridger.execute.not-valid", "&cYou haven't supplied a valid execution!", "fr_FR");
        addUpdatedLocalesEntry("mysqlbridger.execute.complete", "&eExecution &7(&c%set%&7) &efinished&8!", "fr_FR");

        addUpdatedLocalesEntry("mysqlbridger.query.not-supplied", "&cYou haven't supplied an query!", "fr_FR");
        addUpdatedLocalesEntry("mysqlbridger.query.not-valid", "&cYou haven't supplied a valid query!", "fr_FR");
        addUpdatedLocalesEntry("mysqlbridger.query.complete", "&eQuery came back as &7(&c%set%&7)&8: &r%return%", "fr_FR");
    }

    @Override
    public void setupServerConfigFix() {

    }

    @Override
    public void setupDiscordBotFix() {
    }

    @Override
    public void setupCommandsFix() {
        addUpdatedCommandsEntry("commands.bungee.configs.msbexecute.enabled", true);
        addUpdatedCommandsEntry("commands.bungee.configs.msbexecute.base", "msbexecute");
        addUpdatedCommandsEntry("commands.bungee.configs.msbexecute.permission", "streamline.command.mysqlbrider.execute");
        addUpdatedCommandsEntry("commands.bungee.configs.msbexecute.aliases", Arrays.asList("dbexecute", "dbex"));

        addUpdatedCommandsEntry("commands.bungee.configs.msbquery.enabled", true);
        addUpdatedCommandsEntry("commands.bungee.configs.msbquery.base", "msbquery");
        addUpdatedCommandsEntry("commands.bungee.configs.msbquery.permission", "streamline.command.mysqlbrider.query");
        addUpdatedCommandsEntry("commands.bungee.configs.msbquery.aliases", Arrays.asList("dbquery", "dbq"));
    }

    @Override
    public void setupChatsFix() {

    }
}
