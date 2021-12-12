package net.plasmere.streamline.utils.sql;

public enum SQLQueries {

    CREATE_TABLE("CREATE TABLE guild_data (id int(11) NOT NULL, ownerUUID char(128) COLLATE utf8mb4_bin NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;" +
            " CREATE TABLE party_data (id int(11) NOT NULL, ownerUUID char(128) COLLATE utf8mb4_bin NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;" +
            " CREATE TABLE player_addresses (UUID char(128) COLLATE utf8mb4_bin NOT NULL, address varchar(64) COLLATE utf8mb4_bin NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;" +
            " CREATE TABLE player_chat (UUID char(128) COLLATE utf8mb4_bin NOT NULL, chatChannel varchar(64) COLLATE utf8mb4_bin NOT NULL, chatId varchar(64) COLLATE utf8mb4_bin NOT NULL ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;" +
            " CREATE TABLE player_data (UUID char(128) COLLATE utf8mb4_bin NOT NULL, latestName varchar(64) COLLATE utf8mb4_bin NOT NULL, displayName varchar(64) COLLATE utf8mb4_bin NOT NULL, latestIp varchar(64) COLLATE utf8mb4_bin NOT NULL, latestVersion varchar(16) COLLATE utf8mb4_bin NOT NULL, latestServer varchar(64) COLLATE utf8mb4_bin NOT NULL, discordId bigint(20) DEFAULT NULL, muted tinyint(1) DEFAULT 0, mutedUntil datetime DEFAULT NULL, currentPartyId int(11) DEFAULT NULL, currentGuildId int(11) DEFAULT NULL, points int(11) DEFAULT 0) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;" +
            " CREATE TABLE player_experience (UUID char(128) COLLATE utf8mb4_bin NOT NULL, totalExperience int(11) NOT NULL, currentExperience int(11) NOT NULL, level int(11) NOT NULL DEFAULT 1) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;" +
            " CREATE TABLE player_friends (UUID char(128) COLLATE utf8mb4_bin NOT NULL, friendUUID char(128) COLLATE utf8mb4_bin NOT NULL, isPending tinyint(1) DEFAULT 0) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;" +
            " CREATE TABLE player_ignores (UUID char(128) COLLATE utf8mb4_bin NOT NULL, ignoredUUID char(128) COLLATE utf8mb4_bin NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;" +
            " CREATE TABLE player_names (UUID char(128) COLLATE utf8mb4_bin NOT NULL, name int(11) NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;" +
            " CREATE TABLE player_tags (UUID char(128) COLLATE utf8mb4_bin NOT NULL, tags varchar(64) COLLATE utf8mb4_bin NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;" +
            " ALTER TABLE guild_data ADD PRIMARY KEY (id), ADD KEY ownerUUID (ownerUUID);" +
            " ALTER TABLE party_data ADD PRIMARY KEY (id), ADD KEY ownerUUID (ownerUUID);" +
            " ALTER TABLE player_addresses ADD PRIMARY KEY (UUID);" +
            " ALTER TABLE player_chat ADD PRIMARY KEY (UUID);" +
            " ALTER TABLE player_data ADD PRIMARY KEY (UUID);" +
            " ALTER TABLE player_experience ADD PRIMARY KEY (UUID);" +
            " ALTER TABLE player_friends ADD PRIMARY KEY (UUID), ADD UNIQUE KEY friendUUID (friendUUID);" +
            " ALTER TABLE player_ignores ADD PRIMARY KEY (UUID);" +
            " ALTER TABLE player_names ADD PRIMARY KEY (UUID), ADD UNIQUE KEY name (name);" +
            " ALTER TABLE player_tags ADD PRIMARY KEY (UUID);" +
            " ALTER TABLE guild_data MODIFY id int(11) NOT NULL AUTO_INCREMENT;" +
            " ALTER TABLE party_data MODIFY id int(11) NOT NULL AUTO_INCREMENT;" +
            " ALTER TABLE guild_data ADD CONSTRAINT guild_data_ibfk_1 FOREIGN KEY (ownerUUID) REFERENCES player_data (UUID);" +
            " ALTER TABLE party_data ADD CONSTRAINT party_data_ibfk_1 FOREIGN KEY (ownerUUID) REFERENCES player_data (UUID);" +
            " ALTER TABLE player_addresses ADD CONSTRAINT player_addresses_ibfk_1 FOREIGN KEY (UUID) REFERENCES player_data (UUID);" +
            " ALTER TABLE player_chat ADD CONSTRAINT player_chat_ibfk_1 FOREIGN KEY (UUID) REFERENCES player_data (UUID);" +
            " ALTER TABLE player_experience ADD CONSTRAINT player_experience_ibfk_1 FOREIGN KEY (UUID) REFERENCES player_data (UUID);" +
            " ALTER TABLE player_friends ADD CONSTRAINT player_friends_ibfk_1 FOREIGN KEY (UUID) REFERENCES player_data (UUID);" +
            " ALTER TABLE player_ignores ADD CONSTRAINT player_ignores_ibfk_1 FOREIGN KEY (UUID) REFERENCES player_data (UUID);" +
            " ALTER TABLE player_names ADD CONSTRAINT player_names_ibfk_1 FOREIGN KEY (UUID) REFERENCES player_data (UUID);" +
            " ALTER TABLE player_tags ADD CONSTRAINT player_tags_ibfk_1 FOREIGN KEY (UUID) REFERENCES player_data (UUID);"),

    ;

    public String query;

    SQLQueries(String query) {
        this.query = query;
    }

}