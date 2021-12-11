package net.plasmere.streamline.utils.sql;

import com.velocitypowered.api.proxy.Player;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.objects.enums.SavableType;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PlayerUtils;
import net.plasmere.streamline.utils.UUIDUtils;

import java.sql.*;

/**
 *
 * @deprecated
 * Use DataSource.
 *
 */
public class Driver {

    public static Connection connect() throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver").getDeclaredConstructor().newInstance();

            String connectionURL = "jdbc:mysql://%host%:%port%"
                    .replace("%host%", StreamLine.databaseInfo.getHost())
                    .replace("%port%", String.valueOf(StreamLine.databaseInfo.getPort()))
                    .replace("%database%", StreamLine.databaseInfo.getDatabase());

            Connection connection = DriverManager.getConnection(connectionURL, StreamLine.databaseInfo.getUser(), StreamLine.databaseInfo.getPass());

            try {
                Statement statement = connection.createStatement();


                statement.execute("create database if not exists " + StreamLine.databaseInfo.getDatabase());

//                MessagingUtils.logWarning("Created new database " + StreamLine.databaseInfo.getDatabase() + "!");
            } catch (Exception e) {
                e.printStackTrace();
            }

            connectionURL = "jdbc:mysql://%host%:%port%/%database%"
                    .replace("%host%", StreamLine.databaseInfo.getHost())
                    .replace("%port%", String.valueOf(StreamLine.databaseInfo.getPort()))
                    .replace("%database%", StreamLine.databaseInfo.getDatabase());


            connection = DriverManager.getConnection(connectionURL, StreamLine.databaseInfo.getUser(), StreamLine.databaseInfo.getPass());

//            if (ConfigUtils.debug()) MessagingUtils.logInfo("Attempting connection: " + connectionURL + " with User '" + StreamLine.databaseInfo.getUser() + "' and Pass '" + StreamLine.databaseInfo.getPass() + "'");

            return connection;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void update(SavableType type, String uuid) {
        uuid = UUIDUtils.stripUUID(uuid);

        createIfNotExists(type);

//        if (! exists(type, uuid)) {
//            if (ConfigUtils.debug()) MessagingUtils.logInfo("Does not exist with type " + type + " and uuid " + uuid + "! Creating new entry!");
//            insert(type, uuid);
//            return;
//        }

        try {
//            PreparedStatement ps = connect().prepareStatement("update " + type.table + " set " + key + " = ? where uuid = ?");
//            ps.setObject(1, value);
//            ps.setString(2, uuid);
//
//            ps.executeUpdate();

            SavablePlayer player = PlayerUtils.getOrGetPlayerStatByUUID(UUIDUtils.makeDashedUUID(uuid));
            if (player == null) return;

            Connection connection = connect();
            Statement statement = connection.createStatement();
            statement.executeUpdate(buildUpdateForPlayer(type, player));
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ConfigUtils.debug()) MessagingUtils.logInfo("Updated entry with type " + type + " and uuid " + uuid + "!");
    }

    public static void addNewIpToPlayer(String ipAddress, Player player)
    {
        //TODO: IF IP IS ALREADY ON THE TABLE ^ SKIP :)
        try
        {
            Connection connection = connect();

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT ipAddress FROM ipAddress WHERE ipAddress = "+ipAddress+"AND UUID = "+player.getUniqueId());

            //If the ip is already on the db, we will skip this.
            if(rs.isBeforeFirst())
            {
                return;
            }
            rs.close();
            statement.close();

            statement = connection.createStatement();
            statement.executeQuery("INSERT INTO ipAddress () VALUES ()");

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    public static void insert(SavableType type, String uuid) {
//        if (ConfigUtils.debug()) MessagingUtils.logInfo("Trying to insert now!");
//        uuid = UUIDUtils.stripUUID(uuid);
//
//        switch (type) {
//            case PLAYER -> {
//                SavablePlayer player = PlayerUtils.getOrGetPlayerStatByUUID(UUIDUtils.makeDashedUUID(uuid));
//                if (player == null) return;
//
//                try {
//                    Connection connection = connect();
//                    Statement statement = connection.createStatement();
//
//                    statement.executeUpdate(buildInsertForPlayer(type, player));
//
//                    connection.close();
////                    MessagingUtils.logWarning("Inserted! -->\n" + getFormatted(type, uuid));
//                    if (ConfigUtils.debug()) MessagingUtils.logInfo("Inserted new player with uuid " + uuid + " and name " + player.latestName + "!");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    public static void createIfNotExists(SavableType type) {
        switch (type) {
            case PLAYER -> {
                try {
                    Connection connection = connect();
                    Statement statement = connection.createStatement();

                    statement.execute(buildCreateForPlayer(type));

                    connection.close();
//                    MessagingUtils.logWarning("Created new table " + type.table + "!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String buildCreateForPlayer(SavableType type) {
        return  "create table if not exists " + type.table + " ( uuid VARCHAR(255), " + buildCreateValuesForPlayer() + ", PRIMARY KEY ( uuid ) )";
    }

    public static String buildCreateValuesForPlayer() {
        return "total_xp INT(255)" + "," + "current_xp INT(255)" + "," + "lvl INT(255)" + "," + "playtime INT(255)" +
                ", " + "ips VARCHAR(1000)" + ", " + "names VARCHAR(1000)" + ", " + "latest_ip VARCHAR(1000)" + ", " + "muted BOOL" +
                ", " + "chat_channel VARCHAR(1000)" + ", " + "chat_identifier VARCHAR(1000)" +
                ", " + "bypass_for INT(255)" + ", " + "latest_name VARCHAR(1000)" + ", " + "display_name VARCHAR(1000)" + ", " + "guild VARCHAR(1000)"  +
                ", " + "party VARCHAR(1000)" + ", " + "tags VARCHAR(1000)" + ", " + "points INT(255)" + ", " + "last_from VARCHAR(1000)" +
                ", " + "last_to VARCHAR(1000)" + ", " + "reply_to VARCHAR(1000)" + ", " + "last_message VARCHAR(1000)" + ", " + "last_to_message VARCHAR(1000)" +
                ", " + "last_from_message VARCHAR(1000)" + ", " + "ignored VARCHAR(1000)" + ", " + "friends VARCHAR(1000)" + ", " + "pending_to_friends VARCHAR(1000)" +
                ", " + "pending_from_friends VARCHAR(1000)" + ", " + "latest_version VARCHAR(1000)" +
                ", " + "online BOOL" + ", " + "sspy BOOL" + ", " + "gspy BOOL" + ", " + "pspy BOOL" + ", " + "view_sc BOOL" + ", " + "sc BOOL" +
                ", " + "sspy_vs BOOL" + ", " + "gspy_vs BOOL" + ", " + "pspy_vs BOOL" + ", " + "sc_vs BOOL" + ", latest_server VARCHAR(1000)"
                ;
    }

//    public static String buildInsertForPlayer(SavableType type, SavablePlayer player) {
//        return  "insert into " + type.table + " values ( '" + UUIDUtils.stripUUID(player.uuid) + "' , " + buildInsertValuesForPlayer(player) + " )";
//    }
//
//    public static String buildInsertValuesForPlayer(SavablePlayer player) {
//        return player.totalXP + "," + player.currentXP + "," + player.lvl + "," + player.playSeconds +
//                ", '" + player.ips + "', '" + player.names + "', '" + player.latestIP + "', " + player.muted +
//                ", '" + player.chatChannel + "', '" + player.chatIdentifier +
//                "', " + player.bypassFor + ", '" + player.latestName + "', '" + player.displayName + "', '" + player.guild  +
//                "', '" + player.party + "', '" + player.tags + "', " + player.points + ", '" + player.lastFromUUID +
//                "', '" + player.lastToUUID + "', '" + player.replyToUUID + "', '" + player.lastMessage + "', '" + player.lastToMessage +
//                "', '" + player.lastFromMessage + "', '" + player.ignoreds + "', '" + player.friends + "', '" + player.pendingToFriends +
//                "', '" + player.pendingFromFriends + "', '" + player.latestVersion +
//                "', " + player.online + ", " + player.sspy + ", " + player.gspy + ", " + player.pspy + ", " + player.viewsc + ", " + player.sc +
//                ", " + player.sspyvs + ", " + player.gspyvs + ", " + player.pspyvs + ", " + player.scvs + ", '" + player.getFromKey("latest-server") + "'"
//                ;
//    }

    public static String buildUpdateForPlayer(SavableType type, SavablePlayer player) {
        return  "replace into " + type.table + " set `uuid` = '" + UUIDUtils.stripUUID(player.uuid) + "', " + buildUpdateValuesForPlayer(player);
    }

    public static String buildUpdateValuesForPlayer(SavablePlayer player) {
        return "`total_xp` = " + player.totalXP + ", " + "`current_xp` = " + player.currentXP + ", " + "`lvl` = " + player.lvl + ", " + "`playtime` = " + player.playSeconds +
                ", " + "`ips` = '" + player.ips + "', " + "`names` = '" + player.names + "', " + "`latest_ip` = '" + player.latestIP + "', " + "`muted` = " + player.muted +
                ", " + "`chat_channel` = '" + player.chatChannel + "', " + "`chat_identifier` = '" + player.chatIdentifier +
                "', " + "`bypass_for` = " + player.bypassFor + ", " + "`latest_name` = '" + player.latestName + "', " + "`display_name` = '" + player.displayName + "', " + "`guild` = '" + player.guild +
                "', " + "`party` = '" + player.party + "', " + "`tags` = '" + player.tags + "', " + "`points` = " + player.points + ", " + "`last_from` = '" + player.lastFromUUID +
                "', " + "`last_to` = '" + player.lastToUUID + "', " + "`reply_to` = '" + player.replyToUUID + "', " + "`last_message` = '" + player.lastMessage + "', " + "`last_to_message` = '" + player.lastToMessage +
                "', " + "`last_from_message` = '" + player.lastFromMessage + "', " + "`ignored` = '" + player.ignoreds + "', " + "`friends` = '" + player.friends + "', " + "`pending_to_friends` = '" + player.pendingToFriends +
                "', " + "`pending_from_friends` = '" + player.pendingFromFriends + "', " + "`latest_version` = '" + player.latestVersion + "'" +
                ", " + "`online` = " + player.online + ", " + "`sspy` = " + player.sspy + ", " + "`gspy` = " + player.gspy + ", " + "`pspy` = " + player.pspy + ", " + "`view_sc` = " + player.viewsc + ", " + "`sc` = " + player.sc +
                ", " + "`sspy_vs` = " + player.sspyvs + ", " + "`gspy_vs` = " + player.gspyvs + ", " + "`pspy_vs` = " + player.pspyvs + ", " + "`sc_vs` = " + player.scvs + ", `latest_server` = '" + player.getFromKey("latest-server") + "'"
                ;
    }

    public static String getFormatted(SavableType type, String uuid) {
        uuid = UUIDUtils.stripUUID(uuid);

        ResultSet set = get(type, uuid);

        StringBuilder builder = new StringBuilder();

        try {
            ResultSetMetaData metaData = set.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i ++) {
                String label = metaData.getColumnLabel(i);
                builder.append(label).append(" : ").append(set.getObject(label)).append("\n");
            }

            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "FAILED TO GET!";
    }

    public static ResultSet get(SavableType type, String uuid) {
        uuid = UUIDUtils.stripUUID(uuid);

        try {
            Connection connection = connect();
            PreparedStatement ps = connection.prepareStatement("select * from " + type.table + " where uuid = ?");
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            connection.close();
            return rs;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

//    public static boolean exists(SavableType type, String uuid) {
//        uuid = UUIDUtils.stripUUID(uuid);
//
//        try {
//            Connection connection = connect();
//
//            final String queryCheck = "SELECT count(*) from " + type.table + " WHERE uuid = ?";
//            final PreparedStatement ps = connection.prepareStatement(queryCheck);
//            ps.setString(1, uuid);
//            final ResultSet resultSet = ps.executeQuery();
//
//            boolean bool = resultSet.next();
//
//            connection.close();
//
//            return bool;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return false;
//    }
}
