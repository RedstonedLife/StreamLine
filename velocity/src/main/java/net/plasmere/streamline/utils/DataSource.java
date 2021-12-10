package net.plasmere.streamline.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.objects.SavableGuild;
import net.plasmere.streamline.objects.SavableParty;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;

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

    public static boolean updatePlayerData(SavablePlayer player)
    {
        try
        {
            //TODO: find a better way to handle this. i dont know java, but this looks very ugly xd
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

    public static SavablePlayer getPlayerData(SavablePlayer player)
    {
        //TODO
        throw new java.lang.UnsupportedOperationException("Not supported yet.");
    }

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
