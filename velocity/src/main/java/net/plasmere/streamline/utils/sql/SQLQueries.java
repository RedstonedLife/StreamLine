package net.plasmere.streamline.utils.sql;

public class SQLQueries {

    public static String tableCreation = "CREATE TABLE if not exists `player_data` (\n" +
            "  `uuid` char(128) PRIMARY KEY NOT NULL,\n" +
            "  `latestName` varchar(64) NOT NULL,\n" +
            "  `displayName` varchar(64) NOT NULL,\n" +
            "  `latestIp` varchar(64) NOT NULL,\n" +
            "  `latestVersion` varchar(16) NOT NULL,\n" +
            "  `latestServer` varchar(64) NOT NULL,\n" +
            "  `discordId` bigint,\n" +
            "  `muted` boolean DEFAULT 0,\n" +
            "  `mutedUntil` datetime,\n" +
            "  `currentPartyId` int,\n" +
            "  `currentGuildId` int,\n" +
            "  `points` int DEFAULT 0\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE if not exists `player_experience` (\n" +
            "  `uuid` char(128) PRIMARY KEY NOT NULL,\n" +
            "  `totalExperience` int NOT NULL,\n" +
            "  `currentExperience` int NOT NULL,\n" +
            "  `level` int NOT NULL DEFAULT 1\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE if not exists `player_names` (\n" +
            "  `uuid` char(128) PRIMARY KEY NOT NULL,\n" +
            "  `name` int UNIQUE NOT NULL\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE if not exists `player_chat` (\n" +
            "  `uuid` char(128) PRIMARY KEY NOT NULL,\n" +
            "  `chatChannel` varchar(64) NOT NULL,\n" +
            "  `chatId` varchar(64) NOT NULL\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE if not exists `player_addresses` (\n" +
            "  `uuid` char(128) PRIMARY KEY NOT NULL,\n" +
            "  `address` varchar(64) NOT NULL\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE if not exists `player_tags` (\n" +
            "  `uuid` char(128) PRIMARY KEY NOT NULL,\n" +
            "  `tags` varchar(64) NOT NULL\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE if not exists `player_friends` (\n" +
            "  `uuid` char(128) PRIMARY KEY NOT NULL,\n" +
            "  `friendUuid` char(128) UNIQUE NOT NULL,\n" +
            "  `isPending` boolean DEFAULT 0\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE if not exists `player_ignores` (\n" +
            "  `uuid` char(128) PRIMARY KEY NOT NULL,\n" +
            "  `ignoredUuid` char(128) NOT NULL\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE if not exists `party_data` (\n" +
            "  `id` int PRIMARY KEY AUTO_INCREMENT,\n" +
            "  `ownerUuid` char(128) NOT NULL\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE if not exists `guild_data` (\n" +
            "  `id` int PRIMARY KEY AUTO_INCREMENT,\n" +
            "  `ownerUuid` char(128) NOT NULL\n" +
            ");\n" +
            "\n" +
            "ALTER TABLE `player_experience` ADD FOREIGN KEY (`uuid`) REFERENCES `player_data` (`uuid`);\n" +
            "\n" +
            "ALTER TABLE `player_names` ADD FOREIGN KEY (`uuid`) REFERENCES `player_data` (`uuid`);\n" +
            "\n" +
            "ALTER TABLE `player_chat` ADD FOREIGN KEY (`uuid`) REFERENCES `player_data` (`uuid`);\n" +
            "\n" +
            "ALTER TABLE `player_addresses` ADD FOREIGN KEY (`uuid`) REFERENCES `player_data` (`uuid`);\n" +
            "\n" +
            "ALTER TABLE `player_tags` ADD FOREIGN KEY (`uuid`) REFERENCES `player_data` (`uuid`);\n" +
            "\n" +
            "ALTER TABLE `player_friends` ADD FOREIGN KEY (`uuid`) REFERENCES `player_data` (`uuid`);\n" +
            "\n" +
            "ALTER TABLE `player_ignores` ADD FOREIGN KEY (`uuid`) REFERENCES `player_data` (`uuid`);\n" +
            "\n" +
            "ALTER TABLE `party_data` ADD FOREIGN KEY (`ownerUuid`) REFERENCES `player_data` (`uuid`);\n" +
            "\n" +
            "ALTER TABLE `guild_data` ADD FOREIGN KEY (`ownerUuid`) REFERENCES `player_data` (`uuid`);\n";
}
