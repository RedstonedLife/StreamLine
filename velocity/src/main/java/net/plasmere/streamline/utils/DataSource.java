package net.plasmere.streamline.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.objects.SavableGuild;
import net.plasmere.streamline.objects.SavableParty;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.utils.sql.SQLQueries;

import java.sql.*;

import static net.plasmere.streamline.StreamLine.getLogger;

public class DataSource {
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    static {
        config.setJdbcUrl( "jdbc:mysql://%host%:%port%/%database%"
                .replace("%host%", StreamLine.databaseInfo.getHost())
                .replace("%port%", String.valueOf(StreamLine.databaseInfo.getPort()))
                .replace("%database%", StreamLine.databaseInfo.getDatabase()));
        config.setUsername( StreamLine.databaseInfo.getUser());
        config.setPassword( StreamLine.databaseInfo.getPass() );
        config.addDataSourceProperty( "cachePrepStmts" , "true" );
        config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
        config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
        ds = new HikariDataSource( config );
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
        try
        {
            Connection connection = getConnection();
            //TODO GET PREFIX AND DATABASE NAME FROM CONFIG
            String query = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = ? AND table_name = ?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, "streamline");
            statement.setString(2, "player_data");

            ResultSet resultSet = statement.executeQuery();

            int value = resultSet.getInt(1);

            if(value != 0)
                return;

            query = SQLQueries.tableCreation;
            statement = connection.prepareStatement(query);
            statement.executeQuery();

        } catch (SQLException e) {
            getLogger().warn("SQL Error: " + e.getMessage());
        }

    }

    /**
     * Update data of a player on the Database
     *
     * @param player The Player data.
     */
    public static boolean updatePlayerData(SavablePlayer player)
    {
        try
        {
            String query = "UPDATE streamline.player_data " +
                    "SET latestName = '"+player.latestName+"' " +
                    "SET displayName = '"+player.displayName+"' " +
                    "SET latestIp = '"+player.latestIP+"' " +
                    "SET latestVersion = '"+player.latestVersion+"' " +
                    //"SET latestServer = '"+player.latestServer+"' " +
                    "SET discordId = '"+player.discordID+"' " +
                    "SET mutedUntil = '"+player.mutedTill+"' " +
                    "SET points = '"+player.points+"' " +
                    "WHERE player_data.uuid = '"+player.getUniqueId()+"'";

            getConnection().prepareStatement(query).execute();
            return true;
        } catch (SQLException e) {
            getLogger().warn("SQL Error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get player data on the Database
     *
     * @return a savablePlayer object of the player.
     */
    public static SavablePlayer getPlayerData(String UUID)
    {
        try
        {
            Connection connection = getConnection();
            String query = "SELECT * FROM player_data INNER JOIN player_experience ON player_experience.uuid = player_data.uuid WHERE player_data.uuid = ?";
            PreparedStatement statement = connection.prepareStatement(query);
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
                player.lvl = resultSet.getInt("level");
            }

            return player;

        } catch (SQLException e) {
            getLogger().warn("SQL Error: " + e.getMessage());
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
        try
        {
            Connection connection = getConnection();
            String query = "SELECT uuid, address FROM player_ip_addresses WHERE uuid = ? AND address = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, player.getUUID());
            statement.setString(2, address);

            ResultSet result = connection.prepareStatement(query).executeQuery();

            // If there is a result the ip address is already logged.
            if(result.next())
                return;

            query = "INSERT INTO player_addresses (uuid, address) VALUES (?, ?)";
            statement = connection.prepareStatement(query);
            statement.setString(1, player.getUUID());
            statement.setString(2, address);

            statement.execute();
        } catch (SQLException e) {
            getLogger().warn("SQL Error: " + e.getMessage());
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
        try
        {
            Connection connection = getConnection();
            String query = "SELECT uuid, name FROM player_names WHERE uuid = ? AND name = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, player.getUUID());
            statement.setString(2, name);

            ResultSet result = connection.prepareStatement(query).executeQuery();

            // If there is a result the ip address is already logged.
            if(result.next())
                return;

            query = "INSERT INTO player_names (uuid, address) VALUES (?, ?)";
            statement = connection.prepareStatement(query);
            statement.setString(1, player.getUUID());
            statement.setString(2, name);

            statement.execute();
        } catch (SQLException e) {
            getLogger().warn("SQL Error: " + e.getMessage());
        }
    }

    /**
     * Update experience data of a player on the Database
     *
     * @param player The Player data.
     */
    public static void updatePlayerExperience(SavablePlayer player)
    {
        try{
            Connection connection = getConnection();
            String query = "UPDATE player_experience SET totalExperience = ?, currentExperience = ?, level = ? WHERE uuid = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, player.totalXP);
            statement.setInt(2, player.currentXP);
            statement.setInt(3, player.lvl);
            statement.setString(4, player.getUUID());

            statement.execute();
        } catch (SQLException e) {
            getLogger().warn("SQL Error: " + e.getMessage());
        }

    }

    /**
     * Update chat data of a player on the Database
     *
     * @param player The Player data.
     */
    public static void updatePlayerChat(SavablePlayer player)
    {
        try{
            Connection connection = getConnection();
            String query = "UPDATE player_chat SET chatChannel = ?, chatId = ? WHERE uuid = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, player.getUUID());
            statement.setString(2, player.chatChannel.toString());
            statement.setString(3, player.chatIdentifier);

            statement.execute();
        } catch (SQLException e) {
            getLogger().warn("SQL Error: " + e.getMessage());
        }

    }

    public static void updatePlayerParty(SavablePlayer player, SavableParty party)
    {
        //TODO
        throw new java.lang.UnsupportedOperationException("Not supported yet.");
    }

    public static void updatePlayerGuild(SavableGuild guild)
    {
        //TODO
        throw new java.lang.UnsupportedOperationException("Not supported yet.");
    }
}
