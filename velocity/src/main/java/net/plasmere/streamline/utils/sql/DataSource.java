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
            Class.forName("com.mysql.jdbc.Driver").getDeclaredConstructor().newInstance();

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

        try (Connection connection = getConnection())
        {
            try(PreparedStatement statement = connection.prepareStatement(query))
            {
                statement.setString(1, StreamLine.databaseInfo.getDatabase());
                statement.setString(2, "player_data");

                ResultSet resultSet = statement.executeQuery();

                int value = 0;

                if(resultSet.next())
                    value = resultSet.getInt(1);

                if(value != 0) return;
            }

            query = SQLQueries.CREATE_TABLE.query;
            try(PreparedStatement statement = connection.prepareStatement(query))
            {
                statement.execute();
            }


        } catch (SQLException e) {
            if (e.getMessage() != null) MessagingUtils.logWarning("SQL Error: " + e.getMessage());
        }

    }

    /**
     * Update data of a player on the Database
     *
     * @param player The Player data.
     */
    public static void updatePlayerData(SavablePlayer player)
    {
        if (! ConfigUtils.moduleDBUse()) return;

        String query = "REPLACE INTO player_data (uuid, latestName, displayName, latestIp, latestVersion, latestServer, discordId, mutedUntil, points) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

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

            statement.execute();
        } catch (SQLException e) {
            if (e.getMessage() != null) MessagingUtils.logWarning("SQL Error: " + e.getMessage());
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
            }

            return player;

        } catch (SQLException e) {
            if (e.getMessage() != null) MessagingUtils.logWarning("SQL Error: " + e.getMessage());
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

        String query = "REPLACE INTO player_addresses (uuid, address) VALUES (?, ?);";

        try(Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setString(1, player.getUUID());
            statement.setString(2, address);

            connection.prepareStatement(query).execute();

        } catch (SQLException e) {
            if (e.getMessage() != null) MessagingUtils.logWarning("SQL Error: " + e.getMessage());
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

        String query = "REPLACE INTO player_names (uuid, name) VALUES (?, ?)";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setString(1, player.getUUID());
            statement.setString(2, name);

            connection.prepareStatement(query).execute();
        } catch (SQLException e) {
            if (e.getMessage() != null) MessagingUtils.logWarning("SQL Error: " + e.getMessage());
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
            if (e.getMessage() != null) MessagingUtils.logWarning("SQL Error: " + e.getMessage());
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
            if (e.getMessage() != null) MessagingUtils.logWarning("SQL Error: " + e.getMessage());
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
            if (e.getMessage() != null) MessagingUtils.logWarning("SQL Error: " + e.getMessage());
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
            if (e.getMessage() != null) MessagingUtils.logWarning("SQL Error: " + e.getMessage());
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
            if (e.getMessage() != null) MessagingUtils.logWarning("SQL Error: " + e.getMessage());
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
            if (e.getMessage() != null) MessagingUtils.logWarning("SQL Error: " + e.getMessage());
        }
    }
    //endregion

    //region Party Related Stuff
    public static void addPlayerToParty(SavableUser player, SavableParty party)
    {
        if (! ConfigUtils.moduleDBUse()) return;
        //TODO
        throw new java.lang.UnsupportedOperationException("Not supported yet.");
    }
    //endregion

    //region Guild Related Stuff
    public static void addPlayerToGuild(SavableUser player, SavableGuild guild)
    {
        if (! ConfigUtils.moduleDBUse()) return;
        //TODO
        throw new java.lang.UnsupportedOperationException("Not supported yet.");
    }
    //endregion
}
