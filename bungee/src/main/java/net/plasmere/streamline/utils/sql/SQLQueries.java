package net.plasmere.streamline.utils.sql;

public enum SQLQueries {

    CREATE_TABLE("CREATE TABLE IF NOT EXISTS `player_data` (" +
            "  `UUID` char(128) PRIMARY KEY NOT NULL," +
            "  `latestName` varchar(64) NOT NULL," +
            "  `displayName` varchar(64) NOT NULL," +
            "  `latestIp` varchar(64) NOT NULL," +
            "  `latestVersion` varchar(16) NOT NULL," +
            "  `latestServer` varchar(64) NOT NULL," +
            "  `discordId` bigint," +
            "  `muted` boolean DEFAULT 0," +
            "  `mutedUntil` datetime," +
            "  `currentPartyId` int," +
            "  `currentGuildId` int," +
            "  `points` int DEFAULT 0" +
            ");" +
            "" +
            "CREATE TABLE IF NOT EXISTS `player_experience` (" +
            "  `UUID` char(128) PRIMARY KEY NOT NULL," +
            "  `totalExperience` int NOT NULL," +
            "  `currentExperience` int NOT NULL," +
            "  `level` int NOT NULL DEFAULT 1" +
            ");" +
            "" +
            "CREATE TABLE IF NOT EXISTS `player_names` (" +
            "  `UUID` char(128) PRIMARY KEY NOT NULL," +
            "  `name` varchar(64) UNIQUE NOT NULL" +
            ");" +
            "" +
            "CREATE TABLE IF NOT EXISTS `player_chat` (" +
            "  `UUID` char(128) PRIMARY KEY NOT NULL," +
            "  `chatChannel` varchar(64) NOT NULL," +
            "  `chatId` varchar(64) NOT NULL" +
            ");" +
            "" +
            "CREATE TABLE IF NOT EXISTS `player_addresses` (" +
            "  `UUID` char(128) PRIMARY KEY NOT NULL," +
            "  `address` varchar(64) NOT NULL" +
            ");" +
            "" +
            "CREATE TABLE IF NOT EXISTS `player_tags` (" +
            "  `UUID` char(128) PRIMARY KEY NOT NULL," +
            "  `tag` varchar(64) NOT NULL" +
            ");" +
            "" +
            "CREATE TABLE IF NOT EXISTS `player_friends` (" +
            "  `UUID` char(128) PRIMARY KEY NOT NULL," +
            "  `friendUUID` char(128) UNIQUE NOT NULL," +
            "  `isPending` boolean DEFAULT 0" +
            ");" +
            "" +
            "CREATE TABLE IF NOT EXISTS `player_ignores` (" +
            "  `UUID` char(128) PRIMARY KEY NOT NULL," +
            "  `ignoredUUID` char(128) NOT NULL" +
            ");" +
            "" +
            "CREATE TABLE IF NOT EXISTS `guild_data` (" +
            "  `id` int PRIMARY KEY AUTO_INCREMENT," +
            "  `name` varchar(128) UNIQUE NOT NULL," +
            "  `totalExperience` int NOT NULL," +
            "  `currentExperience` int NOT NULL," +
            "  `level` int NOT NULL DEFAULT 1," +
            "  `isMuted` boolean NOT NULL DEFAULT 0," +
            "  `isPublic` boolean NOT NULL DEFAULT 0," +
            "  `voiceId` bigint DEFAULT null," +
            "  `maxSize` int NOT NULL" +
            ");" +
            "" +
            "CREATE TABLE IF NOT EXISTS `guild_member` (" +
            "  `UUID` char(128) UNIQUE," +
            "  `guildId` int," +
            "  `level` ENUM ('MEMBER', 'MODERATOR', 'LEADER') NOT NULL" +
            ");" +
            "" +
            "CREATE TABLE IF NOT EXISTS `party_data` (" +
            "  `id` int PRIMARY KEY AUTO_INCREMENT," +
            "  `isMuted` boolean NOT NULL DEFAULT 0," +
            "  `isPublic` boolean NOT NULL DEFAULT 0," +
            "  `voiceId` bigint DEFAULT null," +
            "  `maxSize` int NOT NULL" +
            ");" +
            "" +
            "CREATE TABLE IF NOT EXISTS `party_member` (" +
            "  `UUID` char(128) UNIQUE," +
            "  `partyId` int," +
            "  `level` ENUM ('MEMBER', 'MODERATOR', 'LEADER') NOT NULL" +
            ");"),

    ;

    public String query;

    SQLQueries(String query) {
        this.query = query;
    }

}