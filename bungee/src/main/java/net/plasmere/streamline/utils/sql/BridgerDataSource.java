package net.plasmere.streamline.utils.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.objects.lists.SingleSet;
import net.plasmere.streamline.objects.savable.users.SavableUser;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PluginUtils;
import net.plasmere.streamline.utils.TextUtils;
import net.plasmere.streamline.utils.objects.*;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class BridgerDataSource {
    public static HashMap<Host, HikariDataSource> loadedHosts = new HashMap<>();

    public static void reloadHikariHosts() {
        loadedHosts.clear();
        for (Host host : StreamLine.msbConfig.loadedHosts) {
            try {
                HikariConfig config = new HikariConfig();
                HikariDataSource ds;

                Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();

                config.setJdbcUrl(host.link);
                config.setUsername(host.user);
                config.setPassword(host.pass);
                config.addDataSourceProperty("cachePrepStmts", "true");
                config.addDataSourceProperty("prepStmtCacheSize", "250");
                config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                config.addDataSourceProperty("allowMultiQueries", "true");
                ds = new HikariDataSource(config);

                loadedHosts.put(host, ds);
            } catch (Exception e) {
                MessagingUtils.logSevere("Something is mis-configured! Caught error: " + e.getMessage());
            }
        }
    }

    public static String sync(Syncable syncable, SavableUser on) {
        String pulled = pull(syncable.pullFrom, on);
        push(syncable.pushTo, pulled, on, syncable.isString);
        return pulled;
    }

    public static String pull(PullAndPushInfo pullFrom, SavableUser on) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            HikariDataSource dataSource = loadedHosts.get(pullFrom.getHostAsHost());
            Connection connection = dataSource.getConnection();
            String sql = "SELECT " + pullFrom.column + " FROM " + pullFrom.table + " WHERE " + pullFrom.where;
            PreparedStatement statement = connection.prepareStatement(TextUtils.replaceAllPlayerBungee(sql, on));
            ResultSet result = statement.executeQuery();
            String pulled = "";
            while (result.next()) {
                pulled = String.valueOf(result.getObject(1));
            }
            return pulled;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return StreamLine.msbConfig.getValuesError();
        }
    }

    public static void push(PullAndPushInfo pushTo, String thing, SavableUser on, boolean isString) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            HikariDataSource dataSource = loadedHosts.get(pushTo.getHostAsHost());
            Connection connection = dataSource.getConnection();
            String sql;
            if (! isString) {
                sql = "UPDATE " + pushTo.table + " SET " + pushTo.column + " = " + thing + " WHERE " + pushTo.where;
            } else {
                sql = "UPDATE " + pushTo.table + " SET " + pushTo.column + " = '" + thing + "' WHERE " + pushTo.where;
            }
            PreparedStatement statement = connection.prepareStatement(TextUtils.replaceAllPlayerBungee(sql, on));
            statement.execute();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static String query(CustomSQLInfo sqlInfo, SavableUser on, String... extra) {
        SavedQueries q = PluginUtils.getSavedQueryByPlayer(on.uuid);
        if (q != null) {
            if (q.getTillExpiry(sqlInfo.identifier) > 0) {
                return q.getResult(sqlInfo.identifier);
            }
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            HikariDataSource dataSource = loadedHosts.get(sqlInfo.getHostAsHost());
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(TextUtils.replaceAllPlayerBungee(TextUtils.replaceArgs(sqlInfo.sql, extra), on));
            ResultSet result = statement.executeQuery();
            String pulled = "";
            while (result.next()) {
                pulled = String.valueOf(result.getObject(1));
            }
            PluginUtils.putQueryResult(on.uuid, sqlInfo.identifier, pulled);
            return pulled;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return StreamLine.msbConfig.getValuesError();
        }
    }

    public static void execute(CustomSQLInfo sqlInfo, SavableUser on, String... extra) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            HikariDataSource dataSource = loadedHosts.get(sqlInfo.getHostAsHost());
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(TextUtils.replaceAllPlayerBungee(TextUtils.replaceArgs(sqlInfo.sql, extra), on));
            statement.execute();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}