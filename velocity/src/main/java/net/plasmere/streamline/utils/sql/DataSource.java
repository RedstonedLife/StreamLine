package net.plasmere.streamline.utils.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.objects.savable.groups.SavableGuild;
import net.plasmere.streamline.objects.savable.groups.SavableParty;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.objects.savable.users.SavableUser;
import net.plasmere.streamline.utils.MessagingUtils;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;

public class DataSource {
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();

            config.setJdbcUrl("jdbc:mysql://%host%:%port%/%database%"
                    .replace("%host%", StreamLine.databaseInfo.getHost())
                    .replace("%port%", String.valueOf(StreamLine.databaseInfo.getPort()))
                    .replace("%database%", StreamLine.databaseInfo.getDatabase()));
            config.setUsername(StreamLine.databaseInfo.getUser());
            config.setPassword(StreamLine.databaseInfo.getPass());
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("allowMultiQueries", "true");
            ds = new HikariDataSource(config);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private DataSource() {}

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    /**
     * This will verify if the player_data table exists
     * if not, it will create all the tables on the selected db
     */
    public static void verifyTables()
    {
        if (! ConfigUtils.moduleDBUse()) return;

        String query = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = ? AND table_name = ?;";

        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, StreamLine.databaseInfo.getDatabase());
                statement.setString(2, "player_data");

                ResultSet resultSet = statement.executeQuery();

                int value = 0;

                if (resultSet.next())
                    //TODO: THIS IS TESTED ONLY ON MARIADB, PLEASE TEST IT ON MYSQL.
                    query = "ALTER TABLE player_data ADD COLUMN IF NOT EXISTS playedSeconds INT NOT NULL DEFAULT '0';";

                try (PreparedStatement statement1 = connection.prepareStatement(query)) {
                    statement1.execute();
                }

                value = resultSet.getInt(1);

                if (value != 0) return;
            }

            query = SQLQueries.CREATE_TABLE.query;
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.execute();
            }
        } catch (SQLException e) {
            if (e.getMessage().endsWith("doesn't exist")) {
                try (Connection connection = getConnection()) {
                    query = SQLQueries.CREATE_TABLE.query;
                    try (PreparedStatement statement = connection.prepareStatement(query)) {
                        statement.execute();
                    }
                } catch (SQLException ex) {
                    if (e.getMessage() != null) MessagingUtils.logWarning("SQL Error (while creating new verified tables): " + e.getMessage());
                }
            } else if (e.getMessage() != null) MessagingUtils.logWarning("SQL Error (while verifying tables): " + e.getMessage());
        }

    }

    public static boolean userExistsOnTheDB(SavableUser player)
    {
        String query = "SELECT COUNT(*) FROM player_data WHERE UUID = ?;";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setString(1, player.uuid);

            ResultSet resultSet = statement.executeQuery();

            boolean returnValue = false;
            if(resultSet.next())
                returnValue = resultSet.getBoolean(1);
                return returnValue;

        } catch (SQLException e) {
            if (e.getMessage() != null) MessagingUtils.logWarning("SQL Error (while checking if user exists on database): " + e.getMessage());
        }
        return false;
    }

    /**
     * Update data of a player on the Database
     *
     * @param player The Player data.
     */
    public static void updatePlayerData(SavablePlayer player)
    {
        if (! ConfigUtils.moduleDBUse()) return;

        String query = "REPLACE INTO player_data (uuid, latestName, displayName, latestIp, latestVersion, latestServer, discordId, mutedUntil, points, playedSeconds) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        try(Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setString(1, player.getUUID());
            statement.setString(2, player.latestName);
            statement.setString(3, player.displayName);
            statement.setString(4, player.latestIP);
            statement.setString(5, player.latestVersion);
            statement.setString(6, "NOT IMPLEMENTED");
            statement.setLong(7, player.discordID);
            statement.setDate(8, new java.sql.Date(player.mutedTill.getTime()));
            statement.setInt(9, player.points);
            statement.setInt(10, player.playSeconds);

            statement.execute();
        } catch (SQLException e) {
            if (e.getMessage() != null) MessagingUtils.logWarning("SQL Error (while updating player data): " + e.getMessage());
        }
    }

    /**
     * Get player data on the Database
     *
     * @return a savablePlayer object of the player.
     */
    public static SavablePlayer getPlayerData(String UUID) throws Exception {
        if (! ConfigUtils.moduleDBUse()) throw new Exception("Database not enabled!");

        String query = "SELECT * FROM player_data INNER JOIN player_experience ON player_experience.uuid = player_data.uuid WHERE player_data.uuid = ?";

        try(Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setString(1, UUID);

            ResultSet resultSet = statement.executeQuery();

            SavablePlayer player = new SavablePlayer(UUID);

            while(resultSet.next())
            {
                player.latestName = resultSet.getString("latestName");
                player.displayName = resultSet.getString("displayName");
                player.latestIP = resultSet.getString("latestIp");
                player.latestVersion = resultSet.getString("latestVersion");
                //player.latestServer = resultSet.getString("latestServer");
                player.discordID = resultSet.getLong("discordId");
                player.muted = resultSet.getBoolean("muted");
                player.mutedTill = resultSet.getDate("mutedUntil");
                player.points = resultSet.getInt("points");
                player.totalXP = resultSet.getInt("totalExperience");
                player.currentXP = resultSet.getInt("currentExperience");
                player.level = resultSet.getInt("level");
                player.playSeconds = resultSet.getInt("playedSeconds");
            }

            return player;

        } catch (SQLException e) {
            if (e.getMessage() != null) MessagingUtils.logWarning("SQL Error (while getting player data): " + e.getMessage());
            return null;
        }

    }

    /**
     * Add a new ip to a player on the Database
     *
     * @param player The Player data.
     * @param address The ip address to add.
     */
    public static void addIpToPlayer(SavablePlayer player, String address)
    {
        if (! ConfigUtils.moduleDBUse()) return;

        if(!userExistsOnTheDB(player))
        {
            MessagingUtils.logWarning("Player doesn't exist on the database, you should execute updatePlayerData first.");
            return;
        }

//        MessagingUtils.logWarning("UUID = " + player.getUUID());

        String query = "REPLACE INTO player_addresses (uuid, address) VALUES (?, ?);";

        try(Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setString(1, player.getUUID());
            statement.setString(2, address);

            statement.execute();

        } catch (SQLException e) {
            if (e.getMessage() != null) MessagingUtils.logWarning("SQL Error (while adding player ip): " + e.getMessage());
        }
    }

    /**
     * Add a name to a player on the Database
     *
     * @param player The Player data.
     * @param name The new name to add.
     */
    public static void addNameToPlayer(SavablePlayer player, String name)
    {
        if (! ConfigUtils.moduleDBUse()) return;

        if(!userExistsOnTheDB(player))
        {
            MessagingUtils.logWarning("Player doesn't exist on the database, you should execute updatePlayerData first.");
            return;
        }
//        MessagingUtils.logWarning("UUID = " + player.getUUID());

        String query = "REPLACE INTO player_names (uuid, name) VALUES (?, ?);";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setString(1, player.getUUID());
            statement.setString(2, name);

            statement.execute();
        } catch (SQLException e) {
            if (e.getMessage() != null) MessagingUtils.logWarning("SQL Error (while adding player name): " + e.getMessage());
        }
    }

    /**
     * Update experience data of a player on the Database
     *
     * @param player The Player data.
     */
    public static void updatePlayerExperience(SavablePlayer player)
    {
        if (! ConfigUtils.moduleDBUse()) return;

        String query = "REPLACE INTO player_experience (uuid, totalExperience, currentExperience, level) VALUES (?, ?, ?, ?);";

        try(Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setString(1, player.getUUID());
            statement.setInt(2, player.totalXP);
            statement.setInt(3, player.currentXP);
            statement.setInt(4, player.level);

            statement.execute();

        } catch (SQLException e) {
            if (e.getMessage() != null) MessagingUtils.logWarning("SQL Error (while updating player experience): " + e.getMessage());
        }
    }

    /**
     * Update chat data of a player on the Database
     *
     * @param player The Player data.
     */
    public static void updatePlayerChat(SavablePlayer player)
    {
        if (! ConfigUtils.moduleDBUse()) return;

        String query = "REPLACE INTO player_chat (uuid, chatChannel, chatId) VALUES (?, ?, ?);";

        try(Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setString(1, player.getUUID());
            statement.setString(2, player.chatChannel.toString());
            statement.setString(3, player.chatIdentifier);

            statement.execute();

        } catch (SQLException e) {
            if (e.getMessage() != null) MessagingUtils.logWarning("SQL Error (while updating player chat): " + e.getMessage());
        }
    }

    //region Friends
    /**
     * Add a friend on the Database
     *
     * @param sender The Sender
     * @param receiver The Receiver
     */
    public static void sendFriendRequest(SavableUser sender, SavableUser receiver)
    {
        if (! ConfigUtils.moduleDBUse()) return;

        String query = "INSERT INTO player_friends (uuid, friendUUID, isPending) VALUES (?, ?, 0), (?, ?, 1)";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setString(1, sender.uuid);
            statement.setString(2, receiver.uuid);
            statement.setString(3, receiver.uuid);
            statement.setString(4, sender.uuid);

            statement.execute();
        } catch (SQLException e) {
            if (e.getMessage() != null) MessagingUtils.logWarning("SQL Error (while sending friend request): " + e.getMessage());
        }
    }

    /**
     * confirm Friend request,
     *
     * BY DEFAULT THE SENDER ALREADY HAS CONFIRMED SO CALL THIS ONLY WHEN THE RECEIVER CONFIRMS
     *
     * @param receiver The Receiver
     * @param sender The Sender
     * @param hasAccepted if the receiver has accepted the request.
     */
    public static void confirmFriendRequest(SavableUser sender, SavableUser receiver, boolean hasAccepted)
    {
        if (! ConfigUtils.moduleDBUse()) return;

        String query;

        if(hasAccepted)
            query = "UPDATE player_friends SET isPending = 0 WHERE uuid = ? AND friendUUID = ?";
        else
            query = "DELETE FROM player_friends WHERE (uuid, friendUUID) IN ((?, ?), (?, ?))";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(query))
        {
            if(hasAccepted)
            {
                statement.setString(1, receiver.uuid);
                statement.setString(2, sender.uuid);
            }
            else
            {
                statement.setString(1, receiver.uuid);
                statement.setString(2, sender.uuid);
                statement.setString(3, sender.uuid);
                statement.setString(4, receiver.uuid);
            }

            statement.execute();

        } catch (SQLException e) {
            if (e.getMessage() != null) MessagingUtils.logWarning("SQL Error (while confirming friend request): " + e.getMessage());
        }
    }
    //endregion

    //region Ignore Related Stuff
    /**
     * Ignore player :)
     *
     * @param sender The Sender
     * @param receiver the player to ignore
     */
    public static void ignorePlayer(SavableUser sender, SavableUser receiver)
    {
        if (! ConfigUtils.moduleDBUse()) return;

        String query = "INSERT INTO player_ignores (uuid, ignoredUUID) VALUES (?, ?)";

        try(Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setString(1, sender.uuid);
            statement.setString(2, receiver.uuid);

            statement.execute();
        } catch (SQLException e) {
            if (e.getMessage() != null) MessagingUtils.logWarning("SQL Error (while ignoring player): " + e.getMessage());
        }
    }
    /**
     * Removes an ignore row from the db
     *
     * @param sender The Sender
     * @param receiver the player that was ignored
     */
    public static void stopIgnoringPlayer(SavableUser sender, SavableUser receiver)
    {
        if (! ConfigUtils.moduleDBUse()) return;

        String query = "DELETE FROM player_ignores WHERE (uuid = ?, ignoredUUID = ?)";

        try(Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setString(1, sender.uuid);
            statement.setString(2, receiver.uuid);

            statement.execute();
        } catch (SQLException e) {
            if (e.getMessage() != null) MessagingUtils.logWarning("SQL Error (while stopping ignoring player): " + e.getMessage());
        }
    }
    //endregion

    //region Party Related Stuff
    public static void addPlayerToParty(SavableUser player, SavableParty party)
    {
        if (! ConfigUtils.moduleDBUse()) return;
        if(!player.party.isEmpty()) return;
        String query = "INSERT INTO party_member (UUID, partyId) VALUES (?, ?)";

        try(Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setString(1, player.uuid);
            statement.setInt(2, party.databaseID);

            statement.execute();
        } catch (SQLException e) {
            if (e.getMessage() != null) MessagingUtils.logWarning("SQL Error (while adding player to party): " + e.getMessage());
        }
    }

    public static void removePlayerFromParty(SavableUser player, SavableParty party)
    {
        if (! ConfigUtils.moduleDBUse()) return;
        if(player.party.isEmpty()) return; //wtf

        String query = "DELETE FROM party_member WHERE (UUID = ?, partyId = ?)";

        try(Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setString(1, player.uuid);
            statement.setInt(2, party.databaseID);

            statement.execute();
        } catch (SQLException e) {
            if (e.getMessage() != null) MessagingUtils.logWarning("SQL Error (while removing player from party): " + e.getMessage());
        }
    }

    public static int createParty(SavableUser founder, SavableParty party)
    {
        if (! ConfigUtils.moduleDBUse()) return -1;

        try(Connection connection = getConnection())
        {
            String query = "INSERT INTO party_data (voiceId, maxSize) VALUES (?, ?); " +
                    "INSERT INTO party_member (UUID, partyId, level) VALUES (?, ?, ?); ";

            try(PreparedStatement statement = connection.prepareStatement(query))
            {
                statement.setLong(1, party.voiceID);
                statement.setInt(2, party.maxSize);
                statement.setString(3, founder.uuid);
                statement.setInt(4, party.databaseID);
                statement.setInt(5, 3);

                statement.execute();
            }

            query = "SELECT id FROM party_data WHERE id = SCOPE_IDENTITY();";

            try(PreparedStatement statement = connection.prepareStatement(query))
            {
                ResultSet resultSet = statement.executeQuery();
                return resultSet.getInt("id");
            }

        } catch (SQLException e) {
            if (e.getMessage() != null) MessagingUtils.logWarning("SQL Error (while creating party): " + e.getMessage());
            return -1;
        }
    }

    public static void updatePartyData(SavableParty party)
    {
        if (! ConfigUtils.moduleDBUse()) return;

        String query = "REPLACE INTO party_data (id, voiceId, maxSize) VALUES (?, ?, ?);";

        try(Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setInt(1, party.databaseID);
            statement.setLong(2, party.voiceID);
            statement.setInt(3, party.maxSize);

            statement.execute();
        } catch (SQLException e) {
            if (e.getMessage() != null) MessagingUtils.logWarning("SQL Error (while updating party data): " + e.getMessage());
        }
    }
    //endregion

    //region Guild Related Stuff
    public static void addPlayerToGuild(SavableUser player, SavableGuild guild)
    {
        if (! ConfigUtils.moduleDBUse()) return;
        if(!player.guild.isEmpty()) return;
        String query = "INSERT INTO guild_member (UUID, guildId) VALUES (?, ?)";

        try(Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setString(1, player.uuid);
            statement.setInt(2, guild.databaseID);

            statement.execute();
        } catch (SQLException e) {
            if (e.getMessage() != null) MessagingUtils.logWarning("SQL Error (while adding player to guild): " + e.getMessage());
        }
    }

    public static void removePlayerFromGuild(SavableUser player, SavableGuild guild)
    {
        if (! ConfigUtils.moduleDBUse()) return;
        if(player.guild.isEmpty()) return; //wtf

        String query = "DELETE FROM guild_member WHERE (UUID = ?, guildId = ?)";

        try(Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setString(1, player.uuid);
            statement.setInt(2, guild.databaseID);

            statement.execute();
        } catch (SQLException e) {
            if (e.getMessage() != null) MessagingUtils.logWarning("SQL Error (while removing player from guild): " + e.getMessage());
        }
    }

    public static int createGuild(SavableUser player, SavableGuild guild)
    {
        if (! ConfigUtils.moduleDBUse()) return -1;

        try(Connection connection = getConnection())
        {
            String query = "INSERT INTO guild_data (name, totalExperience, currentExperience, level, isMuted, isPublic, voiceId, maxSize) VALUES (?, ?, ?, ?, ?, ?, ?, ?);" +
                    " INSERT INTO guild_member (UUID, guildId, level) VALUES (?, ?, ?);";

            try(PreparedStatement statement = connection.prepareStatement(query))
            {
                statement.setString(1, guild.name);
                statement.setInt(2, guild.totalXP);
                statement.setInt(3, guild.currentXP);
                statement.setInt(4, guild.level);
                statement.setBoolean(5, guild.isMuted);
                statement.setBoolean(6, guild.isPublic);
                statement.setLong(7, guild.voiceID);
                statement.setInt(8, guild.maxSize);
                statement.setString(9, player.uuid);
                statement.setInt(10, guild.databaseID);
                statement.setInt(11, 3);

                statement.execute();
            }

            query = "SELECT id FROM guild_data WHERE id = SCOPE_IDENTITY();";

            try(PreparedStatement statement = connection.prepareStatement(query))
            {
                ResultSet resultSet = statement.executeQuery();
                return resultSet.getInt("id");
            }

        } catch (SQLException e) {
            if (e.getMessage() != null) MessagingUtils.logWarning("SQL Error (while creating guild): " + e.getMessage());
            return -1;
        }
    }

    public static void updateGuildData(SavableGuild guild)
    {
        if (! ConfigUtils.moduleDBUse()) return;

        String query = "REPLACE INTO guild_data (id, name, totalExperience, currentExperience, level, isMuted, isPublic, voiceId, maxSize) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

        try(Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setInt(1, guild.databaseID);
            statement.setString(2, guild.name);
            statement.setInt(3, guild.totalXP);
            statement.setInt(4, guild.currentXP);
            statement.setInt(5, guild.level);
            statement.setBoolean(6, guild.isMuted);
            statement.setBoolean(7, guild.isPublic);
            statement.setLong(8, guild.voiceID);
            statement.setInt(9, guild.maxSize);

            statement.execute();
        } catch (SQLException e) {
            if (e.getMessage() != null) MessagingUtils.logWarning("SQL Error (while updating guild data): " + e.getMessage());
        }
    }
    //endregion
}
